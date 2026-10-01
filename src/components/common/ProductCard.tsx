'use client';

import React, { useState } from 'react';
import { useStore } from '../../context/StoreContext';
import { Heart, ShoppingBag } from 'lucide-react';
import { Product, ColorVariant } from '../../types/domain';

export interface ProductCardProps {
  product: Product;
  showQuickAdd?: boolean;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, showQuickAdd = true }) => {
  const { navigate, wishlist, toggleWishlist, addToCart } = useStore();
  const [selectedColor, setSelectedColor] = useState<ColorVariant | null>(
    product.colors && product.colors.length > 0 ? product.colors[0] : null
  );
  const [isHovered, setIsHovered] = useState(false);

  const isSaved = wishlist.includes(product.id);

  // Determine current image (color-specific image or secondary gallery image on hover)
  const primaryImage = selectedColor?.image || product.images[0];
  const secondaryImage = product.images[1] || primaryImage;
  const displayImage = isHovered && product.images.length > 1 ? secondaryImage : primaryImage;

  const handleCardClick = () => {
    navigate('product', { productId: product.id });
  };

  const handleWishlistClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    toggleWishlist(product.id);
  };

  const handleQuickAdd = (e: React.MouseEvent) => {
    e.stopPropagation();
    addToCart(product, selectedColor, 1);
  };

  return (
    <div
      className="product-card"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      onClick={handleCardClick}
    >
      {/* Product Image Frame */}
      <div className="product-image-container">
        <img
          src={displayImage}
          alt={product.name}
          className={`product-main-img ${isHovered ? 'product-img-zoom' : ''}`}
          loading="lazy"
        />

        {/* Badge (Optional & Restrained) */}
        {product.badge && (
          <span className="product-badge badge badge-subtle">
            {product.badge}
          </span>
        )}

        {/* Wishlist Button */}
        <button
          className={`product-wishlist-btn ${isSaved ? 'wishlist-active' : ''}`}
          onClick={handleWishlistClick}
          aria-label={isSaved ? 'Remove from wishlist' : 'Save to wishlist'}
        >
          <Heart size={18} fill={isSaved ? '#68705A' : 'none'} color={isSaved ? '#68705A' : '#1E1D1A'} />
        </button>

        {/* Quick Add Overlay Button on Hover */}
        {showQuickAdd && (
          <button
            className={`product-quick-add-btn ${isHovered ? 'quick-add-visible' : ''}`}
            onClick={handleQuickAdd}
          >
            <ShoppingBag size={15} /> Quick Add
          </button>
        )}
      </div>

      {/* Product Meta */}
      <div className="product-info">
        {/* Color Swatches */}
        {product.colors && product.colors.length > 1 && (
          <div className="product-color-swatches" onClick={(e) => e.stopPropagation()}>
            {product.colors.map((c, idx) => (
              <button
                key={idx}
                className={`color-swatch-dot ${selectedColor?.name === c.name ? 'swatch-active' : ''}`}
                style={{ backgroundColor: c.hex }}
                onClick={() => setSelectedColor(c)}
                title={c.name}
                aria-label={`Select color ${c.name}`}
              />
            ))}
          </div>
        )}

        <h3 className="product-name">{product.name}</h3>

        <div className="product-pricing">
          <span className="product-price">₹{product.price.toLocaleString('en-IN')}</span>
          {product.originalPrice && product.originalPrice > product.price && (
            <span className="product-original-price">₹{product.originalPrice.toLocaleString('en-IN')}</span>
          )}
        </div>
      </div>
    </div>
  );
};
