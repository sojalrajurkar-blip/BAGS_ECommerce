'use client';

import React, { useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { useGsapContext, staggerFadeInUp, fadeInUp } from '../animations';
import { CheckCircle2, ArrowRight, Package, Truck, Calendar, MapPin } from 'lucide-react';
import type { Order } from '../types';

export const OrderConfirmationPage: React.FC = () => {
  const { currentRoute, latestOrder, navigate } = useStore();
  const pageRef = useRef<HTMLDivElement>(null);
  const order = (currentRoute.params?.order as Order) || latestOrder || {
    id: 'ord-fallback',
    orderNumber: '#RRA94201',
    date: 'April 30, 2026',
    customer: {
      id: 'cust-sarah',
      name: 'Sarah Johnson',
      email: 'sarah.johnson@example.com',
      ordersCount: 1,
      totalSpent: 338,
      joinedDate: 'Jan 2026',
    },
    total: 338,
    subtotal: 338,
    discount: 0,
    shipping: 0,
    tax: 0,
    status: 'Confirmed & In Production',
    paymentMethod: 'Visa ending in 4242',
    paymentStatus: 'Captured',
    items: [
      {
        id: 'item-1',
        productId: 'prod-1',
        name: 'The Nomad Backpack',
        color: 'Olive Green',
        price: 189,
        quantity: 1,
        image: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=600&q=80',
      },
    ],
    shippingAddress: {
      fullName: 'Sarah Johnson',
      street: '142 Mercer Street, Apt 4B',
      city: 'New York',
      state: 'NY',
      postalCode: '10012',
      country: 'USA',
    },
  };

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      fadeInUp('.confirm-icon-wrap', {
        trigger: '.confirm-card',
        start: 'top 90%',
      });

      fadeInUp('.confirm-title', {
        trigger: '.confirm-card',
        start: 'top 90%',
        delay: 0.1,
      });

      staggerFadeInUp('.confirm-meta-col', {
        trigger: '.confirm-meta-grid',
        start: 'top 85%',
        stagger: 0.08,
      });

      fadeInUp('.confirm-items-list', {
        trigger: '.confirm-items-list',
        start: 'top 85%',
        delay: 0.1,
      });

      fadeInUp('.confirm-cta-row', {
        trigger: '.confirm-cta-row',
        start: 'top 85%',
        delay: 0.15,
      });
    },
    pageRef,
    []
  );

  return (
    <div className="order-confirm-page container section-sm" ref={pageRef}>
      <div className="confirm-card">
        {/* Success Icon & Header */}
        <div className="confirm-icon-wrap">
          <CheckCircle2 size={48} className="confirm-check-icon" strokeWidth={1.5} />
        </div>
        <span className="section-eyebrow">Thank You For Your Order</span>
        <h1 className="confirm-title font-serif">Your Journey Begins Here</h1>
        <p className="confirm-subtitle">
          We have received your order <strong>{order.orderNumber}</strong>. A confirmation email with tracking details has been sent to your inbox.
        </p>

        {/* Order Details Grid */}
        <div className="confirm-details-box">
          <div className="confirm-meta-grid">
            <div className="confirm-meta-col">
              <span className="confirm-meta-label">
                <Package size={14} /> Order Number
              </span>
              <span className="confirm-meta-val">{order.orderNumber}</span>
            </div>
            <div className="confirm-meta-col">
              <span className="confirm-meta-label">
                <Calendar size={14} /> Order Date
              </span>
              <span className="confirm-meta-val">{order.date}</span>
            </div>
            <div className="confirm-meta-col">
              <span className="confirm-meta-label">
                <Truck size={14} /> Estimated Arrival
              </span>
              <span className="confirm-meta-val">3–5 Business Days</span>
            </div>
            <div className="confirm-meta-col">
              <span className="confirm-meta-label">
                <MapPin size={14} /> Shipping To
              </span>
              <span className="confirm-meta-val">{order.shippingAddress?.fullName || 'Sarah Johnson'}</span>
            </div>
          </div>

          <div className="divider my-20" />

          {/* Ordered Line Items */}
          <div className="confirm-items-list">
            <h4 className="confirm-items-heading">Ordered Items</h4>
            {order.items?.map((item, index) => (
              <div key={index} className="confirm-item-row">
                <div className="confirm-item-img">
                  <img src={item.image} alt={item.name} />
                </div>
                <div className="confirm-item-info">
                  <h5 className="confirm-item-name">{item.name}</h5>
                  <span className="confirm-item-color">
                    Color: {item.color} — Qty: {item.quantity}
                  </span>
                </div>
                <span className="confirm-item-price">₹{(item.price * item.quantity).toLocaleString('en-IN')}</span>
              </div>
            ))}
          </div>

          <div className="divider my-20" />

          <div className="confirm-total-row">
            <span>Total Paid</span>
            <span className="confirm-total-amount">₹{Number(order.total).toLocaleString('en-IN')}</span>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="confirm-cta-row">
          <button
            className="btn btn-primary"
            onClick={() => navigate('orders')}
          >
            Track Your Order <ArrowRight size={16} />
          </button>
          <button
            className="btn btn-secondary"
            onClick={() => navigate('shop')}
          >
            Continue Shopping
          </button>
        </div>
      </div>
    </div>
  );
};
