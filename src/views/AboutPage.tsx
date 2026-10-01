'use client';

import React, { useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealHero, fadeInUp, parallaxImage } from '../animations';
import { ArrowRight, Award, Feather, ShieldCheck } from 'lucide-react';

export const AboutPage: React.FC = () => {
  const { navigate } = useStore();
  const pageRef = useRef<HTMLDivElement>(null);

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealHero({
        image: '.about-hero-img',
        eyebrow: '.about-hero-content .section-eyebrow',
        title: '.about-hero-content .about-title',
        description: '.about-hero-content .about-subtitle',
      });

      fadeInUp('.narrative-grid', {
        trigger: '.narrative-grid',
        start: 'top 85%',
      });

      parallaxImage('.craft-img-frame img', '.craft-showcase-section', { speed: 6 });
      fadeInUp('.craft-text-panel', {
        trigger: '.craft-showcase-section',
        start: 'top 82%',
      });
    },
    pageRef,
    []
  );

  return (
    <div className="about-page" ref={pageRef}>
      {/* 1. Hero Editorial Image */}
      <section className="about-hero">
        <div className="about-hero-img-wrap">
          <img
            src="https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=2000&q=85"
            alt="Handcrafting leather"
            className="about-hero-img"
          />
          <div className="about-hero-overlay" />
        </div>
        <div className="container about-hero-content">
          <Breadcrumbs items={[{ label: 'Our Story' }]} />
          <span className="section-eyebrow text-sand-400">Philosophy & Heritage</span>
          <h1 className="about-title font-serif">Thoughtfully Designed for Modern Journeys</h1>
          <p className="about-subtitle">
            Founded on the belief that everyday objects should carry timeless proportion, unyielding durability, and silent sophistication.
          </p>
        </div>
      </section>

      {/* 2. Brand Origin Narrative */}
      <section className="container section-md">
        <div className="narrative-grid">
          <div className="narrative-col">
            <span className="section-eyebrow">The Origin</span>
            <h2 className="font-serif narrative-heading">
              More Than Just Bags.<br />A Considered Lifestyle.
            </h2>
          </div>
          <div className="narrative-text-col">
            <p className="narrative-p">
              RÓRA began in 2022 out of a singular frustration: modern luggage and backpacks had become either over-engineered tech cases covered in plastic webbing, or fragile luxury novelties unsuited for unpredictable weather.
            </p>
            <p className="narrative-p">
              We set out to create an alternative: pieces stripped of extraneous ornamentation, built from weatherproof sustainable textiles and vegetable-tanned leathers that look as natural on a morning train in Zurich as in a creative studio in Milan.
            </p>
          </div>
        </div>
      </section>

      {/* 3. Craftsmanship & Materials Showcase */}
      <section className="craft-showcase-section">
        <div className="container">
          <div className="craft-grid">
            <div className="craft-img-frame">
              <img
                src="https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80"
                alt="Workshop craftsmanship"
                loading="lazy"
              />
            </div>
            <div className="craft-info-panel">
              <span className="section-eyebrow">Materials & Responsibility</span>
              <h2 className="font-serif craft-title">Honest Materials. Lasting Quality.</h2>
              <p className="craft-p">
                We partner exclusively with family-owned ateliers across Portugal and Tuscany that uphold multi-generational traditions of leather tanning and technical canvas assembly.
              </p>

              <div className="values-list">
                <div className="value-item">
                  <Feather size={20} className="value-icon" />
                  <div>
                    <h4 className="value-title">900D Recycled Nylon</h4>
                    <p className="value-desc">
                      Woven from ocean-bound discarded polymers, offering supreme abrasion resistance with zero virgin plastic debt.
                    </p>
                  </div>
                </div>

                <div className="value-item">
                  <Award size={20} className="value-icon" />
                  <div>
                    <h4 className="value-title">Vegetable-Tanned Tuscan Leather</h4>
                    <p className="value-desc">
                      Tanned with tree barks and mimosa extracts, developing a deep, golden patina over decades.
                    </p>
                  </div>
                </div>

                <div className="value-item">
                  <ShieldCheck size={20} className="value-icon" />
                  <div>
                    <h4 className="value-title">Lifetime Guarantee</h4>
                    <p className="value-desc">
                      If any seam, zipper, or strap fails, we repair it in our European workshop free of charge.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 4. Bottom CTA */}
      <section className="container section-md text-center">
        <span className="section-eyebrow">Start Your Journey</span>
        <h2 className="section-title font-serif">Explore The 2026 Collection</h2>
        <p className="section-subtitle mx-auto mb-24">
          Discover our full range of handcrafted backpacks, totes, and travel bags.
        </p>
        <button className="btn btn-primary btn-lg" onClick={() => navigate('shop')}>
          Browse Collection <ArrowRight size={18} />
        </button>
      </section>
    </div>
  );
};
