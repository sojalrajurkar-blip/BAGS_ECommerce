import { Metadata } from 'next';
import { CheckoutPage } from '@/views/CheckoutPage';

export const metadata: Metadata = {
  title: 'Secure Checkout | RÓRA Atelier',
  description: 'Complete your luxury acquisition with our encrypted, white-glove checkout process.',
};

export default function CheckoutRoute() {
  return <CheckoutPage />;
}
