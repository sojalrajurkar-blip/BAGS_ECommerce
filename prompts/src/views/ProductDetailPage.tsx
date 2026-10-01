'use client';

import React, { useState, useEffect, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { productRepository } from '../data/repositories';
import { ProductCard } from '../components/common/ProductCard';
import { Product, ColorVariant } from '../types/domain';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import {
  useGsapContext,
  revealPageHeader,
  fadeInUp,
  staggerFadeInUp
} from '../animations';
import { Star, Heart, Truck, RefreshCw, ShieldCheck, Plus, Minus, Check, ArrowRight } from 'lucide-react';

export interface ProductDetailPageProps {
  productId?: string;
}

export const ProductDetailPage: React.FC<ProductDetailPageProps> = ({ productId: propProductId } = {}) => {
  const { currentRoute, addToCart, toggleWishlist, wishlist, navigate, addRecentlyViewed } = useStore();
  const rawId = propProductId || (currentRoute.params?.productId as string) || (currentRoute.params?.slug as string) || 'prod-1';
  const productId = String(rawId);
  const pageRef = useRef<HTMLDivElement>(null);

  const [product, setProduct] = useState<Product | null>(null);
  const [relatedProducts, setRelatedProducts] = useState<Product[]>([]);
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);
  const [selectedColor, setSelectedColor] = useState<ColorVariant | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [activeTab, setActiveTab] = useState('description');

  useEffect(() => {
    let isMounted = true;
    const loadProductData = async () => {
      const [prod, related] = await Promise.all([
        productRepository.getProductById(productId),
        productRepository.getRelatedProducts(productId, 4)
      ]);
      if (isMounted) {
        const fallbackProd = await productRepository.getProductById('prod-1');
        const activeProd: Product | null = prod || fallbackProd;
        setProduct(activeProd);
        setRelatedProducts(related);
        setSelectedImageIndex(0);
        if (activeProd?.colors && activeProd.colors.length > 0) {
          setSelectedColor(activeProd.colors[0]);
        }
        setQuantity(1);
        if (activeProd?.id) {
          addRecentlyViewed(activeProd.id);
        }
      }
    };
    loadProductData();
    return () => { isMounted = false; };
  }, [productId]);

  const isSaved = product ? wishlist.includes(product.id) : false;

  const formatDimensions = (dim?: string | { height?: string; width?: string; depth?: string }): string => {
    if (!dim) return '';
    if (typeof dim === 'string') return dim;
    return `${dim.height || ''} × ${dim.width || ''} × ${dim.depth || ''}`.replace(/^ × | × $/g, '');
  };

  const handleAddToCart = () => {
    if (product) addToCart(product, selectedColor, quantity);
  };

  const handleBuyNow = () => {
    if (product) {
      addToCart(product, selectedColor, quantity);
      navigate('checkout');
    }
  };

  // Scoped PDP Animation
  useGsapContext((self, isReduced) => {
    if (isReduced || !product) return;

    // Header & Gallery Entrance
    revealPageHeader({
      breadcrumbs: '.product-detail-page .breadcrumbs-nav',
    });

    fadeInUp('.pdp-gallery', {
      trigger: '.pdp-main-grid',
      start: 'top 90%',
      delay: 0.05,
    });

    fadeInUp('.pdp-meta-panel', {
      trigger: '.pdp-main-grid',
      start: 'top 90%',
      delay: 0.12,
    });

    // Technical Tabs & Specs Entrance
    fadeInUp('.pdp-tabs-section', {
      trigger: '.pdp-tabs-section',
      start: 'top 85%',
    });

    // Related Products Stagger
    if (relatedProducts.length > 0) {
      fadeInUp('.related-products-section .section-header-row', {
        trigger: '.related-products-section',
        start: 'top 85%',
      });

      staggerFadeInUp('.related-products-section .product-card', {
        trigger: '.related-products-section',
        start: 'top 80%',
        stagger: 0.08,
      });
    }
  }, pageRef, [product, relatedProducts.length]);

  if (!product) return null;

  return (
    <div className="product-detail-page container section-sm" ref={pageRef}>
      <Breadcrumbs
        items={[
          { label: 'Shop', page: 'shop' },
          { label: product.category, page: 'category', params: { categoryId: product.category } },
          { label: product.name }
        ]}
      />

      {/* Main PDP Grid: Left Gallery + Right Sticky Meta */}
      <div className="pdp-main-grid">
        {/* Gallery Section */}
        <div className="pdp-gallery">
          {/* Vertical Thumbnail Strip */}
          <div className="pdp-thumbnails">
            {product.images?.map((img: string, idx: number) => (
              <button
                key={idx}
                className={`pdp-thumb-btn ${selectedImageIndex === idx ? 'thumb-active' : ''}`}
                onClick={() => setSelectedImageIndex(idx)}
              >
                <img src={img} alt={`${product.name} view ${idx + 1}`} />
              </button>
            ))}
          </div>

          {/* Main Selected Image */}
          <div className="pdp-main-image-wrap">
            <img
              src={product.images?.[selectedImageIndex] || product.images?.[0]}
              alt={product.name}
              className="pdp-main-image"
            />
            {product.badge && (
              <span className="pdp-badge badge badge-subtle">{product.badge}</span>
            )}
          </div>
        </div>

        {/* Product Meta & Buying Actions */}
        <div className="pdp-meta-panel">
          <span className="pdp-category-tag">{product.category}</span>
          <h1 className="pdp-title font-serif">{product.name}</h1>
          <p className="pdp-tagline">{product.tagline}</p>

          {/* Pricing & Ratings */}
          <div className="pdp-price-rating-row">
            <div className="pdp-pricing">
              <span className="pdp-price">₹{product.price?.toLocaleString('en-IN')}</span>
              {product.originalPrice && product.originalPrice > product.price && (
                <span className="pdp-original-price">₹{product.originalPrice?.toLocaleString('en-IN')}</span>
              )}
            </div>

            <div className="pdp-ratings">
              <div className="pdp-stars">
                {[...Array(5)].map((_, i) => (
                  <Star
                    key={i}
                    size={14}
                    fill={i < Math.floor(product.rating || 5) ? 'var(--color-olive-500)' : 'none'}
                    color="var(--color-olive-500)"
                  />
                ))}
              </div>
              <span className="pdp-rating-val">{product.rating}</span>
              <span className="pdp-review-count">({product.reviewCount} reviews)</span>
            </div>
          </div>

          <p className="pdp-description">{product.description}</p>

          <div className="divider my-20" />

          {/* Color Selection */}
          <div className="pdp-option-group">
            <label className="pdp-option-label">
              Color: <strong>{selectedColor?.name}</strong>
            </label>
            <div className="pdp-colors-row">
              {product.colors?.map((col: ColorVariant, idx: number) => (
                <button
                  key={idx}
                  className={`pdp-color-swatch ${selectedColor?.name === col.name ? 'pdp-swatch-active' : ''}`}
                  style={{ backgroundColor: col.hex }}
                  onClick={() => {
                    setSelectedColor(col);
                    if (col.image) setSelectedImageIndex(0);
                  }}
                  title={col.name}
                >
                  {selectedColor?.name === col.name && (
                    <Check size={14} color="#FFF" />
                  )}
                </button>
              ))}
            </div>
          </div>

          {/* Size / Capacity Indicator */}
          <div className="pdp-option-group">
            <div className="pdp-size-header">
              <label className="pdp-option-label">Capacity & Dimensions</label>
              <span className="pdp-stock-status">In Stock ({product.stock} units)</span>
            </div>
            <div className="pdp-spec-pill">
              <span>{product.capacity}</span> — <span>{formatDimensions(product.dimensions)}</span>
            </div>
          </div>

          {/* Quantity & CTA Buttons */}
          <div className="pdp-cta-block">
            <div className="pdp-quantity-stepper">
              <button
                onClick={() => setQuantity(Math.max(1, quantity - 1))}
                className="pdp-qty-btn"
                aria-label="Decrease quantity"
              >
                <Minus size={14} />
              </button>
              <span className="pdp-qty-num">{quantity}</span>
              <button
                onClick={() => setQuantity(quantity + 1)}
                className="pdp-qty-btn"
                aria-label="Increase quantity"
              >
                <Plus size={14} />
              </button>
            </div>

            <button
              className="btn btn-primary pdp-add-cart-btn"
              onClick={handleAddToCart}
            >
              Add to Bag — ₹{((product.price || 0) * quantity).toLocaleString('en-IN')}
            </button>

            <button
              className={`pdp-wishlist-toggle ${isSaved ? 'wishlist-active' : ''}`}
              onClick={() => toggleWishlist(product.id)}
              aria-label="Wishlist"
            >
              <Heart
                size={20}
                fill={isSaved ? 'var(--accent-primary)' : 'none'}
                color={isSaved ? 'var(--accent-primary)' : 'var(--color-charcoal-900)'}
              />
            </button>
          </div>

          <button
            className="btn btn-secondary btn-full pdp-buy-now-btn"
            onClick={handleBuyNow}
          >
            Buy Now
          </button>

          {/* Trust Guarantees */}
          <div className="pdp-trust-card">
            <div className="pdp-trust-line">
              <Truck size={17} className="pdp-trust-icon" />
              <span>Complimentary express shipping on orders over ₹1,999</span>
            </div>
            <div className="pdp-trust-line">
              <RefreshCw size={17} className="pdp-trust-icon" />
              <span>30-day hassle-free returns with prepaid return label</span>
            </div>
            <div className="pdp-trust-line">
              <ShieldCheck size={17} className="pdp-trust-icon" />
              <span>Lifetime repair & craftsmanship guarantee</span>
            </div>
          </div>
        </div>
      </div>

      {/* Product Details Tabs (Description, Specifications, Features, Shipping) */}
      <section className="pdp-tabs-section section-sm">
        <div className="pdp-tabs-nav">
          <button
            className={`pdp-tab-btn ${activeTab === 'description' ? 'tab-active' : ''}`}
            onClick={() => setActiveTab('description')}
          >
            Story & Details
          </button>
          <button
            className={`pdp-tab-btn ${activeTab === 'specifications' ? 'tab-active' : ''}`}
            onClick={() => setActiveTab('specifications')}
          >
            Specifications
          </button>
          <button
            className={`pdp-tab-btn ${activeTab === 'features' ? 'tab-active' : ''}`}
            onClick={() => setActiveTab('features')}
          >
            Features & Organization
          </button>
          <button
            className={`pdp-tab-btn ${activeTab === 'shipping' ? 'tab-active' : ''}`}
            onClick={() => setActiveTab('shipping')}
          >
            Delivery & Returns
          </button>
        </div>

        <div className="pdp-tab-content">
          {activeTab === 'description' && (
            <div className="tab-pane-story">
              <h3 className="font-serif tab-heading">Built for Every Journey</h3>
              <p className="tab-body-text">{product.story}</p>
              <p className="tab-body-text">{product.description}</p>
              <div className="materials-highlight-box">
                <strong>Primary Materials:</strong> {product.material}
              </div>
            </div>
          )}

          {activeTab === 'specifications' && (
            <div className="tab-pane-specs">
              <h3 className="font-serif tab-heading">Technical Specifications</h3>
              <div className="specs-table">
                {product.specifications && Object.entries(product.specifications).map(([key, value]) => (
                  <div key={key} className="spec-row">
                    <span className="spec-key">{key}</span>
                    <span className="spec-value">{String(value)}</span>
                  </div>
                ))}
                <div className="spec-row">
                  <span className="spec-key">Weight</span>
                  <span className="spec-value">{product.weight}</span>
                </div>
                <div className="spec-row">
                  <span className="spec-key">Dimensions</span>
                  <span className="spec-value">{formatDimensions(product.dimensions)}</span>
                </div>
                <div className="spec-row">
                  <span className="spec-key">SKU</span>
                  <span className="spec-value">{product.sku}</span>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'features' && (
            <div className="tab-pane-features">
              <h3 className="font-serif tab-heading">Engineered Features</h3>
              <ul className="features-list">
                {product.features?.map((feat: string, i: number) => (
                  <li key={i} className="feature-item">
                    <span className="feature-bullet">•</span>
                    <span>{feat}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {activeTab === 'shipping' && (
            <div className="tab-pane-shipping">
              <h3 className="font-serif tab-heading">Shipping & Return Information</h3>
              <p className="tab-body-text">
                All orders are dispatched within 24 business hours. Orders over ₹1,999 receive complimentary express tracked shipping across India (2–4 business days).
              </p>
              <p className="tab-body-text">
                If you are not entirely satisfied with your bag, return it within 30 days of delivery in unused condition with all original tags attached for a full refund or direct exchange.
              </p>
            </div>
          )}
        </div>
      </section>

      {/* Editorial Lookbook Banner for PDP */}
      <section className="pdp-lookbook-banner">
        <div className="lookbook-grid">
          <div className="lookbook-img-box">
            <img
              src="https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1200&q=80"
              alt="Craftsmanship closeup"
              loading="lazy"
            />
          </div>
          <div className="lookbook-text-card">
            <span className="section-eyebrow">Lookbook & Craft</span>
            <h2 className="font-serif lookbook-title">Made for What's Next.</h2>
            <p className="lookbook-desc">
              Every curve, seam, and strap angle is evaluated to relieve shoulder pressure, protect fragile technology, and age gracefully across decades of movement.
            </p>
          </div>
        </div>
      </section>

      {/* Related Products / Frequently Bought Together */}
      <section className="section-md pdp-related-section">
        <div className="section-header-row">
          <div>
            <span className="section-eyebrow">Complete Your Setup</span>
            <h2 className="section-title">You May Also Like</h2>
          </div>
          <button className="btn-text" onClick={() => navigate('shop')}>
            Explore All <ArrowRight size={16} />
          </button>
        </div>

        <div className="products-grid-4">
          {relatedProducts.map(p => (
            <ProductCard key={p.id} product={p} />
          ))}
        </div>
      </section>
    </div>
  );
};
