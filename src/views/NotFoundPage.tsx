'use client';

import React, { useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { useGsapContext, revealHero } from '../animations';
import { ArrowRight } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  const { navigate } = useStore();
  const pageRef = useRef<HTMLDivElement>(null);

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealHero({
        image: '.not-found-bg-img',
        eyebrow: '.not-found-number',
        title: '.not-found-title',
        description: '.not-found-desc',
        cta: '.not-found-cta-group',
        meta: '.not-found-quote',
      });
    },
    pageRef,
    []
  );

  return (
    <div className="not-found-page" ref={pageRef}>
      <div className="not-found-bg-wrap">
        <img
          src="https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=2000&q=80"
          alt="Atmospheric Landscape"
          className="not-found-bg-img"
        />
        <div className="not-found-overlay" />
      </div>

      <div className="container not-found-content">
        <span className="not-found-number font-serif">404</span>
        <h1 className="not-found-title font-serif">Page Not Found</h1>
        <p className="not-found-desc">
          The destination you are seeking does not exist or has been moved. Let us guide you back to our curated collection.
        </p>

        <div className="not-found-cta-group">
          <button className="btn btn-primary btn-lg" onClick={() => navigate('home')}>
            Back to Home
          </button>
          <button
            className="btn btn-secondary btn-lg bg-surface"
            onClick={() => navigate('shop')}
          >
            Explore Collection <ArrowRight size={16} />
          </button>
        </div>

        <div className="not-found-quote">
          <span className="font-serif">"Better journeys lie ahead."</span>
        </div>
      </div>
    </div>
  );
};
