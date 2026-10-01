'use client';

import React, { useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, staggerFadeInUp, fadeInUp } from '../animations';
import { RefreshCw, Clock, ArrowRight, ShieldCheck, FileText } from 'lucide-react';

export const ReturnsPage: React.FC = () => {
  const { navigate } = useStore();
  const pageRef = useRef<HTMLDivElement>(null);

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.returns-page .breadcrumbs-nav',
        eyebrow: '.returns-header .section-eyebrow',
        title: '.returns-header .returns-title',
        subtitle: '.returns-header .returns-subtitle',
      });

      staggerFadeInUp('.returns-steps-grid .return-step-card', {
        trigger: '.returns-steps-grid',
        start: 'top 85%',
        stagger: 0.08,
      });

      fadeInUp('.returns-action-box', {
        trigger: '.returns-action-box',
        start: 'top 85%',
        delay: 0.1,
      });
    },
    pageRef,
    []
  );

  return (
    <div className="returns-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'Returns & Exchanges' }]} />

      <div className="returns-header">
        <span className="section-eyebrow">Thoughtful Guarantee</span>
        <h1 className="returns-title font-serif">Returns & Exchanges</h1>
        <p className="returns-subtitle">
          We want you to love your purchase. If something is not right, our straightforward 4-step process ensures a seamless return.
        </p>
      </div>

      {/* 4 Clean Steps Grid */}
      <div className="returns-steps-grid">
        <div className="return-step-card">
          <div className="step-badge-num">01</div>
          <RefreshCw size={24} className="step-card-icon" />
          <h3 className="font-serif step-card-title">30-Day Policy</h3>
          <p className="step-card-desc">
            You can return unused items within 30 days of delivery for a full refund. Items must be in original condition with tags attached.
          </p>
        </div>

        <div className="return-step-card">
          <div className="step-badge-num">02</div>
          <Clock size={24} className="step-card-icon" />
          <h3 className="font-serif step-card-title">Prompt Refunds</h3>
          <p className="step-card-desc">
            Once our European workshop receives your returned package, your refund is processed to your original payment method within 3–5 business days.
          </p>
        </div>

        <div className="return-step-card">
          <div className="step-badge-num">03</div>
          <ShieldCheck size={24} className="step-card-icon" />
          <h3 className="font-serif step-card-title">Seamless Exchanges</h3>
          <p className="step-card-desc">
            Need a different color or size? We offer complimentary exchanges on all eligible bags with instant inventory reservation.
          </p>
        </div>

        <div className="return-step-card">
          <div className="step-badge-num">04</div>
          <FileText size={24} className="step-card-icon" />
          <h3 className="font-serif step-card-title">How to Start</h3>
          <p className="step-card-desc">
            Log in to your Account, visit My Orders, and click "Request Return" to generate your pre-paid printable shipping label.
          </p>
        </div>
      </div>

      {/* Action Banner */}
      <div className="returns-action-box">
        <div>
          <h3 className="font-serif returns-action-title">Ready to initiate a return?</h3>
          <p className="returns-action-desc">
            Have your order number (e.g. #RRA89241) and postal code ready.
          </p>
        </div>
        <button className="btn btn-primary" onClick={() => navigate('orders')}>
          Start a Return in Account <ArrowRight size={16} />
        </button>
      </div>
    </div>
  );
};
