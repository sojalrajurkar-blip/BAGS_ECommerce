'use client';

import React, { useState, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, fadeInUp } from '../animations';
import { Check, ShieldCheck, Truck, CreditCard, Lock, ArrowRight, ArrowLeft, Loader2, Sparkles } from 'lucide-react';
import { paymentRepository, orderRepository } from '../data/repositories';

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
  paymentMethod: 'razorpay' | 'upi' | 'card' | 'netbanking' | 'applepay' | 'paypal';
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
    appliedCoupon,
    addToast,
    placeOrder,
    navigate,
  } = useStore();

  const [step, setStep] = useState<number>(1); // 1: Shipping, 2: Payment
  const [isProcessing, setIsProcessing] = useState<boolean>(false);
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
    paymentMethod: 'razorpay',
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

  const loadRazorpayScript = (): Promise<boolean> => {
    return new Promise((resolve) => {
      if (typeof window === 'undefined') return resolve(false);
      if ((window as unknown as { Razorpay?: unknown }).Razorpay) return resolve(true);

      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.onload = () => resolve(true);
      script.onerror = () => resolve(false);
      document.body.appendChild(script);
    });
  };

  const handleCompleteOrder = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isProcessing) return;

    const shippingAddress = {
      fullName: `${formData.firstName} ${formData.lastName}`,
      street: formData.address,
      city: formData.city,
      state: formData.state,
      postalCode: formData.zipCode,
      country: formData.country,
      phone: formData.phone,
    };

    if (formData.paymentMethod !== 'razorpay') {
      // Instant Sandbox / Mock fallback
      placeOrder({
        shippingAddress,
        paymentMethod:
          formData.paymentMethod === 'applepay'
            ? 'Apple Pay'
            : formData.paymentMethod === 'paypal'
            ? 'PayPal'
            : formData.paymentMethod === 'upi'
            ? 'UPI (sarah@okaxis)'
            : `Visa ending in 4242`,
      });
      return;
    }

    // Live Razorpay Gateway
    try {
      setIsProcessing(true);
      addToast('Initializing secure Razorpay gateway...', 'info');

      const loaded = await loadRazorpayScript();
      if (!loaded) {
        addToast('Unable to load Razorpay checkout script. Please check your network.', 'error');
        setIsProcessing(false);
        return;
      }

      // 1. Authoritative order placement on backend
      const order = await orderRepository.placeOrder({
        customerName: shippingAddress.fullName,
        customerEmail: formData.email,
        customerPhone: formData.phone,
        shippingAddress,
        billingAddress: shippingAddress,
        paymentMethod: 'RAZORPAY',
        couponCode: appliedCoupon?.code,
      });

      // 2. Create Razorpay order on backend
      const rzpOrder = await paymentRepository.createRazorpayOrder(order.id);

      // 3. Launch Razorpay modal with RÓRA Luxury Atelier theme
      const options = {
        key: process.env.NEXT_PUBLIC_RAZORPAY_KEY_ID || rzpOrder.keyId || 'rzp_test_TkFa9wOUkFRBDH',
        amount: rzpOrder.amountInPaise,
        currency: 'INR',
        name: 'RÓRA Atelier',
        description: `Order #${order.orderNumber} Luxury Leather Acquisition`,
        image: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=200&q=80',
        order_id: rzpOrder.razorpayOrderId,
        prefill: {
          name: `${formData.firstName} ${formData.lastName}`,
          email: formData.email,
          contact: formData.phone,
        },
        notes: {
          orderNumber: order.orderNumber,
          atelierBrand: 'RÓRA Leather Goods',
        },
        theme: {
          color: '#161616',
        },
        handler: async function (response: { razorpay_order_id: string; razorpay_payment_id: string; razorpay_signature: string }) {
          try {
            await paymentRepository.verifyRazorpayPayment({
              orderIdOrNumber: order.id,
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
            });

            addToast(`Payment of ₹${cartTotal.toLocaleString('en-IN')} captured & verified!`, 'success');
            navigate('confirmation', { order });
          } catch (verifyErr) {
            console.error('Signature verification error:', verifyErr);
            addToast('Payment capture verification failed. Please contact concierge.', 'error');
          } finally {
            setIsProcessing(false);
          }
        },
        modal: {
          ondismiss: function () {
            setIsProcessing(false);
            addToast('Payment window closed.', 'info');
          }
        }
      };

      const RazorpayConstructor = (window as unknown as { Razorpay: new (opts: unknown) => { open: () => void } }).Razorpay;
      const rzpInstance = new RazorpayConstructor(options);
      rzpInstance.open();
    } catch (err) {
      console.error('Razorpay Checkout failed:', err);
      addToast('Could not initialize Razorpay payment. Please try again.', 'error');
      setIsProcessing(false);
    }
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
                <div className="flex items-center justify-between mb-16">
                  <h3 className="form-section-title font-serif">Payment Method</h3>
                  <span className="badge badge-accent flex items-center gap-4 text-xs">
                    <ShieldCheck size={12} /> 256-Bit Encrypted
                  </span>
                </div>

                <div className="payment-method-tabs">
                  <button
                    type="button"
                    className={`payment-tab ${formData.paymentMethod === 'razorpay' ? 'tab-selected' : ''}`}
                    onClick={() => handleChange('paymentMethod', 'razorpay')}
                  >
                    <Sparkles size={16} className="text-amber-500" /> Razorpay Gateway (UPI, Cards, EMI, NetBanking)
                  </button>
                  <button
                    type="button"
                    className={`payment-tab ${formData.paymentMethod === 'upi' ? 'tab-selected' : ''}`}
                    onClick={() => handleChange('paymentMethod', 'upi')}
                  >
                    Direct UPI Sandbox
                  </button>
                  <button
                    type="button"
                    className={`payment-tab ${formData.paymentMethod === 'card' ? 'tab-selected' : ''}`}
                    onClick={() => handleChange('paymentMethod', 'card')}
                  >
                    <CreditCard size={18} /> Card Sandbox
                  </button>
                </div>

                {formData.paymentMethod === 'razorpay' && (
                  <div className="card-input-box p-16 rounded-md bg-stone-900/40 border border-stone-800 text-sm text-stone-300">
                    <p className="font-medium text-stone-200 mb-6 flex items-center gap-6">
                      <Lock size={14} className="text-emerald-400" /> Official Razorpay Modal Gateway
                    </p>
                    <p className="text-xs text-stone-400 leading-relaxed">
                      Upon clicking below, the encrypted Razorpay Checkout window will open allowing real-time payment via Google Pay, PhonePe, Paytm, RuPay, Visa, Mastercard, NetBanking, and No-Cost EMI options.
                    </p>
                  </div>
                )}

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
                  disabled={isProcessing}
                >
                  <ArrowLeft size={16} /> Back to Shipping
                </button>
                <button
                  type="submit"
                  className="btn btn-primary btn-lg place-order-btn"
                  disabled={isProcessing}
                >
                  {isProcessing ? (
                    <span className="flex items-center gap-8">
                      <Loader2 size={16} className="animate-spin" /> Launching Razorpay...
                    </span>
                  ) : (
                    <>
                      <Lock size={16} /> Complete Acquisition — ₹{cartTotal.toLocaleString('en-IN')}
                    </>
                  )}
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
