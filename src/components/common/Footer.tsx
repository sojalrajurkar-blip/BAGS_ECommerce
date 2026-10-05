'use client';

import React, { useState } from 'react';
import { usePathname } from 'next/navigation';
import { useStore } from '../../context/StoreContext';
import { ArrowRight, ShieldCheck, RefreshCw, Truck, Award } from 'lucide-react';

export const Footer: React.FC = () => {
  const pathname = usePathname();
  const { navigate, currentRoute, addToast } = useStore();
  const [email, setEmail] = useState('');

  if (pathname?.startsWith('/admin') || currentRoute?.page === 'admin') {
    return null;
  }

  const handleSubscribe = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email) return;
    addToast('Thank you for subscribing to the RÓRA Journal.');
    setEmail('');
  };

  return (
    <footer className="site-footer">
      {/* Trust & Service Features Strip */}
      <section className="footer-trust-strip">
        <div className="container">
          <div className="trust-grid">
            <div className="trust-item">
              <Truck size={22} className="trust-icon" strokeWidth={1.5} />
              <div>
                <h4 className="trust-title">Complimentary Shipping</h4>
                <p className="trust-desc">On all domestic orders over ₹1,999</p>
              </div>
            </div>
            <div className="trust-item">
              <RefreshCw size={22} className="trust-icon" strokeWidth={1.5} />
              <div>
                <h4 className="trust-title">30-Day Thoughtful Returns</h4>
                <p className="trust-desc">Pre-paid return labels included</p>
              </div>
            </div>
            <div className="trust-item">
              <ShieldCheck size={22} className="trust-icon" strokeWidth={1.5} />
              <div>
                <h4 className="trust-title">Lifetime Guarantee</h4>
                <p className="trust-desc">Repairs and craftsmanship warranty</p>
              </div>
            </div>
            <div className="trust-item">
              <Award size={22} className="trust-icon" strokeWidth={1.5} />
              <div>
                <h4 className="trust-title">Sustainable Materials</h4>
                <p className="trust-desc">Certified organic and recycled textiles</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Main Footer Links & Newsletter */}
      <div className="container footer-main">
        <div className="footer-columns">
          {/* Brand Col */}
          <div className="footer-col footer-col-brand">
            <span className="footer-brand-logo">RÓRA</span>
            <p className="footer-brand-desc">
              Thoughtfully engineered bags and everyday accessories crafted with architectural precision, certified materials, and timeless restraint.
            </p>
            <div className="footer-copyright">
              © {new Date().getFullYear()} RÓRA Studios. All rights reserved.
            </div>
          </div>

          {/* Shop Col */}
          <div className="footer-col">
            <h5 className="footer-heading">Collection</h5>
            <ul className="footer-list">
              <li><button onClick={() => navigate('category', { categoryId: 'backpacks' })}>Backpacks</button></li>
              <li><button onClick={() => navigate('category', { categoryId: 'laptop-bags' })}>Laptop Bags</button></li>
              <li><button onClick={() => navigate('category', { categoryId: 'handbags' })}>Handbags</button></li>
              <li><button onClick={() => navigate('category', { categoryId: 'sling-bags' })}>Sling Bags</button></li>
              <li><button onClick={() => navigate('category', { categoryId: 'travel-bags' })}>Travel Bags</button></li>
              <li><button onClick={() => navigate('category', { categoryId: 'tote-bags' })}>Tote Bags</button></li>
              <li><button onClick={() => navigate('shop')}>All Products</button></li>
            </ul>
          </div>

          {/* About / Brand Col */}
          <div className="footer-col">
            <h5 className="footer-heading">Brand</h5>
            <ul className="footer-list">
              <li><button onClick={() => navigate('about')}>Our Story</button></li>
              <li><button onClick={() => navigate('journal')}>Journal</button></li>
              <li><button onClick={() => navigate('about')}>Sustainability & Materials</button></li>
              <li><button onClick={() => navigate('faq')}>Frequently Asked Questions</button></li>
              <li><button onClick={() => navigate('contact')}>Get in Touch</button></li>
              <li><button onClick={() => navigate('admin')}>Admin Portal</button></li>
            </ul>
          </div>

          {/* Client Concierge */}
          <div className="footer-col">
            <h5 className="footer-heading">Concierge</h5>
            <ul className="footer-list">
              <li><button onClick={() => navigate('account')}>Customer Account</button></li>
              <li><button onClick={() => navigate('orders')}>Track Order</button></li>
              <li><button onClick={() => navigate('returns')}>Returns & Exchanges</button></li>
              <li><button onClick={() => navigate('contact')}>Bespoke & Corporate</button></li>
              <li><button onClick={() => navigate('shipping')}>Shipping & Delivery</button></li>
            </ul>
          </div>

          {/* Newsletter Subscription */}
          <div className="footer-col footer-col-newsletter">
            <h5 className="footer-heading">Stay Inspired</h5>
            <p className="footer-newsletter-desc">
              Subscribe for new collection releases, craftsmanship stories, and private journal dispatches.
            </p>
            <form onSubmit={handleSubscribe} className="footer-newsletter-form">
              <input
                type="email"
                placeholder="Your email address"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="footer-newsletter-input"
              />
              <button type="submit" className="footer-newsletter-btn" aria-label="Subscribe">
                <ArrowRight size={18} />
              </button>
            </form>
            <div className="footer-badges">
              <span className="footer-badge-item">Visa</span>
              <span className="footer-badge-item">Mastercard</span>
              <span className="footer-badge-item">Apple Pay</span>
              <span className="footer-badge-item">PayPal</span>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};
