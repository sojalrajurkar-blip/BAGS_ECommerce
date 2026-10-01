import React, { useState } from 'react';
import { MOCK_COUPONS } from '../../data/mockData';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { AdminModal } from '../../components/admin/AdminModal';
import { Plus, Trash2 } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import type { Coupon } from '../../types';

export const AdminCoupons: React.FC = () => {
  const { addToast } = useStore();
  const [coupons, setCoupons] = useState<Coupon[]>(MOCK_COUPONS);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [newCoupon, setNewCoupon] = useState({
    code: '',
    discountPercent: 15,
    minCart: 1999,
    description: '',
    expiry: '31 Dec 2026',
  });

  const handleCreateCoupon = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCoupon.code) return;

    const created: Coupon = {
      code: newCoupon.code.toUpperCase().trim(),
      discountPercent: Number(newCoupon.discountPercent),
      minCart: Number(newCoupon.minCart),
      description: newCoupon.description || `${newCoupon.discountPercent}% off order`,
      uses: 0,
      expiry: newCoupon.expiry,
      status: 'Active',
    };

    setCoupons([created, ...coupons]);
    setIsModalOpen(false);
    setNewCoupon({
      code: '',
      discountPercent: 15,
      minCart: 1999,
      description: '',
      expiry: '31 Dec 2026',
    });
    addToast(`Coupon code "${created.code}" created.`);
  };

  const handleDelete = (code: string) => {
    setCoupons(coupons.filter((c) => c.code !== code));
    addToast(`Coupon "${code}" deleted.`);
  };

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div>
          <h2 className="font-serif text-xl">Promotional & VIP Codes ({coupons.length})</h2>
          <p className="text-sm text-muted">
            Manage discounts, marketing campaign codes, and private member privileges
          </p>
        </div>
        <button className="btn btn-primary btn-sm" onClick={() => setIsModalOpen(true)}>
          <Plus size={16} /> Create Coupon
        </button>
      </div>

      <div className="admin-panel">
        <div className="admin-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Coupon Code</th>
                <th>Discount</th>
                <th>Minimum Cart Value</th>
                <th>Description</th>
                <th>Total Redemptions</th>
                <th>Expiry Date</th>
                <th>Status</th>
                <th className="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {coupons.map((c) => (
                <tr key={c.code}>
                  <td>
                    <span className="sku-code text-sm font-bold text-primary">
                      {c.code}
                    </span>
                  </td>
                  <td>
                    <strong className="text-olive-600">{c.discountPercent}% OFF</strong>
                  </td>
                  <td>
                    <span>₹{c.minCart?.toLocaleString('en-IN') || '0'}</span>
                  </td>
                  <td>
                    <span className="text-sm">{c.description}</span>
                  </td>
                  <td>
                    <strong>{c.uses}</strong> times
                  </td>
                  <td>
                    <span className="text-sm">{c.expiry}</span>
                  </td>
                  <td>
                    <AdminStatusBadge status={c.status || 'Active'} />
                  </td>
                  <td className="text-right">
                    <button
                      className="table-icon-btn delete-btn"
                      onClick={() => handleDelete(c.code)}
                      title="Delete Coupon"
                      aria-label="Delete Coupon"
                    >
                      <Trash2 size={14} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create Coupon Modal */}
      <AdminModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Create Promotional Coupon"
      >
        <form onSubmit={handleCreateCoupon} className="admin-form">
          <div className="form-group">
            <label className="form-label">Coupon Code (Uppercase)</label>
            <input
              type="text"
              required
              className="form-input"
              value={newCoupon.code}
              onChange={(e) => setNewCoupon({ ...newCoupon, code: e.target.value })}
              placeholder="e.g. TRAVELVIP20"
            />
          </div>

          <div className="form-row-2">
            <div className="form-group">
              <label className="form-label">Discount Percentage (%)</label>
              <input
                type="number"
                required
                min={1}
                max={90}
                className="form-input"
                value={newCoupon.discountPercent}
                onChange={(e) => setNewCoupon({ ...newCoupon, discountPercent: Number(e.target.value) })}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Min Cart Value (INR ₹)</label>
              <input
                type="number"
                required
                className="form-input"
                value={newCoupon.minCart}
                onChange={(e) => setNewCoupon({ ...newCoupon, minCart: Number(e.target.value) })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Campaign Description</label>
            <input
              type="text"
              className="form-input"
              value={newCoupon.description}
              onChange={(e) => setNewCoupon({ ...newCoupon, description: e.target.value })}
              placeholder="e.g. Exclusive 20% off for Autumn Travel Club"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Expiry Date</label>
            <input
              type="text"
              className="form-input"
              value={newCoupon.expiry}
              onChange={(e) => setNewCoupon({ ...newCoupon, expiry: e.target.value })}
              placeholder="31 Dec 2026"
            />
          </div>

          <div className="modal-actions-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsModalOpen(false)}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Activate Coupon
            </button>
          </div>
        </form>
      </AdminModal>
    </div>
  );
};
