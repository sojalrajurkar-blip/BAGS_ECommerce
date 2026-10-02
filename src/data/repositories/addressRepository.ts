/**
 * ============================================================================
 * RÓRA Luxury Atelier — Customer Address Repository
 * ============================================================================
 * Interfaces with Spring Boot `/api/v1/account/addresses` endpoints.
 */

import { apiClient } from '../apiClient';
import { Address } from '../../types/domain';

export interface CustomerAddressDto {
  id: string;
  fullName: string;
  street: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  phone?: string;
  isDefault: boolean;
}

export interface AddressPayload {
  fullName: string;
  street: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  phone?: string;
  isDefault?: boolean;
}

export const addressRepository = {
  /**
   * Retrieves all saved addresses for the authenticated customer.
   */
  async getAddresses(): Promise<CustomerAddressDto[]> {
    return apiClient.get<CustomerAddressDto[]>('/account/addresses');
  },

  /**
   * Adds a new saved address.
   */
  async addAddress(payload: AddressPayload): Promise<CustomerAddressDto> {
    return apiClient.post<CustomerAddressDto>('/account/addresses', payload);
  },

  /**
   * Updates an existing address.
   */
  async updateAddress(id: string, payload: AddressPayload): Promise<CustomerAddressDto> {
    return apiClient.put<CustomerAddressDto>(`/account/addresses/${id}`, payload);
  },

  /**
   * Deletes a saved address.
   */
  async deleteAddress(id: string): Promise<void> {
    return apiClient.delete<void>(`/account/addresses/${id}`);
  },

  /**
   * Sets an address as the default shipping address.
   */
  async setDefaultAddress(id: string): Promise<CustomerAddressDto> {
    return apiClient.put<CustomerAddressDto>(`/account/addresses/${id}/default`, {});
  },
};
