/**
 * ============================================================================
 * RÓRA Luxury Atelier — Customer Return & Refund Repository
 * ============================================================================
 * Interfaces with Spring Boot `/api/v1/returns` endpoints.
 */

import { apiClient } from '../apiClient';

export interface ReturnRecordDto {
  id: string;
  orderId: string;
  orderNumber: string;
  customerName: string;
  customerEmail: string;
  customerPhone?: string;
  item: string;
  reason: string;
  customerNotes?: string;
  requestDate: string;
  inspectionStatus: string;
  status: string;
  amount: number;
  adminNotes?: string;
}

export interface CreateReturnPayload {
  orderId: string;
  orderNumber: string;
  customerName: string;
  customerEmail: string;
  customerPhone?: string;
  item: string;
  reason: string;
  customerNotes?: string;
  amount: number;
}

export const returnRepository = {
  /**
   * Submits a customer return request for an order.
   */
  async createReturn(payload: CreateReturnPayload): Promise<ReturnRecordDto> {
    return apiClient.post<ReturnRecordDto>('/returns', payload);
  },

  /**
   * Gets customer's submitted returns.
   */
  async getMyReturns(): Promise<ReturnRecordDto[]> {
    return apiClient.get<ReturnRecordDto[]>('/returns/my-returns');
  },

  /**
   * Gets return details by ID.
   */
  async getReturnById(id: string): Promise<ReturnRecordDto> {
    return apiClient.get<ReturnRecordDto>(`/returns/${id}`);
  },

  /**
   * Gets return requests for a specific order.
   */
  async getReturnsByOrder(orderIdOrNumber: string): Promise<ReturnRecordDto[]> {
    return apiClient.get<ReturnRecordDto[]>(`/returns/order/${orderIdOrNumber}`);
  },
};
