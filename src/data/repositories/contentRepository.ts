import { FAQS, JOURNAL_ARTICLES, MOCK_CMS, MOCK_SETTINGS } from '../mockData';
import { ContentEntry, FaqCategory, CMSContent, StoreSettings } from '../../types/domain';
import { apiClient } from '../apiClient';

const USE_MOCK = process.env.NEXT_PUBLIC_USE_MOCK_DATA === 'true';

export const contentRepository = {
  async getFaqs(): Promise<FaqCategory[]> {
    if (USE_MOCK) {
      return Promise.resolve([...(FAQS as unknown as FaqCategory[])]);
    }

    const data = await apiClient.get<FaqCategory[]>('/cms/faqs');
    if (Array.isArray(data)) {
      return data;
    }
    return [];
  },

  async getJournalArticles(): Promise<ContentEntry[]> {
    if (USE_MOCK) {
      return Promise.resolve([...(JOURNAL_ARTICLES as unknown as ContentEntry[])]);
    }

    const data = await apiClient.get<ContentEntry[]>('/cms/journal');
    if (Array.isArray(data)) {
      return data;
    }
    return [];
  },

  async getJournalArticleBySlug(slug: string): Promise<ContentEntry | null> {
    if (USE_MOCK) {
      const article = (JOURNAL_ARTICLES as unknown as ContentEntry[]).find(a => a.slug === slug || a.id === slug);
      return Promise.resolve(article || null);
    }

    try {
      const data = await apiClient.get<ContentEntry>(`/cms/journal/${slug}`);
      if (data && (data.id || data.slug)) {
        return data;
      }
      return null;
    } catch {
      return null;
    }
  },

  async getCmsContent(): Promise<CMSContent> {
    if (USE_MOCK) {
      return Promise.resolve({ ...MOCK_CMS });
    }

    const data = await apiClient.get<CMSContent>('/cms/content');
    return data || ({} as CMSContent);
  },

  async getSettings(): Promise<StoreSettings> {
    if (USE_MOCK) {
      return Promise.resolve({ ...MOCK_SETTINGS } as unknown as StoreSettings);
    }

    const data = await apiClient.get<StoreSettings>('/settings');
    return data || ({} as StoreSettings);
  }
};
