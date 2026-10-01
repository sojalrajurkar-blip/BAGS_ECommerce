import { Metadata } from 'next';
import { SearchResultsPage } from '@/views/SearchResultsPage';

export const metadata: Metadata = {
  title: 'Search Collection | RÓRA',
  description: 'Search our luxury handbag archive, materials, colors, and editorial journal stories.',
};

export default function SearchRoute() {
  return <SearchResultsPage />;
}
