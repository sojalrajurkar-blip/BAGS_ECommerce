'use client';

import React, { useState, useEffect, useMemo, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { productRepository, categoryRepository } from '../data/repositories';
import { ProductCard } from '../components/common/ProductCard';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, staggerFadeInUp, fadeInUp } from '../animations';
import { SlidersHorizontal, X, Check } from 'lucide-react';
import type { Product, Category } from '../types';

export const ShopPage: React.FC = () => {
  const pageRef = useRef<HTMLDivElement>(null);

  const [allProducts, setAllProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);

  useEffect(() => {
    let isMounted = true;
    const loadCatalog = async () => {
      const [prods, cats] = await Promise.all([
        productRepository.getProducts(),
        categoryRepository.getCategories(),
      ]);
      if (isMounted) {
        setAllProducts(prods);
        setCategories(cats);
      }
    };
    loadCatalog();
    return () => {
      isMounted = false;
    };
  }, []);

  // Filter States
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [selectedColors, setSelectedColors] = useState<string[]>([]);
  const [selectedMaterials, setSelectedMaterials] = useState<string[]>([]);
  const [priceRange, setPriceRange] = useState<number>(7500);
  const [sortBy, setSortBy] = useState<string>('featured');
  const [isMobileFilterOpen, setIsMobileFilterOpen] = useState<boolean>(false);

  // Available Materials
  const materialsList = ['Recycled Nylon', 'Vegetable Leather', 'Waxed Canvas', 'Cordura', 'Organic Cotton'];
  const colorsList = [
    { name: 'Olive Green', hex: '#555E48' },
    { name: 'Charcoal Black', hex: '#1E1D1A' },
    { name: 'Cognac Brown', hex: '#715B49' },
    { name: 'Warm Taupe', hex: '#B9AD9D' },
    { name: 'Natural Sand', hex: '#D8CFC1' },
  ];

  const toggleColor = (colorName: string) => {
    setSelectedColors((prev) =>
      prev.includes(colorName) ? prev.filter((c) => c !== colorName) : [...prev, colorName]
    );
  };

  const toggleMaterial = (mat: string) => {
    setSelectedMaterials((prev) =>
      prev.includes(mat) ? prev.filter((m) => m !== mat) : [...prev, mat]
    );
  };

  const clearAllFilters = () => {
    setSelectedCategory('all');
    setSelectedColors([]);
    setSelectedMaterials([]);
    setPriceRange(7500);
    setSortBy('featured');
  };

  // Filter & Sort Logic
  const filteredProducts = useMemo(() => {
    return allProducts
      .filter((p) => {
        // Category
        if (selectedCategory !== 'all' && p.category !== selectedCategory) return false;
        // Price
        if (p.price > priceRange) return false;
        // Color
        if (selectedColors.length > 0) {
          const hasColor = p.colors.some((c) => selectedColors.includes(c.name));
          if (!hasColor) return false;
        }
        // Material
        if (selectedMaterials.length > 0) {
          const hasMat = selectedMaterials.some((m) =>
            p.material.toLowerCase().includes(m.toLowerCase())
          );
          if (!hasMat) return false;
        }
        return true;
      })
      .sort((a, b) => {
        if (sortBy === 'price-low') return a.price - b.price;
        if (sortBy === 'price-high') return b.price - a.price;
        if (sortBy === 'rating') return b.rating - a.rating;
        return 0; // default featured
      });
  }, [allProducts, selectedCategory, selectedColors, selectedMaterials, priceRange, sortBy]);

  const hasActiveFilters =
    selectedCategory !== 'all' ||
    selectedColors.length > 0 ||
    selectedMaterials.length > 0 ||
    priceRange < 7500;

  // Scoped Shop entrance animation
  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.shop-page .breadcrumbs-nav',
        eyebrow: '.shop-title-area .section-eyebrow',
        title: '.shop-title-area .shop-title',
        subtitle: '.shop-title-area .shop-subtitle',
        meta: '.shop-top-controls',
      });

      fadeInUp('.shop-filter-sidebar', {
        trigger: '.shop-catalog-layout',
        start: 'top 90%',
        delay: 0.1,
      });

      if (filteredProducts.length > 0) {
        staggerFadeInUp('.products-grid-catalog .product-card', {
          trigger: '.products-grid-catalog',
          start: 'top 88%',
          stagger: 0.04, // Snappy & fast
        });
      }
    },
    pageRef,
    [filteredProducts.length]
  );

  return (
    <div className="shop-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'Shop All Products' }]} />

      {/* Editorial Shop Header */}
      <div className="shop-header">
        <div className="shop-title-area">
          <span className="section-eyebrow">The Complete Catalog</span>
          <h1 className="shop-title font-serif">All Bags & Carry Essentials</h1>
          <p className="shop-subtitle">
            Engineered for daily resilience, ergonomic comfort, and architectural longevity.
          </p>
        </div>

        {/* Top Controls: Mobile Filter trigger + Sort select */}
        <div className="shop-top-controls">
          <button
            className="mobile-filter-btn btn btn-secondary btn-sm"
            onClick={() => setIsMobileFilterOpen(true)}
          >
            <SlidersHorizontal size={15} /> Filters ({filteredProducts.length})
          </button>

          <div className="sort-control-wrap">
            <label className="sort-label" htmlFor="sort-select">
              Sort by:
            </label>
            <select
              id="sort-select"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="sort-select"
            >
              <option value="featured">Featured Collection</option>
              <option value="price-low">Price: Low to High</option>
              <option value="price-high">Price: High to Low</option>
              <option value="rating">Highest Rated</option>
            </select>
          </div>
        </div>
      </div>

      {/* Main Catalog Layout: Sidebar Filters + Products Grid */}
      <div className="shop-layout">
        {/* Left Filter Rail (Desktop) */}
        <aside className={`shop-filter-rail ${isMobileFilterOpen ? 'mobile-filter-drawer-open' : ''}`}>
          <div className="filter-header-mobile">
            <h3 className="font-serif">Filter Products</h3>
            <button onClick={() => setIsMobileFilterOpen(false)} aria-label="Close filters">
              <X size={20} />
            </button>
          </div>

          <div className="filter-sections-wrapper">
            {/* Category Filter */}
            <div className="filter-group">
              <h4 className="filter-group-title">Category</h4>
              <div className="filter-category-list">
                <button
                  className={`filter-cat-btn ${selectedCategory === 'all' ? 'active-cat' : ''}`}
                  onClick={() => setSelectedCategory('all')}
                >
                  <span>All Bags</span>
                  <span className="cat-pill-count">{allProducts.length}</span>
                </button>
                {categories.map((cat) => (
                  <button
                    key={cat.id}
                    className={`filter-cat-btn ${selectedCategory === cat.id ? 'active-cat' : ''}`}
                    onClick={() => setSelectedCategory(cat.id)}
                  >
                    <span>{cat.name}</span>
                    <span className="cat-pill-count">{cat.count}</span>
                  </button>
                ))}
              </div>
            </div>

            <div className="divider" />

            {/* Price Range */}
            <div className="filter-group">
              <div className="filter-group-header">
                <h4 className="filter-group-title">Maximum Price</h4>
                <span className="price-val-indicator">₹{priceRange.toLocaleString('en-IN')}</span>
              </div>
              <input
                type="range"
                min="2000"
                max="7500"
                step="250"
                value={priceRange}
                onChange={(e) => setPriceRange(Number(e.target.value))}
                className="price-range-slider"
              />
              <div className="price-slider-ticks">
                <span>₹2,000</span>
                <span>₹7,500</span>
              </div>
            </div>

            <div className="divider" />

            {/* Color Swatch Filters */}
            <div className="filter-group">
              <h4 className="filter-group-title">Color</h4>
              <div className="filter-color-swatches">
                {colorsList.map((c) => (
                  <button
                    key={c.name}
                    className={`filter-color-circle ${selectedColors.includes(c.name) ? 'color-active' : ''}`}
                    style={{ backgroundColor: c.hex }}
                    onClick={() => toggleColor(c.name)}
                    title={c.name}
                  >
                    {selectedColors.includes(c.name) && <Check size={12} color="#FFF" />}
                  </button>
                ))}
              </div>
            </div>

            <div className="divider" />

            {/* Material Checkboxes */}
            <div className="filter-group">
              <h4 className="filter-group-title">Material</h4>
              <div className="filter-checkbox-list">
                {materialsList.map((mat) => (
                  <label key={mat} className="checkbox-label" onClick={() => toggleMaterial(mat)}>
                    <span
                      className={`custom-checkbox ${
                        selectedMaterials.includes(mat) ? 'checkbox-active' : ''
                      }`}
                    >
                      {selectedMaterials.includes(mat) && <Check size={11} />}
                    </span>
                    <span>{mat}</span>
                  </label>
                ))}
              </div>
            </div>

            {hasActiveFilters && (
              <button className="btn btn-secondary btn-full btn-sm" onClick={clearAllFilters}>
                Reset All Filters
              </button>
            )}
          </div>
        </aside>

        {/* Product Grid Area */}
        <main className="shop-product-area">
          {/* Active Filter Badges */}
          {hasActiveFilters && (
            <div className="active-filters-row">
              <span className="active-filter-label">Active filters:</span>
              {selectedCategory !== 'all' && (
                <span className="active-pill" onClick={() => setSelectedCategory('all')}>
                  {selectedCategory} <X size={12} />
                </span>
              )}
              {selectedColors.map((c) => (
                <span key={c} className="active-pill" onClick={() => toggleColor(c)}>
                  {c} <X size={12} />
                </span>
              ))}
              {selectedMaterials.map((m) => (
                <span key={m} className="active-pill" onClick={() => toggleMaterial(m)}>
                  {m} <X size={12} />
                </span>
              ))}
              {priceRange < 7500 && (
                <span className="active-pill" onClick={() => setPriceRange(7500)}>
                  Under ₹{priceRange.toLocaleString('en-IN')} <X size={12} />
                </span>
              )}
              <button className="clear-all-text" onClick={clearAllFilters}>
                Clear all
              </button>
            </div>
          )}

          {/* Results Count */}
          <div className="catalog-meta-row">
            <span className="results-count-text">
              Showing <strong>{filteredProducts.length}</strong>{' '}
              {filteredProducts.length === 1 ? 'product' : 'products'}
            </span>
          </div>

          {/* Products Grid */}
          {filteredProducts.length === 0 ? (
            <div className="no-products-box">
              <p className="no-products-title font-serif">No products match your selected filters</p>
              <p className="no-products-desc">Try clearing one or more filters to view our full collection.</p>
              <button className="btn btn-primary btn-sm" onClick={clearAllFilters}>
                Reset Filters
              </button>
            </div>
          ) : (
            <div className="shop-grid">
              {filteredProducts.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>
          )}
        </main>
      </div>
    </div>
  );
};
