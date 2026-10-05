'use client';

import React from 'react';
import { usePathname } from 'next/navigation';
import { useStore } from '../../context/StoreContext';
import { Search, User, Heart, ShoppingBag, Menu, X } from 'lucide-react';

export const Header: React.FC = () => {
  const pathname = usePathname();
  const {
    navigate,
    currentRoute,
    cartItemCount,
    wishlist,
    setIsCartDrawerOpen,
    setIsSearchModalOpen,
    isMobileMenuOpen,
    setIsMobileMenuOpen
  } = useStore();

  const [mounted, setMounted] = React.useState(false);

  React.useEffect(() => {
    setMounted(true);
  }, []);

  if (pathname?.startsWith('/admin') || currentRoute.page === 'admin') {
    return null;
  }

  const isCurrent = (page: string) => currentRoute.page === page;

  return (
    <>
      <header className="site-header">
        <div className="header-container">
          {/* Mobile Menu Toggle Button */}
          <button
            className="mobile-menu-btn"
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            aria-label="Toggle navigation menu"
          >
            {isMobileMenuOpen ? <X size={22} /> : <Menu size={22} />}
          </button>

          {/* Brand Logo / Wordmark */}
          <div className="header-brand" onClick={() => navigate('home')}>
            <span className="brand-logo">RÓRA</span>
          </div>

          {/* Desktop Navigation */}
          <nav className="desktop-nav" aria-label="Main Navigation">
            <button
              className={`nav-link ${isCurrent('shop') ? 'nav-link-active' : ''}`}
              onClick={() => navigate('shop')}
            >
              Shop
            </button>
            <button
              className={`nav-link ${isCurrent('category') ? 'nav-link-active' : ''}`}
              onClick={() => navigate('shop')}
            >
              Collections
            </button>
            <button
              className={`nav-link ${isCurrent('about') ? 'nav-link-active' : ''}`}
              onClick={() => navigate('about')}
            >
              About
            </button>
            <button
              className={`nav-link ${isCurrent('journal') ? 'nav-link-active' : ''}`}
              onClick={() => navigate('journal')}
            >
              Journal
            </button>
          </nav>

          {/* Header Action Icons */}
          <div className="header-actions">
            <button
              className="action-icon-btn"
              onClick={() => setIsSearchModalOpen(true)}
              aria-label="Search"
            >
              <Search size={20} strokeWidth={1.8} />
            </button>

            <button
              className="action-icon-btn"
              onClick={() => navigate('account')}
              aria-label="Customer Account"
            >
              <User size={20} strokeWidth={1.8} />
            </button>

            <button
              className="action-icon-btn action-icon-with-badge"
              onClick={() => navigate('wishlist')}
              aria-label="Wishlist"
            >
              <Heart size={20} strokeWidth={1.8} />
              {mounted && wishlist.length > 0 && (
                <span className="action-badge">{wishlist.length}</span>
              )}
            </button>

            <button
              className="action-icon-btn action-icon-with-badge cart-trigger-btn"
              onClick={() => setIsCartDrawerOpen(true)}
              aria-label="Shopping Cart"
            >
              <ShoppingBag size={20} strokeWidth={1.8} />
              {mounted && cartItemCount > 0 && (
                <span className="action-badge">{cartItemCount}</span>
              )}
            </button>
          </div>
        </div>

        {/* Mobile Navigation Drawer */}
        {isMobileMenuOpen && (
          <div className="mobile-menu-overlay" onClick={() => setIsMobileMenuOpen(false)}>
            <div className="mobile-menu-drawer" onClick={(e) => e.stopPropagation()}>
              <div className="mobile-menu-header">
                <span className="brand-logo" onClick={() => navigate('home')}>RÓRA</span>
                <button onClick={() => setIsMobileMenuOpen(false)} aria-label="Close menu">
                  <X size={22} />
                </button>
              </div>

              <div className="mobile-nav-links">
                <button className="mobile-nav-link" onClick={() => navigate('shop')}>Shop All</button>
                <button className="mobile-nav-link" onClick={() => navigate('category', { categoryId: 'backpacks' })}>Backpacks</button>
                <button className="mobile-nav-link" onClick={() => navigate('category', { categoryId: 'laptop-bags' })}>Laptop Bags</button>
                <button className="mobile-nav-link" onClick={() => navigate('category', { categoryId: 'handbags' })}>Handbags</button>
                <button className="mobile-nav-link" onClick={() => navigate('category', { categoryId: 'sling-bags' })}>Sling Bags</button>
                <button className="mobile-nav-link" onClick={() => navigate('category', { categoryId: 'travel-bags' })}>Travel Bags</button>
                <button className="mobile-nav-link" onClick={() => navigate('category', { categoryId: 'tote-bags' })}>Tote Bags</button>
                
                <div className="mobile-nav-divider" />
                
                <button className="mobile-nav-link" onClick={() => navigate('about')}>About / Our Story</button>
                <button className="mobile-nav-link" onClick={() => navigate('journal')}>Journal</button>
                <button className="mobile-nav-link" onClick={() => navigate('shipping')}>Shipping & Delivery</button>
                <button className="mobile-nav-link" onClick={() => navigate('faq')}>FAQ</button>
                <button className="mobile-nav-link" onClick={() => navigate('contact')}>Contact</button>
                <button className="mobile-nav-link" onClick={() => navigate('admin')}>Admin Portal</button>
              </div>

              <div className="mobile-menu-footer">
                <p className="mobile-footer-tagline">Thoughtfully Designed for Modern Journeys</p>
              </div>
            </div>
          </div>
        )}
      </header>
    </>
  );
};
