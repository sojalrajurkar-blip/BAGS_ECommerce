import { Metadata } from 'next';
import { AccountPage } from '@/views/AccountPage';

export const metadata: Metadata = {
  title: 'Client Profile & VIP Preferences | RÓRA',
  description: 'Manage your personal details, shipping addresses, payment methods, and bespoke concierge preferences.',
};

export default function AccountRoute() {
  return <AccountPage />;
}
