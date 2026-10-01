import React, { useRef } from 'react';
import { useStore } from '../../context/StoreContext';
import { AdminStat } from '../../components/admin/AdminStat';
import { AdminStatusBadge } from '../../components/admin/AdminStatusBadge';
import { useGsapContext, staggerFadeInUp, fadeInUp } from '../../animations';
import { MOCK_SALES_OVERVIEW } from '../../data/mockData';
import {
  TrendingUp,
  ShoppingBag,
  Users,
  Package,
  ArrowRight,
  Sparkles,
  AlertTriangle,
  ArrowUpRight,
} from 'lucide-react';
import type { Product } from '../../types';

interface AdminDashboardProps {
  setActiveTab: (tab: string) => void;
  productsList?: Product[];
}

export const AdminDashboard: React.FC<AdminDashboardProps> = ({
  setActiveTab,
  productsList = [],
}) => {
  const { orders, navigate } = useStore();
  const pageRef = useRef<HTMLDivElement>(null);

  const lowStockCount = productsList.filter((p) => p.stock <= 10).length;

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      staggerFadeInUp('.admin-stats-grid .admin-stat-card', {
        trigger: '.admin-stats-grid',
        start: 'top 90%',
        stagger: 0.06,
      });

      staggerFadeInUp('.dashboard-split-grid .dashboard-panel', {
        trigger: '.dashboard-split-grid',
        start: 'top 85%',
        stagger: 0.08,
      });

      fadeInUp('.dashboard-panel:last-child', {
        trigger: '.dashboard-panel:last-child',
        start: 'top 85%',
        delay: 0.1,
      });
    },
    pageRef,
    [productsList.length, orders.length]
  );

  return (
    <div className="admin-page-content" ref={pageRef}>
      {/* Top Quick Stats Grid */}
      <div className="admin-stats-grid">
        <AdminStat
          icon={TrendingUp}
          title="Monthly Revenue"
          value={MOCK_SALES_OVERVIEW.monthlyRevenue}
          change={MOCK_SALES_OVERVIEW.monthlyGrowth}
          subtitle="vs last month"
          isPositive={true}
        />
        <AdminStat
          icon={ShoppingBag}
          title="Total Orders"
          value={MOCK_SALES_OVERVIEW.ordersThisMonth.toLocaleString()}
          change={MOCK_SALES_OVERVIEW.ordersGrowth}
          subtitle="vs last month"
          isPositive={true}
        />
        <AdminStat
          icon={Users}
          title="Active Customers"
          value={MOCK_SALES_OVERVIEW.activeCustomers.toLocaleString()}
          change={MOCK_SALES_OVERVIEW.customersGrowth}
          subtitle="verified buyers"
          isPositive={true}
        />
        <AdminStat
          icon={Package}
          title="Active Products"
          value={`${productsList.length} SKUs`}
          change={lowStockCount > 0 ? `${lowStockCount} Low Stock` : 'Stock Healthy'}
          subtitle="in live catalog"
          isPositive={lowStockCount === 0}
        />
      </div>

      {/* Main Split: Sales Breakdown & Operational Alerts */}
      <div className="dashboard-split-grid">
        {/* Sales by Category & Insights */}
        <div className="dashboard-panel">
          <div className="panel-header">
            <div>
              <h3 className="panel-title font-serif">Category Sales Distribution</h3>
              <p className="panel-subtitle">Revenue contribution across handcrafted silhouettes</p>
            </div>
            <button className="panel-text-link" onClick={() => setActiveTab('products')}>
              View Catalog <ArrowRight size={14} />
            </button>
          </div>

          <div className="category-bars-list">
            {MOCK_SALES_OVERVIEW.categoryBreakdown.map((cat, idx) => (
              <div key={idx} className="category-bar-row">
                <div className="category-bar-info">
                  <span className="category-bar-name">{cat.category}</span>
                  <span className="category-bar-rev">
                    {cat.revenue} ({cat.percent}%)
                  </span>
                </div>
                <div className="category-bar-track">
                  <div
                    className="category-bar-fill"
                    style={{ width: `${cat.percent}%` }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Operational Highlights / Quick Actions */}
        <div className="dashboard-panel">
          <div className="panel-header">
            <div>
              <h3 className="panel-title font-serif">Fulfillment & Studio Priorities</h3>
              <p className="panel-subtitle">Live action items requiring attention</p>
            </div>
          </div>

          <div className="priority-cards-list">
            <div className="priority-card" onClick={() => setActiveTab('orders')}>
              <div className="priority-icon info-icon">
                <ShoppingBag size={18} />
              </div>
              <div className="priority-text">
                <span className="priority-title">Orders Ready for Courier Dispatch</span>
                <span className="priority-desc">2 customer orders waiting for Bluedart pickup scan</span>
              </div>
              <ArrowUpRight size={16} className="priority-arrow" />
            </div>

            <div className="priority-card" onClick={() => setActiveTab('inventory')}>
              <div className="priority-icon warning-icon">
                <AlertTriangle size={18} />
              </div>
              <div className="priority-text">
                <span className="priority-title">Inventory Restock Recommendations</span>
                <span className="priority-desc">The Nomad Backpack (Olive) running below 10 units</span>
              </div>
              <ArrowUpRight size={16} className="priority-arrow" />
            </div>

            <div className="priority-card" onClick={() => setActiveTab('reviews')}>
              <div className="priority-icon success-icon">
                <Sparkles size={18} />
              </div>
              <div className="priority-text">
                <span className="priority-title">New Verified Customer Reviews</span>
                <span className="priority-desc">3 new 5-star customer testimonials published</span>
              </div>
              <ArrowUpRight size={16} className="priority-arrow" />
            </div>
          </div>
        </div>
      </div>

      {/* Recent Orders Table */}
      <div className="dashboard-panel">
        <div className="panel-header">
          <div>
            <h3 className="panel-title font-serif">Recent Customer Orders</h3>
            <p className="panel-subtitle">Showing latest customer consignments placed on RÓRA</p>
          </div>
          <button className="panel-text-link" onClick={() => setActiveTab('orders')}>
            View All Orders ({orders.length}) <ArrowRight size={14} />
          </button>
        </div>

        <div className="admin-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Order Number</th>
                <th>Date</th>
                <th>Recipient</th>
                <th>Items Ordered</th>
                <th>Total (INR)</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {orders.slice(0, 5).map((order) => (
                <tr key={order.id}>
                  <td>
                    <span className="order-number-text">{order.orderNumber}</span>
                  </td>
                  <td>{order.date}</td>
                  <td>
                    <strong>{order.shippingAddress?.fullName || 'Customer'}</strong>
                    <span className="table-sub-text">{order.shippingAddress?.city || 'India'}</span>
                  </td>
                  <td>
                    {order.items?.map((i) => i.name).join(', ') || 'Custom Collection'}
                  </td>
                  <td>
                    <strong className="order-price-text">₹{order.total?.toLocaleString('en-IN')}</strong>
                  </td>
                  <td>
                    <AdminStatusBadge status={order.status} />
                  </td>
                  <td>
                    <button
                      className="table-action-btn"
                      onClick={() => navigate('order-details', { orderId: order.id })}
                    >
                      Inspect
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
