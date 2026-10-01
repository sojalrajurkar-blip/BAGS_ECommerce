import React, { useState } from 'react';
import { MOCK_REFUNDS } from '../../data/mockData';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { Search } from 'lucide-react';
import type { RefundRecord } from '../../types';

export const AdminRefunds: React.FC = () => {
  const [refunds] = useState<RefundRecord[]>(MOCK_REFUNDS);
  const [searchQuery, setSearchQuery] = useState<string>('');

  const totalRefunded = refunds.reduce((sum, r) => sum + r.amount, 0);

  const filtered = refunds.filter(
    (r) =>
      r.orderNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.customer.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.transactionRef.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="admin-page-content">
      <div className="inventory-stats-row">
        <div className="inventory-stat-box">
          <span className="inv-stat-label">Total Reimbursements</span>
          <span className="inv-stat-value font-serif">₹{totalRefunded.toLocaleString('en-IN')}</span>
          <span className="inv-stat-sub">Credited back to customer accounts</span>
        </div>
        <div className="inventory-stat-box">
          <span className="inv-stat-label">Average Refund Turnaround</span>
          <span className="inv-stat-value font-serif text-olive-600">
            24–48 Hrs
          </span>
          <span className="inv-stat-sub">Automated source bank reversal</span>
        </div>
      </div>

      <div className="admin-controls-bar">
        <div className="admin-search-box">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Search refunds by Order #, Customer..."
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
                <th>Refund ID</th>
                <th>Return Ref</th>
                <th>Order #</th>
                <th>Customer</th>
                <th>Reimbursement Method</th>
                <th>Bank TXN Reference</th>
                <th>Amount (INR)</th>
                <th>Processed Date</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((rf) => (
                <tr key={rf.id}>
                  <td>
                    <span className="sku-code">{rf.id}</span>
                  </td>
                  <td>
                    <span className="sku-code">{rf.returnRef}</span>
                  </td>
                  <td>
                    <span className="order-number-text">{rf.orderNumber}</span>
                  </td>
                  <td>
                    <strong>{rf.customer}</strong>
                  </td>
                  <td>
                    <span className="text-sm">{rf.method}</span>
                  </td>
                  <td>
                    <span className="sku-code">{rf.transactionRef}</span>
                  </td>
                  <td>
                    <strong className="order-price-text">₹{rf.amount.toLocaleString('en-IN')}</strong>
                  </td>
                  <td>
                    <span className="text-xs">{rf.date}</span>
                  </td>
                  <td>
                    <AdminStatusBadge status={rf.status} />
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
