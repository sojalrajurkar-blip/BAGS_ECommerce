'use client';

import React from 'react';
import { useStore } from '../../context/StoreContext';
import { X, Plus, Minus, Trash2, ArrowRight, ShoppingBag, Heart } from 'lucide-react';
import { CartItem } from '../../types/domain';

export const CartDrawer: React.FC = () => {
  const {
    isCartDrawerOpen,
    setIsCartDrawerOpen,
    cart,
    updateQuantity,
    removeFromCart,
    moveToWishlist,
    cartSubtotal,
    discountAmount,
    shippingFee,
    cartTotal,
    navigate
  } = useStore();

  if (!isCartDrawerOpen) return null;

  return (
    <div className="cart-drawer-overlay" onClick={() => setIsCartDrawerOpen(false)}>
      <div className="cart-drawer" onClick={(e) => e.stopPropagation()}>
        {/* Drawer Header */}
        <div className="cart-drawer-header">
          <div className="cart-drawer-title-group">
            <h3 className="cart-drawer-title">Your Bag</h3>
            <span className="cart-drawer-count">({cart.length} {cart.length === 1 ? 'item' : 'items'})</span>
          </div>
          <button
            className="cart-drawer-close"
            onClick={() => setIsCartDrawerOpen(false)}
            aria-label="Close bag"
          >
            <X size={20} />
          </button>
        </div>

        {/* Free Shipping Progress Indicator */}
        <div className="cart-shipping-notice">
          {cartSubtotal >= 1999 ? (
            <p className="shipping-unlocked">✨ You have unlocked complimentary standard shipping!</p>
          ) : (
            <p className="shipping-progress">
              Add <strong>₹{(1999 - cartSubtotal).toLocaleString('en-IN')}</strong> more for complimentary shipping
            </p>
          )}
        </div>

        {/* Cart Item List */}
        <div className="cart-drawer-items">
          {cart.length === 0 ? (
            <div className="cart-drawer-empty">
              <ShoppingBag size={44} strokeWidth={1.2} className="empty-icon" />
              <p className="empty-title font-serif">Your shopping bag is empty</p>
              <p className="empty-subtitle">Discover our collection of handcrafted bags.</p>
              <button
                className="btn btn-secondary btn-sm"
                onClick={() => {
                  setIsCartDrawerOpen(false);
                  navigate('shop');
                }}
              >
                Explore Collection
              </button>
            </div>
          ) : (
            cart.map((item: CartItem) => (
              <div key={item.id} className="cart-item-row">
                <div
                  className="cart-item-image-wrap"
                  onClick={() => {
                    setIsCartDrawerOpen(false);
                    navigate('product', { productId: item.productId || item.product.id });
                  }}
                >
                  <img
                    src={item.color?.image || item.product.images[0]}
                    alt={item.product.name}
                    className="cart-item-image"
                  />
                </div>

                <div className="cart-item-details">
                  <div className="cart-item-header">
                    <h4
                      className="cart-item-name"
                      onClick={() => {
                        setIsCartDrawerOpen(false);
                        navigate('product', { productId: item.productId || item.product.id });
                      }}
                    >
                      {item.product.name}
                    </h4>
                    <span className="cart-item-price">₹{((item.price || item.product.price) * item.quantity).toLocaleString('en-IN')}</span>
                  </div>

                  <p className="cart-item-variant">Color: {item.color?.name || 'Standard'}</p>

                  <div className="cart-item-actions">
                    {/* Quantity Selector */}
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

                    <div className="cart-item-extra-actions">
                      <button
                        className="item-action-link"
                        onClick={() => moveToWishlist(item)}
                        title="Move to Wishlist"
                      >
                        <Heart size={14} />
                      </button>
                      <button
                        className="item-action-link"
                        onClick={() => removeFromCart(item.id)}
                        title="Remove item"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Drawer Footer / Summary */}
        {cart.length > 0 && (
          <div className="cart-drawer-footer">
            <div className="cart-summary-line">
              <span>Subtotal</span>
              <span className="font-semibold">₹{cartSubtotal.toLocaleString('en-IN')}</span>
            </div>
            {discountAmount > 0 && (
              <div className="cart-summary-line cart-discount">
                <span>Discount</span>
                <span>-₹{discountAmount.toLocaleString('en-IN')}</span>
              </div>
            )}
            <div className="cart-summary-line">
              <span>Estimated Shipping</span>
              <span>{shippingFee === 0 ? 'Complimentary' : `₹${shippingFee}`}</span>
            </div>

            <div className="cart-drawer-divider" />

            <div className="cart-summary-line cart-total-line">
              <span>Total</span>
              <span className="cart-total-amount">₹{cartTotal.toLocaleString('en-IN')}</span>
            </div>

            <div className="cart-footer-buttons">
              <button
                className="btn btn-primary btn-full"
                onClick={() => {
                  setIsCartDrawerOpen(false);
                  navigate('checkout');
                }}
              >
                Proceed to Checkout <ArrowRight size={16} />
              </button>
              <button
                className="btn btn-secondary btn-full btn-sm"
                onClick={() => {
                  setIsCartDrawerOpen(false);
                  navigate('cart');
                }}
              >
                View Full Bag
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
