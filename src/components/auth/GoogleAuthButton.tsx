'use client';

import React, { useEffect, useRef, useState } from 'react';
import { useStore } from '@/context/StoreContext';

interface GoogleAuthButtonProps {
  onSuccess?: () => void;
  className?: string;
}

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: {
            client_id: string;
            callback: (response: { credential: string }) => void;
            auto_select?: boolean;
            cancel_on_tap_outside?: boolean;
          }) => void;
          renderButton: (
            parent: HTMLElement,
            options: {
              theme?: 'outline' | 'filled_blue' | 'filled_black';
              size?: 'large' | 'medium' | 'small';
              type?: 'standard' | 'icon';
              text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
              shape?: 'rectangular' | 'pill' | 'circle' | 'square';
              logo_alignment?: 'left' | 'center';
              width?: number | string;
            }
          ) => void;
          prompt: () => void;
        };
      };
    };
  }
}

export const GoogleAuthButton: React.FC<GoogleAuthButtonProps> = ({ onSuccess, className = '' }) => {
  const { loginWithGoogle } = useStore();
  const buttonContainerRef = useRef<HTMLDivElement>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [scriptLoaded, setScriptLoaded] = useState(false);

  const clientId =
    process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID ||
    '647579746380-rsd8nvl7o86octd3ioscc4a5kbhvc5cn.apps.googleusercontent.com';

  useEffect(() => {
    // Load Google Identity Services script if not present
    if (typeof window === 'undefined') return;

    if (window.google?.accounts?.id) {
      setScriptLoaded(true);
      return;
    }

    const existingScript = document.getElementById('google-gsi-client');
    if (existingScript) {
      existingScript.addEventListener('load', () => setScriptLoaded(true));
      return;
    }

    const script = document.createElement('script');
    script.id = 'google-gsi-client';
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    script.onload = () => setScriptLoaded(true);
    document.head.appendChild(script);
  }, []);

  useEffect(() => {
    if (!scriptLoaded || !window.google?.accounts?.id || !buttonContainerRef.current) return;

    try {
      window.google.accounts.id.initialize({
        client_id: clientId,
        callback: async (response: { credential: string }) => {
          if (!response?.credential) return;
          setIsLoading(true);
          try {
            const success = await loginWithGoogle(response.credential);
            if (success && onSuccess) {
              onSuccess();
            }
          } finally {
            setIsLoading(false);
          }
        },
        auto_select: false,
        cancel_on_tap_outside: true,
      });

      // Clear previous button render
      buttonContainerRef.current.innerHTML = '';

      // Render official Google button with refined luxury aesthetics
      window.google.accounts.id.renderButton(buttonContainerRef.current, {
        theme: 'outline',
        size: 'large',
        type: 'standard',
        text: 'continue_with',
        shape: 'rectangular',
        logo_alignment: 'left',
        width: 380,
      });
    } catch (err) {
      console.error('Google Sign-In initialization error:', err);
    }
  }, [scriptLoaded, clientId, loginWithGoogle, onSuccess]);

  return (
    <div className={`google-auth-wrapper ${className}`}>
      {isLoading ? (
        <div className="google-auth-loading">
          <div className="google-auth-spinner" />
          <span>Securing Atelier Session...</span>
        </div>
      ) : (
        <div ref={buttonContainerRef} className="google-auth-button-container" />
      )}
    </div>
  );
};
