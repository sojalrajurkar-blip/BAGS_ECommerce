/**
 * ============================================================================
 * RÓRA Luxury Atelier — TypeScript Domain Models & Contracts
 * ============================================================================
 * Defines strict, type-safe schemas across all frontend UI components,
 * context providers, and data access repositories.
 */

export interface CategorySalesBreakdown {
  category: string;
  percent: number;
  revenue: string;
}

export interface SalesOverview {
  monthlyRevenue: string;
  monthlyGrowth: string;
  ordersThisMonth: number;
  ordersGrowth: string;
  activeCustomers: number;
  customersGrowth: string;
  averageOrderValue: string;
  aovGrowth: string;
  categoryBreakdown: CategorySalesBreakdown[];
}

export interface ColorVariant {
  id?: string;
  name: string;
  colorName?: string;
  hex: string;
  colorHex?: string;
  image?: string;
  stock?: number;
  price?: number;
}

export interface ProductVariant {
  id: string;
  sku?: string;
  name?: string;
  colorName: string;
  colorHex: string;
  image: string;
  stock: number;
  price?: number;
}

export interface ProductDimensions {
  height?: string;
  width?: string;
  depth?: string;
  volume?: string;
  weight?: string;
  laptop?: string;
}

export interface ProductSpecification {
  material?: string;
  hardware?: string;
  lining?: string;
  dimensions?: string;
  handleDrop?: string;
  weight?: string;
  origin?: string;
  warranty?: string;
  [key: string]: string | undefined;
}

export interface Product {
  id: string;
  slug: string;
  name: string;
  subtitle?: string;
  tagline?: string;
  category: string;
  categoryId?: string;
  price: number;
  originalPrice: number;
  compareAtPrice?: number;
  discount?: number;
  currency?: string;
  rating: number;
  reviewCount: number;
  badge?: string;
  stock: number;
  inStock?: boolean;
  isNewArrival?: boolean;
  isBestSeller?: boolean;
  isCurated?: boolean;
  isFeatured?: boolean;
  images: string[];
  description: string;
  story?: string;
  material: string;
  specifications?: ProductSpecification | Record<string, string>;
  features?: string[];
  colors: ColorVariant[];
  variants?: ProductVariant[];
  careInstructions?: string[];
  tags?: string[];
  capacity?: string;
  dimensions?: string | ProductDimensions;
  weight?: string;
  sku?: string;
}

export interface Category {
  id: string;
  slug: string;
  name: string;
  title?: string;
  headline?: string;
  subtitle?: string;
  description: string;
  image?: string;
  heroImage: string;
  count: number;
  productCount?: number;
  featuredProductIds?: string[];
}

export interface CartItem {
  id: string;
  productId: string;
  variantId?: string;
  quantity: number;
  product: Product;
  selectedVariant?: ProductVariant;
  color: ColorVariant;
  unitPrice?: number;
  totalPrice?: number;
  price: number;
}

export interface WishlistItem {
  id?: string;
  productId: string;
  product?: Product;
  addedAt: string;
}

export interface Address {
  id?: string;
  fullName?: string;
  firstName?: string;
  lastName?: string;
  street?: string;
  addressLine1?: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  phone?: string;
  isDefault?: boolean;
}

export interface Customer {
  id: string;
  name: string;
  firstName?: string;
  lastName?: string;
  email: string;
  phone?: string;
  city?: string;
  tier?: string;
  totalSpent?: number;
  lifetimeValue?: number;
  orderCount?: number;
  ordersCount?: number;
  joinedDate?: string;
  lastOrder?: string;
  addresses?: Address[];
  createdAt?: string;
}

export interface OrderItem {
  id?: string;
  productId?: string;
  variantId?: string;
  name: string;
  color?: string;
  colorName?: string;
  image: string;
  price: number;
  quantity: number;
}

export interface OrderTimelineEvent {
  step: string;
  time?: string;
  date?: string;
  completed: boolean;
  title?: string;
  description?: string;
}

export interface Order {
  id: string;
  orderNumber: string;
  date?: string;
  createdAt?: string;
  status: string;
  customer?: Customer;
  paymentStatus?: string;
  paymentMethod?: string;
  items: OrderItem[];
  subtotal?: number;
  shippingFee?: number;
  shipping?: number;
  tax?: number;
  discount?: number;
  total: number;
  shippingAddress: Address;
  billingAddress?: Address;
  trackingNumber?: string;
  carrier?: string;
  estimatedDelivery?: string;
  timeline?: OrderTimelineEvent[];
}

export interface Review {
  id: string;
  productId?: string;
  productName?: string;
  author: string;
  role?: string;
  rating: number;
  title?: string;
  comment?: string;
  content?: string;
  date: string;
  verified?: boolean;
  verifiedPurchase?: boolean;
  helpfulCount?: number;
  status?: string;
}

