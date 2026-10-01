import { Metadata } from 'next';
import { AdminPage } from '@/views/admin/AdminPage';

export const metadata: Metadata = {
  title: 'Executive Admin Portal | RÓRA Atelier Management',
  description: 'Enterprise luxury atelier operations: Products, Inventory, Orders, Shipments, Returns, CMS, Financial Audits, and Access Control.',
  robots: {
    index: false,
    follow: false,
  },
};

export default function AdminRoute() {
  return <AdminPage />;
}
