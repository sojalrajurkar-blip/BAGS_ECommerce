import { REVIEWS } from '../mockData';
import { Review } from '../../types/domain';

export const reviewRepository = {
  async getAllReviews(): Promise<Review[]> {
    return Promise.resolve([...(REVIEWS as unknown as Review[])]);
  },

  async getReviewsForProduct(productName: string): Promise<Review[]> {
    if (!productName) return Promise.resolve([...(REVIEWS as unknown as Review[])]);
    const reviewsList = REVIEWS as unknown as Review[];
    const filtered = reviewsList.filter(r => 
      r.productName && r.productName.toLowerCase() === productName.toLowerCase()
    );
    return Promise.resolve(filtered.length > 0 ? filtered : [...reviewsList]);
  }
};
