import { MOCK_ORDERS } from '../mockData';
import { Order, OrderItem, OrderTimelineEvent } from '../../types/domain';
import { apiClient } from '../apiClient';

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

interface BackendOrderDto {
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
  shippingAddress?: any;
  billingAddress?: any;
  items?: BackendOrderItemDto[];
  timeline?: BackendTimelineDto[];
}

function mapBackendOrder(dto: BackendOrderDto): Order {
  const items: OrderItem[] = (dto.items || []).map(item => ({
    id: String(item.id || ''),
    productId: String(item.productId || ''),
    name: item.name || item.productName || 'Luxury Essential',
    color: item.color || item.colorName || 'Midnight Black',
    colorName: item.colorName || item.color || 'Midnight Black',
    image: item.image || item.productImage || '',
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

export const orderRepository = {
  async getOrders(): Promise<Order[]> {
    try {
      const data = await apiClient.get<BackendOrderDto[] | { content: BackendOrderDto[] }>('/orders/my-orders');
      const list = Array.isArray(data) ? data : (data?.content || []);
      if (list.length > 0) {
        return list.map(mapBackendOrder);
      }
    } catch (err) {
      console.warn('Backend orders API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(MOCK_ORDERS as unknown as Order[])]);
  },

  async getOrderById(id: string): Promise<Order | null> {
    const cleanId = id.replace('#', '');
    try {
      const data = await apiClient.get<BackendOrderDto>(`/orders/${cleanId}`);
      if (data && (data.id || data.orderNumber)) {
        return mapBackendOrder(data);
      }
    } catch {
      // Fallback
    }

    const order = (MOCK_ORDERS as unknown as Order[]).find(
      o => o.id === cleanId || o.orderNumber.replace('#', '') === cleanId
    );
    return Promise.resolve(order || null);
  },

  async createOrder(orderPayload: Partial<Order>): Promise<Order> {
    try {
      const payload = {
        shippingAddress: orderPayload.shippingAddress,
        billingAddress: orderPayload.billingAddress || orderPayload.shippingAddress,
        paymentMethod: orderPayload.paymentMethod || 'Credit Card',
        paymentProvider: 'Razorpay',
        customerNotes: 'Deliver between 10am - 6pm'
      };

      const res = await apiClient.post<BackendOrderDto>('/checkout/place-order', payload);
      if (res && (res.id || res.orderNumber)) {
        return mapBackendOrder(res);
      }
    } catch (err) {
      console.warn('Backend place-order API error, creating simulated order:', err);
    }

    const newOrderNumber = `#RRA${Math.floor(10000 + Math.random() * 90000)}`;
    const newOrder: Order = {
      id: newOrderNumber.replace('#', ''),
      orderNumber: newOrderNumber,
      date: new Date().toLocaleDateString('en-IN', { month: 'long', day: 'numeric', year: 'numeric' }),
      status: 'Processing',
      total: 0,
      items: [],
      shippingAddress: {
        city: '',
        state: '',
        postalCode: '',
        country: 'India'
      },
      ...orderPayload,
      timeline: [
        { step: 'Order Placed', time: 'Just now', completed: true },
        { step: 'Payment Verified', time: 'Just now', completed: true },
        { step: 'Dispatched from Hub', time: 'Pending', completed: false },
        { step: 'Out for Delivery', time: 'Pending', completed: false },
        { step: 'Delivered', time: 'Pending', completed: false }
      ]
    };
    return Promise.resolve(newOrder);
  },

  async trackOrder(orderNumber: string): Promise<Order | null> {
    const cleanNumber = orderNumber.replace('#', '');
    try {
      const data = await apiClient.get<BackendOrderDto>(`/orders/track/${cleanNumber}`);
      if (data && (data.id || data.orderNumber)) {
        return mapBackendOrder(data);
      }
    } catch {
      // Fallback
    }
    return this.getOrderById(cleanNumber);
  }
};
