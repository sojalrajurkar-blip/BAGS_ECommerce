import { MOCK_COUPONS } from '../mockData';
import { Coupon } from '../../types/domain';
import { apiClient } from '../apiClient';

interface BackendCouponValidationResponse {
  valid: boolean;
  message?: string;
  code: string;
  discountType: string;
  discountValue: number;
  discountAmount?: number;
  minOrderAmount?: number;
  maxDiscountAmount?: number;
}

export const couponRepository = {
  async getCoupons(): Promise<Coupon[]> {
    try {
      const data = await apiClient.get<Coupon[]>('/coupons');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend coupons API unavailable, fallback to local coupons:', err);
    }
    return Promise.resolve([...(MOCK_COUPONS as unknown as Coupon[])]);
  },

  async validateCoupon(code: string, orderAmount = 0): Promise<Coupon | null> {
    if (!code) return Promise.resolve(null);
    const clean = code.trim().toUpperCase();

    try {
      const res = await apiClient.get<BackendCouponValidationResponse>('/coupons/validate', {
        params: {
          code: clean,
          subtotal: orderAmount || 0,
        },
      });

      if (res && res.valid) {
        return {
          code: res.code || clean,
          discountType: res.discountType,
          discountValue: res.discountValue,
          discountPercent: res.discountType === 'PERCENTAGE' ? res.discountValue : undefined,
          minimumSpend: res.minOrderAmount || 0,
          minCart: res.minOrderAmount || 0,
          minOrder: res.minOrderAmount || 0,
          description: res.message || `${res.discountValue}% off order`,
          isActive: true,
          active: true,
          status: 'Active'
        };
      }
    } catch {
      // Fallback
    }

    const coupon = (MOCK_COUPONS as unknown as Coupon[]).find(c => c.code === clean);
    return Promise.resolve(coupon || null);
  }
};
