import { FAQS, JOURNAL_ARTICLES, MOCK_CMS, MOCK_SETTINGS } from '../mockData';
import { ContentEntry, FaqCategory, CMSContent, StoreSettings } from '../../types/domain';

export const contentRepository = {
  async getFaqs(): Promise<FaqCategory[]> {
    return Promise.resolve([...(FAQS as unknown as FaqCategory[])]);
  },

  async getJournalArticles(): Promise<ContentEntry[]> {
    return Promise.resolve([...(JOURNAL_ARTICLES as unknown as ContentEntry[])]);
  },

  async getJournalArticleBySlug(slug: string): Promise<ContentEntry | null> {
    const article = (JOURNAL_ARTICLES as unknown as ContentEntry[]).find(a => a.slug === slug || a.id === slug);
    return Promise.resolve(article || null);
  },

  async getCmsContent(): Promise<CMSContent> {
    return Promise.resolve({ ...MOCK_CMS });
  },

  async getSettings(): Promise<StoreSettings> {
    return Promise.resolve({ ...MOCK_SETTINGS } as unknown as StoreSettings);
  }
};
