'use client';

import React, { useState, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, staggerFadeInUp, fadeInUp } from '../animations';
import { Mail, Phone, MapPin, Clock, ArrowRight } from 'lucide-react';

export const ContactPage: React.FC = () => {
  const { addToast } = useStore();
  const pageRef = useRef<HTMLDivElement>(null);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    subject: 'General Inquiry',
    message: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    addToast('Thank you. Your message has been transmitted to our concierge team.');
    setFormData({ name: '', email: '', subject: 'General Inquiry', message: '' });
  };

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.contact-page .breadcrumbs-nav',
        eyebrow: '.contact-header .section-eyebrow',
        title: '.contact-header .contact-title',
        subtitle: '.contact-header .contact-subtitle',
      });

      staggerFadeInUp('.contact-info-block', {
        trigger: '.contact-info-panel',
        start: 'top 85%',
        stagger: 0.08,
      });

      fadeInUp('.contact-form-card', {
        trigger: '.contact-form-card',
        start: 'top 85%',
        delay: 0.1,
      });
    },
    pageRef,
    []
  );

  return (
    <div className="contact-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'Contact Us' }]} />

      <div className="contact-header">
        <span className="section-eyebrow">Client Concierge</span>
        <h1 className="contact-title font-serif">Get in Touch</h1>
        <p className="contact-subtitle">
          Whether you have a question about sizing, custom leathers, or corporate gifting, we are here to help.
        </p>
      </div>

      <div className="contact-layout">
        {/* Left Column: Direct Info */}
        <div className="contact-info-panel">
          <div className="contact-info-block">
            <Mail size={20} className="contact-info-icon" />
            <div>
              <h4 className="info-block-title">Email Inquiries</h4>
              <p className="info-block-text">hello@rorabags.com</p>
              <span className="info-block-sub">We reply within 1 business day</span>
            </div>
          </div>

          <div className="contact-info-block">
            <Phone size={20} className="contact-info-icon" />
            <div>
              <h4 className="info-block-title">Telephone Concierge</h4>
              <p className="info-block-text">+1 (800) 492-7019</p>
              <span className="info-block-sub">Mon–Fri, 9:00 AM – 6:00 PM CET</span>
            </div>
          </div>

          <div className="contact-info-block">
            <MapPin size={20} className="contact-info-icon" />
            <div>
              <h4 className="info-block-title">Design Studio & Headquarters</h4>
              <p className="info-block-text">
                RÓRA Studio A/S<br />
                Kronprinsensgade 14, 2nd Floor<br />
                1114 Copenhagen K, Denmark
              </p>
            </div>
          </div>

          <div className="contact-info-block">
            <Clock size={20} className="contact-info-icon" />
            <div>
              <h4 className="info-block-title">Flagship Showrooms</h4>
              <p className="info-block-text">
                Copenhagen • Zurich • Milan • New York
              </p>
            </div>
          </div>
        </div>

        {/* Right Column: Contact Form */}
        <div className="contact-form-card">
          <h3 className="form-card-title font-serif">Send a Message</h3>
          <form onSubmit={handleSubmit} className="contact-actual-form">
            <div className="form-group">
              <label className="form-label">Your Name *</label>
              <input
                type="text"
                required
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="form-input"
                placeholder="e.g. Marcus Vance"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Email Address *</label>
              <input
                type="email"
                required
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="form-input"
                placeholder="e.g. marcus@example.com"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Subject</label>
              <select
                value={formData.subject}
                onChange={(e) => setFormData({ ...formData, subject: e.target.value })}
                className="form-select"
              >
                <option value="General Inquiry">General Inquiry</option>
                <option value="Product Sizing & Advice">Product Sizing & Advice</option>
                <option value="Order & Tracking Question">Order & Tracking Question</option>
                <option value="Warranty & Repair">Warranty & Repair</option>
                <option value="Corporate & Bespoke Gifting">Corporate & Bespoke Gifting</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Your Message *</label>
              <textarea
                required
                rows={5}
                value={formData.message}
                onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                className="form-textarea"
                placeholder="How may our concierge assist you today?"
              />
            </div>

            <button type="submit" className="btn btn-primary btn-lg self-start">
              Transmit Message <ArrowRight size={16} />
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};
