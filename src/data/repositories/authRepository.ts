/**
 * ============================================================================
 * RÓRA Luxury Atelier — Authentication Repository
 * ============================================================================
 * Handles JWT authentication, registration, session persistence, and RBAC profile
 * queries with the Spring Boot 3.4 REST backend.
 */

import { apiClient, setAuthToken, getAuthToken } from '../apiClient';

export interface UserSummary {
  id: string;
  name: string;
  email: string;
  status?: string;
  avatarUrl?: string;
  roles: string[];
  permissions?: string[];
}

export interface AuthResponseData {
  token: string;
  tokenType: string;
  expiresInMs: number;
  user: UserSummary;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  name: string;
  email: string;
  password: string;
  phone?: string;
}

const USE_MOCK = process.env.NEXT_PUBLIC_USE_MOCK_DATA === 'true';

export class AuthRepository {
  /**
   * Authenticates user credentials against the backend.
   */
  async login(payload: LoginPayload): Promise<AuthResponseData> {
    if (USE_MOCK) {
      if (payload.email === 'admin@rora-luxury.com' && payload.password === 'Password123!') {
        const mockAdmin: AuthResponseData = {
          token: 'mock-jwt-admin-token',
          tokenType: 'Bearer',
          expiresInMs: 86400000,
          user: {
            id: 'user-admin-root',
            name: 'RÓRA Administrator',
            email: 'admin@rora-luxury.com',
            status: 'ACTIVE',
            roles: ['ROLE_ADMIN', 'ROLE_CUSTOMER'],
            permissions: ['PRODUCT_CREATE', 'PRODUCT_UPDATE', 'ORDER_VIEW', 'INVENTORY_MANAGE', 'AUDIT_VIEW'],
          },
        };
        setAuthToken(mockAdmin.token);
        if (typeof window !== 'undefined') {
          localStorage.setItem('rora_user_profile', JSON.stringify(mockAdmin.user));
        }
        return mockAdmin;
      }

      if (payload.email === 'sarah.customer@rora-luxury.com' && payload.password === 'Password123!') {
        const mockCustomer: AuthResponseData = {
          token: 'mock-jwt-customer-token',
          tokenType: 'Bearer',
          expiresInMs: 86400000,
          user: {
            id: 'cust-sarah',
            name: 'Sarah Johnson',
            email: 'sarah.customer@rora-luxury.com',
            status: 'ACTIVE',
            roles: ['ROLE_CUSTOMER'],
            permissions: [],
          },
        };
        setAuthToken(mockCustomer.token);
        if (typeof window !== 'undefined') {
          localStorage.setItem('rora_user_profile', JSON.stringify(mockCustomer.user));
        }
        return mockCustomer;
      }
      throw new Error('Invalid mock credentials');
    }

    const response = await apiClient.post<AuthResponseData>('/auth/login', payload, {
      skipAuth: true,
    });
    if (response && response.token) {
      setAuthToken(response.token);
      if (typeof window !== 'undefined') {
        localStorage.setItem('rora_user_profile', JSON.stringify(response.user));
      }
    }
    return response;
  }

  /**
   * Registers a new customer account.
   */
  async register(payload: RegisterPayload): Promise<AuthResponseData> {
    if (USE_MOCK) {
      const fallbackUser: AuthResponseData = {
        token: `mock-jwt-reg-${Date.now()}`,
        tokenType: 'Bearer',
        expiresInMs: 86400000,
        user: {
          id: `cust-${Date.now()}`,
          name: payload.name,
          email: payload.email,
          status: 'ACTIVE',
          roles: ['ROLE_CUSTOMER'],
          permissions: [],
        },
      };
      setAuthToken(fallbackUser.token);
      if (typeof window !== 'undefined') {
        localStorage.setItem('rora_user_profile', JSON.stringify(fallbackUser.user));
      }
      return fallbackUser;
    }

    const response = await apiClient.post<AuthResponseData>('/auth/register', payload, {
      skipAuth: true,
    });
    if (response && response.token) {
      setAuthToken(response.token);
      if (typeof window !== 'undefined') {
        localStorage.setItem('rora_user_profile', JSON.stringify(response.user));
      }
    }
    return response;
  }

  /**
   * Fetches current authenticated user profile using active JWT.
   */
  async getCurrentUser(): Promise<UserSummary | null> {
    const token = getAuthToken();
    if (!token) return null;

    try {
      const response = await apiClient.get<UserSummary>('/auth/me');
      if (response) {
        if (typeof window !== 'undefined') {
          localStorage.setItem('rora_user_profile', JSON.stringify(response));
        }
        return response;
      }
    } catch {
      // If token expired or network unavailable, check saved profile
      if (typeof window !== 'undefined') {
        const saved = localStorage.getItem('rora_user_profile');
        if (saved) {
          try {
            return JSON.parse(saved);
          } catch {
            return null;
          }
        }
      }
    }
    return null;
  }

  /**
   * Logs out user, removing JWT and profile from client storage.
   */
  logout(): void {
    setAuthToken(null);
    if (typeof window !== 'undefined') {
      localStorage.removeItem('rora_user_profile');
    }
  }
}

export const authRepository = new AuthRepository();
