import { Metadata } from 'next';
import { CategoryPage } from '@/views/CategoryPage';

interface PageProps {
  params: Promise<{ slug: string }> | { slug: string };
}

export async function generateMetadata({ params }: PageProps): Promise<Metadata> {
  const resolvedParams = await params;
  const slug = resolvedParams?.slug || 'Category';
  const formatted = slug.charAt(0).toUpperCase() + slug.slice(1);
  return {
    title: `${formatted} Collection | RÓRA`,
    description: `Discover our curated selection of luxury ${slug} designed with timeless silhouette and master craftsmanship.`,
  };
}

export default async function CategoryRoute({ params }: PageProps) {
  const resolvedParams = await params;
  return <CategoryPage categoryId={resolvedParams.slug} />;
}
