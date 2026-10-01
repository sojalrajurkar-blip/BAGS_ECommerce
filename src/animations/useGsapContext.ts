import { useLayoutEffect, useEffect, useRef } from 'react';
import { gsap } from './gsapConfig';
import { isReducedMotion } from './reducedMotion';

// Isomorphic layout effect for Next.js and SSR safety
const useIsomorphicLayoutEffect = typeof window !== 'undefined' ? useLayoutEffect : useEffect;

export type GsapAnimationCallback = (context: gsap.Context, isReduced: boolean) => void;

/**
 * Custom hook to execute GSAP animations with automatic lifecycle cleanup using gsap.context().
 */
export const useGsapContext = (
  animationCallback: GsapAnimationCallback,
  scopeRef?: React.RefObject<Element | null>,
  dependencies: React.DependencyList = []
) => {
  const ctxRef = useRef<gsap.Context | null>(null);

  useIsomorphicLayoutEffect(() => {
    if (typeof window === 'undefined') return;

    const reduced = isReducedMotion();
    const scope = scopeRef?.current || undefined;

    // Create GSAP scoped context
    const ctx = gsap.context((self) => {
      animationCallback(self, reduced);
    }, scope);

    ctxRef.current = ctx;

    return () => {
      // Revert all animations and kill ScrollTriggers created within this context
      ctx.revert();
      ctxRef.current = null;
    };
  }, dependencies);

  return ctxRef;
};

export default useGsapContext;
