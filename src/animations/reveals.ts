import { gsap } from './gsapConfig';
import { MOTION_TOKENS } from './motionTokens';
import { isReducedMotion, isMobileViewport } from './reducedMotion';

export type AnimTarget = gsap.DOMTarget | null | undefined;

export interface FadeInUpOptions {
  trigger?: AnimTarget;
  start?: string;
  duration?: number;
  y?: number;
  delay?: number;
  ease?: string;
  onComplete?: () => void;
}

export interface StaggerFadeInUpOptions {
  trigger?: AnimTarget;
  start?: string;
  duration?: number;
  y?: number;
  stagger?: number;
  ease?: string;
}

export interface ParallaxImageOptions {
  speed?: number;
}

export interface PageHeaderElements {
  breadcrumbs?: AnimTarget;
  eyebrow?: AnimTarget;
  title?: AnimTarget;
  subtitle?: AnimTarget;
  meta?: AnimTarget;
}

export interface HeroElements {
  image?: AnimTarget;
  eyebrow?: AnimTarget;
  title?: AnimTarget;
  description?: AnimTarget;
  cta?: AnimTarget;
  meta?: AnimTarget;
}

/**
 * Gentle Fade-In & Rise reveal with ScrollTrigger.
 */
export const fadeInUp = (target: AnimTarget, options: FadeInUpOptions = {}) => {
  if (typeof window === 'undefined' || !target) return null;

  const reduced = isReducedMotion();
  const {
    trigger = target,
    start = 'top 88%',
    duration = MOTION_TOKENS.duration.smooth,
    y = MOTION_TOKENS.distance.base,
    delay = 0,
    ease = MOTION_TOKENS.ease.smooth,
    onComplete,
  } = options;

  if (reduced) {
    return gsap.set(target, { opacity: 1, y: 0 });
  }

  return gsap.fromTo(
    target,
    {
      opacity: 0,
      y,
    },
    {
      opacity: 1,
      y: 0,
      duration,
      delay,
      ease,
      scrollTrigger: {
        trigger: trigger as gsap.DOMTarget,
        start,
        toggleActions: 'play none none none',
        once: true,
      },
      onComplete,
    }
  );
};

/**
 * Staggered Entrance for a group of sibling items (e.g. 3-4 feature cards, category cards, product cards).
 */
export const staggerFadeInUp = (targets: AnimTarget, options: StaggerFadeInUpOptions = {}) => {
  if (typeof window === 'undefined' || !targets) return null;

  const reduced = isReducedMotion();
  const {
    trigger = targets,
    start = 'top 86%',
    duration = MOTION_TOKENS.duration.base,
    y = MOTION_TOKENS.distance.base,
    stagger = MOTION_TOKENS.stagger.base,
    ease = MOTION_TOKENS.ease.standard,
  } = options;

  if (reduced) {
    return gsap.set(targets, { opacity: 1, y: 0 });
  }

  return gsap.fromTo(
    targets,
    {
      opacity: 0,
      y,
    },
    {
      opacity: 1,
      y: 0,
      duration,
      stagger,
      ease,
      scrollTrigger: {
        trigger: trigger as gsap.DOMTarget,
        start,
        toggleActions: 'play none none none',
        once: true,
      },
    }
  );
};

/**
 * Subtle Editorial Image Parallax on scroll.
 */
export const parallaxImage = (imageElement: AnimTarget, triggerElement: AnimTarget, options: ParallaxImageOptions = {}) => {
  if (typeof window === 'undefined' || !imageElement) return null;

  const reduced = isReducedMotion();
  const mobile = isMobileViewport(MOTION_TOKENS.breakpoints.mobile);

  if (reduced || mobile) {
    return gsap.set(imageElement, { yPercent: 0 });
  }

  const {
    speed = 8,
  } = options;

  return gsap.fromTo(
    imageElement,
    {
      yPercent: -speed,
    },
    {
      yPercent: speed,
      ease: 'none',
      scrollTrigger: {
        trigger: triggerElement || imageElement,
        start: 'top bottom',
        end: 'bottom top',
        scrub: 0.6,
      },
    }
  );
};

/**
 * Editorial Page Header Entrance.
 */
