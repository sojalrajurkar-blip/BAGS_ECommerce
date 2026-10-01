'use client';

import React, { useState, useEffect, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { categoryRepository, productRepository } from '../data/repositories';
import { ProductCard } from '../components/common/ProductCard';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealHero, staggerFadeInUp, fadeInUp } from '../animations';
import type { Category, Product } from '../types';

export interface CategoryPageProps {
  categoryId?: string;
}

export const CategoryPage: React.FC<CategoryPageProps> = ({ categoryId: propCategoryId } = {}) => {
  const { currentRoute, navigate } = useStore();
  const categoryId =
    propCategoryId ||
    (currentRoute.params?.categoryId as string) ||
    (currentRoute.params?.slug as string) ||
    'backpacks';
  const pageRef = useRef<HTMLDivElement>(null);

  const [category, setCategory] = useState<Category | null>(null);
  const [products, setProducts] = useState<Product[]>([]);
  const [sortBy, setSortBy] = useState<string>('featured');

  useEffect(() => {
    let isMounted = true;
    const loadCategoryData = async () => {
      const [cats, prods] = await Promise.all([
        categoryRepository.getCategories(),
        productRepository.getProducts({ category: categoryId }),
      ]);
      if (isMounted) {
        const currentCat =
          cats.find((c: Category) => c.id === categoryId || c.slug === categoryId) || cats[0];
        setCategory(currentCat);
        setProducts(prods);
      }
    };
    loadCategoryData();
    return () => {
      isMounted = false;
    };
  }, [categoryId]);

  // Filter products by current category and sort
  const categoryProducts = [...products].sort((a, b) => {
    if (sortBy === 'price-low') return a.price - b.price;
    if (sortBy === 'price-high') return b.price - a.price;
    if (sortBy === 'rating') return b.rating - a.rating;
    return 0;
  });

  // Scoped Category animations
  useGsapContext(
    (_self, isReduced) => {
      if (isReduced || !category) return;

      revealHero({
        image: '.category-hero-img',
        eyebrow: '.category-eyebrow',
        title: '.category-title',
        description: '.category-desc',
      });

      fadeInUp('.category-toolbar', {
        trigger: '.category-toolbar',
        start: 'top 90%',
        delay: 0.1,
      });

      if (categoryProducts.length > 0) {
        staggerFadeInUp('.category-products-grid .product-card', {
          trigger: '.category-products-grid',
          start: 'top 88%',
          stagger: 0.05,
        });
      }
    },
    pageRef,
    [category, categoryProducts.length]
  );

  if (!category) return null;

  return (
    <div className="category-page" ref={pageRef}>
      {/* Category Hero Banner */}
      <section className="category-hero">
        <div className="category-hero-img-wrap">
          <img
            src={category.heroImage || category.image}
            alt={category.name}
            className="category-hero-img"
          />
          <div className="category-hero-overlay" />
        </div>
        <div className="container category-hero-content">
          <Breadcrumbs items={[{ label: 'Shop', page: 'shop' }, { label: category.name }]} />
          <span className="category-eyebrow">Collection</span>
          <h1 className="category-title font-serif">{category.name}</h1>
          <p className="category-desc">{category.description}</p>
        </div>
      </section>

      {/* Category Grid Section */}
      <section className="container section-md">
        <div className="category-toolbar">
          <span className="category-results-count">
            {categoryProducts.length} {categoryProducts.length === 1 ? 'Design' : 'Designs'} Available
          </span>

          <div className="sort-control-wrap">
            <label className="sort-label" htmlFor="cat-sort-select">
              Sort by:
            </label>
            <select
              id="cat-sort-select"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="sort-select"
            >
              <option value="featured">Featured</option>
              <option value="price-low">Price: Low to High</option>
              <option value="price-high">Price: High to Low</option>
              <option value="rating">Highest Rated</option>
            </select>
          </div>
        </div>

        {categoryProducts.length === 0 ? (
          <div className="no-cat-products">
            <p className="font-serif">New styles arriving shortly in this collection.</p>
            <button className="btn btn-secondary btn-sm" onClick={() => navigate('shop')}>
              Explore All Bags
            </button>
          </div>
        ) : (
          <div className="category-grid">
            {categoryProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
};
