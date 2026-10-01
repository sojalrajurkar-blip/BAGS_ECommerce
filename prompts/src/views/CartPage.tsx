'use client';

import React, { useState, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, fadeInUp, staggerFadeInUp } from '../animations';
import { Minus, Plus, Trash2, Heart, ArrowRight, ShoppingBag, Tag, X } from 'lucide-react';

export const CartPage: React.FC = () => {
  const {
    cart,
    updateQuantity,
    removeFromCart,
    moveToWishlist,
    cartSubtotal,
    appliedCoupon,
    applyCoupon,
    removeCoupon,
    discountAmount,
    shippingFee,
    cartTotal,
    navigate,
  } = useStore();

  const pageRef = useRef<HTMLDivElement>(null);
  const [couponInput, setCouponInput] = useState<string>('');

  const handleApplyCoupon = (e: React.FormEvent) => {
    e.preventDefault();
    if (!couponInput.trim()) return;
    applyCoupon(couponInput.trim());
    setCouponInput('');
  };

  // Scoped Cart Page Animation
  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.cart-page .breadcrumbs-nav',
        eyebrow: '.cart-page-header .section-eyebrow',
        title: '.cart-page-header .cart-page-title',
        subtitle: '.cart-page-header .cart-page-subtitle',
      });

      if (cart.length > 0) {
        staggerFadeInUp('.cart-items-column .cart-item-row', {
          trigger: '.cart-items-column',
          start: 'top 88%',
          stagger: 0.05,
        });

        fadeInUp('.cart-summary-column', {
          trigger: '.cart-page-layout',
          start: 'top 88%',
          delay: 0.1,
        });
      } else {
        fadeInUp('.cart-empty-view', {
          trigger: '.cart-empty-view',
          start: 'top 90%',
        });
      }
    },
    pageRef,
    [cart.length]
  );

  return (
    <div className="cart-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'Shopping Bag' }]} />

      <div className="cart-page-header">
        <span className="section-eyebrow">Review Your Selection</span>
        <h1 className="cart-page-title font-serif">Shopping Bag</h1>
        <p className="cart-page-subtitle">
          {cart.length} {cart.length === 1 ? 'item' : 'items'} in your bag
        </p>
      </div>

      {cart.length === 0 ? (
        <div className="cart-empty-view">
          <ShoppingBag size={48} strokeWidth={1.2} className="empty-bag-icon" />
          <h2 className="font-serif empty-title">Your shopping bag is currently empty</h2>
          <p className="empty-desc">
            Explore our curated catalog of backpacks, briefcases, and travel duffels.
          </p>
          <button className="btn btn-primary" onClick={() => navigate('shop')}>
            Explore Collection <ArrowRight size={16} />
          </button>
        </div>
      ) : (
        <div className="cart-page-layout">
          {/* Left: Items Table / List */}
          <div className="cart-items-column">
            <div className="cart-table-header">
              <span>Product</span>
              <span>Quantity</span>
              <span>Total</span>
            </div>

            <div className="cart-table-body">
              {cart.map((item) => (
                <div key={item.id} className="cart-row">
                  {/* Product Info */}
                  <div className="cart-product-info">
                    <div
                      className="cart-img-frame"
                      onClick={() => navigate('product', { productId: item.productId })}
                    >
                      <img src={item.color.image || item.product.images[0]} alt={item.product.name} />
                    </div>
                    <div className="cart-meta-col">
                      <h3
                        className="cart-item-title"
                        onClick={() => navigate('product', { productId: item.productId })}
                      >
                        {item.product.name}
                      </h3>
                      <span className="cart-item-unit-price">₹{item.price.toLocaleString('en-IN')} each</span>
                      <span className="cart-item-color-tag">Color: {item.color.name}</span>

                      <div className="cart-inline-actions">
                        <button
                          className="cart-inline-btn"
                          onClick={() => moveToWishlist(item)}
                        >
                          <Heart size={13} /> Save to Wishlist
                        </button>
                        <button
                          className="cart-inline-btn cart-remove-inline"
                          onClick={() => removeFromCart(item.id)}
                        >
                          <Trash2 size={13} /> Remove
                        </button>
                      </div>
                    </div>
                  </div>

                  {/* Quantity Stepper */}
                  <div className="cart-qty-box">
                    <div className="quantity-control-compact">
                      <button
                        onClick={() => updateQuantity(item.id, item.quantity - 1)}
                        className="qty-btn"
                        aria-label="Decrease quantity"
                      >
                        <Minus size={12} />
                      </button>
                      <span className="qty-value">{item.quantity}</span>
                      <button
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                        className="qty-btn"
                        aria-label="Increase quantity"
                      >
                        <Plus size={12} />
                      </button>
                    </div>
                  </div>

                  {/* Line Total */}
                  <div className="cart-line-total">
                    <span>₹{(item.price * item.quantity).toLocaleString('en-IN')}</span>
                  </div>
                </div>
              ))}
            </div>

            <div className="cart-bottom-nav">
              <button className="btn-text" onClick={() => navigate('shop')}>
                ← Continue Browsing Collection
              </button>
            </div>
          </div>

          {/* Right: Order Summary Card */}
          <aside className="cart-summary-column">
            <div className="order-summary-card">
              <h3 className="summary-title font-serif">Order Summary</h3>

              <div className="summary-lines">
                <div className="summary-line">
                  <span>Subtotal</span>
                  <span>₹{cartSubtotal.toLocaleString('en-IN')}</span>
                </div>

                {appliedCoupon && (
                  <div className="summary-line coupon-active-line">
                    <span className="coupon-tag-label">
                      <Tag size={13} /> Code ({appliedCoupon.code})
                    </span>
                    <span>-₹{discountAmount.toLocaleString('en-IN')}</span>
                  </div>
                )}

                <div className="summary-line">
                  <span>Estimated Shipping</span>
                  <span>{shippingFee === 0 ? 'Complimentary' : `₹${shippingFee}`}</span>
                </div>

                <div className="summary-line">
                  <span>Estimated Tax</span>
                  <span>GST Included</span>
                </div>

                <div className="divider my-14" />

                <div className="summary-line total-line">
                  <span>Estimated Total</span>
                  <span className="total-amount">₹{cartTotal.toLocaleString('en-IN')}</span>
                </div>
              </div>

              {/* Coupon Form */}
              <div className="coupon-section">
                {appliedCoupon ? (
                  <div className="applied-coupon-pill">
                    <span>
                      Coupon <strong>{appliedCoupon.code}</strong> applied ({appliedCoupon.discountPercent}% off)
                    </span>
                    <button onClick={removeCoupon} className="coupon-remove-btn" aria-label="Remove coupon">
                      <X size={14} />
                    </button>
                  </div>
                ) : (
                  <form onSubmit={handleApplyCoupon} className="coupon-form">
                    <input
                      type="text"
                      placeholder="Promo code (e.g. RORA10)"
                      value={couponInput}
                      onChange={(e) => setCouponInput(e.target.value)}
                      className="coupon-input"
                    />
                    <button type="submit" className="btn btn-secondary btn-sm">
                      Apply
                    </button>
                  </form>
                )}
              </div>

              {/* Checkout Button */}
              <button
                className="btn btn-primary btn-full btn-lg checkout-cta-btn"
                onClick={() => navigate('checkout')}
              >
                Proceed to Checkout <ArrowRight size={18} />
              </button>

              <div className="checkout-security-notice">
                <span>🔒 Guaranteed safe & secure 256-bit encrypted checkout</span>
              </div>
            </div>
          </aside>
        </div>
      )}
    </div>
  );
};
