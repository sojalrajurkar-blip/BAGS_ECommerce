import React, { useState } from 'react';
import { MOCK_SHIPMENTS } from '../../data/mockData';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { Search } from 'lucide-react';
import type { ShipmentRecord } from '../../types';

export const AdminShipments: React.FC = () => {
  const [shipments] = useState<ShipmentRecord[]>(MOCK_SHIPMENTS);
  const [searchQuery, setSearchQuery] = useState<string>('');

  const filtered = shipments.filter(
    (s) =>
      s.orderNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.awbNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.courier.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.customer.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.destination.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div className="admin-search-box">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Search AWB, Courier, Order #..."
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
                <th>Consignment</th>
                <th>Order #</th>
                <th>Customer</th>
                <th>Courier Partner</th>
                <th>AWB / Tracking #</th>
                <th>Destination</th>
                <th>Dispatched</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((s) => (
                <tr key={s.id}>
                  <td>
                    <span className="sku-code">{s.id}</span>
                  </td>
                  <td>
                    <span className="order-number-text">{s.orderNumber}</span>
                  </td>
                  <td>
                    <strong>{s.customer}</strong>
                  </td>
                  <td>
                    <span className="font-medium text-primary">{s.courier}</span>
                  </td>
                  <td>
                    <span className="sku-code text-olive-600">{s.awbNumber}</span>
                  </td>
                  <td>
                    <span className="text-sm">{s.destination}</span>
                  </td>
                  <td>
                    <span className="text-xs">{s.dispatchDate}</span>
                  </td>
                  <td>
                    <AdminStatusBadge status={s.status} />
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
