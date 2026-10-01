import { Metadata } from 'next';
import { AboutPage } from '@/views/AboutPage';

export const metadata: Metadata = {
  title: 'Our Heritage & Philosophy | RÓRA Atelier',
  description: 'Learn about RÓRA’s master leather artisans, sustainable material sourcing, and uncompromising standards of craftsmanship.',
};

export default function AboutRoute() {
  return <AboutPage />;
}
