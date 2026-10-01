/**
 * ============================================================================
 * RÓRA — Motion Tokens (Zero AI Slop)
 * Grounded in: prompts/Premium_Bags_Design_Tokens.md
 * ============================================================================
 * Design Philosophy:
 * Restrained, editorial, tactile, silent luxury.
 * Animations should feel like weight and natural momentum, never mechanical.
 */

export const MOTION_TOKENS = {
  // Durations (in seconds for GSAP)
  duration: {
    micro: 0.15,      // Hover states, button clicks, icon ticks
    fast: 0.25,       // Popovers, badge reveals, dropdowns
    base: 0.45,       // Drawers, modals, card transitions
    smooth: 0.65,     // Section entrance, filter transitions
    editorial: 0.9,   // High-impact storytelling reveals, editorial copy
    hero: 1.2,        // Initial landing hero load sequence
  },

  // GSAP Easing Functions
  ease: {
    standard: 'power2.out',        // Natural deceleration
    smooth: 'power3.out',          // Soft cinematic stop
    editorial: 'power2.inOut',     // Symmetrical luxury easing
    expo: 'expo.out',              // Snappy yet smooth arrival
    subtle: 'sine.out',            // Gentle ambient movement
    enter: 'power3.out',
    exit: 'power2.in',
  },

  // Distances (in pixels)
  distance: {
    subtle: 12,       // Micro reveals, caption entrances
    base: 24,         // Card and content standard rise
    editorial: 40,    // Section heading and statement entrances
    hero: 50,         // Hero initial translateY
  },

  // Stagger intervals (in seconds)
  stagger: {
    fast: 0.04,       // Small lists, swatches, menu items
    base: 0.08,       // Product cards in grid (max 4-6 animated)
    editorial: 0.14,  // Headline lines, featured narrative blocks
  },

  // Scales
  scale: {
    subtleHover: 1.03, // Image zoom on product cards
    revealZoom: 1.06,  // Hero subtle scale settle
  },

  // Responsive Breakpoints
  breakpoints: {
    mobile: 768,
    tablet: 1024,
    desktop: 1280,
  },
} as const;

export default MOTION_TOKENS;
