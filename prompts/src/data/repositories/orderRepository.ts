import { MOCK_ORDERS } from '../mockData';
import { Order } from '../../types/domain';

export const orderRepository = {
  async getOrders(): Promise<Order[]> {
    return Promise.resolve([...(MOCK_ORDERS as unknown as Order[])]);
  },

  async getOrderById(id: string): Promise<Order | null> {
    const cleanId = id.replace('#', '');
    const order = (MOCK_ORDERS as unknown as Order[]).find(
      o => o.id === cleanId || o.orderNumber.replace('#', '') === cleanId
    );
    return Promise.resolve(order || null);
  },

  async createOrder(orderPayload: Partial<Order>): Promise<Order> {
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
  }
};