export const revealPageHeader = ({ breadcrumbs, eyebrow, title, subtitle, meta }: PageHeaderElements = {}) => {
  if (typeof window === 'undefined') return null;

  const reduced = isReducedMotion();
  if (reduced) {
    const list = [breadcrumbs, eyebrow, title, subtitle, meta].filter(Boolean);
    if (list.length) gsap.set(list, { opacity: 1, y: 0 });
    return null;
  }

  const tl = gsap.timeline({ delay: 0.05 });

  if (breadcrumbs) {
    tl.fromTo(
      breadcrumbs,
      { opacity: 0, y: -6 },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.fast, ease: MOTION_TOKENS.ease.standard }
    );
  }

  if (eyebrow) {
    tl.fromTo(
      eyebrow,
      { opacity: 0, y: MOTION_TOKENS.distance.subtle },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.fast, ease: MOTION_TOKENS.ease.standard },
      '-=0.1'
    );
  }

  if (title) {
    tl.fromTo(
      title,
      { opacity: 0, y: MOTION_TOKENS.distance.base },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.smooth, ease: MOTION_TOKENS.ease.smooth },
      '-=0.15'
    );
  }

  if (subtitle) {
    tl.fromTo(
      subtitle,
      { opacity: 0, y: MOTION_TOKENS.distance.subtle },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.base, ease: MOTION_TOKENS.ease.standard },
      '-=0.2'
    );
  }

  if (meta) {
    tl.fromTo(
      meta,
      { opacity: 0, y: MOTION_TOKENS.distance.subtle },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.fast, ease: MOTION_TOKENS.ease.standard },
      '-=0.15'
    );
  }

  return tl;
};

/**
 * Editorial Hero Entrance Sequence.
 */
export const revealHero = ({ image, eyebrow, title, description, cta, meta }: HeroElements = {}) => {
  if (typeof window === 'undefined') return null;

  const reduced = isReducedMotion();
  if (reduced) {
    const list = [image, eyebrow, title, description, cta, meta].filter(Boolean);
    if (list.length) gsap.set(list, { opacity: 1, y: 0, scale: 1 });
    return null;
  }

  const tl = gsap.timeline({ delay: 0.08 });

  if (image) {
    tl.fromTo(
      image,
      { opacity: 0.85, scale: 1.04 },
      { opacity: 1, scale: 1, duration: MOTION_TOKENS.duration.hero, ease: MOTION_TOKENS.ease.smooth }
    );
  }

  if (eyebrow) {
    tl.fromTo(
      eyebrow,
      { opacity: 0, y: MOTION_TOKENS.distance.subtle },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.fast, ease: MOTION_TOKENS.ease.standard },
      '-=0.9'
    );
  }

  if (title) {
    tl.fromTo(
      title,
      { opacity: 0, y: MOTION_TOKENS.distance.editorial },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.editorial, ease: MOTION_TOKENS.ease.smooth },
      '-=0.75'
    );
  }

  if (description) {
    tl.fromTo(
      description,
      { opacity: 0, y: MOTION_TOKENS.distance.base },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.smooth, ease: MOTION_TOKENS.ease.standard },
      '-=0.6'
    );
  }

  if (cta) {
    tl.fromTo(
      cta,
      { opacity: 0, y: MOTION_TOKENS.distance.subtle },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.base, ease: MOTION_TOKENS.ease.standard },
      '-=0.45'
    );
  }

  if (meta) {
    tl.fromTo(
      meta,
      { opacity: 0 },
      { opacity: 1, duration: MOTION_TOKENS.duration.smooth, ease: MOTION_TOKENS.ease.standard },
      '-=0.3'
    );
  }

  return tl;
};

export interface SectionHeaderElements {
  eyebrow?: AnimTarget;
  title?: AnimTarget;
  subtitle?: AnimTarget;
}

/**
 * Editorial Section Header entrance animation.
 */
export const revealSectionHeader = (
  { eyebrow, title, subtitle }: SectionHeaderElements,
  trigger?: AnimTarget
) => {
  if (typeof window === 'undefined') return null;

  const reduced = isReducedMotion();
  if (reduced) {
    const list = [eyebrow, title, subtitle].filter(Boolean);
    if (list.length) gsap.set(list, { opacity: 1, y: 0 });
    return null;
  }

  const tl = gsap.timeline({
    scrollTrigger: {
      trigger: trigger || title || eyebrow,
      start: 'top 85%',
      toggleActions: 'play none none none',
      once: true,
    },
  });

  if (eyebrow) {
    tl.fromTo(
      eyebrow,
      { opacity: 0, y: MOTION_TOKENS.distance.subtle },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.fast, ease: MOTION_TOKENS.ease.standard }
    );
  }

  if (title) {
    tl.fromTo(
      title,
      { opacity: 0, y: MOTION_TOKENS.distance.base },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.smooth, ease: MOTION_TOKENS.ease.smooth },
      eyebrow ? '-=0.15' : '0'
    );
  }

  if (subtitle) {
    tl.fromTo(
      subtitle,
      { opacity: 0, y: MOTION_TOKENS.distance.subtle },
      { opacity: 1, y: 0, duration: MOTION_TOKENS.duration.base, ease: MOTION_TOKENS.ease.standard },
      '-=0.2'
    );
  }

  return tl;
};
