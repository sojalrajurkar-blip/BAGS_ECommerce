'use client';

import React, { useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Order } from '../types/domain';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, staggerFadeInUp, fadeInUp } from '../animations';
import { CheckCircle2, Clock, Truck, Package, MapPin, RefreshCw, ArrowLeft } from 'lucide-react';

export interface OrderTrackingPageProps {
  orderId?: string;
}

export const OrderTrackingPage: React.FC<OrderTrackingPageProps> = ({ orderId: propOrderId } = {}) => {
  const { currentRoute, orders, navigate, addToast } = useStore();
  const orderId = propOrderId || currentRoute.params?.orderId;
  const order = orders.find((o: Order) => o.id === orderId) || orders[0];
  const pageRef = useRef<HTMLDivElement>(null);

  useGsapContext((self, isReduced) => {
    if (isReduced) return;

    revealPageHeader({
      breadcrumbs: '.tracking-page .breadcrumbs-nav',
      eyebrow: '.tracking-header .section-eyebrow',
      title: '.tracking-header .tracking-title',
      subtitle: '.tracking-header .tracking-date',
    });

    staggerFadeInUp('.tracking-timeline-flow .timeline-node', {
      trigger: '.timeline-section',
      start: 'top 85%',
      stagger: 0.1,
    });

    fadeInUp('.tracking-items-section', {
      trigger: '.tracking-items-section',
      start: 'top 85%',
      delay: 0.1,
    });

    staggerFadeInUp('.tracking-sidebar .sidebar-card', {
      trigger: '.tracking-sidebar',
      start: 'top 85%',
      stagger: 0.1,
    });
  }, pageRef, [orderId]);

  if (!order) return null;

  return (
    <div className="tracking-page container section-sm" ref={pageRef}>
      <Breadcrumbs
        items={[
          { label: 'Account', page: 'account' },
          { label: 'Orders', page: 'orders' },
          { label: order ? `Order ${order.orderNumber}` : 'Tracking' }
        ]}
      />

      <div className="tracking-layout">
        <main className="tracking-main-card">
          {/* Header */}
          <div className="tracking-header">
            <div>
              <span className="section-eyebrow">Real-Time Dispatch Tracking</span>
              <h1 className="tracking-title font-serif">{order.orderNumber}</h1>
              <span className="tracking-date">Ordered on {order.date}</span>
            </div>
            <span className="badge badge-olive tracking-status-badge">
              {order.status}
            </span>
          </div>

          <div className="divider my-20" />

          {/* Visual Tracking Progress Timeline */}
          <div className="timeline-section">
            <h3 className="timeline-heading font-serif">Shipment Progress</h3>
            <div className="tracking-timeline-flow">
              {(order.timeline || []).map((step, idx: number) => (
                <div key={idx} className={`timeline-node ${step.completed ? 'node-done' : 'node-pending'}`}>
                  <div className="node-marker">
                    {step.completed ? (
                      <CheckCircle2 size={18} className="node-check-icon" />
                    ) : (
                      <Clock size={16} className="node-pending-icon" />
                    )}
                  </div>
                  <div className="node-info">
                    <span className="node-step-name">{step.step}</span>
                    <span className="node-step-time">{step.time}</span>
                  </div>
                  {idx < (order.timeline || []).length - 1 && <div className="node-bar" />}
                </div>
              ))}
            </div>
          </div>

          <div className="divider my-24" />

          {/* Ordered Line Items */}
          <div className="tracking-items-section">
            <h3 className="font-serif timeline-heading">Items in this Package</h3>
            <div className="tracking-items-list">
              {(order.items || []).map((item, idx: number) => (
                <div key={idx} className="tracking-item-row">
                  <div className="tracking-img-wrap">
                    <img src={item.image} alt={item.name} />
                  </div>
                  <div className="tracking-item-meta">
                    <h4 className="tracking-item-name">{item.name}</h4>
                    <span className="tracking-item-variant">Color: {item.color} — Qty: {item.quantity}</span>
                  </div>
                  <span className="tracking-item-price">₹{(item.price * item.quantity).toLocaleString('en-IN')}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="tracking-bottom-actions">
            <button className="btn btn-secondary btn-sm" onClick={() => navigate('orders')}>
              <ArrowLeft size={14} /> Back to Orders
            </button>
            <button
              className="btn btn-secondary btn-sm"
              onClick={() => {
                addToast('Return & exchange request submitted.');
                navigate('returns');
              }}
            >
              <RefreshCw size={14} /> Request Return or Exchange
            </button>
          </div>
        </main>

        {/* Right Info Box */}
        <aside className="tracking-sidebar">
          <div className="sidebar-card">
            <h4 className="sidebar-heading font-serif">Delivery Address</h4>
            <div className="sidebar-address-info">
              <MapPin size={16} className="sidebar-icon" />
              <div>
                <strong>{order.shippingAddress?.fullName || 'Sarah Johnson'}</strong>
                <p>
                  {order.shippingAddress?.street || '142 Mercer Street, Apt 4B'}<br />
                  {order.shippingAddress?.city || 'New York'}, {order.shippingAddress?.state || 'NY'} {order.shippingAddress?.postalCode || '10012'}<br />
                  {order.shippingAddress?.country || 'United States'}
                </p>
              </div>
            </div>
          </div>

          <div className="sidebar-card">
            <h4 className="sidebar-heading font-serif">Courier Details</h4>
            <p className="courier-text">
              <strong>Carrier:</strong> DHL Express Carbon-Neutral<br />
              <strong>Tracking ID:</strong> DHL-994827103-US<br />
              <strong>Service:</strong> Doorstep Signature Required
            </p>
          </div>
        </aside>
      </div>
    </div>
  );
};
