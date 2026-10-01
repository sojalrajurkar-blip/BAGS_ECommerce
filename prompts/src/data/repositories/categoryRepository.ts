import { CATEGORIES } from '../mockData';
import { Category } from '../../types/domain';

export const categoryRepository = {
  async getCategories(): Promise<Category[]> {
    return Promise.resolve([...(CATEGORIES as unknown as Category[])]);
  },

  async getCategoryById(id: string): Promise<Category | null> {
    const category = (CATEGORIES as unknown as Category[]).find(c => c.id === id || c.slug === id);
    return Promise.resolve(category || null);
  },

  async getCategoryBySlug(slug: string): Promise<Category | null> {
    const category = (CATEGORIES as unknown as Category[]).find(c => c.slug === slug);
    return Promise.resolve(category || null);
  }
};
