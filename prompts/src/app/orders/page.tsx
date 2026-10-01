import { Metadata } from 'next';
import { OrdersPage } from '@/views/OrdersPage';

export const metadata: Metadata = {
  title: 'Order History & Invoices | RÓRA',
  description: 'View previous luxury purchases, live fulfillment progress, and download official certificates of authenticity.',
};

export default function OrdersRoute() {
  return <OrdersPage />;
}
