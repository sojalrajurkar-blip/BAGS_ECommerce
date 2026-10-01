import { Metadata } from 'next';
import { ReturnsPage } from '@/views/ReturnsPage';

export const metadata: Metadata = {
  title: 'Complimentary Returns & Exchanges | RÓRA',
  description: 'Our policy on complimentary 30-day worldwide returns, exchanges, and warranty services.',
};

export default function ReturnsRoute() {
  return <ReturnsPage />;
}
