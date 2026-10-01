'use client';

import React, { useState, useEffect, useRef } from 'react';
import { useStore } from '../../context/StoreContext';
import { Search, X, ArrowRight, History } from 'lucide-react';
import { productRepository, categoryRepository } from '../../data/repositories';
import type { Product, Category } from '../../types';

export const SearchModal: React.FC = () => {
  const { isSearchModalOpen, setIsSearchModalOpen, navigate } = useStore();
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [recentSearches, setRecentSearches] = useState<string[]>([
    'Nomad Backpack',
    'Leather Tote',
    'Olive Canvas',
    'Sling',
  ]);
  const [filteredProducts, setFilteredProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (isSearchModalOpen) {
      setTimeout(() => inputRef.current?.focus(), 80);
      categoryRepository.getCategories().then(setCategories);
    }
  }, [isSearchModalOpen]);

  useEffect(() => {
    if (!searchTerm.trim()) {
      setFilteredProducts([]);
      return;
    }
    let isCurrent = true;
    productRepository.searchProducts(searchTerm).then((results) => {
      if (isCurrent) setFilteredProducts(results);
    });
    return () => {
      isCurrent = false;
    };
  }, [searchTerm]);

  if (!isSearchModalOpen) return null;

  const handleSelectProduct = (productId: string) => {
    setIsSearchModalOpen(false);
    navigate('product', { productId });
  };

  const handleFullSearch = (term: string) => {
    if (!term) return;
    setRecentSearches((prev) => [term, ...prev.filter((t) => t !== term)].slice(0, 5));
    setIsSearchModalOpen(false);
    navigate('search', { query: term });
  };

  return (
    <div className="search-modal-overlay" onClick={() => setIsSearchModalOpen(false)}>
      <div className="search-modal-container" onClick={(e) => e.stopPropagation()}>
        {/* Search Input Bar */}
        <div className="search-bar-row">
          <Search size={22} className="search-bar-icon" />
          <input
            ref={inputRef}
            type="text"
            placeholder="Search for bags, materials, or collections..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') handleFullSearch(searchTerm);
            }}
            className="search-modal-input"
          />
          {searchTerm && (
            <button className="search-clear-btn" onClick={() => setSearchTerm('')} aria-label="Clear search">
              <X size={18} />
            </button>
          )}
          <button className="search-modal-close" onClick={() => setIsSearchModalOpen(false)}>
            ESC
          </button>
        </div>

        {/* Content Area */}
        <div className="search-modal-body">
          {searchTerm.trim() ? (
            <div className="search-results-section">
              <div className="search-results-header">
                <span className="results-count">
                  {filteredProducts.length} {filteredProducts.length === 1 ? 'result' : 'results'} for "{searchTerm}"
                </span>
                {filteredProducts.length > 0 && (
                  <button
                    className="btn-text"
                    onClick={() => handleFullSearch(searchTerm)}
                  >
                    View all in shop <ArrowRight size={14} />
                  </button>
                )}
              </div>

              {filteredProducts.length === 0 ? (
                <div className="search-no-results">
                  <p className="no-results-title font-serif">No products found</p>
                  <p className="no-results-desc">Try searching for "Backpack", "Leather", "Olive", or "Tote".</p>
                </div>
              ) : (
                <div className="search-product-grid">
                  {filteredProducts.map((p) => (
                    <div
                      key={p.id}
                      className="search-product-card"
                      onClick={() => handleSelectProduct(p.id)}
                    >
                      <div className="search-prod-img-wrap">
                        <img src={p.images[0]} alt={p.name} />
                      </div>
                      <div className="search-prod-info">
                        <span className="search-prod-cat">{p.category}</span>
                        <h4 className="search-prod-title">{p.name}</h4>
                        <span className="search-prod-price">₹{p.price.toLocaleString('en-IN')}</span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ) : (
            <div className="search-suggestions-layout">
              {/* Recent Searches */}
              <div className="search-section">
                <span className="search-section-label">
                  <History size={14} /> Recent Searches
                </span>
                <div className="search-pills">
                  {recentSearches.map((term, i) => (
                    <button
                      key={i}
                      className="search-pill"
                      onClick={() => {
                        setSearchTerm(term);
                        handleFullSearch(term);
                      }}
                    >
                      {term}
                    </button>
                  ))}
                </div>
              </div>

              {/* Popular Categories */}
              <div className="search-section">
                <span className="search-section-label">Explore by Category</span>
                <div className="search-category-grid">
                  {categories.slice(0, 6).map((cat) => (
                    <button
                      key={cat.id}
                      className="search-category-tile"
                      onClick={() => {
                        setIsSearchModalOpen(false);
                        navigate('category', { categoryId: cat.id });
                      }}
                    >
                      <span className="cat-tile-name">{cat.name}</span>
                      <span className="cat-tile-count">{cat.count} items</span>
                    </button>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
