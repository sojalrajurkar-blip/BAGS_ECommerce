import React, { useState } from 'react';
import { MOCK_PAYMENTS } from '../../data/mockData';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { Search } from 'lucide-react';
import type { PaymentRecord } from '../../types';

export const AdminPayments: React.FC = () => {
  const [payments] = useState<PaymentRecord[]>(MOCK_PAYMENTS);
  const [searchQuery, setSearchQuery] = useState<string>('');

  const totalCaptured = payments
    .filter((p) => p.status === 'Captured')
    .reduce((sum, p) => sum + p.amount, 0);

  const filtered = payments.filter(
    (p) =>
      p.orderNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.customer.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.gatewayRef.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.method.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="admin-page-content">
      {/* Top Payments Summary */}
      <div className="inventory-stats-row">
        <div className="inventory-stat-box">
          <span className="inv-stat-label">Total Volume Processed</span>
          <span className="inv-stat-value font-serif">₹{totalCaptured.toLocaleString('en-IN')}</span>
          <span className="inv-stat-sub">Across 100% verified gateway transactions</span>
        </div>
        <div className="inventory-stat-box">
          <span className="inv-stat-label">Settlement Success Rate</span>
          <span className="inv-stat-value font-serif text-olive-600">
            99.8%
          </span>
          <span className="inv-stat-sub">UPI Auto-settlement, Cards & NetBanking</span>
        </div>
        <div className="inventory-stat-box">
          <span className="inv-stat-label">Refund Volume</span>
          <span className="inv-stat-value font-serif text-secondary">
            ₹5,499
          </span>
          <span className="inv-stat-sub">1 processed return reimbursement</span>
        </div>
      </div>

      <div className="admin-controls-bar">
        <div className="admin-search-box">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Search by Order #, Customer, Gateway TXN..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="admin-input"
          />
        </div>
      </div>

      <div className="admin-panel">
        <div className="admin-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Transaction ID</th>
                <th>Order #</th>
                <th>Customer</th>
                <th>Amount (INR)</th>
                <th>Payment Method</th>
                <th>Gateway Reference</th>
                <th>Date & Time</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((p) => (
                <tr key={p.id}>
                  <td>
                    <span className="sku-code">{p.id}</span>
                  </td>
                  <td>
                    <span className="order-number-text">{p.orderNumber}</span>
                  </td>
                  <td>
                    <strong>{p.customer}</strong>
                  </td>
                  <td>
                    <strong className="order-price-text">₹{p.amount.toLocaleString('en-IN')}</strong>
                  </td>
                  <td>
                    <span className="text-sm">{p.method}</span>
                  </td>
                  <td>
                    <span className="sku-code">{p.gatewayRef}</span>
                  </td>
                  <td>
                    <span className="text-xs text-secondary">{p.date}</span>
                  </td>
                  <td>
                    <AdminStatusBadge status={p.status} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
