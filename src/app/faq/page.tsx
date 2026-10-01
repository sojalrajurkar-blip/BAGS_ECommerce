import { Metadata } from 'next';
import { FAQPage } from '@/views/FAQPage';

export const metadata: Metadata = {
  title: 'Frequently Asked Questions | RÓRA Client Care',
  description: 'Answers to inquiries regarding leather care, orders, insured worldwide shipping, guarantees, and returns.',
};

export default function FAQRoute() {
  return <FAQPage />;
}
