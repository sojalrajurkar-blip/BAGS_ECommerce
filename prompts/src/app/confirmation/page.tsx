import { Metadata } from 'next';
import { OrderConfirmationPage } from '@/views/OrderConfirmationPage';

export const metadata: Metadata = {
  title: 'Order Confirmed | RÓRA Atelier',
  description: 'Thank you for your acquisition. Your bespoke order is now being prepared for expedited white-glove dispatch.',
};

export default function ConfirmationRoute() {
  return <OrderConfirmationPage />;
}
