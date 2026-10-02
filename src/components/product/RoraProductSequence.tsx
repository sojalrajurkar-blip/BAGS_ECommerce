'use client';

import React, { useEffect, useRef, useState, useCallback } from 'react';
import { gsap, ScrollTrigger } from '../../animations/gsapConfig';
import { isReducedMotion } from '../../animations/reducedMotion';
import { ChevronDown, Sparkles, Layers, ShieldCheck, Cpu } from 'lucide-react';

export interface SequencePhase {
  phase: string;
  title: string;
  subtitle: string;
  badge?: string;
  startProgress: number;
  endProgress: number;
}

export interface RoraProductSequenceProps {
  frames?: string[];
  frameCount?: number;
  sectionHeight?: string | number;
  posterFrame?: string;
  className?: string;
  title?: string;
  eyebrow?: string;
  description?: string;
  phases?: SequencePhase[];
  productName?: string;
  isHeroMode?: boolean;
  primaryCtaText?: string;
  onPrimaryCta?: () => void;
  secondaryCtaText?: string;
  onSecondaryCta?: () => void;
  bottomMetaLeft?: string;
  bottomMetaRight?: string;
}

const DEFAULT_FRAME_COUNT = 300;

const DEFAULT_PHASES: SequencePhase[] = [
  {
    phase: 'PHASE 01',
    title: 'CRAFTED TO LAST',
    subtitle: 'Sculptural silhouette formed from full-grain Tuscan calfskin, tanned naturally with plant extracts for an enduring patina.',
    badge: 'Artisan Material',
    startProgress: 0.05,
    endProgress: 0.32,
  },
  {
    phase: 'PHASE 02',
    title: 'DETAIL IN EVERY STITCH',
    subtitle: 'Reinforced saddle stitching with wax-coated German thread, micro-beveled edges, and hand-brushed brass hardware.',
    badge: 'Precision Atelier',
    startProgress: 0.38,
    endProgress: 0.66,
  },
  {
    phase: 'PHASE 03',
    title: 'EXPLODED ARCHITECTURE',
    subtitle: 'Deconstructed internal chassis revealing protective micro-suede lining, hidden magnetic seals, and modular compartments.',
    badge: 'Exploded View',
    startProgress: 0.72,
    endProgress: 0.98,
  },
];

