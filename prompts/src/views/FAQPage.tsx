'use client';

import React, { useState, useEffect, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { contentRepository } from '../data/repositories';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, staggerFadeInUp, fadeInUp } from '../animations';
import { Plus, Minus, ArrowRight } from 'lucide-react';
import type { FaqCategory } from '../types';

export const FAQPage: React.FC = () => {
  const { navigate } = useStore();
  const [faqs, setFaqs] = useState<FaqCategory[]>([]);
  const [openItems, setOpenItems] = useState<Record<string, boolean>>({ '0-0': true, '1-0': true });
  const pageRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    let isMounted = true;
    contentRepository.getFaqs().then((data) => {
      if (isMounted) setFaqs(data);
    });
    return () => {
      isMounted = false;
    };
  }, []);

  const toggleAccordion = (catIdx: number, itemIdx: number) => {
    const key = `${catIdx}-${itemIdx}`;
    setOpenItems((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  useGsapContext(
    (_self, isReduced) => {
      if (isReduced) return;

      revealPageHeader({
        breadcrumbs: '.faq-page .breadcrumbs-nav',
        eyebrow: '.faq-header .section-eyebrow',
        title: '.faq-header .faq-title',
        subtitle: '.faq-header .faq-subtitle',
      });

      if (faqs.length > 0) {
        staggerFadeInUp('.faq-category-block', {
          trigger: '.faq-content-container',
          start: 'top 85%',
          stagger: 0.1,
        });
      }

      fadeInUp('.faq-contact-card', {
        trigger: '.faq-contact-card',
        start: 'top 85%',
        delay: 0.1,
      });
    },
    pageRef,
    [faqs.length]
  );

  return (
    <div className="faq-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'FAQ' }]} />

      <div className="faq-header text-center">
        <span className="section-eyebrow">Assistance & Knowledge</span>
        <h1 className="faq-title font-serif">Frequently Asked Questions</h1>
        <p className="faq-subtitle">
          Find answers to common questions about our bags, shipping, materials, and warranty.
        </p>
      </div>

      <div className="faq-content-container">
        {faqs.map((category, catIdx) => (
          <div key={catIdx} className="faq-category-block">
            <h3 className="faq-category-title font-serif">{category.category}</h3>

            <div className="faq-accordion-list">
              {category.items.map((item, itemIdx) => {
                const key = `${catIdx}-${itemIdx}`;
                const isOpen = !!openItems[key];

                return (
                  <div key={itemIdx} className="faq-accordion-row">
                    <button
                      className="faq-question-btn"
                      onClick={() => toggleAccordion(catIdx, itemIdx)}
                      aria-expanded={isOpen}
                    >
                      <span className="faq-question-text">{item.q}</span>
                      <span className="faq-toggle-icon">
                        {isOpen ? <Minus size={18} /> : <Plus size={18} />}
                      </span>
                    </button>

                    {isOpen && (
                      <div className="faq-answer-panel">
                        <p className="faq-answer-text">{item.a}</p>
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        ))}
      </div>

      {/* Still Have Questions CTA Banner */}
      <section className="faq-contact-card">
        <div className="faq-contact-content">
          <h3 className="font-serif faq-contact-title">Still have questions?</h3>
          <p className="faq-contact-desc">
            Our concierge team in Copenhagen is available Monday through Friday to assist with sizing, bespoke inquiries, or corporate gifting.
          </p>
          <button className="btn btn-primary" onClick={() => navigate('contact')}>
            Get in Touch <ArrowRight size={16} />
          </button>
        </div>
      </section>
    </div>
  );
};
