import {
  MOCK_SALES_OVERVIEW,
  MOCK_CUSTOMERS,
  MOCK_PAYMENTS,
  MOCK_SHIPMENTS,
  MOCK_RETURNS,
  MOCK_REFUNDS,
  PRODUCTS,
  MOCK_ADMIN_USERS,
  MOCK_ROLES,
  MOCK_SETTINGS,
  MOCK_AUDIT_LOGS,
  MOCK_CMS
} from '../mockData';
import {
  Customer,
  AdminUser,
  AuditLog,
  SalesOverview,
  PaymentRecord,
  ShipmentRecord,
  ReturnRecord,
  RefundRecord,
  AdminRole,
  StoreSettings,
  CMSContent,
  Product
} from '../../types/domain';
import { apiClient } from '../apiClient';

export const adminRepository = {
  async getSalesOverview(): Promise<SalesOverview> {
    try {
      const data = await apiClient.get<SalesOverview>('/admin/dashboard/summary');
      if (data && data.monthlyRevenue) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin sales overview API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve({ ...MOCK_SALES_OVERVIEW } as unknown as SalesOverview);
  },

  async getCustomers(): Promise<Customer[]> {
    try {
      const data = await apiClient.get<Customer[]>('/admin/customers');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin customers API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(MOCK_CUSTOMERS as unknown as Customer[])]);
  },

  async getPayments(): Promise<PaymentRecord[]> {
    try {
      const data = await apiClient.get<PaymentRecord[]>('/admin/payments');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin payments API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(MOCK_PAYMENTS as unknown as PaymentRecord[])]);
  },

  async getShipments(): Promise<ShipmentRecord[]> {
    try {
      const data = await apiClient.get<ShipmentRecord[]>('/admin/shipments');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin shipments API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(MOCK_SHIPMENTS as unknown as ShipmentRecord[])]);
  },

  async getReturns(): Promise<ReturnRecord[]> {
    try {
      const data = await apiClient.get<ReturnRecord[]>('/admin/returns');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin returns API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(MOCK_RETURNS as unknown as ReturnRecord[])]);
  },

  async getRefunds(): Promise<RefundRecord[]> {
    try {
      const data = await apiClient.get<RefundRecord[]>('/admin/refunds');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin refunds API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(MOCK_REFUNDS as unknown as RefundRecord[])]);
  },

  async getInventory(): Promise<Partial<Product>[]> {
    try {
      const data = await apiClient.get<Product[]>('/admin/inventory');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin inventory API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve(
      PRODUCTS.map(p => ({
        id: p.id,
        name: p.name,
        sku: p.sku,
        stock: p.stock,
        price: p.price,
        category: p.category
      }))
    );
  },

  async getAdminUsers(): Promise<AdminUser[]> {
    try {
      const data = await apiClient.get<AdminUser[]>('/admin/users');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin users API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(MOCK_ADMIN_USERS as unknown as AdminUser[])]);
  },

  async getRoles(): Promise<AdminRole[]> {
    try {
      const data = await apiClient.get<AdminRole[]>('/admin/roles');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin roles API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(MOCK_ROLES as unknown as AdminRole[])]);
  },

  async getSettings(): Promise<StoreSettings> {
    try {
      const data = await apiClient.get<StoreSettings>('/admin/settings');
      if (data && data.storeName) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin settings API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve({ ...MOCK_SETTINGS } as unknown as StoreSettings);
  },

  async getAuditLogs(): Promise<AuditLog[]> {
    try {
      const data = await apiClient.get<AuditLog[]>('/admin/audit-logs');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin audit logs API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve([...(MOCK_AUDIT_LOGS as unknown as AuditLog[])]);
  },

  async getCms(): Promise<CMSContent> {
    try {
      const data = await apiClient.get<CMSContent>('/admin/cms');
      if (data && data.announcementBar) {
        return data;
      }
    } catch (err) {
      console.warn('Backend admin CMS API unavailable, fallback to local dataset:', err);
    }
    return Promise.resolve({ ...MOCK_CMS } as unknown as CMSContent);
  }
};
