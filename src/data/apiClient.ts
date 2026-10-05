/**
 * ============================================================================
 * RÓRA Luxury Atelier — Centralized API Client
 * ============================================================================
 * HTTP client for communicating with the Spring Boot 3.4 REST backend.
 * Provides automated JWT Bearer token attachment, X-Session-ID guest tracking,
 * standardized ApiResponse<T> unwrapping, and resilient fallback handling.
 */

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

export interface ApiResponseWrapper<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp?: string;
  errorCode?: string;
}

export interface RequestOptions extends RequestInit {
  params?: Record<string, string | number | boolean | undefined | null>;
  skipAuth?: boolean;
  skipSession?: boolean;
}

/**
 * Generates or retrieves a persistent guest session ID.
 */
export function getSessionId(): string {
  if (typeof window === 'undefined') {
    return 'ssr-session';
  }

  const STORAGE_KEY = 'rora_session_id';
  let sessionId = localStorage.getItem(STORAGE_KEY);
  if (!sessionId) {
    sessionId = `sess_${Date.now()}_${Math.random().toString(36).substring(2, 11)}`;
    try {
      localStorage.setItem(STORAGE_KEY, sessionId);
    } catch {
      // Ignore localStorage write errors in private browsing
    }
  }
  return sessionId;
}

/**
 * Retrieves the stored JWT authentication token if available.
 */
export function getAuthToken(): string | null {
  if (typeof window === 'undefined') {
    return null;
  }
  return localStorage.getItem('rora_auth_token');
}

/**
 * Sets or clears the stored JWT authentication token.
 */
export function setAuthToken(token: string | null): void {
  if (typeof window === 'undefined') return;
  if (token) {
    localStorage.setItem('rora_auth_token', token);
  } else {
    localStorage.removeItem('rora_auth_token');
  }
}

/**
 * Builds standard request headers with session and auth context.
 */
function buildHeaders(options?: RequestOptions): Headers {
  const headers = new Headers(options?.headers || {});

  if (!headers.has('Content-Type') && !(options?.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }
  if (!headers.has('Accept')) {
    headers.set('Accept', 'application/json');
  }

  if (!options?.skipSession) {
    const sessionId = getSessionId();
    if (sessionId) {
      headers.set('X-Session-ID', sessionId);
    }
  }

  if (!options?.skipAuth) {
    const token = getAuthToken();
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }
  }

  return headers;
}

/**
 * Builds the complete URL with search query parameters.
 */
function buildUrl(endpoint: string, params?: Record<string, string | number | boolean | undefined | null>): string {
  const cleanEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
  const base = API_BASE_URL.endsWith('/') ? API_BASE_URL.slice(0, -1) : API_BASE_URL;
  const url = new URL(`${base}${cleanEndpoint}`);

  if (params) {
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        url.searchParams.append(key, String(value));
      }
    });
  }

  return url.toString();
}

/**
 * Core fetch execution with response unwrap and error management.
 */
async function request<T>(endpoint: string, options: RequestOptions = {}): Promise<T> {
  const url = buildUrl(endpoint, options.params);
  const headers = buildHeaders(options);

  const fetchConfig: RequestInit = {
    ...options,
    headers,
  };

  const response = await fetch(url, fetchConfig);

  // Parse response body
  let data: unknown;
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    const errorPayload = typeof data === 'object' && data !== null ? (data as Record<string, unknown>) : null;
    const errorMessage = (errorPayload?.message as string) || (errorPayload?.error as string) || `HTTP ${response.status}: ${response.statusText}`;
    const error = Object.assign(new Error(errorMessage), {
      status: response.status,
      data,
    });
    throw error;
  }

  // If backend wrapped in ApiResponse<T>, unwrap data property
  if (data && typeof data === 'object' && 'data' in data && 'success' in data) {
    return (data as { data: T }).data;
  }

  return data as T;
}

export const apiClient = {
  get<T>(endpoint: string, options?: RequestOptions): Promise<T> {
    return request<T>(endpoint, { ...options, method: 'GET' });
  },

  post<T>(endpoint: string, body?: unknown, options?: RequestOptions): Promise<T> {
    return request<T>(endpoint, {
      ...options,
      method: 'POST',
      body: body instanceof FormData ? body : JSON.stringify(body),
    });
  },

  put<T>(endpoint: string, body?: unknown, options?: RequestOptions): Promise<T> {
    return request<T>(endpoint, {
      ...options,
      method: 'PUT',
      body: body instanceof FormData ? body : JSON.stringify(body),
    });
  },

  patch<T>(endpoint: string, body?: unknown, options?: RequestOptions): Promise<T> {
    return request<T>(endpoint, {
      ...options,
      method: 'PATCH',
      body: body instanceof FormData ? body : JSON.stringify(body),
    });
  },

  delete<T>(endpoint: string, options?: RequestOptions): Promise<T> {
    return request<T>(endpoint, { ...options, method: 'DELETE' });
  },

  getBaseUrl(): string {
    return API_BASE_URL;
  }
};