export interface Coupon {
  code: string;
  description?: string;
  discountType?: string;
  discountValue?: number;
  discountPercent?: number;
  minimumSpend?: number;
  minCart?: number;
  minOrder?: number;
  expiryDate?: string;
  expiry?: string;
  uses?: number;
  usageCount?: number;
  usageLimit?: number;
  isActive?: boolean;
  active?: boolean;
  status?: string;
}

export interface PaymentRecord {
  id: string;
  orderNumber: string;
  customer: string;
  amount: number;
  method: string;
  gatewayRef: string;
  date: string;
  status: string;
}

export interface ShipmentRecord {
  id: string;
  orderNumber: string;
  customer: string;
  courier: string;
  awbNumber: string;
  destination: string;
  dispatchDate: string;
  status: string;
}

export interface ReturnRecord {
  id: string;
  orderNumber: string;
  customer: string;
  item: string;
  reason: string;
  inspectionStatus: string;
  amount: number;
  status: string;
}

export interface RefundRecord {
  id: string;
  returnRef: string;
  orderNumber: string;
  customer: string;
  method: string;
  transactionRef: string;
  amount: number;
  date: string;
  status: string;
}

export interface AdminRole {
  id: string;
  name: string;
  usersCount: number;
  description: string;
  permissions: string[];
}

export interface StoreSettings {
  storeName: string;
  currency: string;
  supportEmail: string;
  supportPhone: string;
  warehouseAddress: string;
  freeShippingThreshold: number;
  standardShippingFee: number;
  taxRate: string;
  inventoryAlertThreshold: number;
}

export interface CMSContent {
  announcementBar: {
    text: string;
    link?: string;
  };
  heroBanner: {
    eyebrow: string;
    title: string;
    subtitle: string;
    ctaPrimary?: string;
    ctaSecondary?: string;
  };
  craftsmanshipFeature: {
    heading: string;
    paragraph1: string;
    paragraph2?: string;
  };
}

export interface ContentEntry {
  id: string;
  slug: string;
  title: string;
  subtitle?: string;
  category: string;
  readTime: string;
  publishedAt?: string;
  date?: string;
  author?: string;
  image: string;
  excerpt: string;
  content: string;
  tags?: string[];
}

export interface AdminUser {
  id: string;
  name: string;
  email: string;
  role: string;
  status: string;
  lastActive: string;
  avatar?: string;
}

export interface AuditLog {
  id: string | number;
  action: string;
  user: string;
  target?: string;
  entity?: string;
  timestamp: string;
  ipAddress?: string;
  status?: string;
  severity?: string;
}

export interface FaqItem {
  q: string;
  a: string;
}

export interface FaqCategory {
  category: string;
  items: FaqItem[];
}

export interface StoreRoute {
  page: string;
  params?: Record<string, unknown>;
}

export interface ToastMessage {
  id: number;
  message: string;
  type?: 'default' | 'success' | 'info' | 'error';
}

export interface StoreContextType {
  currentRoute: StoreRoute;
  navigate: (page: string, params?: Record<string, unknown>) => void;
  cart: CartItem[];
  wishlist: string[];
  seedDemoData: () => void;
  recentlyViewed: string[];
  addRecentlyViewed: (productId: string) => void;
  addToCart: (product: Product, color?: ColorVariant | null, quantity?: number) => void;
  updateQuantity: (cartItemId: string, quantity: number) => void;
  removeFromCart: (cartItemId: string) => void;
  moveToWishlist: (cartItem: CartItem) => void;
  toggleWishlist: (productId: string) => void;
  appliedCoupon: Coupon | null;
  applyCoupon: (code: string) => Promise<{ success: boolean; message: string }>;
  removeCoupon: () => void;
  cartSubtotal: number;
  discountAmount: number;
  shippingFee: number;
  estimatedTax: number;
  cartTotal: number;
  cartItemCount: number;
  isCartDrawerOpen: boolean;
  setIsCartDrawerOpen: (open: boolean) => void;
  isMobileMenuOpen: boolean;
  setIsMobileMenuOpen: (open: boolean) => void;
  isSearchModalOpen: boolean;
  setIsSearchModalOpen: (open: boolean) => void;
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  toasts: ToastMessage[];
  addToast: (message: string, type?: 'default' | 'success' | 'info' | 'error') => void;
  orders: Order[];
  latestOrder: Order | null;
  placeOrder: (orderDetails: { shippingAddress: Address; paymentMethod?: string }) => void;
  adminProducts: Product[];
  setAdminProducts: React.Dispatch<React.SetStateAction<Product[]>>;
  adminCategories: Category[];
  setAdminCategories: React.Dispatch<React.SetStateAction<Category[]>>;
  user: {
    id: string;
    name: string;
    email: string;
    status?: string;
    avatarUrl?: string;
    roles: string[];
    permissions?: string[];
  } | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<boolean>;
  loginWithGoogle: (idToken: string) => Promise<boolean>;
  register: (name: string, email: string, password: string, phone?: string) => Promise<boolean>;
  logout: () => void;
}
