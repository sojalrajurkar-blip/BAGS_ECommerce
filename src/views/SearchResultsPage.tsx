'use client';

import React, { useState, useEffect, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { productRepository, categoryRepository } from '../data/repositories';
import { ProductCard } from '../components/common/ProductCard';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, staggerFadeInUp, fadeInUp } from '../animations';
import { Search, X } from 'lucide-react';
import type { Product, Category } from '../types';

export const SearchResultsPage: React.FC = () => {
  const { currentRoute } = useStore();
  const initialQuery = (currentRoute.params?.query as string) || '';
  const pageRef = useRef<HTMLDivElement>(null);

  const [query, setQuery] = useState<string>(initialQuery);
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [allProducts, setAllProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);

  useEffect(() => {
    let isMounted = true;
    const loadSearchData = async () => {
      const [prods, cats] = await Promise.all([
        productRepository.getProducts(),
        categoryRepository.getCategories(),
      ]);
      if (isMounted) {
        setAllProducts(prods);
        setCategories(cats);
      }
    };
    loadSearchData();
    return () => {
      isMounted = false;
    };
  }, []);

  const filteredProducts = allProducts.filter((p) => {
    const matchesQuery =
      query.trim() === '' ||
      p.name.toLowerCase().includes(query.toLowerCase()) ||
      p.material.toLowerCase().includes(query.toLowerCase()) ||
      p.category.toLowerCase().includes(query.toLowerCase()) ||
      (p.tagline || '').toLowerCase().includes(query.toLowerCase());

    const matchesCategory = selectedCategory === 'all' || p.category === selectedCategory;

    return matchesQuery && matchesCategory;
  });

  // Scoped Search Results animation
  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.search-results-page .breadcrumbs-nav',
        eyebrow: '.search-header-box .section-eyebrow',
        title: '.search-header-box .search-page-title',
        subtitle: '.search-input-wrapper',
        meta: '.search-category-filters',
      });

      if (filteredProducts.length > 0) {
        staggerFadeInUp('.search-products-grid .product-card', {
          trigger: '.search-products-grid',
          start: 'top 88%',
          stagger: 0.04,
        });
      } else {
        fadeInUp('.search-empty-state', {
          trigger: '.search-empty-state',
          start: 'top 90%',
        });
      }
    },
    pageRef,
    [filteredProducts.length]
  );

  return (
    <div className="search-results-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'Search Results' }]} />

      {/* Search Header Bar */}
      <div className="search-header-box">
        <span className="section-eyebrow">Interactive Catalog Search</span>
        <h1 className="search-page-title font-serif">
          {query ? `Search Results for "${query}"` : 'Search Our Collection'}
        </h1>

        <div className="search-input-wrapper">
          <Search size={20} className="search-input-icon" />
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search by model name, material (e.g. Leather, Nylon), or color..."
            className="search-page-input"
          />
          {query && (
            <button className="search-page-clear" onClick={() => setQuery('')} aria-label="Clear search">
              <X size={18} />
            </button>
          )}
        </div>

        {/* Category Shortcut Pills */}
        <div className="search-category-pills">
          <button
            className={`search-cat-pill ${selectedCategory === 'all' ? 'pill-active' : ''}`}
            onClick={() => setSelectedCategory('all')}
          >
            All Categories ({allProducts.length})
          </button>
          {categories.slice(0, 5).map((cat) => (
            <button
              key={cat.id}
              className={`search-cat-pill ${selectedCategory === cat.id ? 'pill-active' : ''}`}
              onClick={() => setSelectedCategory(cat.id)}
            >
              {cat.name}
            </button>
          ))}
        </div>
      </div>

      {/* Results Display */}
      <div className="search-results-body">
        <div className="results-count-bar">
          <span>
            Found <strong>{filteredProducts.length}</strong>{' '}
            {filteredProducts.length === 1 ? 'item' : 'items'}
          </span>
        </div>

        {filteredProducts.length === 0 ? (
          <div className="search-empty-state">
            <p className="font-serif empty-title">No matching bags found</p>
            <p className="empty-desc">
              We couldn't find any items matching "{query}". Try checking your spelling or explore our popular categories below.
            </p>
            <button
              className="btn btn-primary btn-sm"
              onClick={() => {
                setQuery('');
                setSelectedCategory('all');
              }}
            >
              View All Bags
            </button>
          </div>
        ) : (
          <div className="search-grid-layout">
            {filteredProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
