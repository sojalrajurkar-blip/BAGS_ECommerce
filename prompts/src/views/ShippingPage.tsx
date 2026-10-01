'use client';

import React, { useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, staggerFadeInUp, fadeInUp } from '../animations';
import {
  Truck,
  Clock,
  ShieldCheck,
  Globe,
  Package,
  HelpCircle,
  ArrowRight,
  Sparkles,
} from 'lucide-react';

export const ShippingPage: React.FC = () => {
  const { navigate } = useStore();
  const pageRef = useRef<HTMLDivElement>(null);

  const breadcrumbs = [
    { label: 'Home', page: 'home' },
    { label: 'Shipping & Delivery' },
  ];

  const shippingMethods = [
    {
      title: 'Standard Domestic Delivery',
      time: '3–5 Business Days',
      cost: 'Complimentary over ₹1,999 (or ₹199)',
      badge: 'Most Popular',
      desc: 'Dispatched via premium surface express with end-to-end SMS & email tracking updates.',
    },
    {
      title: 'Metro Express Air Delivery',
      time: '1–2 Business Days',
      cost: '₹349 flat fee',
      badge: 'Fastest',
      desc: 'Priority air courier available for major metros (Mumbai, Delhi NCR, Bengaluru, Chennai, Hyderabad, Kolkata).',
    },
    {
      title: 'International Priority Courier',
      time: '5–8 Business Days',
      cost: 'Calculated at checkout (approx. ₹2,499)',
      badge: 'Worldwide',
      desc: 'Delivered via DHL Express / FedEx International with all customs documentation handled upfront.',
    },
  ];

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.shipping-page-wrapper .breadcrumbs-nav',
        eyebrow: '.shipping-header .section-eyebrow',
        title: '.shipping-header .shipping-title',
        subtitle: '.shipping-header .shipping-subtitle',
      });

      staggerFadeInUp('.shipping-tiers-grid .shipping-tier-card', {
        trigger: '.shipping-tiers-section',
        start: 'top 85%',
        stagger: 0.1,
      });

      staggerFadeInUp('.shipping-details-grid .shipping-policy-block', {
        trigger: '.shipping-details-grid',
        start: 'top 85%',
        stagger: 0.08,
      });

      fadeInUp('.shipping-support-cta', {
        trigger: '.shipping-support-cta',
        start: 'top 85%',
        delay: 0.1,
      });
    },
    pageRef,
    []
  );

  return (
    <div className="shipping-page-wrapper" ref={pageRef}>
      <div className="container">
        <Breadcrumbs items={breadcrumbs} />

        {/* Hero Header */}
        <header className="shipping-header">
          <span className="section-eyebrow">Client Services & Logistics</span>
          <h1 className="shipping-title">Shipping & Delivery</h1>
          <p className="shipping-subtitle">
            Every RÓRA piece is inspected, wrapped in recycled archival tissue, and packed in reusable canvas storage dust bags before departure from our studio.
          </p>
        </header>

        {/* Shipping Methods Grid */}
        <section className="shipping-tiers-section">
          <div className="shipping-tiers-grid">
            {shippingMethods.map((method, idx) => (
              <div key={idx} className="shipping-tier-card">
                <div className="tier-header">
                  <div className="tier-icon-wrap">
                    {idx === 0 && <Truck size={20} />}
                    {idx === 1 && <Sparkles size={20} />}
                    {idx === 2 && <Globe size={20} />}
                  </div>
                  <span className="tier-badge">{method.badge}</span>
                </div>
                <h3 className="tier-title">{method.title}</h3>
                <div className="tier-time-row">
                  <Clock size={16} />
                  <span>{method.time}</span>
                </div>
                <div className="tier-cost">{method.cost}</div>
                <p className="tier-desc">{method.desc}</p>
              </div>
            ))}
          </div>
        </section>

        {/* Detailed Shipping Policies Grid */}
        <div className="shipping-details-grid">
          {/* Domestic Logistics */}
          <div className="shipping-policy-block">
            <div className="policy-block-icon">
              <Package size={22} />
            </div>
            <h3 className="policy-block-title">Domestic Shipping Across India</h3>
            <p className="policy-block-text">
              We deliver to over 19,000+ postal PIN codes across India via Bluedart, Delhivery, and DTDC Express. Orders placed before 2:00 PM IST Monday through Friday are processed and dispatched on the same business day. Orders placed over the weekend are dispatched the following Monday.
            </p>
            <ul className="policy-features-list">
              <li>Complimentary standard shipping on all orders ₹1,999 and above.</li>
              <li>Live OTP verification upon physical delivery for ultimate package security.</li>
              <li>Tamper-evident, sealed security packaging on all shipments.</li>
            </ul>
          </div>

          {/* International Shipping */}
          <div className="shipping-policy-block">
            <div className="policy-block-icon">
              <Globe size={22} />
            </div>
            <h3 className="policy-block-title">Worldwide Global Deliveries</h3>
            <p className="policy-block-text">
              RÓRA ships globally to over 45 countries including the United States, United Kingdom, European Union, UAE, Singapore, and Australia. International shipments are sent via DHL Express with signature required upon arrival.
            </p>
            <ul className="policy-features-list">
              <li>Real-time doorstep customs clearance handling.</li>
              <li>Estimated delivery time: 5–8 business days depending on customs destination.</li>
              <li>Import duties & local VAT are shown clearly before final payment.</li>
            </ul>
          </div>

          {/* Real-time Order Tracking */}
          <div className="shipping-policy-block">
            <div className="policy-block-icon">
              <Clock size={22} />
            </div>
            <h3 className="policy-block-title">Tracking Your Consignment</h3>
            <p className="policy-block-text">
              As soon as your shipment leaves our studio, you will receive an SMS and email notification containing your unique AWB tracking code and direct live tracking link.
            </p>
            <div className="track-cta-box">
              <span>Have an existing order number?</span>
              <button onClick={() => navigate('orders')} className="btn btn-secondary btn-sm">
                Track Your Order <ArrowRight size={14} />
              </button>
            </div>
          </div>

          {/* Delays, Lost & Damaged Packages */}
          <div className="shipping-policy-block">
            <div className="policy-block-icon">
              <ShieldCheck size={22} />
            </div>
            <h3 className="policy-block-title">Transit Protection & Damaged Goods</h3>
            <p className="policy-block-text">
              All RÓRA shipments are 100% insured against loss or transit damage during shipment. In the unlikely event that your package arrives with damaged external packaging or missing seals:
            </p>
            <ul className="policy-features-list">
              <li>Please photograph the damaged parcel before opening.</li>
              <li>Notify our concierge team at <a href="mailto:concierge@rorastudios.com">concierge@rorastudios.com</a> within 48 hours of delivery.</li>
              <li>We will arrange a complimentary immediate replacement or full refund.</li>
            </ul>
          </div>
        </div>

        {/* Concierge Support CTA Banner */}
        <section className="shipping-support-cta">
          <div className="support-cta-content">
            <div className="support-cta-icon">
              <HelpCircle size={32} />
            </div>
            <div>
              <h2 className="support-cta-heading">Need Personalized Shipping Assistance?</h2>
              <p className="support-cta-desc">
                Our client concierge team is available Monday to Saturday (9:00 AM – 7:00 PM IST) to assist with address changes, expedited delivery requests, or corporate gift dispatches.
              </p>
            </div>
          </div>
          <div className="support-cta-buttons">
            <button onClick={() => navigate('contact')} className="btn btn-primary">
              Contact Concierge <ArrowRight size={15} />
            </button>
            <button onClick={() => navigate('faq')} className="btn btn-secondary">
              Read FAQ
            </button>
          </div>
        </section>
      </div>
    </div>
  );
};
