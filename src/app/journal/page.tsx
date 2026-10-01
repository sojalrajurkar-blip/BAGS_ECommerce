import { Metadata } from 'next';
import { JournalPage } from '@/views/JournalPage';

export const metadata: Metadata = {
  title: 'The Journal | RÓRA Editorial & Stories',
  description: 'Essays on design philosophy, artisan interviews, material provenance, and architectural inspiration.',
};

export default function JournalRoute() {
  return <JournalPage />;
}
