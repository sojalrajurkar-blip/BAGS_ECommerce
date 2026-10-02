/**
 * ============================================================================
 * RÓRA Luxury Atelier — Product Catalog Repository
 * ============================================================================
 * Authoritative interface for products, variants, availability, categories,
 * filtering, sorting, pagination, and full-text search with Spring Boot REST backend.
 */

import { PRODUCTS } from '../mockData';
import { Product } from '../../types/domain';
import { apiClient } from '../apiClient';

export interface ProductFilters {
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  material?: string;
  color?: string;
  badge?: string;
  sortBy?: string;
  search?: string;
  limit?: number;
  page?: number;
}

interface BackendPagedProductResponse {
  content: Product[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

const USE_MOCK = process.env.NEXT_PUBLIC_USE_MOCK_DATA === 'true';

export const productRepository = {
  /**
   * Fetches paginated & filtered products from Spring Boot backend.
   */
  async getProducts(filters: ProductFilters = {}): Promise<Product[]> {
    if (USE_MOCK) {
      return this.getLocalFilteredProducts(filters);
    }

    try {
      const params: Record<string, string | number | boolean | undefined> = {};
      if (filters.category && filters.category !== 'all') params.category = filters.category;
      if (filters.minPrice !== undefined) params.minPrice = filters.minPrice;
      if (filters.maxPrice !== undefined) params.maxPrice = filters.maxPrice;
      if (filters.material && filters.material !== 'all') params.material = filters.material;
      if (filters.color && filters.color !== 'all') params.color = filters.color;
      if (filters.search) params.search = filters.search;
      if (filters.sortBy) params.sortBy = filters.sortBy;
      if (filters.limit) params.limit = filters.limit;
      if (filters.page) params.page = filters.page;

      const res = await apiClient.get<BackendPagedProductResponse | Product[]>('/products', { params });
      
      let productsList: Product[] = [];
      if (Array.isArray(res)) {
        productsList = res;
      } else if (res && Array.isArray(res.content)) {
        productsList = res.content;
      }

      if (productsList.length > 0) {
        return productsList;
      }
    } catch (err) {
      console.warn('Backend products query failed, using seeded catalog:', err);
    }

    return this.getLocalFilteredProducts(filters);
  },

  /**
   * Retrieves single product by its database ID or slug.
   */
  async getProductById(id: string): Promise<Product | null> {
    if (!USE_MOCK) {
      try {
        const data = await apiClient.get<Product>(`/products/${id}`);
        if (data && data.id) {
          return data;
        }
      } catch (err) {
        console.warn(`Backend product fetch for "${id}" failed:`, err);
      }
    }
    const product = (PRODUCTS as unknown as Product[]).find(p => p.id === id || p.slug === id);
    return product || null;
  },

  /**
   * Retrieves single product by its URL slug.
   */
  async getProductBySlug(slug: string): Promise<Product | null> {
    if (!USE_MOCK) {
      try {
        const data = await apiClient.get<Product>(`/products/${slug}`);
        if (data && (data.slug || data.id)) {
          return data;
        }
      } catch (err) {
        console.warn(`Backend product fetch for slug "${slug}" failed:`, err);
      }
    }
    const product = (PRODUCTS as unknown as Product[]).find(p => p.slug === slug || p.id === slug);
    return product || null;
  },

  /**
   * Retrieves featured flagship products.
   */
  async getFeaturedProducts(limit = 4): Promise<Product[]> {
    if (!USE_MOCK) {
      try {
        const data = await apiClient.get<Product[]>('/products/featured');
        if (Array.isArray(data) && data.length > 0) {
          return data.slice(0, limit);
        }
      } catch {
        // Fallback to seeded items if endpoint not populated
      }
    }
    return (PRODUCTS as unknown as Product[]).slice(0, limit);
  },

  /**
   * Retrieves best-selling artisanal carry items.
   */
  async getBestSellers(limit = 4): Promise<Product[]> {
    if (!USE_MOCK) {
      try {
        const data = await apiClient.get<Product[]>('/products/best-sellers');
        if (Array.isArray(data) && data.length > 0) {
          return data.slice(0, limit);
        }
      } catch {
        // Fallback
      }
    }
    const bestSellers = (PRODUCTS as unknown as Product[]).filter(
      p => p.badge === 'Best Seller' || p.badge === 'Popular' || p.badge === 'Staff Pick'
    ).slice(0, limit);
    return bestSellers.length > 0 ? bestSellers : (PRODUCTS as unknown as Product[]).slice(0, limit);
  },

  /**
   * Retrieves related products for cross-sell recommendations.
   */
  async getRelatedProducts(productId: string, limit = 4): Promise<Product[]> {
    if (!USE_MOCK) {
      try {
        const data = await apiClient.get<Product[]>(`/products/${productId}/related`);
        if (Array.isArray(data) && data.length > 0) {
          return data.slice(0, limit);
        }
      } catch {
        // Fallback
      }
    }
    const all = PRODUCTS as unknown as Product[];
    const current = all.find(p => p.id === productId || p.slug === productId);
    let related = all.filter(p => p.id !== productId && p.slug !== productId);

    if (current) {
      const sameCategory = related.filter(p => p.category === current.category);
      const otherCategory = related.filter(p => p.category !== current.category);
      related = [...sameCategory, ...otherCategory];
    }

    return related.slice(0, limit);
  },

  /**
   * Debounced full-text search against the backend catalog.
   */
  async searchProducts(query = ''): Promise<Product[]> {
    if (!query || !query.trim()) {
      return [];
    }

    if (!USE_MOCK) {
      try {
        const data = await apiClient.get<Product[]>('/products/search', { params: { q: query.trim() } });
        if (Array.isArray(data)) {
          return data;
        }
      } catch (err) {
        console.warn('Backend search query failed:', err);
      }
    }

    const q = query.toLowerCase().trim();
    return (PRODUCTS as unknown as Product[]).filter(p =>
      p.name.toLowerCase().includes(q) ||
      p.description.toLowerCase().includes(q) ||
      p.category.toLowerCase().includes(q) ||
      (p.material && p.material.toLowerCase().includes(q)) ||
      (p.tagline && p.tagline.toLowerCase().includes(q))
    );
  },

  /**
   * Local filtered dataset helper.
   */
  getLocalFilteredProducts(filters: ProductFilters = {}): Product[] {
    let result = [...(PRODUCTS as unknown as Product[])];

    if (filters.category && filters.category !== 'all') {
      result = result.filter(p => p.category === filters.category || p.categoryId === filters.category);
    }

    if (filters.minPrice !== undefined) {
      result = result.filter(p => p.price >= filters.minPrice!);
    }

    if (filters.maxPrice !== undefined) {
      result = result.filter(p => p.price <= filters.maxPrice!);
    }

    if (filters.material && filters.material !== 'all') {
      result = result.filter(p => p.material && p.material.toLowerCase().includes(filters.material!.toLowerCase()));
    }

    if (filters.color && filters.color !== 'all') {
      result = result.filter(p => p.colors && p.colors.some(c => c.name.toLowerCase() === filters.color!.toLowerCase()));
    }

    if (filters.badge && filters.badge !== 'all') {
      result = result.filter(p => p.badge && p.badge.toLowerCase() === filters.badge!.toLowerCase());
    }

    if (filters.sortBy) {
      switch (filters.sortBy) {
        case 'price-asc':
          result.sort((a, b) => a.price - b.price);
          break;
        case 'price-desc':
          result.sort((a, b) => b.price - a.price);
          break;
        case 'rating':
          result.sort((a, b) => b.rating - a.rating);
          break;
        case 'newest':
          result.sort((a, b) => (b.badge === 'New Arrival' ? 1 : 0) - (a.badge === 'New Arrival' ? 1 : 0));
          break;
        default:
          break;
      }
    }

    return result;
  },
};
