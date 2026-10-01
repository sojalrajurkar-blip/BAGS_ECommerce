import React from 'react';
import { MOCK_ROLES } from '../../data/mockData';
import { Shield, Check } from 'lucide-react';
import type { AdminRole } from '../../types';

export const AdminRoles: React.FC = () => {
  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div>
          <h2 className="font-serif text-xl">Roles & Permission Matrix</h2>
          <p className="text-sm text-muted">
            Granular access controls enforced across catalog, order dispatch, refunds, and financial reporting
          </p>
        </div>
      </div>

      <div className="admin-roles-grid">
        {MOCK_ROLES.map((role: AdminRole) => (
          <div key={role.id} className="role-card">
            <div className="role-card-header">
              <div className="role-icon-wrap">
                <Shield size={18} />
              </div>
              <div>
                <h3 className="role-title font-serif">{role.name}</h3>
                <span className="role-user-count">
                  {role.usersCount} Active Operator{role.usersCount > 1 ? 's' : ''}
                </span>
              </div>
            </div>

            <p className="role-desc">{role.description}</p>

            <div className="permissions-box">
              <span className="permissions-label">Granted Scope:</span>
              <div className="permissions-tags">
                {role.permissions.map((perm: string, pIdx: number) => (
                  <span key={pIdx} className="permission-tag">
                    <Check size={12} /> {perm}
                  </span>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