export const RoraProductSequence: React.FC<RoraProductSequenceProps> = ({
  frames: customFrames,
  frameCount = DEFAULT_FRAME_COUNT,
  sectionHeight = '300vh',
  posterFrame,
  className = '',
  title = 'Deconstructed Craftsmanship',
  eyebrow = 'Engineered Atelier Experience',
  description = 'Scroll down to explore the 300-frame deconstruction of the RÓRA Handcrafted Silhouette.',
  phases = DEFAULT_PHASES,
  productName = 'The Artisan Atelier Handbag',
  isHeroMode = false,
  primaryCtaText,
  onPrimaryCta,
  secondaryCtaText,
  onSecondaryCta,
  bottomMetaLeft,
  bottomMetaRight,
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const imagesRef = useRef<(HTMLImageElement | null)[]>([]);
  const currentFrameRef = useRef<number>(0);
  const triggerRef = useRef<ScrollTrigger | null>(null);

  const [firstFrameLoaded, setFirstFrameLoaded] = useState<boolean>(false);
  const [loadProgress, setLoadProgress] = useState<number>(0);
  const [activePhaseIndex, setActivePhaseIndex] = useState<number>(0);
  const [scrollProgress, setScrollProgress] = useState<number>(0);
  const [reducedMotion, setReducedMotion] = useState<boolean>(false);

  // Generate frame URLs programmatically if not provided
  const framesList = React.useMemo(() => {
    if (customFrames && customFrames.length > 0) return customFrames;
    return Array.from(
      { length: frameCount },
      (_, index) => `/images/rora/product-sequence/frame-${String(index + 1).padStart(3, '0')}.jpg`
    );
  }, [customFrames, frameCount]);

  /**
   * High-DPI Contain-Style Canvas Rendering
   */
  const drawFrame = useCallback(
    (frameIndex: number) => {
      const canvas = canvasRef.current;
      if (!canvas) return;

      const ctx = canvas.getContext('2d');
      if (!ctx) return;

      const targetIdx = Math.max(0, Math.min(frameCount - 1, Math.round(frameIndex)));
      currentFrameRef.current = targetIdx;

      // Find target image or closest available loaded neighbor
      let img = imagesRef.current[targetIdx];
      if (!img || !img.complete || img.naturalWidth === 0) {
        for (let offset = 1; offset < frameCount; offset++) {
          const prev = targetIdx - offset;
          const next = targetIdx + offset;
          if (prev >= 0 && imagesRef.current[prev]?.complete && imagesRef.current[prev]?.naturalWidth !== 0) {
            img = imagesRef.current[prev];
            break;
          }
          if (next < frameCount && imagesRef.current[next]?.complete && imagesRef.current[next]?.naturalWidth !== 0) {
            img = imagesRef.current[next];
            break;
          }
        }
      }

      if (!img || !img.complete || img.naturalWidth === 0) return;

      const dpr = typeof window !== 'undefined' ? window.devicePixelRatio || 1 : 1;
      const canvasWidth = canvas.clientWidth;
      const canvasHeight = canvas.clientHeight;

      if (canvasWidth === 0 || canvasHeight === 0) return;

      const bufferWidth = Math.round(canvasWidth * dpr);
      const bufferHeight = Math.round(canvasHeight * dpr);

      if (canvas.width !== bufferWidth || canvas.height !== bufferHeight) {
        canvas.width = bufferWidth;
        canvas.height = bufferHeight;
      }

      ctx.save();
      ctx.scale(dpr, dpr);
      ctx.clearRect(0, 0, canvasWidth, canvasHeight);

      const imgWidth = img.naturalWidth;
      const imgHeight = img.naturalHeight;
      const imgRatio = imgWidth / imgHeight;
      const canvasRatio = canvasWidth / canvasHeight;

      let renderWidth = canvasWidth;
      let renderHeight = canvasHeight;
      let offsetX = 0;
      let offsetY = 0;

      // Generous luxury contain framing
      if (canvasRatio > imgRatio) {
        renderHeight = canvasHeight * 0.86;
        renderWidth = renderHeight * imgRatio;
        offsetX = (canvasWidth - renderWidth) / 2;
        offsetY = (canvasHeight - renderHeight) / 2;
      } else {
        renderWidth = canvasWidth * 0.90;
        renderHeight = renderWidth / imgRatio;
        offsetX = (canvasWidth - renderWidth) / 2;
        offsetY = (canvasHeight - renderHeight) / 2;
      }

      ctx.imageSmoothingEnabled = true;
      ctx.imageSmoothingQuality = 'high';
      ctx.drawImage(img, offsetX, offsetY, renderWidth, renderHeight);
      ctx.restore();
    },
    [frameCount]
  );

  /**
   * Progressive Preloading Engine
   */
  useEffect(() => {
    if (typeof window === 'undefined') return;

    const isReduced = isReducedMotion();
    setReducedMotion(isReduced);

    imagesRef.current = new Array(frameCount).fill(null);
    let isCancelled = false;
    let loadedCount = 0;

    // 1. PRIORITY 1: Load Frame 1 immediately
    const firstImg = new Image();
    firstImg.src = framesList[0];
    firstImg.onload = () => {
      if (isCancelled) return;
      imagesRef.current[0] = firstImg;
      setFirstFrameLoaded(true);
      drawFrame(0);
    };

    // 2. PRIORITY 2: Load Keyframes across the sequence (e.g. every 20th frame)
    const keyframeIndices: number[] = [];
    for (let i = 20; i < frameCount; i += 20) {
      keyframeIndices.push(i);
    }
    // Also include final exploded frame
    keyframeIndices.push(frameCount - 1);

    keyframeIndices.forEach((idx) => {
      const img = new Image();
      img.src = framesList[idx];
      img.onload = () => {
        if (isCancelled) return;
        imagesRef.current[idx] = img;
        loadedCount++;
        setLoadProgress(Math.round((loadedCount / frameCount) * 100));
      };
    });

    // 3. PRIORITY 3: Progressive chunked loading of all remaining frames
    const remainingIndices: number[] = [];
    for (let i = 1; i < frameCount; i++) {
      if (!keyframeIndices.includes(i)) {
        remainingIndices.push(i);
      }
    }

    let currentIndex = 0;
    const CHUNK_SIZE = 8;

    const loadNextChunk = () => {
      if (isCancelled || currentIndex >= remainingIndices.length) return;

      const chunk = remainingIndices.slice(currentIndex, currentIndex + CHUNK_SIZE);
      currentIndex += CHUNK_SIZE;

      chunk.forEach((frameIdx) => {
        const img = new Image();
        img.src = framesList[frameIdx];
        img.onload = () => {
          if (isCancelled) return;
          imagesRef.current[frameIdx] = img;
          loadedCount++;
          setLoadProgress(Math.round((loadedCount / frameCount) * 100));
        };
      });

      if (typeof window.requestIdleCallback === 'function') {
        window.requestIdleCallback(() => loadNextChunk());
      } else {
        setTimeout(loadNextChunk, 40);
      }
    };

    // Start background progressive streaming after first frame mounts
    const timer = setTimeout(loadNextChunk, 100);

    return () => {
      isCancelled = true;
      clearTimeout(timer);
    };
  }, [frameCount, framesList, drawFrame]);

  /**
   * Window Resize Listener to preserve crisp retina canvas buffer
   */
  useEffect(() => {
    if (typeof window === 'undefined') return;

    let resizeTimer: NodeJS.Timeout;
    const handleResize = () => {
      clearTimeout(resizeTimer);
      resizeTimer = setTimeout(() => {
        drawFrame(currentFrameRef.current);
      }, 50);
    };

    window.addEventListener('resize', handleResize);
    return () => {
      window.removeEventListener('resize', handleResize);
      clearTimeout(resizeTimer);
    };
  }, [drawFrame]);

  /**
   * GSAP ScrollTrigger Sequence Setup
   */
  useEffect(() => {
    if (typeof window === 'undefined' || !containerRef.current) return;

    if (reducedMotion) {
      drawFrame(frameCount - 1);
      return;
    }

    const sequenceProxy = { frame: 0 };
    const sectionEl = containerRef.current;

    const ctx = gsap.context(() => {
      triggerRef.current = ScrollTrigger.create({
        trigger: sectionEl,
        start: 'top top',
        end: '+=2600',
        pin: true,
        scrub: 0.4,
        onUpdate: (self) => {
          const rawProgress = self.progress;
          setScrollProgress(rawProgress);

          // Calculate continuous frame index
          const targetFrame = Math.min(frameCount - 1, Math.floor(rawProgress * (frameCount - 1)));
          sequenceProxy.frame = targetFrame;
          drawFrame(targetFrame);

          // Update active storytelling phase
          const matchingPhaseIdx = phases.findIndex(
            (p) => rawProgress >= p.startProgress && rawProgress <= p.endProgress
          );
          if (matchingPhaseIdx !== -1) {
            setActivePhaseIndex(matchingPhaseIdx);
          }
        },
      });
    }, sectionEl);

    return () => {
      ctx.revert();
      if (triggerRef.current) {
        triggerRef.current.kill();
        triggerRef.current = null;
      }
    };
  }, [reducedMotion, frameCount, phases, drawFrame]);

  return (
    <section
      ref={containerRef}
      className={`rora-sequence-section ${className}`}
      style={{
        position: 'relative',
        width: '100%',
        backgroundColor: '#141311',
        color: '#F7F4EE',
        overflow: 'hidden',
      }}
      aria-label={`${productName} 360-degree exploded craftsmanship interactive sequence`}
    >
      {/* Pinned Viewport Container */}
      <div
        className="rora-sequence-viewport"
        style={{
          position: 'relative',
          width: '100%',
          height: '100vh',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        {/* Top Floating Editorial Eyebrow */}
        <header
          className="sequence-header"
          style={{
            position: 'absolute',
            top: '2.5rem',
            left: '50%',
            transform: 'translateX(-50%)',
            textAlign: 'center',
            zIndex: 10,
            pointerEvents: 'none',
            width: '90%',
            maxWidth: '680px',
          }}
        >
          <div
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.5rem',
              padding: '0.35rem 1rem',
              borderRadius: '9999px',
              backgroundColor: 'rgba(255, 255, 255, 0.08)',
              border: '1px solid rgba(255, 255, 255, 0.12)',
              backdropFilter: 'blur(8px)',
              fontSize: '0.75rem',
              letterSpacing: '0.14em',
              textTransform: 'uppercase',
              color: '#C9B99F',
              marginBottom: '0.6rem',
            }}
          >
            <Sparkles size={12} />
            <span>{eyebrow}</span>
          </div>
          <h2
            style={{
              fontFamily: 'var(--font-display, serif)',
              fontSize: 'clamp(1.75rem, 3.2vw, 2.75rem)',
              fontWeight: 400,
              letterSpacing: '-0.02em',
              margin: '0 0 0.4rem 0',
              color: '#F7F4EE',
              lineHeight: 1.1,
            }}
          >
            {title}
          </h2>
          <p
            style={{
              fontSize: '0.875rem',
              color: 'rgba(247, 244, 238, 0.65)',
              margin: '0 0 1rem 0',
              fontFamily: 'var(--font-body, sans-serif)',
            }}
          >
            {description}
          </p>

          {/* Optional Hero CTAs */}
          {(primaryCtaText || secondaryCtaText) && (
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '0.85rem',
                pointerEvents: 'auto',
                marginTop: '0.5rem',
              }}
            >
              {primaryCtaText && (
                <button
                  type="button"
                  className="btn btn-primary"
                  onClick={onPrimaryCta}
                  style={{
                    backgroundColor: '#C9B99F',
                    color: '#141311',
                    fontWeight: 500,
                    padding: '0.65rem 1.4rem',
                    fontSize: '0.85rem',
                    borderRadius: '999px',
                    border: 'none',
                    cursor: 'pointer',
                    display: 'inline-flex',
                    alignItems: 'center',
                    gap: '0.4rem',
                    transition: 'transform 0.2s ease, background-color 0.2s ease',
                  }}
                >
                  {primaryCtaText}
                </button>
              )}
              {secondaryCtaText && (
                <button
                  type="button"
                  onClick={onSecondaryCta}
                  style={{
                    backgroundColor: 'rgba(255, 255, 255, 0.08)',
                    color: '#F7F4EE',
                    fontWeight: 400,
                    padding: '0.65rem 1.4rem',
                    fontSize: '0.85rem',
                    borderRadius: '999px',
                    border: '1px solid rgba(255, 255, 255, 0.2)',
                    backdropFilter: 'blur(8px)',
                    cursor: 'pointer',
                    transition: 'background-color 0.2s ease',
                  }}
                >
                  {secondaryCtaText}
                </button>
              )}
            </div>
          )}
        </header>

        {/* The Central Canvas Drawing Surface */}
        <canvas
          ref={canvasRef}
          role="img"
          aria-label={`${productName} 300-frame interactive deconstruction view`}
          style={{
            width: '100%',
            height: '100%',
            display: 'block',
            cursor: 'grab',
          }}
        />

        {/* Minimal Initial Frame Loader Overlay */}
        {!firstFrameLoaded && (
          <div
            style={{
              position: 'absolute',
              inset: 0,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              backgroundColor: '#141311',
              zIndex: 20,
              gap: '1rem',
            }}
          >
            <span
              style={{
                fontFamily: 'var(--font-display, serif)',
                fontSize: '2rem',
                letterSpacing: '0.25em',
                color: '#C9B99F',
              }}
            >
              RÓRA
            </span>
            <span
              style={{
                fontSize: '0.8rem',
                letterSpacing: '0.15em',
                textTransform: 'uppercase',
                color: 'rgba(247, 244, 238, 0.5)',
              }}
            >
              Preparing Atelier Experience...
            </span>
          </div>
        )}

        {/* Floating Storytelling Phase Overlays */}
        <div
          className="sequence-phase-container"
          style={{
            position: 'absolute',
            bottom: '3rem',
            left: '50%',
            transform: 'translateX(-50%)',
            width: '90%',
            maxWidth: '560px',
            zIndex: 15,
            pointerEvents: 'none',
          }}
        >
          {phases.map((phase, idx) => {
            const isVisible =
              scrollProgress >= phase.startProgress && scrollProgress <= phase.endProgress;

            return (
              <div
                key={phase.phase}
                style={{
                  position: idx === activePhaseIndex ? 'relative' : 'absolute',
                  inset: 0,
                  opacity: isVisible || (reducedMotion && idx === phases.length - 1) ? 1 : 0,
                  transform: isVisible
                    ? 'translateY(0px)'
                    : 'translateY(16px)',
                  transition: 'opacity 0.4s ease, transform 0.4s ease',
                  padding: '1.25rem 1.5rem',
                  borderRadius: '12px',
                  backgroundColor: 'rgba(26, 25, 22, 0.75)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                  backdropFilter: 'blur(16px)',
                  boxShadow: '0 20px 40px rgba(0, 0, 0, 0.4)',
                  textAlign: 'left',
                }}
              >
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    marginBottom: '0.35rem',
                  }}
                >
                  <span
                    style={{
                      fontSize: '0.7rem',
                      letterSpacing: '0.2em',
                      color: '#C9B99F',
                      fontWeight: 600,
                      textTransform: 'uppercase',
                    }}
                  >
                    {phase.phase}
                  </span>
                  {phase.badge && (
                    <span
                      style={{
                        fontSize: '0.65rem',
                        letterSpacing: '0.1em',
                        padding: '0.2rem 0.55rem',
                        borderRadius: '4px',
                        backgroundColor: 'rgba(104, 112, 90, 0.25)',
                        color: '#B9C2A9',
                        border: '1px solid rgba(104, 112, 90, 0.35)',
                        textTransform: 'uppercase',
                      }}
                    >
                      {phase.badge}
                    </span>
                  )}
                </div>
                <h3
                  style={{
                    fontFamily: 'var(--font-display, serif)',
                    fontSize: '1.35rem',
                    color: '#F7F4EE',
                    margin: '0 0 0.35rem 0',
                    letterSpacing: '-0.01em',
                    fontWeight: 400,
                  }}
                >
                  {phase.title}
                </h3>
                <p
                  style={{
                    fontSize: '0.85rem',
                    color: 'rgba(247, 244, 238, 0.75)',
                    lineHeight: 1.5,
                    margin: 0,
                    fontFamily: 'var(--font-body, sans-serif)',
                  }}
                >
                  {phase.subtitle}
                </p>
              </div>
            );
          })}
        </div>

        {/* Bottom Scroll Indicator / Scrub Tracker */}
        <div
          style={{
            position: 'absolute',
            bottom: '1rem',
            left: '50%',
            transform: 'translateX(-50%)',
            display: 'flex',
            alignItems: 'center',
            gap: '0.75rem',
            zIndex: 10,
            pointerEvents: 'none',
          }}
        >
          <div
            style={{
              width: '120px',
              height: '3px',
              borderRadius: '999px',
              backgroundColor: 'rgba(255, 255, 255, 0.15)',
              overflow: 'hidden',
            }}
          >
            <div
              style={{
                height: '100%',
                width: `${Math.round(scrollProgress * 100)}%`,
                backgroundColor: '#C9B99F',
                transition: 'width 0.1s linear',
              }}
            />
          </div>
          <span
            style={{
              fontSize: '0.65rem',
              letterSpacing: '0.15em',
              color: 'rgba(247, 244, 238, 0.5)',
              textTransform: 'uppercase',
              fontVariantNumeric: 'tabular-nums',
            }}
          >
            {Math.round(scrollProgress * 100)}%
          </span>
        </div>
      </div>
    </section>
  );
};

export default RoraProductSequence;
