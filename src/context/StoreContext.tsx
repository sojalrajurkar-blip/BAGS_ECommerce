'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import {
  productRepository,
  categoryRepository,
  orderRepository,
  couponRepository,
  authRepository,
  cartRepository,
  wishlistRepository,
  UserSummary
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

  // Cart State (Persisted in localStorage & synced with backend)
  const [cart, setCart] = useState<CartItem[]>([]);
  // Wishlist State (Persisted & synced with backend)
  const [wishlist, setWishlist] = useState<string[]>([]);
  const [hasHydrated, setHasHydrated] = useState(false);

  // Hydrate cart and wishlist from localStorage after initial client mount
  useEffect(() => {
    try {
      const savedCart = localStorage.getItem('rora_cart');
      if (savedCart) {
        setCart(JSON.parse(savedCart));
      }
      const savedWishlist = localStorage.getItem('rora_wishlist');
      if (savedWishlist) {
        setWishlist(JSON.parse(savedWishlist));
      }
    } catch (e) {
      console.error(e);
    } finally {
      setHasHydrated(true);
    }
  }, []);

  // Save cart to localStorage when changed (only after initial hydration)
  useEffect(() => {
    if (!hasHydrated || typeof window === 'undefined') return;
    try {
      localStorage.setItem('rora_cart', JSON.stringify(cart));
    } catch (e) {
      console.error(e);
    }
  }, [cart, hasHydrated]);

  // Save wishlist to localStorage when changed (only after initial hydration)
  useEffect(() => {
    if (!hasHydrated || typeof window === 'undefined') return;
    try {
      localStorage.setItem('rora_wishlist', JSON.stringify(wishlist));
    } catch (e) {
      console.error(e);
    }
  }, [wishlist, hasHydrated]);

  // Authentication State
  const [user, setUser] = useState<UserSummary | null>(null);

  useEffect(() => {
    authRepository.getCurrentUser().then(u => {
      if (u) setUser(u);
    });

    productRepository.getProducts().then(prods => {
      if (prods && prods.length > 0) {
        setAdminProducts(prods);
      }
    }).catch(() => {});

    categoryRepository.getCategories().then(cats => {
      if (cats && cats.length > 0) {
        setAdminCategories(cats);
      }
    }).catch(() => {});
  }, []);

  // Load orders & wishlist when auth changes
  useEffect(() => {
    orderRepository.getOrders().then(ordList => {
      if (ordList && ordList.length > 0) {
        setOrders(ordList);
      }
    }).catch(() => {});

    wishlistRepository.getWishlist().then(wRes => {
      if (wRes && wRes.items && wRes.items.length > 0) {
        setWishlist(wRes.items.map(item => item.productId));
      }
    }).catch(() => {});
  }, [user]);

  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      const res = await authRepository.login({ email, password });
      setUser(res.user);
      addToast(`Welcome back, ${res.user.name}.`, 'success');
      return true;
    } catch (e) {
      console.error(e);
      addToast('Invalid credentials. Please check your email and password.', 'error');
      return false;
    }
  };

  const loginWithGoogle = async (idToken: string): Promise<boolean> => {
    try {
      const res = await authRepository.loginWithGoogle(idToken);
      setUser(res.user);
      addToast(`Welcome to RÓRA Atelier, ${res.user.name}.`, 'success');
      return true;
    } catch (e) {
      console.error(e);
      addToast('Google authentication failed. Please try again.', 'error');
      return false;
    }
  };

  const register = async (name: string, email: string, password: string, phone?: string): Promise<boolean> => {
    try {
      const res = await authRepository.register({ name, email, password, phone });
      setUser(res.user);
      addToast(`Account created successfully. Welcome to RÓRA, ${res.user.name}.`, 'success');
      return true;
    } catch (e) {
      console.error(e);
      addToast('Registration failed. Please try again.', 'error');
      return false;
    }
  };

  const logout = () => {
    authRepository.logout();
    setUser(null);
    addToast('You have been signed out.');
  };

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

    // Backend background sync
    cartRepository.addItem({
      productId: product.id,
      quantity,
      colorName: selectedColor.name,
    }).catch(() => {});

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
    cartRepository.updateQuantity(cartItemId, quantity).catch(() => {});
  };

  const removeFromCart = (cartItemId: string) => {
    setCart(prev => prev.filter(item => item.id !== cartItemId));
    cartRepository.removeItem(cartItemId).catch(() => {});
    addToast('Item removed from bag.');
  };

  const moveToWishlist = (cartItem: CartItem) => {
    if (cartItem.productId && !wishlist.includes(cartItem.productId)) {
      setWishlist(prev => [...prev, cartItem.productId!]);
      wishlistRepository.addItem(cartItem.productId!).catch(() => {});
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
      wishlistRepository.removeItem(productId).catch(() => {});
      addToast(`Removed "${product ? product.name : 'Item'}" from wishlist.`);
    } else {
      setWishlist(prev => [...prev, productId]);
      wishlistRepository.addItem(productId).catch(() => {});
      addToast(`Saved "${product ? product.name : 'Item'}" to wishlist.`);
    }
  };

  // Coupon & Financial Totals (INR ₹)
  const [appliedCoupon, setAppliedCoupon] = useState<Coupon | null>(null);

  const applyCoupon = async (code: string) => {
    const cleanCode = code.trim().toUpperCase();
    const currentSubtotal = cart.reduce((sum, item) => sum + (item.price || item.product.price) * item.quantity, 0);
    const found = await couponRepository.validateCoupon(cleanCode, currentSubtotal);
    if (found) {
      setAppliedCoupon(found);
      cartRepository.applyCoupon(cleanCode).catch(() => {});
      addToast(`Coupon "${found.code}" applied: ${found.discountPercent || found.discountValue}% off!`);
      return { success: true, message: `Coupon applied: ${found.discountPercent || found.discountValue}% off` };
    } else {
      addToast('Invalid coupon code. Try "RORA10"', 'error');
      return { success: false, message: 'Invalid coupon code.' };
    }
  };

  const removeCoupon = () => {
    setAppliedCoupon(null);
    cartRepository.removeCoupon().catch(() => {});
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

  const placeOrder = async (orderDetails: { shippingAddress: Address; paymentMethod?: string }) => {
    try {
      const order = await orderRepository.placeOrder({
        customerName: orderDetails.shippingAddress.fullName || user?.name || 'Valued Client',
        customerEmail: user?.email || 'client@rora-luxury.com',
        customerPhone: orderDetails.shippingAddress.phone || (user as unknown as { phone?: string })?.phone || '+91 98200 12345',
        shippingAddress: orderDetails.shippingAddress,
        billingAddress: orderDetails.shippingAddress,
        paymentMethod: orderDetails.paymentMethod || 'UPI / Card (Mock)',
        couponCode: appliedCoupon?.code,
      });

      const finalOrder: Order = {
        ...order,
        items: order.items && order.items.length > 0 ? order.items : cart.map(item => ({
          id: item.productId,
          productId: item.productId,
          name: item.product.name,
          color: item.color?.name,
          colorName: item.color?.name,
          price: item.price || item.product.price,
          quantity: item.quantity,
          image: item.color?.image || item.product.images[0]
        })),
        total: order.total || cartTotal,
        shippingAddress: orderDetails.shippingAddress,
      };

      setOrders(prev => [finalOrder, ...prev.filter(o => o.id !== finalOrder.id)]);
      setLatestOrder(finalOrder);
      setCart([]);
      setAppliedCoupon(null);
      cartRepository.clearCart().catch(() => {});
      addToast(`Order ${finalOrder.orderNumber} placed successfully!`, 'success');
      navigate('confirmation', { order: finalOrder });
    } catch (e) {
      console.error('placeOrder error:', e);
      addToast('Failed to place order. Please try again.', 'error');
    }
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
        setAdminCategories,
        user,
        isAuthenticated: Boolean(user),
        login,
        loginWithGoogle,
        register,
        logout
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
