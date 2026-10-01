import { useState, useEffect } from 'react';

/**
 * Checks if the user prefers reduced motion (SSR safe).
 */
export const isReducedMotion = (): boolean => {
  if (typeof window === 'undefined') return false;
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
};

/**
 * Checks if current viewport is a mobile device (for throttling complex animations).
 */
export const isMobileViewport = (breakpoint = 768): boolean => {
  if (typeof window === 'undefined') return false;
  return window.innerWidth < breakpoint;
};

/**
 * React hook to reactively track prefers-reduced-motion user preference.
 */
export const useReducedMotion = (): boolean => {
  const [reduced, setReduced] = useState<boolean>(isReducedMotion);

  useEffect(() => {
    if (typeof window === 'undefined') return;

    const mediaQuery = window.matchMedia('(prefers-reduced-motion: reduce)');
    const onChange = (event: MediaQueryListEvent) => setReduced(event.matches);

    // Initial check
    setReduced(mediaQuery.matches);

    const legacyMediaQuery = mediaQuery as unknown as {
      addListener?: (listener: (e: MediaQueryListEvent) => void) => void;
      removeListener?: (listener: (e: MediaQueryListEvent) => void) => void;
    };

    if (mediaQuery.addEventListener) {
      mediaQuery.addEventListener('change', onChange);
      return () => mediaQuery.removeEventListener('change', onChange);
    } else if (legacyMediaQuery.addListener && legacyMediaQuery.removeListener) {
      // Fallback for older browsers
      legacyMediaQuery.addListener(onChange);
      return () => legacyMediaQuery.removeListener?.(onChange);
    }
  }, []);

  return reduced;
};
