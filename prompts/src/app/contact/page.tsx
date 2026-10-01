import { Metadata } from 'next';
import { ContactPage } from '@/views/ContactPage';

export const metadata: Metadata = {
  title: 'Contact Concierge & Private Appointments | RÓRA',
  description: 'Connect directly with our client services team or request a private appointment at our flagship atelier.',
};

export default function ContactRoute() {
  return <ContactPage />;
}
