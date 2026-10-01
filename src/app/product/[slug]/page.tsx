import { Metadata } from 'next';
import { ProductDetailPage } from '@/views/ProductDetailPage';

interface PageProps {
  params: Promise<{ slug: string }> | { slug: string };
}

export async function generateMetadata({ params }: PageProps): Promise<Metadata> {
  const resolvedParams = await params;
  const slug = resolvedParams?.slug || 'Product';
  return {
    title: `${slug.replace(/-/g, ' ').toUpperCase()} | RÓRA Atelier`,
    description: `Full details, leather provenance, specifications, and styling for ${slug.replace(/-/g, ' ')}.`,
  };
}

export default async function ProductDetailRoute({ params }: PageProps) {
  const resolvedParams = await params;
  return <ProductDetailPage productId={resolvedParams.slug} />;
}
