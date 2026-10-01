import { Metadata } from 'next';
import { OrderTrackingPage } from '@/views/OrderTrackingPage';

interface PageProps {
  params: Promise<{ orderId: string }> | { orderId: string };
}

export async function generateMetadata({ params }: PageProps): Promise<Metadata> {
  const resolvedParams = await params;
  const id = resolvedParams?.orderId || 'Order';
  return {
    title: `Track Order ${id} | RÓRA`,
    description: `Real-time white-glove shipment tracking and milestone progress for order ${id}.`,
  };
}

export default async function OrderDetailRoute({ params }: PageProps) {
  const resolvedParams = await params;
  return <OrderTrackingPage orderId={resolvedParams.orderId} />;
}
