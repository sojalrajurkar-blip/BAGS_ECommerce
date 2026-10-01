package com.rora.backend.shopping;

import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.ProductVariantRepository;
import com.rora.backend.catalog.service.ProductService;
import com.rora.backend.shopping.cart.dto.AddToCartRequest;
import com.rora.backend.shopping.cart.dto.CartDto;
import com.rora.backend.shopping.cart.entity.Cart;
import com.rora.backend.shopping.cart.entity.CartItem;
import com.rora.backend.shopping.cart.repository.CartItemRepository;
import com.rora.backend.shopping.cart.repository.CartRepository;
import com.rora.backend.shopping.cart.service.CartService;
import com.rora.backend.shopping.coupon.dto.CouponValidationResult;
import com.rora.backend.shopping.coupon.service.CouponService;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CartService cartService;

    private User mockUser;
    private Product mockProduct;
    private ProductVariant mockVariant;
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        mockUser = User.builder().id("user-1").email("user@example.com").build();

        mockVariant = ProductVariant.builder()
                .id("var-1")
                .sku("RRA-NMD-01-OLV")
                .colorName("Olive Green")
                .stock(10)
                .build();

        mockProduct = Product.builder()
                .id("prod-1")
                .slug("the-nomad-backpack")
                .name("The Nomad Backpack")
                .price(BigDecimal.valueOf(4899.00))
                .stock(20)
                .variants(new ArrayList<>(List.of(mockVariant)))
                .images(new ArrayList<>())
                .build();
        mockVariant.setProduct(mockProduct);

        mockCart = Cart.builder()
                .id("cart-1")
                .user(mockUser)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void testAddItemToCart_NewItem() {
        when(cartRepository.findByUserId("user-1")).thenReturn(Optional.of(mockCart));
        when(productService.findEntityByIdOrSlug("prod-1")).thenReturn(mockProduct);
        when(productVariantRepository.findById("var-1")).thenReturn(Optional.of(mockVariant));
        when(cartItemRepository.findByCartIdAndProductIdAndVariantId("cart-1", "prod-1", "var-1")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        CartItem createdItem = CartItem.builder()
                .id("item-1")
                .cart(mockCart)
                .product(mockProduct)
                .variant(mockVariant)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(4899.00))
                .totalPrice(BigDecimal.valueOf(4899.00))
                .build();
        when(cartItemRepository.findByCartId("cart-1")).thenReturn(List.of(createdItem));

        AddToCartRequest request = AddToCartRequest.builder()
                .productId("prod-1")
                .variantId("var-1")
                .quantity(1)
                .build();

        CartDto result = cartService.addItem("user-1", null, request);

        assertNotNull(result);
        assertEquals(1, result.getItemCount());
        assertEquals(BigDecimal.valueOf(4899.00), result.getSubtotal());
        // Since subtotal > 1999, shipping should be free (0)
        assertEquals(BigDecimal.ZERO, result.getShippingFee());
        assertTrue(result.isFreeShippingEligible());
    }

    @Test
    void testApplyCoupon_Success() {
        when(cartRepository.findByUserId("user-1")).thenReturn(Optional.of(mockCart));

        CartItem item = CartItem.builder()
                .id("item-1")
                .cart(mockCart)
                .product(mockProduct)
                .variant(mockVariant)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(4899.00))
                .totalPrice(BigDecimal.valueOf(4899.00))
                .build();
        when(cartItemRepository.findByCartId("cart-1")).thenReturn(List.of(item));

        when(couponService.validateCoupon("RORA10", BigDecimal.valueOf(4899.00), "user-1"))
                .thenReturn(CouponValidationResult.builder()
                        .valid(true)
                        .code("RORA10")
                        .discountAmount(BigDecimal.valueOf(489.90))
                        .build());

        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        CartDto result = cartService.applyCoupon("user-1", null, "RORA10");

        assertNotNull(result);
        assertEquals("RORA10", result.getAppliedCoupon());
        assertEquals(0, BigDecimal.valueOf(489.90).compareTo(result.getDiscountAmount()));
        assertEquals(0, new BigDecimal("4409.10").compareTo(result.getTotal()));
    }
}
