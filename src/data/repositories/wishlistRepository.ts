/**
 * ============================================================================
 * RÓRA Luxury Atelier — Wishlist Repository
 * ============================================================================
 * Interfaces with Spring Boot `/api/v1/wishlist` endpoints.
 */

import { apiClient } from '../apiClient';

export interface BackendWishlistItemDto {
  id: string;
  productId: string;
  productName: string;
  productSlug: string;
  imageUrl?: string;
  price: number;
  inStock?: boolean;
  addedAt: string;
}

export interface BackendWishlistDto {
  id: string;
  userId: string;
  items: BackendWishlistItemDto[];
  itemCount: number;
}

export const wishlistRepository = {
  /**
   * Retrieves authenticated customer wishlist.
   */
  async getWishlist(): Promise<BackendWishlistDto> {
    return apiClient.get<BackendWishlistDto>('/wishlist');
  },

  /**
   * 1-Click toggles a product into or out of customer wishlist.
   */
  async toggleItem(productIdOrSlug: string): Promise<BackendWishlistDto> {
    return apiClient.post<BackendWishlistDto>(`/wishlist/toggle/${productIdOrSlug}`, {});
  },

  /**
   * Adds an item to customer wishlist.
   */
  async addItem(productIdOrSlug: string): Promise<BackendWishlistDto> {
    return apiClient.post<BackendWishlistDto>(`/wishlist/items/${productIdOrSlug}`, {});
  },

  /**
   * Removes an item from customer wishlist.
   */
  async removeItem(productIdOrSlug: string): Promise<BackendWishlistDto> {
    return apiClient.delete<BackendWishlistDto>(`/wishlist/items/${productIdOrSlug}`);
  },

  /**
   * Clears customer wishlist completely.
   */
  async clearWishlist(): Promise<void> {
    return apiClient.delete<void>('/wishlist');
  },
};
