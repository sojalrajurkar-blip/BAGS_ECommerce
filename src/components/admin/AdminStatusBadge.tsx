import React from 'react';

interface AdminStatusBadgeProps {
  status: string;
  type?: string;
}

export const AdminStatusBadge: React.FC<AdminStatusBadgeProps> = ({ status }) => {
  const getBadgeStyle = () => {
    switch (status?.toLowerCase()) {
      // Success / Delivered / Completed / Active
      case 'delivered':
      case 'captured':
      case 'completed':
      case 'active':
      case 'passed (pristine condition)':
      case 'approved & refunded':
      case 'published':
      case 'in stock':
        return {
          bg: 'rgba(104, 112, 90, 0.15)',
          color: 'var(--color-olive-700)',
          border: 'rgba(104, 112, 90, 0.3)',
        };

      // Info / In Transit / Shipped / Super Admin
      case 'shipped':
      case 'in transit':
      case 'super admin':
      case 'vip':
      case 'dispatched':
        return {
          bg: 'rgba(95, 112, 112, 0.15)',
          color: 'var(--info)',
          border: 'rgba(95, 112, 112, 0.3)',
        };

      // Warning / Processing / Pending / Under Review / Low Stock
      case 'processing':
      case 'pending':
      case 'under review':
      case 'awaiting hub delivery':
      case 'low stock':
      case 'regular':
      case 'store manager':
        return {
          bg: 'rgba(161, 124, 69, 0.15)',
          color: 'var(--warning)',
          border: 'rgba(161, 124, 69, 0.3)',
        };

      // Error / Cancelled / Refunded / Out of Stock / Flagged
      case 'cancelled':
      case 'refunded':
      case 'out of stock':
      case 'flagged':
      case 'inactive':
        return {
          bg: 'rgba(138, 77, 67, 0.15)',
          color: 'var(--error)',
          border: 'rgba(138, 77, 67, 0.3)',
        };

      default:
        return {
          bg: 'var(--surface-secondary)',
          color: 'var(--text-secondary)',
          border: 'var(--border-subtle)',
        };
    }
  };

  const style = getBadgeStyle();

  return (
    <span
      className="admin-status-badge"
      style={{
        backgroundColor: style.bg,
        color: style.color,
        borderColor: style.border,
      }}
    >
      {status}
    </span>
  );
};
