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

export const adminRepository = {
  async getSalesOverview(): Promise<SalesOverview> {
    return Promise.resolve({ ...MOCK_SALES_OVERVIEW } as unknown as SalesOverview);
  },
  async getCustomers(): Promise<Customer[]> {
    return Promise.resolve([...(MOCK_CUSTOMERS as unknown as Customer[])]);
  },
  async getPayments(): Promise<PaymentRecord[]> {
    return Promise.resolve([...(MOCK_PAYMENTS as unknown as PaymentRecord[])]);
  },
  async getShipments(): Promise<ShipmentRecord[]> {
    return Promise.resolve([...(MOCK_SHIPMENTS as unknown as ShipmentRecord[])]);
  },
  async getReturns(): Promise<ReturnRecord[]> {
    return Promise.resolve([...(MOCK_RETURNS as unknown as ReturnRecord[])]);
  },
  async getRefunds(): Promise<RefundRecord[]> {
    return Promise.resolve([...(MOCK_REFUNDS as unknown as RefundRecord[])]);
  },
  async getInventory(): Promise<Partial<Product>[]> {
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
    return Promise.resolve([...(MOCK_ADMIN_USERS as unknown as AdminUser[])]);
  },
  async getRoles(): Promise<AdminRole[]> {
    return Promise.resolve([...(MOCK_ROLES as unknown as AdminRole[])]);
  },
  async getSettings(): Promise<StoreSettings> {
    return Promise.resolve({ ...MOCK_SETTINGS } as unknown as StoreSettings);
  },
  async getAuditLogs(): Promise<AuditLog[]> {
    return Promise.resolve([...(MOCK_AUDIT_LOGS as unknown as AuditLog[])]);
  },
  async getCms(): Promise<CMSContent> {
    return Promise.resolve({ ...MOCK_CMS } as unknown as CMSContent);
  }
};
