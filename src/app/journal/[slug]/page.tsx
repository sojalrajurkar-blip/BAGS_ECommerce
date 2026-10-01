import { Metadata } from 'next';
import { JournalPage } from '@/views/JournalPage';

interface PageProps {
  params: Promise<{ slug: string }> | { slug: string };
}

export async function generateMetadata({ params }: PageProps): Promise<Metadata> {
  const resolvedParams = await params;
  const slug = resolvedParams?.slug || 'Story';
  return {
    title: `${slug.replace(/-/g, ' ').toUpperCase()} | The RÓRA Journal`,
    description: `Read our in-depth essay on ${slug.replace(/-/g, ' ')}.`,
  };
}

export default async function JournalArticleRoute({ params }: PageProps) {
  const resolvedParams = await params;
  return <JournalPage articleSlug={resolvedParams.slug} />;
}
