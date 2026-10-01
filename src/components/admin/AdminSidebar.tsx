import React from 'react';
import { useStore } from '../../context/StoreContext';
import {
  LayoutDashboard,
  Package,
  FolderTree,
  Boxes,
  ShoppingBag,
  CreditCard,
  Truck,
  RotateCcw,
  Receipt,
  Users,
  Star,
  Tag,
  FileEdit,
  UserCheck,
  Shield,
  Settings,
  History,
  Store,
  LogOut,
  LucideIcon,
} from 'lucide-react';

interface AdminSidebarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  onLogout?: () => void;
  productCount?: number;
  orderCount?: number;
  couponCount?: number;
}

interface NavItem {
  id: string;
  label: string;
  icon: LucideIcon;
  badge?: number;
}

interface NavSection {
  title: string;
  items: NavItem[];
}

export const AdminSidebar: React.FC<AdminSidebarProps> = ({
  activeTab,
  setActiveTab,
  onLogout,
  productCount = 0,
  orderCount = 0,
  couponCount = 0,
}) => {
  const { navigate } = useStore();

  const navSections: NavSection[] = [
    {
      title: 'Overview',
      items: [{ id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard }],
    },
    {
      title: 'Catalog',
      items: [
        { id: 'products', label: 'Products', icon: Package, badge: productCount },
        { id: 'categories', label: 'Categories', icon: FolderTree },
        { id: 'inventory', label: 'Inventory', icon: Boxes },
      ],
    },
    {
      title: 'Fulfillment',
      items: [
        { id: 'orders', label: 'Orders', icon: ShoppingBag, badge: orderCount },
        { id: 'payments', label: 'Payments', icon: CreditCard },
        { id: 'shipments', label: 'Shipments', icon: Truck },
        { id: 'returns', label: 'Returns', icon: RotateCcw },
        { id: 'refunds', label: 'Refunds', icon: Receipt },
      ],
    },
    {
      title: 'Community',
      items: [
        { id: 'customers', label: 'Customers', icon: Users },
        { id: 'reviews', label: 'Reviews', icon: Star },
      ],
    },
    {
      title: 'Growth & Content',
      items: [
        { id: 'coupons', label: 'Coupons', icon: Tag, badge: couponCount },
        { id: 'cms', label: 'CMS & Content', icon: FileEdit },
      ],
    },
    {
      title: 'Administration',
      items: [
        { id: 'users', label: 'Staff Users', icon: UserCheck },
        { id: 'roles', label: 'Roles & Access', icon: Shield },
        { id: 'settings', label: 'Store Settings', icon: Settings },
        { id: 'audit', label: 'Audit Logs', icon: History },
      ],
    },
  ];

  return (
    <aside className="admin-sidebar">
      {/* Brand Badge */}
      <div className="admin-brand-header">
        <div className="admin-brand-logo-row">
          <span className="admin-brand-name font-serif">RÓRA</span>
          <span className="admin-badge">Admin Pro</span>
        </div>
        <span className="admin-subheading">Store Management Console</span>
      </div>

      {/* Navigation Sections */}
      <nav className="admin-nav-scroll" aria-label="Admin Navigation">
        {navSections.map((section, sIdx) => (
          <div key={sIdx} className="admin-nav-section">
            <span className="admin-nav-section-title">{section.title}</span>
            <div className="admin-nav-group">
              {section.items.map((item) => {
                const Icon = item.icon;
                const isActive = activeTab === item.id;
                return (
                  <button
                    key={item.id}
                    className={`admin-nav-btn ${isActive ? 'admin-nav-active' : ''}`}
                    onClick={() => setActiveTab(item.id)}
                    aria-current={isActive ? 'page' : undefined}
                  >
                    <div className="nav-btn-content">
                      <Icon size={16} strokeWidth={1.75} />
                      <span>{item.label}</span>
                    </div>
                    {item.badge !== undefined && item.badge > 0 && (
                      <span className={`admin-item-badge ${isActive ? 'item-badge-active' : ''}`}>
                        {item.badge}
                      </span>
                    )}
                  </button>
                );
              })}
            </div>
          </div>
        ))}
      </nav>

      {/* Footer Operator Controls */}
      <div className="admin-sidebar-footer">
        <div className="admin-operator-card">
          <div className="operator-avatar">SJ</div>
          <div className="operator-info">
            <span className="operator-name">Sarah Jenkins</span>
            <span className="operator-role">Super Admin</span>
          </div>
          {onLogout && (
            <button className="operator-logout-btn" onClick={onLogout} title="Sign Out of Demo">
              <LogOut size={15} />
            </button>
          )}
        </div>

        <button className="admin-return-btn" onClick={() => navigate('home')}>
          <Store size={15} />
          <span>View Public Store</span>
        </button>
      </div>
    </aside>
  );
};
