import React, { useState } from 'react';
import { MOCK_RETURNS } from '../../data/mockData';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { Search, Check, X } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import type { ReturnRecord } from '../../types';

export const AdminReturns: React.FC = () => {
  const { addToast } = useStore();
  const [returnsList, setReturnsList] = useState<ReturnRecord[]>(MOCK_RETURNS);
  const [searchQuery, setSearchQuery] = useState<string>('');

  const handleApprove = (id: string) => {
    setReturnsList((prev) =>
      prev.map((r) =>
        r.id === id
          ? {
              ...r,
              status: 'Approved & Refunded',
              inspectionStatus: 'Passed (Pristine Condition)',
            }
          : r
      )
    );
    addToast(`Return ${id} approved and queued for bank reimbursement.`);
  };

  const handleReject = (id: string) => {
    setReturnsList((prev) =>
      prev.map((r) =>
        r.id === id
          ? { ...r, status: 'Rejected', inspectionStatus: 'Failed Policy Check' }
          : r
      )
    );
    addToast(`Return ${id} rejected.`);
  };

  const filtered = returnsList.filter(
    (r) =>
      r.orderNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.customer.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.item.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div className="admin-search-box">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Search return requests..."
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
                <th>Return ID</th>
                <th>Order #</th>
                <th>Customer</th>
                <th>Product Silhouette</th>
                <th>Return Reason</th>
                <th>Inspection Condition</th>
                <th>Amount (INR)</th>
                <th>Status</th>
                <th className="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((r) => (
                <tr key={r.id}>
                  <td>
                    <span className="sku-code">{r.id}</span>
                  </td>
                  <td>
                    <span className="order-number-text">{r.orderNumber}</span>
                  </td>
                  <td>
                    <strong>{r.customer}</strong>
                  </td>
                  <td>
                    <span className="text-sm">{r.item}</span>
                  </td>
                  <td>
                    <span className="text-xs text-secondary">{r.reason}</span>
                  </td>
                  <td>
                    <span className="text-xs font-medium">{r.inspectionStatus}</span>
                  </td>
                  <td>
                    <strong className="order-price-text">₹{r.amount.toLocaleString('en-IN')}</strong>
                  </td>
                  <td>
                    <AdminStatusBadge status={r.status} />
                  </td>
                  <td className="text-right">
                    {r.status === 'Under Review' ? (
                      <div className="table-actions-row">
                        <button
                          className="btn btn-olive btn-sm"
                          onClick={() => handleApprove(r.id)}
                          title="Approve Return"
                          aria-label="Approve Return"
                        >
                          <Check size={14} /> Approve
                        </button>
                        <button
                          className="btn btn-secondary btn-sm"
                          onClick={() => handleReject(r.id)}
                          title="Reject Return"
                          aria-label="Reject Return"
                        >
                          <X size={14} />
                        </button>
                      </div>
                    ) : (
                      <span className="text-xs text-muted">Completed</span>
                    )}
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
