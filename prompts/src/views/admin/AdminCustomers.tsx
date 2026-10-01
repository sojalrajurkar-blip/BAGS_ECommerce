import React, { useState } from 'react';
import { MOCK_CUSTOMERS } from '../../data/mockData';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { Search, Mail, Phone, MapPin } from 'lucide-react';
import type { Customer } from '../../types';

export const AdminCustomers: React.FC = () => {
  const [customers] = useState<Customer[]>(MOCK_CUSTOMERS);
  const [searchQuery, setSearchQuery] = useState<string>('');

  const filtered = customers.filter(
    (c) =>
      c.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (c.city && c.city.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div className="admin-search-box">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Search customers by name, email, city..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="admin-input"
          />
        </div>
        <div className="controls-right-info">
          <span>
            Total Buyers: <strong>{customers.length}</strong>
          </span>
        </div>
      </div>

      <div className="admin-panel">
        <div className="admin-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Customer</th>
                <th>Contact</th>
                <th>Location</th>
                <th>Tier</th>
                <th>Orders</th>
                <th>Lifetime Spend (INR)</th>
                <th>Last Purchase</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((c) => (
                <tr key={c.id}>
                  <td>
                    <div className="customer-name-cell">
                      <div className="customer-avatar">
                        {c.name
                          .split(' ')
                          .map((n) => n[0])
                          .join('')}
                      </div>
                      <div>
                        <strong className="table-item-title">{c.name}</strong>
                        <span className="table-sub-text">Member since {c.joinedDate}</span>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div className="contact-cell">
                      <span>
                        <Mail size={13} /> {c.email}
                      </span>
                      {c.phone && (
                        <span>
                          <Phone size={13} /> {c.phone}
                        </span>
                      )}
                    </div>
                  </td>
                  <td>
                    <span className="location-tag">
                      <MapPin size={13} /> {c.city || 'India'}
                    </span>
                  </td>
                  <td>
                    <AdminStatusBadge status={c.tier || 'Standard'} />
                  </td>
                  <td>
                    <strong>{c.ordersCount}</strong> orders
                  </td>
                  <td>
                    <strong className="order-price-text font-serif text-base">
                      ₹{(c.lifetimeValue || c.totalSpent || 0).toLocaleString('en-IN')}
                    </strong>
                  </td>
                  <td>
                    <span className="text-sm">{c.lastOrder || 'Recent'}</span>
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
