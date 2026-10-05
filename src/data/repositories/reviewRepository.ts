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

const USE_MOCK = process.env.NEXT_PUBLIC_USE_MOCK_DATA === 'true';

export const reviewRepository = {
  async getAllReviews(): Promise<Review[]> {
    if (USE_MOCK) {
      return Promise.resolve([...(REVIEWS as unknown as Review[])]);
    }

    const data = await apiClient.get<BackendReviewDto[] | { content: BackendReviewDto[] }>('/reviews/featured');
    const list = Array.isArray(data) ? data : (data?.content || []);
    return list.map(mapBackendReview);
  },

  async getReviewsForProduct(productNameOrId: string): Promise<Review[]> {
    if (!productNameOrId) return this.getAllReviews();

    if (USE_MOCK) {
      const reviewsList = REVIEWS as unknown as Review[];
      const filtered = reviewsList.filter(r =>
        (r.productName && r.productName.toLowerCase() === productNameOrId.toLowerCase()) ||
        (r.productId && r.productId === productNameOrId)
      );
      return Promise.resolve(filtered.length > 0 ? filtered : [...reviewsList]);
    }

    const data = await apiClient.get<BackendReviewDto[] | { content: BackendReviewDto[] }>(`/reviews/product/${productNameOrId}`);
    const list = Array.isArray(data) ? data : (data?.content || []);
    return list.map(mapBackendReview);
  },

  async submitReview(reviewPayload: Partial<Review>): Promise<Review> {
    if (USE_MOCK) {
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

    const payload = {
      productId: reviewPayload.productId,
      rating: reviewPayload.rating || 5,
      title: reviewPayload.title || 'Exceptional Craftsmanship',
      content: reviewPayload.comment || reviewPayload.content || '',
      reviewerName: reviewPayload.author || 'Anonymous Patron',
      reviewerEmail: 'patron@rora-luxury.com'
    };

    const res = await apiClient.post<BackendReviewDto>('/reviews', payload);
    if (!res || !res.id) {
      throw new Error('Failed to submit review: Invalid backend response');
    }
    return mapBackendReview(res);
  }
};
