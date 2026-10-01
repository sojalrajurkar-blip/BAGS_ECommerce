'use client';

import React, { useState, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, fadeInUp } from '../animations';
import { User, Package, MapPin, Heart, RefreshCw, Settings, LogOut, Plus, Edit2, Trash2 } from 'lucide-react';

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
  const { navigate, orders, wishlist, addToast } = useStore();
  const [activeTab, setActiveTab] = useState<string>('profile');
  const pageRef = useRef<HTMLDivElement>(null);

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
    name: 'Sarah Johnson',
    email: 'sarah.johnson@example.com',
    phone: '+1 (555) 234-8901',
  });

  const handleSaveProfile = (e: React.FormEvent) => {
    e.preventDefault();
    addToast('Account profile updated successfully.');
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
    [activeTab]
  );

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
              <h3 className="user-name-title">{profileData.name}</h3>
              <span className="user-email-subtitle">{profileData.email}</span>
            </div>
          </div>

          <div className="divider my-16" />

          <nav className="account-nav-list" aria-label="Account Tabs">
            <button
              className={`account-nav-item ${activeTab === 'profile' ? 'item-active' : ''}`}
              onClick={() => setActiveTab('profile')}
            >
              <User size={16} /> Profile Details
            </button>
            <button
              className={`account-nav-item ${activeTab === 'orders' ? 'item-active' : ''}`}
              onClick={() => navigate('orders')}
            >
              <Package size={16} /> My Orders ({orders.length})
            </button>
            <button
              className={`account-nav-item ${activeTab === 'addresses' ? 'item-active' : ''}`}
              onClick={() => setActiveTab('addresses')}
            >
              <MapPin size={16} /> Saved Addresses ({addresses.length})
            </button>
            <button
              className="account-nav-item"
              onClick={() => navigate('wishlist')}
            >
              <Heart size={16} /> Wishlist ({wishlist.length})
            </button>
            <button
              className="account-nav-item"
              onClick={() => navigate('returns')}
            >
              <RefreshCw size={16} /> Returns & Exchanges
            </button>
            <button
              className={`account-nav-item ${activeTab === 'settings' ? 'item-active' : ''}`}
              onClick={() => setActiveTab('settings')}
            >
              <Settings size={16} /> Preferences
            </button>
            <button
              className="account-nav-item account-logout-item"
              onClick={() => {
                addToast('Logged out of demo session.');
                navigate('home');
              }}
            >
              <LogOut size={16} /> Log Out
            </button>
          </nav>
        </aside>

        {/* Right Content Area */}
        <main className="account-main-content">
          {activeTab === 'profile' && (
            <div className="account-panel">
              <h2 className="font-serif panel-title">Personal Profile</h2>
              <p className="panel-desc">Manage your personal information and contact settings.</p>

              <form onSubmit={handleSaveProfile} className="profile-form">
                <div className="form-group">
                  <label className="form-label">Full Name</label>
                  <input
                    type="text"
                    value={profileData.name}
                    onChange={(e) => setProfileData({ ...profileData, name: e.target.value })}
                    className="form-input"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Email Address</label>
                  <input
                    type="email"
                    value={profileData.email}
                    onChange={(e) => setProfileData({ ...profileData, email: e.target.value })}
                    className="form-input"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Phone Number</label>
                  <input
                    type="tel"
                    value={profileData.phone}
                    onChange={(e) => setProfileData({ ...profileData, phone: e.target.value })}
                    className="form-input"
                  />
                </div>
                <button type="submit" className="btn btn-primary self-start">
                  Save Profile Changes
                </button>
              </form>
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
