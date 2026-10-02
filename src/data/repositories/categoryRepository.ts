import { CATEGORIES } from '../mockData';
import { Category } from '../../types/domain';
import { apiClient } from '../apiClient';

export const categoryRepository = {
  async getCategories(): Promise<Category[]> {
    try {
      const data = await apiClient.get<Category[]>('/categories');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (error) {
      console.warn('Backend categories API unavailable, falling back to local dataset:', error);
    }
    return Promise.resolve([...(CATEGORIES as unknown as Category[])]);
  },

  async getCategoryById(id: string): Promise<Category | null> {
    try {
      const data = await apiClient.get<Category>(`/categories/${id}`);
      if (data && data.id) {
        return data;
      }
    } catch {
      // Fallback
    }
    const category = (CATEGORIES as unknown as Category[]).find(c => c.id === id || c.slug === id);
    return Promise.resolve(category || null);
  },

  async getCategoryBySlug(slug: string): Promise<Category | null> {
    try {
      const data = await apiClient.get<Category>(`/categories/${slug}`);
      if (data && data.slug) {
        return data;
      }
    } catch {
      // Fallback
    }
    const category = (CATEGORIES as unknown as Category[]).find(c => c.slug === slug);
    return Promise.resolve(category || null);
  }
};
