/**
 * ============================================================================
 * RÓRA Luxury Atelier — Cart Repository
 * ============================================================================
 * Interfaces with Spring Boot `/api/v1/cart` endpoints supporting guest session
 * tracking via X-Session-ID, customer authenticated persistence, and cart merging.
 */

import { apiClient, getSessionId } from '../apiClient';
import { CartItem, Product, ColorVariant } from '../../types/domain';

export interface BackendCartItemDto {
  id: string;
  productId: string;
  productName: string;
  productSlug?: string;
  variantId?: string;
  colorName?: string;
  colorHex?: string;
  imageUrl?: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  inStock?: boolean;
}

export interface BackendCartDto {
  id: string;
  userId?: string;
  sessionId?: string;
  items: BackendCartItemDto[];
  itemCount: number;
  subtotal: number;
  discountAmount: number;
  shippingFee: number;
  taxAmount: number;
  totalAmount: number;
  appliedCouponCode?: string;
}

export interface AddItemPayload {
  productId: string;
  variantId?: string;
  quantity: number;
  colorName?: string;
}

function mapBackendCartItem(dto: BackendCartItemDto): CartItem {
  return {
    id: dto.id || `${dto.productId}-${dto.colorName || 'Standard'}`,
    productId: dto.productId,
    variantId: dto.variantId,
    quantity: dto.quantity,
    price: dto.unitPrice,
    unitPrice: dto.unitPrice,
    totalPrice: dto.totalPrice,
    color: {
      name: dto.colorName || 'Standard',
      hex: dto.colorHex || '#1A1A1A',
    },
    product: {
      id: dto.productId,
      name: dto.productName,
      slug: dto.productSlug || dto.productId,
      price: dto.unitPrice,
      originalPrice: dto.unitPrice,
      rating: 5,
      reviewCount: 1,
      image: dto.imageUrl || '',
      images: dto.imageUrl ? [dto.imageUrl] : [],
      category: 'Backpacks',
      badge: '',
      stock: 15,
      inStock: dto.inStock !== false,
      description: 'Handcrafted luxury carry essential.',
      story: '',
      material: 'Full-Grain Tuscan Leather',
      materials: ['Full-Grain Tuscan Leather'],
      colors: [{ name: dto.colorName || 'Standard', hex: dto.colorHex || '#1A1A1A' }],
      specifications: {},
      features: [],
      dimensions: '42cm x 30cm x 15cm',
      weight: '1.2 kg',
      capacity: '20L',
      sku: dto.variantId || dto.productId,
    } as Product,
  };
}

export const cartRepository = {
  /**
   * Retrieves the active cart from the backend.
   */
  async getCart(): Promise<BackendCartDto> {
    const sessionId = getSessionId();
    return apiClient.get<BackendCartDto>('/cart', {
      params: { sessionId },
    });
  },

  /**
   * Adds an item to the backend cart.
   */
  async addItem(payload: AddItemPayload): Promise<BackendCartDto> {
    const sessionId = getSessionId();
    return apiClient.post<BackendCartDto>('/cart/items', payload, {
      params: { sessionId },
    });
  },

  /**
   * Updates quantity of a line item.
   */
  async updateQuantity(itemId: string, quantity: number): Promise<BackendCartDto> {
    const sessionId = getSessionId();
    return apiClient.put<BackendCartDto>(`/cart/items/${itemId}`, { quantity }, {
      params: { sessionId },
    });
  },

  /**
   * Removes a line item from the cart.
   */
  async removeItem(itemId: string): Promise<BackendCartDto> {
    const sessionId = getSessionId();
    return apiClient.delete<BackendCartDto>(`/cart/items/${itemId}`, {
      params: { sessionId },
    });
  },

  /**
   * Empties the entire cart.
   */
  async clearCart(): Promise<BackendCartDto> {
    const sessionId = getSessionId();
    return apiClient.delete<BackendCartDto>('/cart', {
      params: { sessionId },
    });
  },

  /**
   * Applies a promo coupon to the cart.
   */
  async applyCoupon(code: string): Promise<BackendCartDto> {
    const sessionId = getSessionId();
    return apiClient.post<BackendCartDto>('/cart/apply-coupon', { code }, {
      params: { sessionId },
    });
  },

  /**
   * Removes applied coupon from the cart.
   */
  async removeCoupon(): Promise<BackendCartDto> {
    const sessionId = getSessionId();
    return apiClient.delete<BackendCartDto>('/cart/remove-coupon', {
      params: { sessionId },
    });
  },

  /**
   * Merges anonymous guest cart into authenticated customer cart on login.
   */
  async mergeCart(guestSessionId: string): Promise<BackendCartDto> {
    return apiClient.post<BackendCartDto>('/cart/merge', { sessionId: guestSessionId });
  },

  mapItem: mapBackendCartItem,
};
