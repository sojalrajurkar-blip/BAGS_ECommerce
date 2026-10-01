import { Metadata } from 'next';
import { HomePage } from '@/views/HomePage';

export const metadata: Metadata = {
  title: 'RÓRA | Luxury Leather Handbags & Accessories',
  description: 'Handcrafted luxury leather goods engineered with quiet architectural restraint and tactile elegance.',
};

export default function Home() {
  return <HomePage />;
}
