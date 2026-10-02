import { FAQS, JOURNAL_ARTICLES, MOCK_CMS, MOCK_SETTINGS } from '../mockData';
import { ContentEntry, FaqCategory, CMSContent, StoreSettings } from '../../types/domain';
import { apiClient } from '../apiClient';

export const contentRepository = {
  async getFaqs(): Promise<FaqCategory[]> {
    try {
      const data = await apiClient.get<FaqCategory[]>('/cms/faqs');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend FAQs API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(FAQS as unknown as FaqCategory[])]);
  },

  async getJournalArticles(): Promise<ContentEntry[]> {
    try {
      const data = await apiClient.get<ContentEntry[]>('/cms/journal');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend Journal API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(JOURNAL_ARTICLES as unknown as ContentEntry[])]);
  },

  async getJournalArticleBySlug(slug: string): Promise<ContentEntry | null> {
    try {
      const data = await apiClient.get<ContentEntry>(`/cms/journal/${slug}`);
      if (data && (data.id || data.slug)) {
        return data;
      }
    } catch {
      // Fallback
    }
    const article = (JOURNAL_ARTICLES as unknown as ContentEntry[]).find(a => a.slug === slug || a.id === slug);
    return Promise.resolve(article || null);
  },

  async getCmsContent(): Promise<CMSContent> {
    try {
      const data = await apiClient.get<CMSContent>('/cms/content');
      if (data && data.announcementBar) {
        return data;
      }
    } catch (err) {
      console.warn('Backend CMS Content API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve({ ...MOCK_CMS });
  },

  async getSettings(): Promise<StoreSettings> {
    try {
      const data = await apiClient.get<StoreSettings>('/settings');
      if (data && data.storeName) {
        return data;
      }
    } catch (err) {
      console.warn('Backend Settings API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve({ ...MOCK_SETTINGS } as unknown as StoreSettings);
  }
};
