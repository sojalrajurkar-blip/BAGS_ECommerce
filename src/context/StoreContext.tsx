'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import {
  productRepository,
  categoryRepository,
  orderRepository,
  couponRepository
} from '../data/repositories';
import { PRODUCTS, CATEGORIES, MOCK_ORDERS } from '../data/mockData';

import {
  Product,
  Category,
  ColorVariant,
  CartItem,
  Order,
  Address,
  StoreRoute,
  ToastMessage,
  Coupon,
  StoreContextType
} from '../types/domain';

const StoreContext = createContext<StoreContextType | null>(null);

export const StoreProvider = ({ children }: { children: React.ReactNode }) => {
  const router = useRouter();
  const pathname = usePathname();

  // Navigation / Page State
  const [currentRoute, setCurrentRoute] = useState<StoreRoute>({
    page: 'home',
    params: {}
  });

  // Sync current route from pathname
  useEffect(() => {
    if (!pathname) return;
    if (pathname === '/') setCurrentRoute(prev => ({ ...prev, page: 'home' }));
    else if (pathname.startsWith('/shop')) setCurrentRoute(prev => ({ ...prev, page: 'shop' }));
    else if (pathname.startsWith('/category')) setCurrentRoute(prev => ({ ...prev, page: 'category' }));
    else if (pathname.startsWith('/product')) setCurrentRoute(prev => ({ ...prev, page: 'product' }));
    else if (pathname.startsWith('/search')) setCurrentRoute(prev => ({ ...prev, page: 'search' }));
    else if (pathname.startsWith('/wishlist')) setCurrentRoute(prev => ({ ...prev, page: 'wishlist' }));
    else if (pathname.startsWith('/cart')) setCurrentRoute(prev => ({ ...prev, page: 'cart' }));
    else if (pathname.startsWith('/checkout')) setCurrentRoute(prev => ({ ...prev, page: 'checkout' }));
    else if (pathname.startsWith('/confirmation')) setCurrentRoute(prev => ({ ...prev, page: 'confirmation' }));
    else if (pathname.startsWith('/account')) setCurrentRoute(prev => ({ ...prev, page: 'account' }));
    else if (pathname.startsWith('/orders')) setCurrentRoute(prev => ({ ...prev, page: 'orders' }));
    else if (pathname.startsWith('/about')) setCurrentRoute(prev => ({ ...prev, page: 'about' }));
    else if (pathname.startsWith('/journal')) setCurrentRoute(prev => ({ ...prev, page: 'journal' }));
    else if (pathname.startsWith('/faq')) setCurrentRoute(prev => ({ ...prev, page: 'faq' }));
    else if (pathname.startsWith('/contact')) setCurrentRoute(prev => ({ ...prev, page: 'contact' }));
    else if (pathname.startsWith('/returns')) setCurrentRoute(prev => ({ ...prev, page: 'returns' }));
    else if (pathname.startsWith('/shipping')) setCurrentRoute(prev => ({ ...prev, page: 'shipping' }));
    else if (pathname.startsWith('/admin')) setCurrentRoute(prev => ({ ...prev, page: 'admin' }));
  }, [pathname]);

  // Unified Next.js router bridge
  const navigate = (page: string, params: Record<string, unknown> = {}) => {
    setCurrentRoute({ page, params });
    setIsCartDrawerOpen(false);
    setIsMobileMenuOpen(false);
    setIsSearchModalOpen(false);

    if (typeof window !== 'undefined') {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    if (page.startsWith('/')) {
      router.push(page);
      return;
    }

    switch (page) {
      case 'home':
        router.push('/');
        break;
      case 'shop':
        router.push('/shop');
        break;
      case 'category':
        if (params.categoryId || params.slug) {
          router.push(`/category/${params.categoryId || params.slug}`);
        } else {
          router.push('/shop');
        }
        break;
      case 'product':
        if (params.productId || params.slug) {
          router.push(`/product/${params.productId || params.slug}`);
        } else {
          router.push('/shop');
        }
        break;
      case 'search':
        if (params.query) {
          router.push(`/search?q=${encodeURIComponent(String(params.query))}`);
        } else {
          router.push('/search');
        }
        break;
      case 'wishlist':
        router.push('/wishlist');
        break;
      case 'cart':
        router.push('/cart');
        break;
      case 'checkout':
        router.push('/checkout');
        break;
      case 'confirmation':
        router.push('/confirmation');
        break;
      case 'account':
        router.push('/account');
        break;
      case 'orders':
        router.push('/orders');
        break;
      case 'order-details':
      case 'order-tracking':
        if (params.orderId) {
          router.push(`/orders/${params.orderId}`);
        } else {
          router.push('/orders');
        }
        break;
      case 'about':
        router.push('/about');
        break;
      case 'journal':
        if (params.articleSlug) {
          router.push(`/journal/${params.articleSlug}`);
        } else {
          router.push('/journal');
        }
        break;
      case 'faq':
        router.push('/faq');
        break;
      case 'contact':
        router.push('/contact');
        break;
      case 'returns':
        router.push('/returns');
        break;
      case 'shipping':
        router.push('/shipping');
        break;
      case 'admin':
        router.push('/admin');
        break;
      default:
        router.push(`/${page}`);
    }
  };

  // Cart State (Persisted in localStorage)
  const [cart, setCart] = useState<CartItem[]>(() => {
    if (typeof window === 'undefined') return [];
    try {
      const saved = localStorage.getItem('rora_cart');
      if (saved) return JSON.parse(saved);
    } catch (e) {
      console.error(e);
    }
    return [];
  });

  useEffect(() => {
    if (typeof window === 'undefined') return;
    try {
      localStorage.setItem('rora_cart', JSON.stringify(cart));
    } catch (e) {
      console.error(e);
    }
  }, [cart]);

  // Wishlist State (Persisted)
  const [wishlist, setWishlist] = useState<string[]>(() => {
    if (typeof window === 'undefined') return [];
    try {
      const saved = localStorage.getItem('rora_wishlist');
      if (saved) return JSON.parse(saved);
    } catch (e) {
      console.error(e);
    }
    return [];
  });

  useEffect(() => {
    if (typeof window === 'undefined') return;
    try {
      localStorage.setItem('rora_wishlist', JSON.stringify(wishlist));
    } catch (e) {
      console.error(e);
    }
  }, [wishlist]);

  // Demo state seeding helper
  const seedDemoData = () => {
    if (PRODUCTS && PRODUCTS.length >= 3) {
      const demoCart: CartItem[] = [
        {
          id: `${PRODUCTS[0].id}-${PRODUCTS[0].colors?.[0]?.name || 'Standard'}`,
          productId: PRODUCTS[0].id,
          product: PRODUCTS[0] as Product,
          color: PRODUCTS[0].colors?.[0] as ColorVariant,
          quantity: 1,
          price: PRODUCTS[0].price
        },
        {
          id: `${PRODUCTS[1].id}-${PRODUCTS[1].colors?.[0]?.name || 'Standard'}`,
          productId: PRODUCTS[1].id,
          product: PRODUCTS[1] as Product,
          color: PRODUCTS[1].colors?.[0] as ColorVariant,
          quantity: 1,
          price: PRODUCTS[1].price
        }
      ];
      setCart(demoCart);
      setWishlist([PRODUCTS[0].id, PRODUCTS[1].id, PRODUCTS[2].id]);
      addToast('Demo sample items added to cart and wishlist.');
    }
  };

  // Recently Viewed
  const [recentlyViewed, setRecentlyViewed] = useState<string[]>(['prod-1', 'prod-2', 'prod-3', 'prod-4']);

  const addRecentlyViewed = (productId: string) => {
    setRecentlyViewed(prev => {
      const filtered = prev.filter(id => id !== productId);
      return [productId, ...filtered].slice(0, 6);
    });
  };

  // Toasts / Notifications
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const addToast = (message: string, type: 'default' | 'info' | 'error' | 'success' = 'info') => {
    const id = Date.now() + Math.random();
    setToasts(prev => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id));
    }, 3200);
  };

  // Cart Drawer & Modals
  const [isCartDrawerOpen, setIsCartDrawerOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isSearchModalOpen, setIsSearchModalOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // Cart Functions
  const addToCart = (product: Product, color?: ColorVariant | null, quantity = 1) => {
    const selectedColor: ColorVariant = color || (product.colors && product.colors[0]) || { name: 'Standard', hex: '#1C1B1A' };
    const cartItemId = `${product.id}-${selectedColor.name}`;

    setCart(prev => {
      const existingIndex = prev.findIndex(item => item.id === cartItemId);
      if (existingIndex > -1) {
        const updated = [...prev];
        updated[existingIndex].quantity += quantity;
        return updated;
      } else {
        return [
          ...prev,
          {
            id: cartItemId,
            productId: product.id,
            product,
            color: selectedColor,
            quantity,
            price: product.price
          }
        ];
      }
    });

    addToast(`Added "${product.name}" (${selectedColor.name}) to your bag.`);
    setIsCartDrawerOpen(true);
  };

  const updateQuantity = (cartItemId: string, quantity: number) => {
    if (quantity <= 0) {
      removeFromCart(cartItemId);
      return;
    }
    setCart(prev =>
      prev.map(item => (item.id === cartItemId ? { ...item, quantity } : item))
    );
  };

  const removeFromCart = (cartItemId: string) => {
    setCart(prev => prev.filter(item => item.id !== cartItemId));
    addToast('Item removed from bag.');
  };

  const moveToWishlist = (cartItem: CartItem) => {
    if (cartItem.productId && !wishlist.includes(cartItem.productId)) {
      setWishlist(prev => [...prev, cartItem.productId!]);
    }
    removeFromCart(cartItem.id);
    addToast(`Moved "${cartItem.product.name}" to your wishlist.`);
  };

  // Wishlist Functions
  const toggleWishlist = (productId: string) => {
    const exists = wishlist.includes(productId);
    const product = (PRODUCTS as Product[]).find(p => p.id === productId);
    if (exists) {
      setWishlist(prev => prev.filter(id => id !== productId));
      addToast(`Removed "${product ? product.name : 'Item'}" from wishlist.`);
    } else {
      setWishlist(prev => [...prev, productId]);
      addToast(`Saved "${product ? product.name : 'Item'}" to wishlist.`);
    }
  };

  // Coupon & Financial Totals (INR ₹)
  const [appliedCoupon, setAppliedCoupon] = useState<Coupon | null>(null);

  const applyCoupon = async (code: string) => {
    const cleanCode = code.trim().toUpperCase();
    const found = await couponRepository.validateCoupon(cleanCode);
    if (found) {
      setAppliedCoupon(found);
      addToast(`Coupon "${found.code}" applied: ${found.discountPercent}% off!`);
      return { success: true, message: `Coupon applied: ${found.discountPercent}% off` };
    } else {
      addToast('Invalid coupon code. Try "RORA10"', 'error');
      return { success: false, message: 'Invalid coupon code.' };
    }
  };

  const removeCoupon = () => {
    setAppliedCoupon(null);
    addToast('Coupon removed.');
  };

  // Cart calculations in Rupees (₹)
  const cartSubtotal = cart.reduce((sum, item) => sum + (item.price || item.product.price) * item.quantity, 0);
  const discountAmount = appliedCoupon ? Math.round((cartSubtotal * (appliedCoupon.discountPercent || appliedCoupon.discountValue || 0)) / 100) : 0;
  const shippingFee = cartSubtotal >= 1999 || cartSubtotal === 0 ? 0 : 199;
  const estimatedTax = 0; // GST included
  const cartTotal = Math.max(0, cartSubtotal - discountAmount + shippingFee);
  const cartItemCount = cart.reduce((sum, item) => sum + item.quantity, 0);

  // Orders State
  const [orders, setOrders] = useState<Order[]>(MOCK_ORDERS as unknown as Order[]);
  const [latestOrder, setLatestOrder] = useState<Order | null>(null);

  const placeOrder = (orderDetails: { shippingAddress: Address; paymentMethod?: string }) => {
    const newOrderNumber = `#RRA${Math.floor(10000 + Math.random() * 90000)}`;
    const newOrder: Order = {
      id: newOrderNumber.replace('#', ''),
      orderNumber: newOrderNumber,
      date: new Date().toLocaleDateString('en-IN', { month: 'long', day: 'numeric', year: 'numeric' }),
      status: 'Processing',
      total: cartTotal,
      items: cart.map(item => ({
        id: item.productId,
        productId: item.productId,
        name: item.product.name,
        color: item.color?.name,
        colorName: item.color?.name,
        price: item.price || item.product.price,
        quantity: item.quantity,
        image: item.color?.image || item.product.images[0]
      })),
      shippingAddress: orderDetails.shippingAddress,
      paymentMethod: orderDetails.paymentMethod || 'UPI / Card (Mock)',
      timeline: [
        { step: 'Order Placed', time: 'Just now', completed: true },
        { step: 'Payment Verified', time: 'Just now', completed: true },
        { step: 'Dispatched from Hub', time: 'Pending', completed: false },
        { step: 'Out for Delivery', time: 'Pending', completed: false },
        { step: 'Delivered', time: 'Pending', completed: false }
      ]
    };

    setOrders(prev => [newOrder, ...prev]);
    setLatestOrder(newOrder);
    setCart([]);
    setAppliedCoupon(null);
    navigate('confirmation', { order: newOrder });
  };

  // Admin dynamic data
  const [adminProducts, setAdminProducts] = useState<Product[]>(PRODUCTS as unknown as Product[]);
  const [adminCategories, setAdminCategories] = useState<Category[]>(CATEGORIES as unknown as Category[]);

  return (
    <StoreContext.Provider
      value={{
        currentRoute,
        navigate,
        cart,
        wishlist,
        seedDemoData,
        recentlyViewed,
        addRecentlyViewed,
        addToCart,
        updateQuantity,
        removeFromCart,
        moveToWishlist,
        toggleWishlist,
        appliedCoupon,
        applyCoupon,
        removeCoupon,
        cartSubtotal,
        discountAmount,
        shippingFee,
        estimatedTax,
        cartTotal,
        cartItemCount,
        isCartDrawerOpen,
        setIsCartDrawerOpen,
        isMobileMenuOpen,
        setIsMobileMenuOpen,
        isSearchModalOpen,
        setIsSearchModalOpen,
        searchQuery,
        setSearchQuery,
        toasts,
        addToast,
        orders,
        latestOrder,
        placeOrder,
        adminProducts,
        setAdminProducts,
        adminCategories,
        setAdminCategories
      }}
    >
      {children}
    </StoreContext.Provider>
  );
};

export const useStore = () => {
  const context = useContext(StoreContext);
  if (!context) {
    throw new Error('useStore must be used within a StoreProvider');
  }
  return context;
};
