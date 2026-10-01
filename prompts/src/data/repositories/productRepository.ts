import { PRODUCTS } from '../mockData';
import { Product } from '../../types/domain';

export interface ProductFilters {
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  material?: string;
  color?: string;
  badge?: string;
  sortBy?: string;
}

export const productRepository = {
  async getProducts(filters: ProductFilters = {}): Promise<Product[]> {
    let result = [...(PRODUCTS as unknown as Product[])];

    if (filters.category && filters.category !== 'all') {
      result = result.filter(p => p.category === filters.category);
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

    return Promise.resolve(result);
  },

  async getProductById(id: string): Promise<Product | null> {
    const product = (PRODUCTS as unknown as Product[]).find(p => p.id === id);
    return Promise.resolve(product || null);
  },

  async getProductBySlug(slug: string): Promise<Product | null> {
    const product = (PRODUCTS as unknown as Product[]).find(p => p.slug === slug);
    return Promise.resolve(product || null);
  },

  async getFeaturedProducts(limit = 4): Promise<Product[]> {
    const featured = (PRODUCTS as unknown as Product[]).slice(0, limit);
    return Promise.resolve(featured);
  },

  async getBestSellers(limit = 4): Promise<Product[]> {
    const bestSellers = (PRODUCTS as unknown as Product[]).filter(
      p => p.badge === 'Best Seller' || p.badge === 'Popular' || p.badge === 'Staff Pick'
    ).slice(0, limit);
    return Promise.resolve(bestSellers);
  },

  async getRelatedProducts(productId: string, limit = 4): Promise<Product[]> {
    const all = PRODUCTS as unknown as Product[];
    const current = all.find(p => p.id === productId);
    let related = all.filter(p => p.id !== productId);

    if (current) {
      const sameCategory = related.filter(p => p.category === current.category);
      const otherCategory = related.filter(p => p.category !== current.category);
      related = [...sameCategory, ...otherCategory];
    }

    return Promise.resolve(related.slice(0, limit));
  },

  async searchProducts(query = ''): Promise<Product[]> {
    if (!query || !query.trim()) {
      return Promise.resolve([]);
    }

    const q = query.toLowerCase().trim();
    const matches = (PRODUCTS as unknown as Product[]).filter(p =>
      p.name.toLowerCase().includes(q) ||
      p.description.toLowerCase().includes(q) ||
      p.category.toLowerCase().includes(q) ||
      (p.material && p.material.toLowerCase().includes(q)) ||
      (p.tagline && p.tagline.toLowerCase().includes(q))
    );

    return Promise.resolve(matches);
  }
};
