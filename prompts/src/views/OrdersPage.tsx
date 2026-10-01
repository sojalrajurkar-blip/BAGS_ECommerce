'use client';

import React, { useState, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, staggerFadeInUp, fadeInUp } from '../animations';
import { Package, ArrowRight, Calendar } from 'lucide-react';

export const OrdersPage: React.FC = () => {
  const { orders, navigate } = useStore();
  const [filterStatus, setFilterStatus] = useState<string>('All');
  const pageRef = useRef<HTMLDivElement>(null);

  const filteredOrders =
    filterStatus === 'All'
      ? orders
      : orders.filter((o) => o.status.toLowerCase() === filterStatus.toLowerCase());

  const getStatusBadgeClass = (status: string): string => {
    switch (status.toLowerCase()) {
      case 'delivered':
        return 'badge-status-delivered';
      case 'shipped':
        return 'badge-status-shipped';
      case 'processing':
        return 'badge-status-processing';
      case 'cancelled':
        return 'badge-status-cancelled';
      default:
        return 'badge-subtle';
    }
  };

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.orders-page .breadcrumbs-nav',
        eyebrow: '.orders-header .section-eyebrow',
        title: '.orders-header .orders-title',
        subtitle: '.orders-header .orders-subtitle',
        meta: '.order-status-tabs',
      });

      if (filteredOrders.length > 0) {
        staggerFadeInUp('.orders-list .order-card', {
          trigger: '.orders-list',
          start: 'top 85%',
          stagger: 0.08,
        });
      } else {
        fadeInUp('.orders-empty-box', {
          trigger: '.orders-empty-box',
          start: 'top 85%',
        });
      }
    },
    pageRef,
    [filterStatus, filteredOrders.length]
  );

  return (
    <div className="orders-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'Account', page: 'account' }, { label: 'My Orders' }]} />

      <div className="orders-header">
        <span className="section-eyebrow">Purchase History</span>
        <h1 className="orders-title font-serif">My Orders</h1>
        <p className="orders-subtitle">
          Track packages, view receipts, and request returns or exchanges.
        </p>

        {/* Status Filter Tabs */}
        <div className="order-status-tabs">
          {['All', 'Processing', 'Shipped', 'Delivered', 'Cancelled'].map((tab) => (
            <button
              key={tab}
              className={`status-tab-btn ${filterStatus === tab ? 'tab-active' : ''}`}
              onClick={() => setFilterStatus(tab)}
            >
              {tab}
            </button>
          ))}
        </div>
      </div>

      {filteredOrders.length === 0 ? (
        <div className="orders-empty-box">
          <Package size={44} strokeWidth={1.2} className="empty-pkg-icon" />
          <h3 className="font-serif empty-title">No orders found</h3>
          <p className="empty-subtitle">You have no orders currently in "{filterStatus}" status.</p>
          <button className="btn btn-primary btn-sm" onClick={() => navigate('shop')}>
            Start Exploring
          </button>
        </div>
      ) : (
        <div className="orders-list">
          {filteredOrders.map((order) => (
            <div key={order.id} className="order-card">
              <div className="order-card-top">
                <div className="order-id-group">
                  <span className="order-number">{order.orderNumber}</span>
                  <span className="order-date-text">
                    <Calendar size={13} /> Placed on {order.date}
                  </span>
                </div>

                <div className="order-status-group">
                  <span className={`badge ${getStatusBadgeClass(order.status)}`}>
                    {order.status}
                  </span>
                  <span className="order-total-price">₹{Number(order.total).toLocaleString('en-IN')}</span>
                </div>
              </div>

              <div className="divider my-14" />

              <div className="order-card-body">
                {/* Thumbnails of items */}
                <div className="order-thumbs-row">
                  {order.items?.map((item, idx) => (
                    <div key={idx} className="order-thumb-wrap" title={`${item.name} (${item.color})`}>
                      <img src={item.image} alt={item.name} />
                      {item.quantity > 1 && (
                        <span className="order-thumb-qty">×{item.quantity}</span>
                      )}
                    </div>
                  ))}
                  <div className="order-item-count-label">
                    <span>
                      {order.items?.length} {order.items?.length === 1 ? 'item' : 'items'}
                    </span>
                  </div>
                </div>

                <div className="order-card-action">
                  <button
                    className="btn btn-secondary btn-sm"
                    onClick={() => navigate('order-details', { orderId: order.id })}
                  >
                    View Details & Tracking <ArrowRight size={14} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
