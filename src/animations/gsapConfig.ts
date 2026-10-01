import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { MOTION_TOKENS } from './motionTokens';

// Safe SSR / Client-side Plugin Registration
if (typeof window !== 'undefined') {
  gsap.registerPlugin(ScrollTrigger);

  // Set default GSAP settings for RÓRA
  gsap.defaults({
    duration: MOTION_TOKENS.duration.smooth,
    ease: MOTION_TOKENS.ease.smooth,
  });

  // Optimize ScrollTrigger refresh settings
  ScrollTrigger.config({
    limitCallbacks: true,
    ignoreMobileResize: true,
  });
}

/**
 * Recalculates ScrollTrigger trigger positions (e.g. after dynamic DOM changes or route switches).
 */
export const refreshScrollTrigger = (): void => {
  if (typeof window !== 'undefined') {
    ScrollTrigger.refresh();
  }
};

/**
 * Clears all active ScrollTrigger instances.
 */
export const killAllScrollTriggers = (): void => {
  if (typeof window !== 'undefined') {
    ScrollTrigger.getAll().forEach((trigger) => trigger.kill());
  }
};

export { gsap, ScrollTrigger };
export default gsap;
