/**
 * ============================================================================
 * RÓRA Luxury Atelier — Payment & Razorpay Gateway Repository
 * ============================================================================
 */

import { apiClient } from '../apiClient';

export interface RazorpayOrderDto {
  razorpayOrderId: string;
  orderId: string;
  orderNumber: string;
  amount: number;
  amountInPaise: number;
  currency: string;
  keyId: string;
  customerName?: string;
  customerEmail?: string;
  customerPhone?: string;
  description?: string;
}

export interface RazorpayVerifyDto {
  orderIdOrNumber: string;
  razorpayOrderId: string;
  razorpayPaymentId: string;
  razorpaySignature: string;
}

export interface PaymentDto {
  id: string;
  orderId?: string;
  orderNumber: string;
  amount: number;
  currency: string;
  paymentMethod: string;
  paymentProvider: string;
  status: string;
  transactionReference?: string;
  failureReason?: string;
}

export const paymentRepository = {
  /**
   * Create Razorpay order on the Spring Boot backend
   */
  async createRazorpayOrder(orderIdOrNumber: string): Promise<RazorpayOrderDto> {
    return await apiClient.post<RazorpayOrderDto>(
      `/payments/razorpay/create-order/${encodeURIComponent(orderIdOrNumber)}`,
      {}
    );
  },

  /**
   * Cryptographically verify Razorpay signature and capture payment
   */
  async verifyRazorpayPayment(payload: RazorpayVerifyDto): Promise<PaymentDto> {
    return await apiClient.post<PaymentDto>('/payments/razorpay/verify', payload);
  },

  /**
   * Get payment details by order ID or number
   */
  async getPaymentByOrderId(orderIdOrNumber: string): Promise<PaymentDto | null> {
    try {
      return await apiClient.get<PaymentDto>(`/payments/order/${encodeURIComponent(orderIdOrNumber)}`);
    } catch {
      return null;
    }
  }
};
