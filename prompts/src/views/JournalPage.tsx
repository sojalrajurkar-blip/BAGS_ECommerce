'use client';

import React, { useState, useEffect, useRef } from 'react';
import { useStore } from '../context/StoreContext';
import { contentRepository } from '../data/repositories';
import { ContentEntry } from '../types/domain';
import { Breadcrumbs } from '../components/common/Breadcrumbs';
import { useGsapContext, revealPageHeader, staggerFadeInUp, fadeInUp } from '../animations';
import { Calendar, Clock, ArrowRight, ArrowLeft } from 'lucide-react';

export interface JournalPageProps {
  articleSlug?: string;
}

export const JournalPage: React.FC<JournalPageProps> = ({ articleSlug: propSlug } = {}) => {
  const { currentRoute, navigate } = useStore();
  const activeArticleSlug = propSlug || currentRoute.params?.articleSlug;
  const [articles, setArticles] = useState<ContentEntry[]>([]);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const pageRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    let isMounted = true;
    contentRepository.getJournalArticles().then(arts => {
      if (isMounted) setArticles(arts);
    });
    return () => { isMounted = false; };
  }, []);

  const activeArticle = articles.find(a => a.slug === activeArticleSlug);
  const categories = ['All', 'Travel & Mobility', 'Philosophy & Lifestyle', 'Craftsmanship & Materials', 'Product Stories'];

  const filteredArticles = selectedCategory === 'All'
    ? articles
    : articles.filter(a => a.category === selectedCategory);

  useGsapContext((self, isReduced) => {
    if (isReduced) return;

    if (activeArticle) {
      revealPageHeader({
        breadcrumbs: '.article-page .breadcrumbs-nav',
        eyebrow: '.article-category-badge',
        title: '.article-headline',
        subtitle: '.article-meta-bar',
      });

      fadeInUp('.article-hero-image-wrap', {
        trigger: '.article-hero-image-wrap',
        start: 'top 85%',
        delay: 0.1,
      });

      fadeInUp('.article-body-content', {
        trigger: '.article-body-content',
        start: 'top 85%',
        delay: 0.15,
      });
    } else {
      revealPageHeader({
        breadcrumbs: '.journal-page .breadcrumbs-nav',
        eyebrow: '.journal-header .section-eyebrow',
        title: '.journal-header .journal-title',
        subtitle: '.journal-header .journal-subtitle',
        meta: '.journal-category-tabs',
      });

      if (filteredArticles.length > 0) {
        fadeInUp('.journal-featured-card', {
          trigger: '.journal-featured-card',
          start: 'top 85%',
        });

        staggerFadeInUp('.journal-grid .journal-card', {
          trigger: '.journal-grid',
          start: 'top 85%',
          stagger: 0.08,
        });
      }
    }
  }, pageRef, [activeArticleSlug, selectedCategory, filteredArticles.length]);

  // Single Article View
  if (activeArticle) {
    return (
      <div className="article-page container section-sm" ref={pageRef}>
        <Breadcrumbs
          items={[
            { label: 'Journal', page: 'journal' },
            { label: activeArticle.title }
          ]}
        />

        <article className="article-container">
          <button className="btn-text article-back-btn" onClick={() => navigate('journal')}>
            <ArrowLeft size={16} /> Back to Journal
          </button>

          <span className="article-category-badge">{activeArticle.category}</span>
          <h1 className="article-headline font-serif">{activeArticle.title}</h1>

          <div className="article-meta-bar">
            <span className="article-meta-item"><Calendar size={14} /> {activeArticle.date}</span>
            <span className="article-meta-item"><Clock size={14} /> {activeArticle.readTime}</span>
            <span className="article-meta-item">By RÓRA Editorial Team</span>
          </div>

          <div className="article-hero-image-wrap">
            <img src={activeArticle.image} alt={activeArticle.title} />
          </div>

          <div className="article-body-content">
            <p className="article-lead-p">{activeArticle.excerpt}</p>
            {activeArticle.content?.split('###').map((section: string, idx: number) => {
              if (!section.trim()) return null;
              const lines = section.trim().split('\n');
              const heading = lines[0];
              const body = lines.slice(1).join('\n');
              return (
                <div key={idx} className="article-section-block">
                  <h3 className="font-serif article-subheading">{heading}</h3>
                  <p className="article-paragraph">{body}</p>
                </div>
              );
            })}
          </div>
        </article>
      </div>
    );
  }

  // Journal Grid View
  return (
    <div className="journal-page container section-sm" ref={pageRef}>
      <Breadcrumbs items={[{ label: 'Journal' }]} />

      <div className="journal-header">
        <span className="section-eyebrow">Dispatches & Stories</span>
        <h1 className="journal-title font-serif">The RÓRA Journal</h1>
        <p className="journal-subtitle">
          Stories, tips, and design essays for your next journey.
        </p>

        {/* Category Filter Pills */}
        <div className="journal-category-tabs">
          {categories.map((cat) => (
            <button
              key={cat}
              className={`journal-tab-btn ${selectedCategory === cat ? 'tab-active' : ''}`}
              onClick={() => setSelectedCategory(cat)}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      {/* Featured First Article */}
      {filteredArticles.length > 0 && (
        <div
          className="journal-featured-card"
          onClick={() => navigate('journal', { articleSlug: filteredArticles[0].slug })}
        >
          <div className="featured-img-wrap">
            <img src={filteredArticles[0].image} alt={filteredArticles[0].title} />
          </div>
          <div className="featured-meta-wrap">
            <span className="journal-card-cat">{filteredArticles[0].category}</span>
            <h2 className="featured-title font-serif">{filteredArticles[0].title}</h2>
            <p className="featured-excerpt">{filteredArticles[0].excerpt}</p>
            <div className="journal-card-footer">
              <span>{filteredArticles[0].date} — {filteredArticles[0].readTime}</span>
              <span className="read-more-btn">Read Article <ArrowRight size={14} /></span>
            </div>
          </div>
        </div>
      )}

      {/* Rest of Articles Grid */}
      <div className="journal-grid">
        {filteredArticles.slice(1).map((article) => (
          <article
            key={article.id}
            className="journal-card"
            onClick={() => navigate('journal', { articleSlug: article.slug })}
          >
            <div className="journal-card-img-wrap">
              <img src={article.image} alt={article.title} loading="lazy" />
            </div>
            <div className="journal-card-body">
              <span className="journal-card-cat">{article.category}</span>
              <h3 className="journal-card-title font-serif">{article.title}</h3>
              <p className="journal-card-excerpt">{article.excerpt}</p>
              <div className="journal-card-footer">
                <span>{article.date} — {article.readTime}</span>
                <span className="read-more-btn">Read <ArrowRight size={14} /></span>
              </div>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
};
