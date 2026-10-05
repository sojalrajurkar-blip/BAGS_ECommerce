import { CATEGORIES } from '../mockData';
import { Category } from '../../types/domain';
import { apiClient } from '../apiClient';

const USE_MOCK = process.env.NEXT_PUBLIC_USE_MOCK_DATA === 'true';

export const categoryRepository = {
  async getCategories(): Promise<Category[]> {
    if (USE_MOCK) {
      return Promise.resolve([...(CATEGORIES as unknown as Category[])]);
    }

    const data = await apiClient.get<Category[]>('/categories');
    if (Array.isArray(data)) {
      return data;
    }
    return [];
  },

  async getCategoryById(id: string): Promise<Category | null> {
    if (USE_MOCK) {
      const category = (CATEGORIES as unknown as Category[]).find(c => c.id === id || c.slug === id);
      return Promise.resolve(category || null);
    }

    try {
      const data = await apiClient.get<Category>(`/categories/${id}`);
      if (data && data.id) {
        return data;
      }
      return null;
    } catch {
      return null;
    }
  },

  async getCategoryBySlug(slug: string): Promise<Category | null> {
    if (USE_MOCK) {
      const category = (CATEGORIES as unknown as Category[]).find(c => c.slug === slug);
      return Promise.resolve(category || null);
    }

    try {
      const data = await apiClient.get<Category>(`/categories/${slug}`);
      if (data && data.slug) {
        return data;
      }
      return null;
    } catch {
      return null;
    }
  }
};
