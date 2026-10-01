import React from 'react';
import { LucideIcon } from 'lucide-react';

interface AdminStatProps {
  icon?: LucideIcon;
  title: string;
  value: string | number;
  change?: string;
  isPositive?: boolean;
  subtitle?: string;
}

export const AdminStat: React.FC<AdminStatProps> = ({
  icon: Icon,
  title,
  value,
  change,
  isPositive = true,
  subtitle,
}) => {
  return (
    <div className="admin-stat-card">
      <div className="admin-stat-top">
        <span className="admin-stat-title">{title}</span>
        {Icon && (
          <div className="admin-stat-icon-wrap">
            <Icon size={18} />
          </div>
        )}
      </div>
      <div className="admin-stat-value font-serif">{value}</div>
      <div className="admin-stat-footer">
        {change && (
          <span className={`admin-stat-change ${isPositive ? 'stat-positive' : 'stat-negative'}`}>
            {change}
          </span>
        )}
        {subtitle && <span className="admin-stat-subtitle">{subtitle}</span>}
      </div>
    </div>
  );
};
