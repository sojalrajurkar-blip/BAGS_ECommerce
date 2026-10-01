import React from 'react';
import { Search, Bell, Calendar } from 'lucide-react';
import { useStore } from '../../context/StoreContext';

interface AdminHeaderProps {
  title: string;
  subtitle?: string;
  searchPlaceholder?: string;
  searchValue?: string;
  onSearchChange?: (val: string) => void;
  actionButton?: React.ReactNode;
}

export const AdminHeader: React.FC<AdminHeaderProps> = ({
  title,
  subtitle,
  searchPlaceholder,
  searchValue,
  onSearchChange,
  actionButton,
}) => {
  const { addToast } = useStore();

  const handleNotificationClick = () => {
    addToast('All store operations running normally with 0 system errors.');
  };

  return (
    <header className="admin-header">
      <div className="admin-header-title-box">
        <h1 className="admin-header-title font-serif">{title}</h1>
        {subtitle && <p className="admin-header-subtitle">{subtitle}</p>}
      </div>

      <div className="admin-header-actions">
        {onSearchChange && (
          <div className="admin-search-wrapper">
            <Search size={15} className="admin-search-icon" />
            <input
              type="text"
              className="admin-search-input"
              placeholder={searchPlaceholder || 'Filter records...'}
              value={searchValue || ''}
              onChange={(e) => onSearchChange(e.target.value)}
            />
          </div>
        )}

        <div className="admin-date-badge">
          <Calendar size={14} />
          <span>
            {new Date().toLocaleDateString('en-IN', {
              month: 'short',
              day: 'numeric',
              year: 'numeric',
            })}
          </span>
        </div>

        <button
          className="admin-icon-action-btn"
          onClick={handleNotificationClick}
          title="Notifications"
          aria-label="Notifications"
        >
          <Bell size={17} />
          <span className="notification-dot" />
        </button>

        {actionButton}
      </div>
    </header>
  );
};
