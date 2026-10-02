'use client';

import React, { useState, useEffect, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import {
  productRepository,
  categoryRepository,
  reviewRepository,
} from '../data/repositories';
import { ProductCard } from '../components/common/ProductCard';
import { RoraProductSequence } from '../components/product/RoraProductSequence';
import {
  useGsapContext,
  revealHero,
  revealSectionHeader,
  fadeInUp,
  staggerFadeInUp,
  parallaxImage,
} from '../animations';
import { ArrowRight, Star } from 'lucide-react';
import type { Category, Product, Review } from '../types';

export const HomePage: React.FC = () => {
  const { navigate } = useStore();
  const pageRef = useRef<HTMLDivElement>(null);

  const [categories, setCategories] = useState<Category[]>([]);
  const [bestSellers, setBestSellers] = useState<Product[]>([]);
  const [reviews, setReviews] = useState<Review[]>([]);

  useEffect(() => {
    let isMounted = true;
    const loadHomeData = async () => {
      const [cats, best, revs] = await Promise.all([
        categoryRepository.getCategories(),
        productRepository.getBestSellers(4),
        reviewRepository.getAllReviews(),
      ]);
      if (isMounted) {
        setCategories(cats);
        setBestSellers(best);
        setReviews(revs);
      }
    };
    loadHomeData();
    return () => {
      isMounted = false;
    };
  }, []);

  // Initialize scoped editorial animations
  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      // 1. Hero Entrance Sequence
      revealHero({
        image: '.hero-backdrop-image',
        eyebrow: '.hero-eyebrow',
        title: '.hero-title',
        description: '.hero-description',
        cta: '.hero-cta-group',
        meta: '.hero-bottom-meta',
      });

      // 2. Category Showcase Entrance
      revealSectionHeader(
        {
          eyebrow: '.category-showcase-section .section-eyebrow',
          title: '.category-showcase-section .section-title',
          subtitle: '.category-showcase-section .section-subtitle',
        },
        '.category-showcase-section'
      );
      if (categories.length > 0) {
        staggerFadeInUp('.category-tile', {
          trigger: '.category-scroll-grid',
          start: 'top 85%',
          stagger: 0.06,
        });
      }

      // 3. Editorial Split Craftsmanship Section
      parallaxImage('.editorial-image', '.editorial-split-section', { speed: 6 });
      fadeInUp('.editorial-text-panel', {
        trigger: '.editorial-split-section',
        start: 'top 80%',
      });

      // 4. Best Sellers Entrance
      revealSectionHeader(
        {
          eyebrow: '.best-sellers-section .section-eyebrow',
          title: '.best-sellers-section .section-title',
          subtitle: '.best-sellers-section .section-subtitle',
        },
        '.best-sellers-section'
      );
      if (bestSellers.length > 0) {
        staggerFadeInUp('.best-sellers-section .product-card', {
          trigger: '.best-sellers-section .products-grid-4',
          start: 'top 85%',
          stagger: 0.08,
        });
      }

      // 5. Lifestyle Promo Banner Parallax
      parallaxImage('.banner-bg-img', '.lifestyle-banner-section', { speed: 8 });
      fadeInUp('.banner-card', {
        trigger: '.lifestyle-banner-section',
        start: 'top 80%',
      });

      // 6. Testimonial Reviews Entrance
      revealSectionHeader(
        {
          eyebrow: '.reviews-section .section-eyebrow',
          title: '.reviews-section .section-title',
        },
        '.reviews-section'
      );
      if (reviews.length > 0) {
        staggerFadeInUp('.review-card', {
          trigger: '.reviews-grid',
          start: 'top 85%',
          stagger: 0.08,
        });
      }
    },
    pageRef,
    [categories, bestSellers, reviews]
  );

  return (
    <div className="home-page" ref={pageRef}>
      {/* 1. 300-FRAME GSAP CANVAS STORYTELLING HERO SECTION */}
      <RoraProductSequence
        isHeroMode={true}
        eyebrow="The 2026 Collection — Handcrafted Atelier"
        title="Thoughtfully Designed for Modern Journeys"
        description="Scroll down to explore the 300-frame precision deconstruction of the RÓRA Handcrafted Silhouette."
        productName="The Artisan Atelier Silhouette"
        primaryCtaText="Explore Collection"
        onPrimaryCta={() => navigate('shop')}
        secondaryCtaText="Discover Our Story"
        onSecondaryCta={() => navigate('about')}
      />

      {/* 2. EXPLORE OUR COLLECTION (Category Showcase) */}
      <section className="section-md category-showcase-section">
        <div className="container">
          <div className="section-header-row">
            <div>
              <span className="section-eyebrow">Categories</span>
              <h2 className="section-title">Explore Our Collection</h2>
              <p className="section-subtitle">
                Find the perfect bag engineered for every chapter of your journey.
              </p>
            </div>
            <button className="btn-text" onClick={() => navigate('shop')}>
              View All <ArrowRight size={16} />
            </button>
          </div>

          <div className="category-scroll-grid">
            {categories.map((cat) => (
              <div
                key={cat.id}
                className="category-tile"
                onClick={() => navigate('category', { categoryId: cat.id })}
              >
                <div className="cat-tile-img-box">
                  <img src={cat.heroImage} alt={cat.name} loading="lazy" />
                </div>
                <div className="cat-tile-meta">
                  <h3 className="cat-tile-title">{cat.name}</h3>
                  <span className="cat-tile-subtitle">{cat.count} Styles</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 3. EDITORIAL STORY BLOCK — "Crafted for Real Life" */}
      <section className="editorial-split-section">
        <div className="container">
          <div className="editorial-split-grid">
            <div className="editorial-image-frame">
              <img
                src="https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1400&q=80"
                alt="Leather craft details"
                className="editorial-image"
                loading="lazy"
              />
            </div>
            <div className="editorial-text-panel">
              <span className="section-eyebrow">Ethos & Materials</span>
              <h2 className="editorial-heading font-serif">
                Crafted for Real Life
              </h2>
              <p className="editorial-body">
                We believe that the objects you carry every day should be as resilient as they are refined. Our bags combine weatherproof recycled nylon, Tuscan vegetable-tanned leather, and solid brass hardware.
              </p>
              <p className="editorial-body">
                Each piece is engineered without unnecessary ornamentation—delivering silent functionality, balanced weight distribution, and a silhouette that never ages.
              </p>
              <div className="editorial-cta">
                <button className="btn btn-secondary" onClick={() => navigate('about')}>
                  Our Craftsmanship & Story <ArrowRight size={16} />
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 4. BEST SELLERS SHOWCASE */}
      <section className="section-md best-sellers-section">
        <div className="container">
          <div className="section-header-row">
            <div>
              <span className="section-eyebrow">Curated Favorites</span>
              <h2 className="section-title">Best Sellers</h2>
              <p className="section-subtitle">
                Our most loved styles, chosen by thousands of travelers and creators worldwide.
              </p>
            </div>
            <button className="btn-text" onClick={() => navigate('shop')}>
              View All Best Sellers <ArrowRight size={16} />
            </button>
          </div>

          <div className="products-grid-4">
            {bestSellers.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        </div>
      </section>

      {/* 5. FULL-BLEED LIFESTYLE PROMO BANNER */}
      <section className="lifestyle-banner-section">
        <div className="banner-bg-wrap">
          <img
            src="https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=2000&q=85"
            alt="Mountain Expedition Lifestyle"
            className="banner-bg-img"
            loading="lazy"
          />
          <div className="banner-overlay" />
        </div>
        <div className="container banner-content-container">
          <div className="banner-card">
            <span className="banner-eyebrow">Designed for the Open Road</span>
            <h2 className="banner-heading font-serif">
              More Than a Bag. A Better Way to Travel.
            </h2>
            <p className="banner-subtext">
              Engineered with ergonomic lumbar support, airline cabin-approved dimensions, and effortless dual-access compartments.
            </p>
            <button
              className="btn btn-primary"
              onClick={() => navigate('category', { categoryId: 'travel-bags' })}
            >
              Discover Travel Duffels <ArrowRight size={16} />
            </button>
          </div>
        </div>
      </section>

      {/* 6. VERIFIED EDITORIAL REVIEWS */}
      <section className="section-md reviews-section">
        <div className="container">
          <div className="reviews-header text-center">
            <span className="section-eyebrow">Tested on the Road</span>
            <h2 className="section-title">What Explorers Say</h2>
          </div>

          <div className="reviews-grid">
            {reviews.map((rev) => (
              <div key={rev.id} className="review-card">
                <div className="review-stars">
                  {[...Array(rev.rating)].map((_, i) => (
                    <Star key={i} size={15} fill="var(--color-olive-500)" color="var(--color-olive-500)" />
                  ))}
                </div>
                <h4 className="review-title font-serif">"{rev.title}"</h4>
                <p className="review-content">{rev.content}</p>
                <div className="review-author-meta">
                  <div>
                    <span className="review-author-name">{rev.author}</span>
                    <span className="review-author-role">{rev.role}</span>
                  </div>
                  <span className="review-product-tag">{rev.productName}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
};
