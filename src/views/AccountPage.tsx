'use client';

import React, { useState, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, fadeInUp } from '../animations';
import {
  User,
  Package,
  MapPin,
  Heart,
  RefreshCw,
  Settings,
  LogOut,
  Plus,
  Edit2,
  Trash2,
  Lock,
  Mail,
  Phone,
  ShieldCheck,
  ArrowRight,
  Sparkles,
} from 'lucide-react';

interface SavedAddress {
  id: string;
  isDefault: boolean;
  name: string;
  street: string;
  city: string;
  state: string;
  zip: string;
  country: string;
  phone: string;
}

export const AccountPage: React.FC = () => {
  const { user, login, register, logout, navigate, orders, wishlist, addToast } = useStore();
  const [activeTab, setActiveTab] = useState<string>('profile');
  const [authMode, setAuthMode] = useState<'login' | 'register'>('login');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const pageRef = useRef<HTMLDivElement>(null);

  // Form states
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');

  // Mock Addresses
  const [addresses] = useState<SavedAddress[]>([
    {
      id: 'addr-1',
      isDefault: true,
      name: 'Sarah Johnson (Home)',
      street: '142 Mercer Street, Apt 4B',
      city: 'New York',
      state: 'NY',
      zip: '10012',
      country: 'United States',
      phone: '+1 (555) 234-8901',
    },
    {
      id: 'addr-2',
      isDefault: false,
      name: 'Sarah Johnson (Studio)',
      street: '88 Franklin Street, Floor 3',
      city: 'New York',
      state: 'NY',
      zip: '10013',
      country: 'United States',
      phone: '+1 (555) 987-6543',
    },
  ]);

  const [profileData, setProfileData] = useState({
    name: user?.name || 'Sarah Johnson',
    email: user?.email || 'sarah.customer@rora-luxury.com',
    phone: '+91 98200 12345',
  });

  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const ok = await login(email, password);
      if (ok) {
        setEmail('');
        setPassword('');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleRegisterSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const ok = await register(name, email, password, phone);
      if (ok) {
        setName('');
        setEmail('');
        setPassword('');
        setPhone('');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const fillQuickDemo = (userEmail: string, pass: string) => {
    setEmail(userEmail);
    setPassword(pass);
  };

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.account-page .breadcrumbs-nav',
      });

      fadeInUp('.account-nav-card', {
        trigger: '.account-layout',
        start: 'top 85%',
      });

      fadeInUp('.account-main-content', {
        trigger: '.account-layout',
        start: 'top 85%',
        delay: 0.1,
      });
    },
    pageRef,
    [activeTab, user]
  );

  // If user is not authenticated, show luxury customer auth screen
  if (!user) {
    return (
      <div className="luxury-auth-stage" ref={pageRef}>
        <div className="container">
          <Breadcrumbs items={[{ label: 'Clientele Sign In' }]} />

          <div className="luxury-auth-grid">
            {/* Left Editorial Visual & Atelier Storytelling */}
            <div className="luxury-auth-hero">
              <img
                src="https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1200&q=80"
                alt="RÓRA Handcrafted Carry Essential"
                className="luxury-auth-hero-img"
              />
              <div className="luxury-auth-hero-overlay" />

              <div className="luxury-auth-hero-content">
                <div>
                  <span className="luxury-auth-pill">
                    <Sparkles size={12} /> RÓRA Atelier Circle
                  </span>
                </div>

                <div className="luxury-auth-hero-quote">
                  <h2 className="luxury-auth-hero-title">
                    Crafted for a Lifetime of Distinguished Journeys.
                  </h2>
                  <p className="luxury-auth-hero-desc">
                    Enter the private atelier client portal to manage bespoke commissions, archival order receipts, and priority dispatch schedules.
                  </p>
                </div>

                <div className="luxury-auth-perks-list">
                  <div className="luxury-auth-perk-item">
                    <ShieldCheck size={16} className="luxury-auth-perk-icon" />
                    <span>Lifetime Atelier Repair Guarantee & Material Certification</span>
                  </div>
                  <div className="luxury-auth-perk-item">
                    <Sparkles size={16} className="luxury-auth-perk-icon" />
                    <span>Exclusive Private Previews & Bespoke Monogramming</span>
                  </div>
                  <div className="luxury-auth-perk-item">
                    <Lock size={16} className="luxury-auth-perk-icon" />
                    <span>End-to-End Encrypted Client Privacy & Order Vault</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Right: Refined Client Auth Card */}
            <div className="luxury-auth-form-side">
              <div className="luxury-auth-brand-row">
                <span className="luxury-auth-brand-emblem">RÓRA</span>
                <span className="luxury-auth-brand-sub">Clientele Privileges & Access</span>
              </div>

              {/* Segmented Tab Switcher */}
              <div className="luxury-auth-tab-switch">
                <button
                  type="button"
                  className={`luxury-auth-tab-btn ${authMode === 'login' ? 'active-tab' : ''}`}
                  onClick={() => setAuthMode('login')}
                >
                  Client Sign In
                </button>
                <button
                  type="button"
                  className={`luxury-auth-tab-btn ${authMode === 'register' ? 'active-tab' : ''}`}
                  onClick={() => setAuthMode('register')}
                >
                  Create Client Profile
                </button>
              </div>

              {/* Sign In Form */}
              {authMode === 'login' && (
                <form onSubmit={handleLoginSubmit} className="luxury-auth-form">
                  <div className="luxury-input-group">
                    <label className="luxury-input-label">Client Email Address</label>
                    <div className="luxury-input-field-wrap">
                      <Mail className="luxury-input-icon" size={16} />
                      <input
                        type="email"
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="sarah.customer@rora-luxury.com"
                        className="luxury-input"
                      />
                    </div>
                  </div>

                  <div className="luxury-input-group">
                    <label className="luxury-input-label">Master Password</label>
                    <div className="luxury-input-field-wrap">
                      <Lock className="luxury-input-icon" size={16} />
                      <input
                        type="password"
                        required
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="••••••••••••"
                        className="luxury-input"
                      />
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={isSubmitting}
                    className="luxury-auth-submit-btn"
                  >
                    {isSubmitting ? 'Verifying Credentials...' : 'Authenticate & Enter Atelier'}
                    <ArrowRight size={16} />
                  </button>
                </form>
              )}

              {/* Register Form */}
              {authMode === 'register' && (
                <form onSubmit={handleRegisterSubmit} className="luxury-auth-form">
                  <div className="luxury-input-group">
                    <label className="luxury-input-label">Full Name</label>
                    <div className="luxury-input-field-wrap">
                      <User className="luxury-input-icon" size={16} />
                      <input
                        type="text"
                        required
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="Sarah Johnson"
                        className="luxury-input"
                      />
                    </div>
                  </div>

                  <div className="luxury-input-group">
                    <label className="luxury-input-label">Email Address</label>
                    <div className="luxury-input-field-wrap">
                      <Mail className="luxury-input-icon" size={16} />
                      <input
                        type="email"
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="sarah@example.com"
                        className="luxury-input"
                      />
                    </div>
                  </div>

                  <div className="luxury-input-group">
                    <label className="luxury-input-label">Phone Number (Optional)</label>
                    <div className="luxury-input-field-wrap">
                      <Phone className="luxury-input-icon" size={16} />
                      <input
                        type="tel"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        placeholder="+91 98200 12345"
                        className="luxury-input"
                      />
                    </div>
                  </div>

                  <div className="luxury-input-group">
                    <label className="luxury-input-label">Choose Password</label>
                    <div className="luxury-input-field-wrap">
                      <Lock className="luxury-input-icon" size={16} />
                      <input
                        type="password"
                        required
                        minLength={8}
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Minimum 8 characters"
                        className="luxury-input"
                      />
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={isSubmitting}
                    className="luxury-auth-submit-btn"
                  >
                    {isSubmitting ? 'Creating Atelier Profile...' : 'Complete Registration'}
                    <ArrowRight size={16} />
                  </button>
                </form>
              )}

              {/* 1-Click Instant Demo Persona Cards */}
              <div className="luxury-quick-access-box">
                <div className="luxury-quick-access-title">
                  <Sparkles size={13} />
                  <span>One-Click Instant Demo Credentials</span>
                </div>
                <div className="luxury-persona-chips">
                  <button
                    type="button"
                    className="luxury-persona-chip-btn"
                    onClick={() => fillQuickDemo('sarah.customer@rora-luxury.com', 'Password123!')}
                  >
                    <div>
                      <div className="luxury-chip-label">Sarah Customer (VIP Member)</div>
                      <div className="luxury-chip-email">sarah.customer@rora-luxury.com • Password123!</div>
                    </div>
                    <span className="admin-badge">Auto Fill</span>
                  </button>

                  <button
                    type="button"
                    className="luxury-persona-chip-btn"
                    onClick={() => fillQuickDemo('admin@rora-luxury.com', 'Password123!')}
                  >
                    <div>
                      <div className="luxury-chip-label">Super Administrator</div>
                      <div className="luxury-chip-email">admin@rora-luxury.com • Password123!</div>
                    </div>
                    <span className="admin-badge">Auto Fill</span>
                  </button>
                </div>
              </div>

              <div className="luxury-security-footer">
                <ShieldCheck size={14} />
                <span>256-Bit SSL Encrypted Studio Vault</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Authenticated Dashboard View
  return (
    <div className="account-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'Account' }]} />

      <div className="account-layout">
        {/* Left Sidebar Navigation */}
        <aside className="account-nav-card">
          <div className="account-user-banner">
            <div className="user-avatar-circle">
              <User size={24} className="user-avatar-icon" />
            </div>
            <div>
              <h3 className="user-name-title">{user.name}</h3>
              <span className="user-email-subtitle">{user.email}</span>
              <div className="mt-1 flex gap-1">
                {user.roles?.map((r) => (
                  <span key={r} className="text-[10px] uppercase font-semibold px-2 py-0.5 bg-muted rounded-full text-muted-foreground">
                    {r.replace('ROLE_', '')}
                  </span>
                ))}
              </div>
            </div>
          </div>

          <nav className="account-nav-menu" aria-label="Account Navigation">
            <button
              className={`account-nav-item ${activeTab === 'profile' ? 'active' : ''}`}
              onClick={() => setActiveTab('profile')}
            >
              <User size={18} />
              <span>Personal Profile</span>
            </button>
            <button
              className={`account-nav-item ${activeTab === 'orders' ? 'active' : ''}`}
              onClick={() => setActiveTab('orders')}
            >
              <Package size={18} />
              <span>Order History</span>
              {orders.length > 0 && <span className="nav-count-badge">{orders.length}</span>}
            </button>
            <button
              className={`account-nav-item ${activeTab === 'addresses' ? 'active' : ''}`}
              onClick={() => setActiveTab('addresses')}
            >
              <MapPin size={18} />
              <span>Saved Addresses</span>
            </button>
            <button
              className={`account-nav-item ${activeTab === 'wishlist' ? 'active' : ''}`}
              onClick={() => navigate('wishlist')}
            >
              <Heart size={18} />
              <span>My Wishlist</span>
              {wishlist.length > 0 && <span className="nav-count-badge">{wishlist.length}</span>}
            </button>
            <button
              className={`account-nav-item ${activeTab === 'returns' ? 'active' : ''}`}
              onClick={() => navigate('returns')}
            >
              <RefreshCw size={18} />
              <span>Returns & Warranty</span>
            </button>
            <button
              className={`account-nav-item ${activeTab === 'settings' ? 'active' : ''}`}
              onClick={() => setActiveTab('settings')}
            >
              <Settings size={18} />
              <span>Preferences</span>
            </button>
            {user.roles?.includes('ROLE_ADMIN') && (
              <button
                className="account-nav-item text-primary font-medium"
                onClick={() => navigate('admin')}
              >
                <ShieldCheck size={18} />
                <span>Admin Backoffice</span>
              </button>
            )}
            <div className="nav-divider" />
            <button className="account-nav-item text-error" onClick={logout}>
              <LogOut size={18} />
              <span>Sign Out</span>
            </button>
          </nav>
        </aside>

        {/* Main Content Area */}
        <main className="account-main-content">
          {activeTab === 'profile' && (
            <div className="account-panel">
              <h2 className="font-serif panel-title">Personal Profile</h2>
              <p className="panel-desc">Manage your identity details, email addresses, and contact phone.</p>

              <form onSubmit={(e) => { e.preventDefault(); addToast('Profile details updated.'); }} className="profile-form">
                <div className="form-group">
                  <label className="form-label">Full Name</label>
                  <input
                    type="text"
                    className="form-input"
                    value={profileData.name}
                    onChange={(e) => setProfileData({ ...profileData, name: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Email Address</label>
                  <input
                    type="email"
                    className="form-input"
                    value={profileData.email}
                    disabled
                  />
                  <span className="form-hint">Contact concierge to alter verified primary email.</span>
                </div>

                <div className="form-group">
                  <label className="form-label">Phone Number</label>
                  <input
                    type="tel"
                    className="form-input"
                    value={profileData.phone}
                    onChange={(e) => setProfileData({ ...profileData, phone: e.target.value })}
                  />
                </div>

                <div className="panel-actions">
                  <button type="submit" className="btn btn-primary">
                    Save Changes
                  </button>
                </div>
              </form>
            </div>
          )}

          {activeTab === 'orders' && (
            <div className="account-panel">
              <h2 className="font-serif panel-title">Recent Orders</h2>
              <p className="panel-desc">Track real-time shipment dispatches, download invoices, and request returns.</p>

              {orders.length === 0 ? (
                <div className="empty-state-card">
                  <Package size={36} className="text-muted" />
                  <h4 className="font-serif">No Orders Found</h4>
                  <p className="text-muted">You have not placed any orders yet.</p>
                  <button className="btn btn-primary btn-sm mt-3" onClick={() => navigate('shop')}>
                    Explore Collection
                  </button>
                </div>
              ) : (
                <div className="orders-list">
                  {orders.map((order) => (
                    <div key={order.id} className="order-history-card">
                      <div className="order-header-row">
                        <div>
                          <span className="order-number-text">{order.orderNumber}</span>
                          <span className="order-date-text">
                            Placed on {order.createdAt ? new Date(order.createdAt).toLocaleDateString('en-IN', { dateStyle: 'medium' }) : 'Recently'}
                          </span>
                        </div>
                        <div className="order-meta-col">
                          <span className={`badge ${order.status === 'Delivered' ? 'badge-success' : 'badge-gold'}`}>
                            {order.status}
                          </span>
                          <span className="order-amount-text">₹{(order.total || 0).toLocaleString()}</span>
                        </div>
                      </div>

                      <div className="order-items-preview">
                        {order.items.map((item, idx) => {
                          const name = item.name || 'Luxury Carry';
                          const color = item.colorName || item.color || 'Standard';
                          return (
                            <div key={idx} className="item-thumbnail-cell">
                              <span className="item-qty-tag">{item.quantity}x</span>
                              <span className="item-name-preview">{name} ({color})</span>
                            </div>
                          );
                        })}
                      </div>

                      <div className="order-footer-actions">
                        <button
                          className="btn-text btn-sm"
                          onClick={() => navigate('order-details', { orderId: order.id })}
                        >
                          View Order Details & Tracking →
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {activeTab === 'addresses' && (
            <div className="account-panel">
              <div className="panel-header-row">
                <div>
                  <h2 className="font-serif panel-title">Saved Addresses</h2>
                  <p className="panel-desc">Manage your primary shipping and billing destinations.</p>
                </div>
                <button
                  className="btn btn-secondary btn-sm"
                  onClick={() => addToast('Add new address modal opened.')}
                >
                  <Plus size={14} /> Add Address
                </button>
              </div>

              <div className="addresses-grid">
                {addresses.map((addr) => (
                  <div key={addr.id} className="address-card">
                    <div className="address-card-header">
                      <h4 className="address-title">{addr.name}</h4>
                      {addr.isDefault && <span className="badge badge-olive">Default</span>}
                    </div>
                    <p className="address-text">
                      {addr.street}<br />
                      {addr.city}, {addr.state} {addr.zip}<br />
                      {addr.country}<br />
                      {addr.phone}
                    </p>
                    <div className="address-actions">
                      <button className="btn-text btn-sm"><Edit2 size={12} /> Edit</button>
                      <button className="btn-text btn-sm text-error"><Trash2 size={12} /> Delete</button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === 'settings' && (
            <div className="account-panel">
              <h2 className="font-serif panel-title">Account Preferences</h2>
              <p className="panel-desc">Tailor communications, currencies, and notifications.</p>

              <div className="preferences-list">
                <div className="pref-row">
                  <div>
                    <h4 className="pref-title">Order Status SMS Notifications</h4>
                    <p className="pref-desc">Receive instant text updates when your parcel is dispatched or out for delivery.</p>
                  </div>
                  <input type="checkbox" defaultChecked />
                </div>
                <div className="divider" />
                <div className="pref-row">
                  <div>
                    <h4 className="pref-title">Private Journal & Release Previews</h4>
                    <p className="pref-desc">Receive exclusive previews of limited-run collection drops.</p>
                  </div>
                  <input type="checkbox" defaultChecked />
                </div>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};
