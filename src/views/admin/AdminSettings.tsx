import React, { useState } from 'react';
import { MOCK_SETTINGS } from '../../data/mockData';
import { Save } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import type { StoreSettings } from '../../types';

export const AdminSettings: React.FC = () => {
  const { addToast } = useStore();
  const [settings, setSettings] = useState<StoreSettings>(MOCK_SETTINGS);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    addToast('Store configuration & logistics policies saved.');
  };

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div>
          <h2 className="font-serif text-xl">Store Settings & Logistics Parameters</h2>
          <p className="text-sm text-muted">
            Configure studio operating parameters, tax rules, currency symbols, and shipping fees
          </p>
        </div>
      </div>

      <form onSubmit={handleSave} className="admin-settings-form">
        {/* General Store Profile */}
        <div className="admin-panel p-6">
          <h3 className="panel-title font-serif mb-16">Brand & Studio Profile</h3>
          <div className="form-row-2">
            <div className="form-group">
              <label className="form-label">Brand Name</label>
              <input
                type="text"
                className="form-input"
                value={settings.storeName}
                onChange={(e) => setSettings({ ...settings, storeName: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Default Currency</label>
              <input
                type="text"
                className="form-input"
                value={settings.currency}
                onChange={(e) => setSettings({ ...settings, currency: e.target.value })}
              />
            </div>
          </div>

          <div className="form-row-2">
            <div className="form-group">
              <label className="form-label">Client Concierge Email</label>
              <input
                type="email"
                className="form-input"
                value={settings.supportEmail}
                onChange={(e) => setSettings({ ...settings, supportEmail: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Concierge Phone Line</label>
              <input
                type="text"
                className="form-input"
                value={settings.supportPhone}
                onChange={(e) => setSettings({ ...settings, supportPhone: e.target.value })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Studio & Fulfillment Warehouse Address</label>
            <input
              type="text"
              className="form-input"
              value={settings.warehouseAddress}
              onChange={(e) => setSettings({ ...settings, warehouseAddress: e.target.value })}
            />
          </div>
        </div>

        {/* Shipping & Financial Rules */}
        <div className="admin-panel p-6">
          <h3 className="panel-title font-serif mb-16">Shipping & Taxation Rules</h3>
          <div className="form-row-2">
            <div className="form-group">
              <label className="form-label">Free Shipping Threshold (INR ₹)</label>
              <input
                type="number"
                className="form-input"
                value={settings.freeShippingThreshold}
                onChange={(e) =>
                  setSettings({ ...settings, freeShippingThreshold: Number(e.target.value) })
                }
              />
            </div>
            <div className="form-group">
              <label className="form-label">Standard Delivery Fee Below Threshold (INR ₹)</label>
              <input
                type="number"
                className="form-input"
                value={settings.standardShippingFee}
                onChange={(e) =>
                  setSettings({ ...settings, standardShippingFee: Number(e.target.value) })
                }
              />
            </div>
          </div>

          <div className="form-row-2">
            <div className="form-group">
              <label className="form-label">Goods & Services Tax (GST)</label>
              <input
                type="text"
                className="form-input"
                value={settings.taxRate}
                onChange={(e) => setSettings({ ...settings, taxRate: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Low Inventory Alert Threshold</label>
              <input
                type="number"
                className="form-input"
                value={settings.inventoryAlertThreshold}
                onChange={(e) =>
                  setSettings({ ...settings, inventoryAlertThreshold: Number(e.target.value) })
                }
              />
            </div>
          </div>
        </div>

        <div className="flex justify-end">
          <button type="submit" className="btn btn-primary">
            <Save size={16} /> Save Configuration
          </button>
        </div>
      </form>
    </div>
  );
};
