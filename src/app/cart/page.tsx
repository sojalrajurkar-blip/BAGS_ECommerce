import { Metadata } from 'next';
import { CartPage } from '@/views/CartPage';

export const metadata: Metadata = {
  title: 'Shopping Bag | RÓRA',
  description: 'Review the exquisite items selected for your personal collection with complimentary insured global delivery.',
};

export default function CartRoute() {
  return <CartPage />;
}
