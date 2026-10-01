import React, { useState } from 'react';
import { REVIEWS } from '../../data/mockData';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { Star, Search } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import type { Review } from '../../types';

interface AdminReviewItem extends Review {
  status: string;
}

export const AdminReviews: React.FC = () => {
  const { addToast } = useStore();
  const [reviewsList, setReviewsList] = useState<AdminReviewItem[]>(
    REVIEWS.map((r) => ({ ...r, status: 'Published' }))
  );
  const [searchQuery, setSearchQuery] = useState<string>('');

  const toggleStatus = (id: string) => {
    setReviewsList((prev) =>
      prev.map((r) =>
        r.id === id
          ? { ...r, status: r.status === 'Published' ? 'Flagged' : 'Published' }
          : r
      )
    );
    addToast('Review moderation status updated.');
  };

  const filtered = reviewsList.filter(
    (r) =>
      r.author.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.productName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (r.content || '').toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div className="admin-search-box">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Search reviews by customer, product, quote..."
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
                <th>Customer & Role</th>
                <th>Product Silhouette</th>
                <th>Rating</th>
                <th>Review Snippet</th>
                <th>Date</th>
                <th>Status</th>
                <th className="text-right">Moderation</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((r) => (
                <tr key={r.id}>
                  <td>
                    <strong>{r.author}</strong>
                    <span className="table-sub-text">{r.role}</span>
                  </td>
                  <td>
                    <span className="font-medium text-primary">{r.productName}</span>
                  </td>
                  <td>
                    <div className="flex items-center gap-1 text-olive-600">
                      {[...Array(r.rating)].map((_, i) => (
                        <Star key={i} size={13} fill="currentColor" />
                      ))}
                    </div>
                  </td>
                  <td>
                    <p className="text-sm text-secondary max-w-sm">
                      "{r.content}"
                    </p>
                  </td>
                  <td>
                    <span className="text-xs">{r.date}</span>
                  </td>
                  <td>
                    <AdminStatusBadge status={r.status} />
                  </td>
                  <td className="text-right">
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => toggleStatus(r.id)}
                    >
                      {r.status === 'Published' ? 'Hide / Flag' : 'Publish'}
                    </button>
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
