'use client';

import React, { useState, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, fadeInUp } from '../animations';
import { Check, ShieldCheck, Truck, CreditCard, Lock, ArrowRight, ArrowLeft } from 'lucide-react';

interface CheckoutFormData {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  shippingMethod: 'standard' | 'express';
  paymentMethod: 'upi' | 'card' | 'netbanking' | 'applepay' | 'paypal';
  cardNumber: string;
  cardExpiry: string;
  cardCvc: string;
  saveInfo: boolean;
}

export const CheckoutPage: React.FC = () => {
  const {
    cart,
    cartSubtotal,
    discountAmount,
    cartTotal,
    placeOrder,
    navigate,
  } = useStore();

  const [step, setStep] = useState<number>(1); // 1: Shipping, 2: Payment
  const pageRef = useRef<HTMLDivElement>(null);

  // Form State
  const [formData, setFormData] = useState<CheckoutFormData>({
    firstName: 'Sarah',
    lastName: 'Johnson',
    email: 'sarah.johnson@example.com',
    phone: '+91 98765 43210',
    address: '142 Bandra West, Hill Road',
    city: 'Mumbai',
    state: 'Maharashtra',
    zipCode: '400050',
    country: 'India',
    shippingMethod: 'standard',
    paymentMethod: 'upi',
    cardNumber: '4242 •••• •••• 4242',
    cardExpiry: '08/28',
    cardCvc: '888',
    saveInfo: true,
  });

  const [errors, setErrors] = useState<Record<string, string | null>>({});

  const handleChange = (field: keyof CheckoutFormData, value: unknown) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: null }));
    }
  };

  const validateShipping = (): boolean => {
    const errs: Record<string, string> = {};
    if (!formData.firstName.trim()) errs.firstName = 'Required';
    if (!formData.lastName.trim()) errs.lastName = 'Required';
    if (!formData.email.trim()) errs.email = 'Required';
    if (!formData.address.trim()) errs.address = 'Required';
    if (!formData.city.trim()) errs.city = 'Required';
    if (!formData.zipCode.trim()) errs.zipCode = 'Required';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleNextToPayment = (e: React.FormEvent) => {
    e.preventDefault();
    if (validateShipping()) {
      setStep(2);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  const handleCompleteOrder = (e: React.FormEvent) => {
    e.preventDefault();
    placeOrder({
      shippingAddress: {
        fullName: `${formData.firstName} ${formData.lastName}`,
        street: formData.address,
        city: formData.city,
        state: formData.state,
        postalCode: formData.zipCode,
        country: formData.country,
      },
      paymentMethod:
        formData.paymentMethod === 'applepay'
          ? 'Apple Pay'
          : formData.paymentMethod === 'paypal'
          ? 'PayPal'
          : formData.paymentMethod === 'upi'
          ? 'UPI (sarah@okaxis)'
          : `Visa ending in 4242`,
    });
  };

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.checkout-page .breadcrumbs-nav',
      });

      fadeInUp('.checkout-stepper', {
        trigger: '.checkout-stepper',
        start: 'top 90%',
      });

      fadeInUp('.checkout-forms-column', {
        trigger: '.checkout-layout',
        start: 'top 85%',
      });

      fadeInUp('.checkout-summary-column', {
        trigger: '.checkout-layout',
        start: 'top 85%',
        delay: 0.1,
      });
    },
    pageRef,
    [step, cart.length]
  );

  if (cart.length === 0) {
    return (
      <div className="container section-md text-center">
        <h2 className="font-serif section-title">Your shopping bag is empty</h2>
        <p className="mb-24">Please add products to your bag before proceeding to checkout.</p>
        <button className="btn btn-primary" onClick={() => navigate('shop')}>
          Return to Shop
        </button>
      </div>
    );
  }

  return (
    <div className="checkout-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'Bag', page: 'cart' }, { label: 'Checkout' }]} />

      {/* Checkout Step Progress Bar */}
      <div className="checkout-stepper">
        <div className={`step-node ${step >= 1 ? 'step-active' : ''} ${step > 1 ? 'step-done' : ''}`}>
          <span className="step-circle">{step > 1 ? <Check size={14} /> : '1'}</span>
          <span className="step-label">Shipping</span>
        </div>
        <div className="step-connector" />
        <div className={`step-node ${step >= 2 ? 'step-active' : ''}`}>
          <span className="step-circle">2</span>
          <span className="step-label">Payment</span>
        </div>
        <div className="step-connector" />
        <div className="step-node">
          <span className="step-circle">3</span>
          <span className="step-label">Confirmation</span>
        </div>
      </div>

      <div className="checkout-layout">
        {/* Left Column: Multi-Step Forms */}
        <div className="checkout-forms-column">
          {step === 1 && (
            <form onSubmit={handleNextToPayment} className="checkout-form">
              {/* Contact Information */}
              <div className="form-section-card">
                <h3 className="form-section-title font-serif">1. Contact Information</h3>
                <div className="form-grid-2">
                  <div className="form-group">
                    <label className="form-label">Email Address *</label>
                    <input
                      type="email"
                      value={formData.email}
                      onChange={(e) => handleChange('email', e.target.value)}
                      className="form-input"
                    />
                    {errors.email && <span className="error-text">{errors.email}</span>}
                  </div>
                  <div className="form-group">
                    <label className="form-label">Phone Number *</label>
                    <input
                      type="tel"
                      value={formData.phone}
                      onChange={(e) => handleChange('phone', e.target.value)}
                      className="form-input"
                    />
                  </div>
                </div>
              </div>

              {/* Delivery Address */}
              <div className="form-section-card">
                <h3 className="form-section-title font-serif">2. Shipping Address</h3>
                <div className="form-grid-2">
                  <div className="form-group">
                    <label className="form-label">First Name *</label>
                    <input
                      type="text"
                      value={formData.firstName}
                      onChange={(e) => handleChange('firstName', e.target.value)}
                      className="form-input"
                    />
                    {errors.firstName && <span className="error-text">{errors.firstName}</span>}
                  </div>
                  <div className="form-group">
                    <label className="form-label">Last Name *</label>
                    <input
                      type="text"
                      value={formData.lastName}
                      onChange={(e) => handleChange('lastName', e.target.value)}
                      className="form-input"
                    />
                    {errors.lastName && <span className="error-text">{errors.lastName}</span>}
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Street Address *</label>
                  <input
                    type="text"
                    value={formData.address}
                    onChange={(e) => handleChange('address', e.target.value)}
                    className="form-input"
                    placeholder="House number and street name"
                  />
                  {errors.address && <span className="error-text">{errors.address}</span>}
                </div>

                <div className="form-grid-3">
                  <div className="form-group">
                    <label className="form-label">City *</label>
                    <input
                      type="text"
                      value={formData.city}
                      onChange={(e) => handleChange('city', e.target.value)}
                      className="form-input"
                    />
                    {errors.city && <span className="error-text">{errors.city}</span>}
                  </div>
                  <div className="form-group">
                    <label className="form-label">State / Province</label>
                    <input
                      type="text"
                      value={formData.state}
                      onChange={(e) => handleChange('state', e.target.value)}
                      className="form-input"
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">ZIP / Postal Code *</label>
                    <input
                      type="text"
                      value={formData.zipCode}
                      onChange={(e) => handleChange('zipCode', e.target.value)}
                      className="form-input"
                    />
                    {errors.zipCode && <span className="error-text">{errors.zipCode}</span>}
                  </div>
                </div>
              </div>

              {/* Shipping Method Selection */}
              <div className="form-section-card">
                <h3 className="form-section-title font-serif">3. Delivery Method</h3>
                <div className="shipping-options-list">
                  <label
                    className={`shipping-option-card ${formData.shippingMethod === 'standard' ? 'option-selected' : ''}`}
                    onClick={() => handleChange('shippingMethod', 'standard')}
                  >
                    <input
                      type="radio"
                      name="shipping"
                      checked={formData.shippingMethod === 'standard'}
                      onChange={() => {}}
                    />
                    <div className="shipping-option-meta">
                      <span className="shipping-option-name">Standard Tracked Delivery (3–5 business days)</span>
                      <span className="shipping-option-desc">Dispatched in 100% biodegradable carbon-neutral box</span>
                    </div>
                    <span className="shipping-option-price">
                      {cartSubtotal >= 1999 ? 'Free' : '₹199'}
                    </span>
                  </label>

                  <label
                    className={`shipping-option-card ${formData.shippingMethod === 'express' ? 'option-selected' : ''}`}
                    onClick={() => handleChange('shippingMethod', 'express')}
                  >
                    <input
                      type="radio"
                      name="shipping"
                      checked={formData.shippingMethod === 'express'}
                      onChange={() => {}}
                    />
                    <div className="shipping-option-meta">
                      <span className="shipping-option-name">Priority Express (1–2 business days)</span>
                      <span className="shipping-option-desc">Next-flight courier with door signature verification</span>
                    </div>
                    <span className="shipping-option-price">₹199</span>
                  </label>
                </div>
              </div>

              <div className="checkout-btn-row">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => navigate('cart')}
                >
                  <ArrowLeft size={16} /> Return to Bag
                </button>
                <button type="submit" className="btn btn-primary btn-lg">
                  Continue to Payment <ArrowRight size={16} />
                </button>
              </div>
            </form>
          )}

          {step === 2 && (
            <form onSubmit={handleCompleteOrder} className="checkout-form">
              {/* Review Shipping Summary */}
              <div className="form-section-card">
                <div className="review-shipping-header">
                  <div>
                    <span className="form-label">Deliver To</span>
                    <p className="review-address-text">
                      <strong>{formData.firstName} {formData.lastName}</strong> — {formData.address}, {formData.city}, {formData.state} {formData.zipCode}
                    </p>
                  </div>
                  <button type="button" className="btn-text btn-sm" onClick={() => setStep(1)}>
                    Edit Address
                  </button>
                </div>
              </div>

              {/* Payment Method */}
              <div className="form-section-card">
                <h3 className="form-section-title font-serif">Payment Method</h3>
                <p className="payment-prototype-note">
                  Prototype Sandbox: No real bank deduction will be made.
                </p>

                <div className="payment-method-tabs">
                  <button
                    type="button"
                    className={`payment-tab ${formData.paymentMethod === 'upi' ? 'tab-selected' : ''}`}
                    onClick={() => handleChange('paymentMethod', 'upi')}
                  >
                    UPI (GPay / PhonePe / Paytm)
                  </button>
                  <button
                    type="button"
                    className={`payment-tab ${formData.paymentMethod === 'card' ? 'tab-selected' : ''}`}
                    onClick={() => handleChange('paymentMethod', 'card')}
                  >
                    <CreditCard size={18} /> Credit / Debit Card
                  </button>
                  <button
                    type="button"
                    className={`payment-tab ${formData.paymentMethod === 'netbanking' ? 'tab-selected' : ''}`}
                    onClick={() => handleChange('paymentMethod', 'netbanking')}
                  >
                    NetBanking
                  </button>
                </div>

                {formData.paymentMethod === 'card' && (
                  <div className="card-input-box">
                    <div className="form-group">
                      <label className="form-label">Card Number</label>
                      <input
                        type="text"
                        value={formData.cardNumber}
                        onChange={(e) => handleChange('cardNumber', e.target.value)}
                        className="form-input font-mono"
                      />
                    </div>
                    <div className="form-grid-2">
                      <div className="form-group">
                        <label className="form-label">Expiry Date</label>
                        <input
                          type="text"
                          value={formData.cardExpiry}
                          onChange={(e) => handleChange('cardExpiry', e.target.value)}
                          className="form-input"
                          placeholder="MM/YY"
                        />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Security Code (CVV)</label>
                        <input
                          type="text"
                          value={formData.cardCvc}
                          onChange={(e) => handleChange('cardCvc', e.target.value)}
                          className="form-input"
                          placeholder="123"
                        />
                      </div>
                    </div>
                  </div>
                )}
                {formData.paymentMethod === 'upi' && (
                  <div className="card-input-box">
                    <div className="form-group">
                      <label className="form-label">UPI ID / VPA</label>
                      <input
                        type="text"
                        defaultValue="sarah@okaxis"
                        className="form-input"
                        placeholder="username@upi"
                      />
                    </div>
                  </div>
                )}
              </div>

              <div className="checkout-btn-row">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setStep(1)}
                >
                  <ArrowLeft size={16} /> Back to Shipping
                </button>
                <button type="submit" className="btn btn-primary btn-lg place-order-btn">
                  <Lock size={16} /> Place Order — ₹{cartTotal.toLocaleString('en-IN')}
                </button>
              </div>
            </form>
          )}
        </div>

        {/* Right Column: Order Summary with mini thumbnails */}
        <aside className="checkout-summary-column">
          <div className="order-summary-card">
            <h3 className="summary-title font-serif">Order Summary</h3>

            {/* Mini Line Items */}
            <div className="checkout-items-mini-list">
              {cart.map((item) => (
                <div key={item.id} className="checkout-mini-item">
                  <div className="checkout-mini-img-wrap">
                    <img src={item.color.image || item.product.images[0]} alt={item.product.name} />
                    <span className="checkout-mini-qty-badge">{item.quantity}</span>
                  </div>
                  <div className="checkout-mini-meta">
                    <h4 className="checkout-mini-name">{item.product.name}</h4>
                    <span className="checkout-mini-color">{item.color.name}</span>
                  </div>
                  <span className="checkout-mini-price">₹{(item.price * item.quantity).toLocaleString('en-IN')}</span>
                </div>
              ))}
            </div>

            <div className="divider my-14" />

            <div className="summary-lines">
              <div className="summary-line">
                <span>Subtotal</span>
                <span>₹{cartSubtotal.toLocaleString('en-IN')}</span>
              </div>
              {discountAmount > 0 && (
                <div className="summary-line coupon-active-line">
                  <span>Discount</span>
                  <span>-₹{discountAmount.toLocaleString('en-IN')}</span>
                </div>
              )}
              <div className="summary-line">
                <span>Shipping</span>
                <span>
                  {formData.shippingMethod === 'express'
                    ? '₹199'
                    : cartSubtotal >= 1999
                    ? 'Complimentary'
                    : '₹199'}
                </span>
              </div>
              <div className="divider my-10" />
              <div className="summary-line total-line">
                <span>Total</span>
                <span className="total-amount">₹{cartTotal.toLocaleString('en-IN')}</span>
              </div>
            </div>

            <div className="checkout-trust-badges">
              <div className="trust-badge-row">
                <ShieldCheck size={16} className="trust-badge-icon" />
                <span>256-Bit SSL Encrypted & Protected</span>
              </div>
              <div className="trust-badge-row">
                <Truck size={16} className="trust-badge-icon" />
                <span>Full transit insurance included</span>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
};
