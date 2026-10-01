'use client';

import React, { useState, useEffect, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { productRepository } from '../data/repositories';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { ProductCard } from '../components/common/ProductCard';
import {
  useGsapContext,
  revealPageHeader,
  staggerFadeInUp,
  fadeInUp,
  revealSectionHeader,
} from '../animations';
import { Heart, Trash2, ShoppingBag, ArrowRight } from 'lucide-react';
import type { Product } from '../types';

export const WishlistPage: React.FC = () => {
  const { wishlist, toggleWishlist, addToCart, recentlyViewed, navigate } = useStore();
  const pageRef = useRef<HTMLDivElement>(null);
  const [allProducts, setAllProducts] = useState<Product[]>([]);

  useEffect(() => {
    let isMounted = true;
    productRepository.getProducts().then((prods) => {
      if (isMounted) setAllProducts(prods);
    });
    return () => {
      isMounted = false;
    };
  }, []);

  const savedProducts = allProducts.filter((p) => wishlist.includes(p.id));
  const recentProducts = allProducts
    .filter((p) => recentlyViewed.includes(p.id) && !wishlist.includes(p.id))
    .slice(0, 4);

  // Scoped Wishlist animation
  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.wishlist-page .breadcrumbs-nav',
        eyebrow: '.wishlist-header .section-eyebrow',
        title: '.wishlist-header .wishlist-title',
        subtitle: '.wishlist-header .wishlist-subtitle',
      });

      if (savedProducts.length > 0) {
        staggerFadeInUp('.wishlist-items-list .wishlist-item-row', {
          trigger: '.wishlist-items-list',
          start: 'top 88%',
          stagger: 0.05,
        });
      } else {
        fadeInUp('.wishlist-empty-card', {
          trigger: '.wishlist-empty-card',
          start: 'top 90%',
        });
      }

      if (recentProducts.length > 0) {
        revealSectionHeader(
          {
            eyebrow: '.recently-viewed-section .section-eyebrow',
            title: '.recently-viewed-section .section-title',
          },
          '.recently-viewed-section'
        );

        staggerFadeInUp('.recently-viewed-section .product-card', {
          trigger: '.recently-viewed-section .products-grid-4',
          start: 'top 85%',
          stagger: 0.06,
        });
      }
    },
    pageRef,
    [savedProducts.length, recentProducts.length]
  );

  return (
    <div className="wishlist-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'Wishlist' }]} />

      <div className="wishlist-header">
        <span className="section-eyebrow">Saved Items</span>
        <h1 className="wishlist-title font-serif">Your Wishlist</h1>
        <p className="wishlist-subtitle">
          Your saved favorites, ready whenever you are to embark on your next journey.
        </p>
      </div>

      {savedProducts.length === 0 ? (
        <div className="wishlist-empty-card">
          <Heart size={44} strokeWidth={1.2} className="empty-heart-icon" />
          <h3 className="font-serif empty-title">Your wishlist is currently empty</h3>
          <p className="empty-subtitle">
            Explore our collection and click the heart icon to save your favorite styles.
          </p>
          <button className="btn btn-primary" onClick={() => navigate('shop')}>
            Explore Collection <ArrowRight size={16} />
          </button>
        </div>
      ) : (
        <div className="wishlist-items-list">
          {savedProducts.map((product) => (
            <div key={product.id} className="wishlist-item-row">
              <div
                className="wishlist-item-img-box"
                onClick={() => navigate('product', { productId: product.id })}
              >
                <img src={product.images[0]} alt={product.name} />
              </div>

              <div className="wishlist-item-info">
                <span className="wishlist-item-cat">{product.category}</span>
                <h3
                  className="wishlist-item-name"
                  onClick={() => navigate('product', { productId: product.id })}
                >
                  {product.name}
                </h3>
                <span className="wishlist-item-price">₹{product.price.toLocaleString('en-IN')}</span>
                <span className="wishlist-item-stock">In Stock — Ready to ship</span>
              </div>

              <div className="wishlist-item-actions">
                <button
                  className="btn btn-primary btn-sm wishlist-add-cart-btn"
                  onClick={() => addToCart(product, product.colors[0], 1)}
                >
                  <ShoppingBag size={14} /> Add to Bag
                </button>
                <button
                  className="wishlist-remove-btn"
                  onClick={() => toggleWishlist(product.id)}
                  title="Remove from wishlist"
                  aria-label="Remove from wishlist"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Tasteful "Recently Viewed" Section */}
      {recentProducts.length > 0 && (
        <section className="recently-viewed-section section-sm">
          <div className="section-header-row">
            <div>
              <span className="section-eyebrow">Based on your activity</span>
              <h2 className="section-title">Recently Viewed</h2>
            </div>
          </div>

          <div className="products-grid-4">
            {recentProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        </section>
      )}
    </div>
  );
};
