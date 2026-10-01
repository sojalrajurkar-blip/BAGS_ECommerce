import React, { useState } from 'react';
import { MOCK_ADMIN_USERS } from '../../data/mockData';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { AdminModal } from '../../components/admin/AdminModal';
import { Plus, Mail, Trash2 } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import type { AdminUser } from '../../types';

export const AdminUsers: React.FC = () => {
  const { addToast } = useStore();
  const [users, setUsers] = useState<AdminUser[]>(MOCK_ADMIN_USERS);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [newUser, setNewUser] = useState({
    name: '',
    email: '',
    role: 'Store Manager',
    status: 'Active',
  });

  const handleAddUser = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newUser.name || !newUser.email) return;

    const created: AdminUser = {
      id: `usr-${Date.now()}`,
      name: newUser.name,
      email: newUser.email,
      role: newUser.role,
      lastActive: 'Just now',
      status: 'Active',
    };

    setUsers([...users, created]);
    setIsModalOpen(false);
    setNewUser({ name: '', email: '', role: 'Store Manager', status: 'Active' });
    addToast(`Added staff account "${created.name}".`);
  };

  const handleDelete = (id: string, name: string) => {
    if (window.confirm(`Revoke operator access for "${name}"?`)) {
      setUsers(users.filter((u) => u.id !== id));
      addToast(`Staff account "${name}" removed.`);
    }
  };

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div>
          <h2 className="font-serif text-xl">Store Operators & Staff ({users.length})</h2>
          <p className="text-sm text-muted">
            Manage administrative personnel, permission assignments, and console operators
          </p>
        </div>
        <button className="btn btn-primary btn-sm" onClick={() => setIsModalOpen(true)}>
          <Plus size={16} /> Add Staff User
        </button>
      </div>

      <div className="admin-panel">
        <div className="admin-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Operator</th>
                <th>Work Email</th>
                <th>Assigned Role</th>
                <th>Last Active</th>
                <th>Status</th>
                <th className="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td>
                    <div className="customer-name-cell">
                      <div className="operator-avatar">
                        {u.name
                          .split(' ')
                          .map((n) => n[0])
                          .join('')}
                      </div>
                      <div>
                        <strong className="table-item-title">{u.name}</strong>
                        <span className="sku-code">{u.id}</span>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span className="text-sm">
                      <Mail size={13} className="inline mr-1 align-middle" />
                      {u.email}
                    </span>
                  </td>
                  <td>
                    <AdminStatusBadge status={u.role} />
                  </td>
                  <td>
                    <span className="text-xs text-secondary">{u.lastActive}</span>
                  </td>
                  <td>
                    <AdminStatusBadge status={u.status} />
                  </td>
                  <td className="text-right">
                    {u.role !== 'Super Admin' && (
                      <button
                        className="table-icon-btn delete-btn"
                        onClick={() => handleDelete(u.id, u.name)}
                        title="Delete User"
                        aria-label="Delete User"
                      >
                        <Trash2 size={14} />
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <AdminModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Invite New Staff Operator"
      >
        <form onSubmit={handleAddUser} className="admin-form">
          <div className="form-group">
            <label className="form-label">Full Name</label>
            <input
              type="text"
              required
              className="form-input"
              value={newUser.name}
              onChange={(e) => setNewUser({ ...newUser, name: e.target.value })}
              placeholder="e.g. Vikram Malhotra"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Work Email</label>
            <input
              type="email"
              required
              className="form-input"
              value={newUser.email}
              onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
              placeholder="vikram.m@rorastudios.com"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Assigned Role</label>
            <select
              className="form-select"
              value={newUser.role}
              onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
            >
              <option value="Store Manager">Store Manager</option>
              <option value="Customer Support Lead">Customer Support Lead</option>
              <option value="Content & CMS Editor">Content & CMS Editor</option>
              <option value="Warehouse Manager">Warehouse Manager</option>
            </select>
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
              Send Operator Invite
            </button>
          </div>
        </form>
      </AdminModal>
    </div>
  );
};
