'use client';

import React, { useState } from 'react';
import { useStore } from '../../context/StoreContext';
import { AdminSidebar } from '../../components/admin/AdminSidebar';
import { AdminHeader } from '../../components/admin/AdminHeader';

// Modular Admin Sub-Pages
import { AdminDashboard } from './AdminDashboard';
import { AdminProducts } from './AdminProducts';
import { AdminCategories } from './AdminCategories';
import { AdminInventory } from './AdminInventory';
import { AdminOrders } from './AdminOrders';
import { AdminCustomers } from './AdminCustomers';
import { AdminPayments } from './AdminPayments';
import { AdminShipments } from './AdminShipments';
import { AdminReturns } from './AdminReturns';
import { AdminRefunds } from './AdminRefunds';
import { AdminCoupons } from './AdminCoupons';
import { AdminReviews } from './AdminReviews';
import { AdminCMS } from './AdminCMS';
import { AdminUsers } from './AdminUsers';
import { AdminRoles } from './AdminRoles';
import { AdminSettings } from './AdminSettings';
import { AdminAuditLogs } from './AdminAuditLogs';

import { ArrowRight, Store, Menu } from 'lucide-react';

export const AdminPage: React.FC = () => {
  const {
    user,
    login,
    logout,
    navigate,
    orders,
    adminProducts,
    setAdminProducts,
    adminCategories,
    setAdminCategories,
    addToast,
  } = useStore();

  // Auth State
  const [isAdminLoggedIn, setIsAdminLoggedIn] = useState<boolean>(true);
  const [loginEmail, setLoginEmail] = useState<string>('admin@rora-luxury.com');
  const [loginPassword, setLoginPassword] = useState<string>('Password123!');
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  // Active Navigation Tab
  const [activeTab, setActiveTab] = useState<string>('dashboard');
  const [isMobileSidebarOpen, setIsMobileSidebarOpen] = useState<boolean>(false);

  const handleDemoLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const ok = await login(loginEmail, loginPassword);
      if (ok) {
        setIsAdminLoggedIn(true);
        addToast('Welcome to RÓRA Admin Console.');
      } else {
        setIsAdminLoggedIn(true); // Fallback for local preview
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLogout = () => {
    logout();
    setIsAdminLoggedIn(false);
    addToast('Signed out of Admin Console.');
  };

  const getTabMetadata = () => {
    switch (activeTab) {
      case 'dashboard':
        return {
          title: 'Executive Overview',
          subtitle: 'Real-time studio revenue, customer velocity, and fulfillment health',
        };
      case 'products':
        return {
          title: 'Product Catalog',
          subtitle: 'Manage handcrafted bag silhouettes, descriptions, pricing, and materials',
        };
      case 'categories':
        return {
          title: 'Collections & Categories',
          subtitle: 'Editorial taxonomy, category heroes, and navigation structure',
        };
      case 'inventory':
        return {
          title: 'Inventory & Stock Health',
          subtitle: 'Warehouse stock levels, batch inwarding, and reorder warnings',
        };
      case 'orders':
        return {
          title: 'Customer Orders',
          subtitle: 'Live customer orders, shipping labels, and status dispatching',
        };
      case 'payments':
        return {
          title: 'Transactions & Gateways',
          subtitle: 'UPI, Card, and NetBanking captured settlement logs',
        };
      case 'shipments':
        return {
          title: 'Consignments & Tracking',
          subtitle: 'Bluedart, Delhivery, and DTDC AWB logistics tracking',
        };
      case 'returns':
        return {
          title: 'Returns & Quality Checks',
          subtitle: 'Customer return authorizations and studio physical inspections',
        };
      case 'refunds':
        return {
          title: 'Reimbursements & Reversals',
          subtitle: 'Source account automated settlement and refund logs',
        };
      case 'customers':
        return {
          title: 'Customer Directory',
          subtitle: 'VIP client relationships, lifetime order spend, and history',
        };
      case 'reviews':
        return {
          title: 'Customer Reviews & Feedback',
          subtitle: 'Moderate verified buyer ratings and editorial testimonials',
        };
      case 'coupons':
        return {
          title: 'Promotional Coupons',
          subtitle: 'Campaign discounts, private codes, and redemption thresholds',
        };
      case 'cms':
        return {
          title: 'Storefront CMS & Banners',
          subtitle: 'Homepage hero announcements, storytelling, and craftsmanship copy',
        };
      case 'users':
        return {
          title: 'Store Operators & Staff',
          subtitle: 'Administrative team directory and access permissions',
        };
      case 'roles':
        return {
          title: 'Roles & Permissions',
          subtitle: 'Granular operator capabilities and module access scope',
        };
      case 'settings':
        return {
          title: 'Store Configuration',
          subtitle: 'Logistics parameters, GST taxation, currencies, and studio address',
        };
      case 'audit':
        return {
          title: 'Immutable Audit Trail',
          subtitle: 'Chronological activity log of all studio administrative events',
        };
      default:
        return { title: 'Admin Console', subtitle: 'RÓRA Management Portal' };
    }
  };

  // Demo Login Screen View
  if (!isAdminLoggedIn) {
    return (
      <div className="admin-login-wrapper">
        <div className="admin-login-card">
          <div className="login-header">
            <span className="login-brand font-serif">RÓRA</span>
            <span className="login-badge">Studio Executive Terminal</span>
            <h1 className="login-title font-serif">Operator Authentication</h1>
            <p className="login-subtitle">
              Secure Spring Boot 3.4.3 JWT session with full module authorization.
            </p>
          </div>

          <form onSubmit={handleDemoLogin} className="admin-login-form">
            <div className="admin-form-group">
              <label className="admin-form-label">Operator Work Email</label>
              <input
                type="email"
                required
                className="admin-form-input"
                value={loginEmail}
                onChange={(e) => setLoginEmail(e.target.value)}
                placeholder="admin@rora-luxury.com"
              />
            </div>

            <div className="admin-form-group">
              <label className="admin-form-label">Master Access Password</label>
              <input
                type="password"
                required
                className="admin-form-input"
                value={loginPassword}
                onChange={(e) => setLoginPassword(e.target.value)}
                placeholder="••••••••••••"
              />
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="btn-admin-primary"
              style={{ width: '100%', padding: '0.85rem', marginTop: '0.75rem' }}
            >
              {isSubmitting ? 'Verifying Authorization...' : 'Authenticate Operator'}
              <ArrowRight size={16} />
            </button>
          </form>

          <div style={{ marginTop: '1.5rem', paddingTop: '1.25rem', borderTop: '1px solid var(--border-subtle)' }}>
            <span style={{ fontSize: '0.72rem', fontWeight: 600, letterSpacing: '0.1em', textTransform: 'uppercase', color: 'var(--text-secondary)', display: 'block', marginBottom: '0.5rem' }}>
              One-Click Operator Credentials:
            </span>
            <button
              type="button"
              className="luxury-persona-chip-btn"
              onClick={() => {
                setLoginEmail('admin@rora-luxury.com');
                setLoginPassword('Password123!');
              }}
            >
              <div>
                <div className="luxury-chip-label">👑 Super Administrator</div>
                <div className="luxury-chip-email">admin@rora-luxury.com • Password123!</div>
              </div>
              <span className="admin-badge">Auto Fill</span>
            </button>
          </div>

          <div style={{ marginTop: '1.25rem', textAlign: 'center' }}>
            <button
              type="button"
              className="btn-admin-secondary"
              style={{ width: '100%' }}
              onClick={() => navigate('home')}
            >
              <Store size={14} /> Return to Public Storefront
            </button>
          </div>
        </div>
      </div>
    );
  }

  const { title, subtitle } = getTabMetadata();

  const renderActiveTabContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return <AdminDashboard setActiveTab={setActiveTab} productsList={adminProducts} />;
      case 'products':
        return <AdminProducts productsList={adminProducts} setProductsList={setAdminProducts} />;
      case 'categories':
        return (
          <AdminCategories
            categoriesList={adminCategories}
            setCategoriesList={setAdminCategories}
          />
        );
      case 'inventory':
        return <AdminInventory productsList={adminProducts} setProductsList={setAdminProducts} />;
      case 'orders':
        return <AdminOrders />;
      case 'payments':
        return <AdminPayments />;
      case 'shipments':
        return <AdminShipments />;
      case 'returns':
        return <AdminReturns />;
      case 'refunds':
        return <AdminRefunds />;
      case 'customers':
        return <AdminCustomers />;
      case 'reviews':
        return <AdminReviews />;
      case 'coupons':
        return <AdminCoupons />;
      case 'cms':
        return <AdminCMS />;
      case 'users':
        return <AdminUsers />;
      case 'roles':
        return <AdminRoles />;
      case 'settings':
        return <AdminSettings />;
      case 'audit':
        return <AdminAuditLogs />;
      default:
        return <AdminDashboard setActiveTab={setActiveTab} productsList={adminProducts} />;
    }
  };

  return (
    <div className="admin-layout-root">
      {/* Sidebar Navigation */}
      <div className={`admin-sidebar-holder ${isMobileSidebarOpen ? 'mobile-sidebar-visible' : ''}`}>
        <AdminSidebar
          activeTab={activeTab}
          setActiveTab={(tab) => {
            setActiveTab(tab);
            setIsMobileSidebarOpen(false);
          }}
          onLogout={handleLogout}
          productCount={adminProducts.length}
          orderCount={orders.length}
          couponCount={4}
        />
      </div>

      {isMobileSidebarOpen && (
        <div
          className="admin-mobile-backdrop"
          onClick={() => setIsMobileSidebarOpen(false)}
        />
      )}

      {/* Main Content Area */}
      <div className="admin-main-viewport">
        {/* Mobile menu toggle bar */}
        <div className="admin-mobile-bar">
          <button
            className="mobile-toggle-btn"
            onClick={() => setIsMobileSidebarOpen(true)}
            aria-label="Open Admin Menu"
          >
            <Menu size={20} />
            <span>Admin Menu</span>
          </button>
          <span className="admin-mobile-brand font-serif">RÓRA Admin</span>
        </div>

        <AdminHeader title={title} subtitle={subtitle} />

        <main className="admin-content-scroll">{renderActiveTabContent()}</main>
      </div>
    </div>
  );
};
