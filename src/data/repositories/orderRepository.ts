/**
 * ============================================================================
 * RÓRA Luxury Atelier — Order & Checkout Repository
 * ============================================================================
 * Interfaces with Spring Boot `/api/v1/orders` and `/api/v1/checkout/place-order`
 * for authoritative server-side price calculation, inventory depletion, payment
 * ledger recording, and live tracking.
 */

import { MOCK_ORDERS } from '../mockData';
import { Order, OrderItem, OrderTimelineEvent, Address } from '../../types/domain';
import { apiClient, getSessionId } from '../apiClient';

interface BackendOrderItemDto {
  id?: number | string;
  productId?: number | string;
  productName?: string;
  name?: string;
  productSlug?: string;
  productImage?: string;
  image?: string;
  colorName?: string;
  color?: string;
  quantity: number;
  unitPrice?: number;
  price?: number;
  totalPrice?: number;
}

interface BackendTimelineDto {
  step?: string;
  status?: string;
  title?: string;
  description?: string;
  timestamp?: string;
  date?: string;
  time?: string;
  completed?: boolean;
}

export interface BackendOrderDto {
  id: number | string;
  orderNumber: string;
  trackingNumber?: string;
  orderStatus?: string;
  status?: string;
  paymentStatus?: string;
  paymentMethod?: string;
  shippingStatus?: string;
  carrier?: string;
  estimatedDelivery?: string;
  subtotal?: number;
  discountAmount?: number;
  discount?: number;
  shippingFee?: number;
  shipping?: number;
  taxAmount?: number;
  tax?: number;
  totalAmount?: number;
  total?: number;
  orderDate?: string;
  createdAt?: string;
  date?: string;
  shippingAddress?: Address;
  billingAddress?: Address;
  items?: BackendOrderItemDto[];
  timeline?: BackendTimelineDto[];
}

export interface PlaceOrderPayload {
  customerName: string;
  customerEmail: string;
  customerPhone?: string;
  shippingAddress: Address;
  billingAddress?: Address;
  paymentMethod?: string;
  couponCode?: string;
}

function mapBackendOrder(dto: BackendOrderDto): Order {
  const items: OrderItem[] = (dto.items || []).map(item => ({
    id: String(item.id || ''),
    productId: String(item.productId || ''),
    name: item.name || item.productName || 'Luxury Essential',
    color: item.color || item.colorName || 'Midnight Black',
    colorName: item.colorName || item.color || 'Midnight Black',
    image: item.image || item.productImage || 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&q=80&w=800',
    price: item.price ?? item.unitPrice ?? 0,
    quantity: item.quantity || 1
  }));

  const timeline: OrderTimelineEvent[] = (dto.timeline || []).map(t => ({
    step: t.step || t.title || t.status || 'Update',
    title: t.title || t.step || t.status || 'Update',
    description: t.description,
    time: t.time || t.timestamp || 'Recent',
    date: t.date || t.timestamp,
    completed: t.completed !== undefined ? t.completed : true
  }));

  const rawDate = dto.orderDate || dto.createdAt || dto.date;
  const formattedDate = rawDate
    ? new Date(rawDate).toLocaleDateString('en-IN', { month: 'long', day: 'numeric', year: 'numeric' })
    : new Date().toLocaleDateString('en-IN', { month: 'long', day: 'numeric', year: 'numeric' });

  return {
    id: String(dto.id).replace('#', ''),
    orderNumber: dto.orderNumber.startsWith('#') ? dto.orderNumber : `#${dto.orderNumber}`,
    date: formattedDate,
    createdAt: rawDate,
    status: dto.orderStatus || dto.status || 'Processing',
    paymentStatus: dto.paymentStatus || 'Paid',
    paymentMethod: dto.paymentMethod || 'Credit Card',
    items: items.length > 0 ? items : [],
    subtotal: dto.subtotal || dto.totalAmount || 0,
    discount: dto.discountAmount || dto.discount || 0,
    shippingFee: dto.shippingFee || dto.shipping || 0,
    shipping: dto.shippingFee || dto.shipping || 0,
    tax: dto.taxAmount || dto.tax || 0,
    total: dto.totalAmount || dto.total || 0,
    trackingNumber: dto.trackingNumber || `TRK-${dto.orderNumber.replace(/[^a-zA-Z0-9]/g, '')}`,
    carrier: dto.carrier || 'BlueDart Express Luxury Logistics',
    estimatedDelivery: dto.estimatedDelivery || '3-5 Business Days',
    shippingAddress: dto.shippingAddress || {
      city: 'Mumbai',
      state: 'Maharashtra',
      postalCode: '400001',
      country: 'India'
    },
    billingAddress: dto.billingAddress,
    timeline: timeline.length > 0 ? timeline : [
      { step: 'Order Placed', time: 'Just now', completed: true },
      { step: 'Payment Verified', time: 'Just now', completed: true },
      { step: 'Dispatched from Hub', time: 'Pending', completed: false },
      { step: 'Out for Delivery', time: 'Pending', completed: false },
      { step: 'Delivered', time: 'Pending', completed: false }
    ]
  };
}

