import { Metadata } from 'next';
import { ShopPage } from '@/views/ShopPage';

export const metadata: Metadata = {
  title: 'Shop All Handbags & Leather Goods | RÓRA',
  description: 'Explore our complete collection of handcrafted luxury tote bags, shoulder bags, crossbodies, and artisanal leather accessories.',
};

export default function Shop() {
  return <ShopPage />;
}
