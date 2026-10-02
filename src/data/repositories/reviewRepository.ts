import { REVIEWS } from '../mockData';
import { Review } from '../../types/domain';
import { apiClient } from '../apiClient';

interface BackendReviewDto {
  id: number | string;
  productId?: number | string;
  productName?: string;
  author?: string;
  reviewerName?: string;
  role?: string;
  rating: number;
  title?: string;
  comment?: string;
  content?: string;
  reviewText?: string;
  date?: string;
  createdAt?: string;
  verified?: boolean;
  verifiedPurchase?: boolean;
  helpfulCount?: number;
  status?: string;
}

function mapBackendReview(r: BackendReviewDto): Review {
  return {
    id: String(r.id),
    productId: r.productId ? String(r.productId) : undefined,
    productName: r.productName,
    author: r.author || r.reviewerName || 'Verified Collector',
    role: r.role || 'Verified Collector',
    rating: r.rating || 5,
    title: r.title,
    comment: r.comment || r.content || r.reviewText || '',
    content: r.content || r.comment || r.reviewText || '',
    date: r.date || (r.createdAt ? new Date(r.createdAt).toLocaleDateString('en-IN', { month: 'short', year: 'numeric' }) : 'Recent'),
    verified: r.verified !== undefined ? r.verified : true,
    verifiedPurchase: r.verifiedPurchase !== undefined ? r.verifiedPurchase : true,
    helpfulCount: r.helpfulCount || 0,
    status: r.status || 'Approved'
  };
}

export const reviewRepository = {
  async getAllReviews(): Promise<Review[]> {
    try {
      const data = await apiClient.get<BackendReviewDto[]>('/reviews/featured');
      if (Array.isArray(data) && data.length > 0) {
        return data.map(mapBackendReview);
      }
    } catch (err) {
      console.warn('Backend reviews API unavailable, fallback to local reviews:', err);
    }
    return Promise.resolve([...(REVIEWS as unknown as Review[])]);
  },

  async getReviewsForProduct(productNameOrId: string): Promise<Review[]> {
    if (!productNameOrId) return this.getAllReviews();

    try {
      const data = await apiClient.get<BackendReviewDto[]>(`/reviews/product/${productNameOrId}`);
      if (Array.isArray(data) && data.length > 0) {
        return data.map(mapBackendReview);
      }
    } catch {
      // Fallback to searching local list
    }

    const reviewsList = REVIEWS as unknown as Review[];
    const filtered = reviewsList.filter(r =>
      (r.productName && r.productName.toLowerCase() === productNameOrId.toLowerCase()) ||
      (r.productId && r.productId === productNameOrId)
    );
    return Promise.resolve(filtered.length > 0 ? filtered : [...reviewsList]);
  },

  async submitReview(reviewPayload: Partial<Review>): Promise<Review> {
    try {
      const payload = {
        productId: reviewPayload.productId,
        rating: reviewPayload.rating || 5,
        title: reviewPayload.title || 'Exceptional Craftsmanship',
        content: reviewPayload.comment || reviewPayload.content || '',
        reviewerName: reviewPayload.author || 'Anonymous Patron',
        reviewerEmail: 'patron@rora-luxury.com'
      };

      const res = await apiClient.post<BackendReviewDto>('/reviews', payload);
      if (res && res.id) {
        return mapBackendReview(res);
      }
    } catch (err) {
      console.warn('Backend submit review API error, creating simulated review:', err);
    }

    return Promise.resolve({
      id: `rev-${Date.now()}`,
      author: reviewPayload.author || 'Anonymous Patron',
      role: 'Verified Patron',
      rating: reviewPayload.rating || 5,
      title: reviewPayload.title,
      comment: reviewPayload.comment || reviewPayload.content,
      content: reviewPayload.content || reviewPayload.comment,
      date: 'Just now',
      verified: true,
      verifiedPurchase: true,
      helpfulCount: 0
    });
  }
};
