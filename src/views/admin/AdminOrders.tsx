import React, { useState } from 'react';
import { useStore } from '../../context/StoreContext';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { AdminModal } from '../../components/admin/AdminModal';
import { Search, Filter, Eye } from 'lucide-react';
import type { Order } from '../../types';

export const AdminOrders: React.FC = () => {
  const { orders, addToast } = useStore();
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);

  const handleUpdateStatus = (orderId: string, newStatus: string) => {
    if (selectedOrder) {
      setSelectedOrder({ ...selectedOrder, status: newStatus });
    }
    addToast(`Order ${orderId} status updated to "${newStatus}".`);
  };

  const filtered = orders.filter((o) => {
    const matchesSearch =
      o.orderNumber?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      o.shippingAddress?.fullName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      o.shippingAddress?.city?.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus =
      statusFilter === 'all' || o.status.toLowerCase() === statusFilter.toLowerCase();
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="admin-page-content">
      <div className="admin-controls-bar">
        <div className="controls-left">
          <div className="admin-search-box">
            <Search size={16} className="search-icon" />
            <input
              type="text"
              placeholder="Search by Order #, Customer, or City..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="admin-input"
            />
          </div>

          <div className="admin-filter-box">
            <Filter size={15} />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="admin-select"
            >
              <option value="all">All Statuses ({orders.length})</option>
              <option value="processing">Processing</option>
              <option value="shipped">Shipped</option>
              <option value="delivered">Delivered</option>
              <option value="cancelled">Cancelled</option>
            </select>
          </div>
        </div>
      </div>

      <div className="admin-panel">
        <div className="admin-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Order #</th>
                <th>Date</th>
                <th>Customer</th>
                <th>Items Ordered</th>
                <th>Payment</th>
                <th>Total (INR)</th>
                <th>Status</th>
                <th className="text-right">Action</th>
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 ? (
                <tr>
                  <td colSpan={8} className="text-center p-40">
                    <p className="text-muted">No orders found.</p>
                  </td>
                </tr>
              ) : (
                filtered.map((o) => (
                  <tr key={o.id}>
                    <td>
                      <span className="order-number-text">{o.orderNumber}</span>
                    </td>
                    <td>{o.date}</td>
                    <td>
                      <strong>{o.shippingAddress?.fullName || 'Customer'}</strong>
                      <span className="table-sub-text">{o.shippingAddress?.city || 'India'}</span>
                    </td>
                    <td>
                      {o.items?.map((i) => `${i.name} (${i.quantity}x)`).join(', ') || 'Custom Bag'}
                    </td>
                    <td>
                      <span className="text-sm">{o.paymentMethod || 'UPI / Card'}</span>
                    </td>
                    <td>
                      <strong className="order-price-text">₹{o.total?.toLocaleString('en-IN')}</strong>
                    </td>
                    <td>
                      <AdminStatusBadge status={o.status} />
                    </td>
                    <td className="text-right">
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => setSelectedOrder(o)}
                      >
                        <Eye size={14} /> Details
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Order Detail Modal */}
      <AdminModal
        isOpen={Boolean(selectedOrder)}
        onClose={() => setSelectedOrder(null)}
        title={`Order Details: ${selectedOrder?.orderNumber}`}
        maxWidth="680px"
      >
        {selectedOrder && (
          <div className="order-detail-modal-body">
            <div className="order-meta-grid">
              <div>
                <span className="meta-label">Placed On</span>
                <span className="meta-val">{selectedOrder.date}</span>
              </div>
              <div>
                <span className="meta-label">Current Status</span>
                <div className="mt-4">
                  <AdminStatusBadge status={selectedOrder.status} />
                </div>
              </div>
              <div>
                <span className="meta-label">Payment Method</span>
                <span className="meta-val">{selectedOrder.paymentMethod}</span>
              </div>
              <div>
                <span className="meta-label">Order Total</span>
                <span className="meta-val font-serif text-xl">
                  ₹{selectedOrder.total?.toLocaleString('en-IN')}
                </span>
              </div>
            </div>

            <div className="modal-section-divider" />

            <h4 className="modal-sub-title font-serif">Customer & Delivery Destination</h4>
            <p className="text-sm text-secondary leading-relaxed">
              <strong>{selectedOrder.shippingAddress?.fullName}</strong><br />
              {selectedOrder.shippingAddress?.street}, {selectedOrder.shippingAddress?.city}, {selectedOrder.shippingAddress?.state} {selectedOrder.shippingAddress?.postalCode}<br />
              {selectedOrder.shippingAddress?.country || 'India'}
            </p>

            <div className="modal-section-divider" />

            <h4 className="modal-sub-title font-serif">Consignment Items</h4>
            <div className="modal-items-list">
              {selectedOrder.items?.map((item, idx) => (
                <div key={idx} className="modal-item-row">
                  <img src={item.image} alt={item.name} className="modal-item-img" />
                  <div className="flex-1">
                    <strong className="text-sm">{item.name}</strong>
                    <span className="table-sub-text">{item.color} • Qty: {item.quantity}</span>
                  </div>
                  <strong className="text-sm">
                    ₹{(item.price * item.quantity).toLocaleString('en-IN')}
                  </strong>
                </div>
              ))}
            </div>

            <div className="modal-section-divider" />

            <h4 className="modal-sub-title font-serif">Update Fulfillment Status</h4>
            <div className="status-action-buttons">
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={() => handleUpdateStatus(selectedOrder.id, 'Processing')}
              >
                Mark Processing
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={() => handleUpdateStatus(selectedOrder.id, 'Shipped')}
              >
                Mark Shipped
              </button>
              <button
                type="button"
                className="btn btn-olive btn-sm"
                onClick={() => handleUpdateStatus(selectedOrder.id, 'Delivered')}
              >
                Mark Delivered
              </button>
            </div>
          </div>
        )}
      </AdminModal>
    </div>
  );
};
