import { MOCK_COUPONS } from '../mockData';
import { Coupon } from '../../types/domain';

export const couponRepository = {
  async getCoupons(): Promise<Coupon[]> {
    return Promise.resolve([...(MOCK_COUPONS as unknown as Coupon[])]);
  },

  async validateCoupon(code: string): Promise<Coupon | null> {
    if (!code) return Promise.resolve(null);
    const clean = code.trim().toUpperCase();
    const coupon = (MOCK_COUPONS as unknown as Coupon[]).find(c => c.code === clean);
    return Promise.resolve(coupon || null);
  }
};
