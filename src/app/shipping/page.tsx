import { Metadata } from 'next';
import { ShippingPage } from '@/views/ShippingPage';

export const metadata: Metadata = {
  title: 'Insured Global Shipping & White-Glove Delivery | RÓRA',
  description: 'Learn about our discreet, fully-insured international delivery services, duties, and customs handling.',
};

export default function ShippingRoute() {
  return <ShippingPage />;
}
