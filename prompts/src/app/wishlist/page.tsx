import { Metadata } from 'next';
import { WishlistPage } from '@/views/WishlistPage';

export const metadata: Metadata = {
  title: 'Your Saved Pieces | RÓRA Wishlist',
  description: 'View and manage your curated wishlist of luxury handcrafted handbags and accessories.',
};

export default function WishlistRoute() {
  return <WishlistPage />;
}