const USE_MOCK = process.env.NEXT_PUBLIC_USE_MOCK_DATA === 'true';

export const orderRepository = {
  /**
   * Fetches customer's orders from the backend.
   */
  async getOrders(): Promise<Order[]> {
    if (USE_MOCK) {
      return (MOCK_ORDERS as unknown as Order[]);
    }

    const data = await apiClient.get<BackendOrderDto[] | { content: BackendOrderDto[] }>('/orders/my-orders');
    const list = Array.isArray(data) ? data : (data?.content || []);
    return list.map(mapBackendOrder);
  },

  /**
   * Fetches order by ID or order number.
   */
  async getOrderById(id: string): Promise<Order | null> {
    const cleanId = id.replace('#', '');
    if (USE_MOCK) {
      const order = (MOCK_ORDERS as unknown as Order[]).find(
        o => o.id === cleanId || o.orderNumber.replace('#', '') === cleanId
      );
      return order || null;
    }

    try {
      const data = await apiClient.get<BackendOrderDto>(`/orders/${cleanId}`);
      if (data && (data.id || data.orderNumber)) {
        return mapBackendOrder(data);
      }
      return null;
    } catch (err: unknown) {
      const status = (err as { status?: number })?.status;
      if (status === 404) {
        return null;
      }
      throw err;
    }
  },

  /**
   * Authoritative order placement connecting to `/api/v1/checkout/place-order`.
   */
  async placeOrder(orderPayload: PlaceOrderPayload): Promise<Order> {
    if (USE_MOCK) {
      const newOrderNumber = `#RRA${Math.floor(10000 + Math.random() * 90000)}`;
      const newOrder: Order = {
        id: newOrderNumber.replace('#', ''),
        orderNumber: newOrderNumber,
        date: new Date().toLocaleDateString('en-IN', { month: 'long', day: 'numeric', year: 'numeric' }),
        status: 'Processing',
        total: 0,
        items: [],
        shippingAddress: orderPayload.shippingAddress,
        timeline: [
          { step: 'Order Placed', time: 'Just now', completed: true },
          { step: 'Payment Verified', time: 'Just now', completed: true },
          { step: 'Dispatched from Hub', time: 'Pending', completed: false },
          { step: 'Out for Delivery', time: 'Pending', completed: false },
          { step: 'Delivered', time: 'Pending', completed: false }
        ]
      };
      return newOrder;
    }

    const sessionId = getSessionId();

    const payload = {
      customerName: orderPayload.customerName || 'Valued Client',
      customerEmail: orderPayload.customerEmail || 'client@rora-luxury.com',
      customerPhone: orderPayload.customerPhone || '+91 98200 12345',
      shippingAddress: {
        fullName: orderPayload.shippingAddress.fullName || orderPayload.customerName,
        street: orderPayload.shippingAddress.street || orderPayload.shippingAddress.addressLine1 || 'Main Street',
        addressLine2: orderPayload.shippingAddress.addressLine2,
        city: orderPayload.shippingAddress.city || 'Mumbai',
        state: orderPayload.shippingAddress.state || 'Maharashtra',
        postalCode: orderPayload.shippingAddress.postalCode || '400001',
        country: orderPayload.shippingAddress.country || 'India',
        phone: orderPayload.shippingAddress.phone || orderPayload.customerPhone,
      },
      billingAddress: orderPayload.billingAddress || orderPayload.shippingAddress,
      paymentMethod: orderPayload.paymentMethod || 'Credit Card / Net Banking',
      couponCode: orderPayload.couponCode,
      sessionId,
    };

    const res = await apiClient.post<BackendOrderDto>('/checkout/place-order', payload);
    if (!res || (!res.id && !res.orderNumber)) {
      throw new Error('Failed to create order: Invalid backend response');
    }
    return mapBackendOrder(res);
  },

  /**
   * Tracks an order with live logistics events.
   */
  async trackOrder(orderNumber: string): Promise<Order | null> {
    const cleanNumber = orderNumber.replace('#', '');
    if (USE_MOCK) {
      return this.getOrderById(cleanNumber);
    }

    try {
      const data = await apiClient.get<BackendOrderDto>(`/orders/track/${cleanNumber}`);
      if (data && (data.id || data.orderNumber)) {
        return mapBackendOrder(data);
      }
      return null;
    } catch (err: unknown) {
      const status = (err as { status?: number })?.status;
      if (status === 404) {
        return null;
      }
      throw err;
    }
  }
};
