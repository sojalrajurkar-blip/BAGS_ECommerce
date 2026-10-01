import React, { useState } from 'react';
import { MOCK_AUDIT_LOGS } from '../../data/mockData';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { Search } from 'lucide-react';
import type { AuditLog } from '../../types';

export const AdminAuditLogs: React.FC = () => {
  const [logs] = useState<AuditLog[]>(MOCK_AUDIT_LOGS);
  const [searchQuery, setSearchQuery] = useState<string>('');

  const filtered = logs.filter(
    (l) =>
      l.action.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.user.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (l.entity || '').toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div className="admin-search-box">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Search audit trail by action, actor, or entity..."
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
                <th>Log ID</th>
                <th>Operation / Action</th>
                <th>Actor / Operator</th>
                <th>Target Resource</th>
                <th>Timestamp</th>
                <th>Severity</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((l) => (
                <tr key={l.id}>
                  <td>
                    <span className="sku-code">#LOG-{String(l.id).padStart(4, '0')}</span>
                  </td>
                  <td>
                    <strong className="text-primary">{l.action}</strong>
                  </td>
                  <td>
                    <span className="text-sm">{l.user}</span>
                  </td>
                  <td>
                    <span className="sku-code text-secondary">{l.entity}</span>
                  </td>
                  <td>
                    <span className="text-xs text-muted">{l.timestamp}</span>
                  </td>
                  <td>
                    <AdminStatusBadge status={l.severity || 'Info'} />
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
