package com.rora.backend.order.service;

import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.catalog.repository.ProductVariantRepository;
import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.inventory.service.InventoryService;
import com.rora.backend.order.dto.*;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderItem;
import com.rora.backend.order.entity.OrderTimelineEvent;
import com.rora.backend.order.repository.OrderItemRepository;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.order.repository.OrderTimelineEventRepository;
import com.rora.backend.shopping.cart.entity.Cart;
import com.rora.backend.shopping.cart.entity.CartItem;
import com.rora.backend.shopping.cart.repository.CartRepository;
import com.rora.backend.shopping.cart.service.CartService;
import com.rora.backend.shopping.coupon.dto.CouponValidationResult;
import com.rora.backend.shopping.coupon.service.CouponService;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderTimelineEventRepository orderTimelineEventRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartService cartService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductVariantRepository productVariantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CouponService couponService;
    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private ProductVariant testVariant;
    private AddressDto testAddress;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-100")
                .email("test@rora-luxury.com")
                .name("Sarah Test")
                .build();

        testProduct = Product.builder()
                .id("prod-1")
                .name("The Nomad Backpack")
                .price(BigDecimal.valueOf(4899.00))
                .stock(20)
                .build();

        testVariant = ProductVariant.builder()
                .id("var-1-1")
                .product(testProduct)
                .colorName("Olive Green")
                .stock(10)
                .priceOverride(BigDecimal.valueOf(4899.00))
                .build();

        testAddress = AddressDto.builder()
                .fullName("Sarah Test")
                .street("142 Bandra West, Hill Road")
                .city("Mumbai")
                .state("MH")
                .postalCode("400050")
                .country("India")
                .phone("+91 98200 12345")
                .build();
    }

    @Test
    @DisplayName("Place direct order successfully with stock deduction and 5-step timeline")
    void testPlaceOrder_DirectItems_Success() {
        CheckoutRequest request = CheckoutRequest.builder()
                .customerName("Sarah Test")
                .customerEmail("test@rora-luxury.com")
                .customerPhone("+91 98200 12345")
                .shippingAddress(testAddress)
                .items(List.of(
                        CheckoutItemDto.builder()
                                .productId("prod-1")
                                .variantId("var-1-1")
                                .quantity(2)
                                .build()
                ))
                .build();

        when(userRepository.findById("user-100")).thenReturn(Optional.of(testUser));
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(testProduct));
        when(productVariantRepository.findById("var-1-1")).thenReturn(Optional.of(testVariant));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId("order-uuid-1");
            return o;
        });

        OrderDto result = orderService.placeOrder("user-100", request);

        assertNotNull(result);
        assertNotNull(result.getOrderNumber());
        assertEquals(0, BigDecimal.valueOf(9798.00).compareTo(result.getSubtotal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getShippingFee())); // >= 1999 so free shipping
        assertEquals(0, BigDecimal.valueOf(9798.00).compareTo(result.getTotal()));
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getTimeline().size());
        assertTrue(result.getTimeline().get(0).isCompleted()); // Order Placed
        assertTrue(result.getTimeline().get(1).isCompleted()); // Payment Verified

        // Verify stock deducted
        assertEquals(8, testVariant.getStock());
        assertEquals(18, testProduct.getStock());
    }

    @Test
    @DisplayName("Place order with coupon discount applied")
    void testPlaceOrder_WithCoupon_Success() {
        CheckoutRequest request = CheckoutRequest.builder()
                .customerName("Sarah Test")
                .customerEmail("test@rora-luxury.com")
                .shippingAddress(testAddress)
                .couponCode("RORA10")
                .items(List.of(
                        CheckoutItemDto.builder()
                                .productId("prod-1")
                                .variantId("var-1-1")
                                .quantity(1)
                                .build()
                ))
                .build();

        when(userRepository.findById("user-100")).thenReturn(Optional.of(testUser));
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(testProduct));
        when(productVariantRepository.findById("var-1-1")).thenReturn(Optional.of(testVariant));
        when(couponService.validateCoupon("RORA10", BigDecimal.valueOf(4899.00), "user-100"))
                .thenReturn(CouponValidationResult.builder()
                        .valid(true)
                        .code("RORA10")
                        .discountAmount(BigDecimal.valueOf(489.90))
                        .build());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId("order-uuid-2");
            return o;
        });

        OrderDto result = orderService.placeOrder("user-100", request);

        assertNotNull(result);
        assertEquals("RORA10", result.getCouponCode());
        assertEquals(0, BigDecimal.valueOf(489.90).compareTo(result.getDiscountAmount()));
        assertEquals(0, BigDecimal.valueOf(4409.10).compareTo(result.getTotal()));
        verify(couponService).recordUsage(eq("RORA10"), eq("user-100"), eq("order-uuid-2"), eq(BigDecimal.valueOf(489.90)));
    }

    @Test
    @DisplayName("Place order from cart throws BadRequestException if cart is empty")
    void testPlaceOrder_EmptyCart_ThrowsBadRequest() {
        CheckoutRequest request = CheckoutRequest.builder()
                .customerName("Sarah Test")
                .customerEmail("test@rora-luxury.com")
                .shippingAddress(testAddress)
                .build();

        when(userRepository.findById("user-100")).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId("user-100")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> orderService.placeOrder("user-100", request));
    }

    @Test
    @DisplayName("Track order by number with hashtag prefix support")
    void testTrackOrder_Success() {
        Order order = Order.builder()
                .id("RRA-89241")
                .orderNumber("#RRA89241")
                .status("Delivered")
                .subtotal(BigDecimal.valueOf(8798.00))
                .total(BigDecimal.valueOf(8798.00))
                .timelineEvents(new ArrayList<>())
                .items(new ArrayList<>())
                .build();

        when(orderRepository.findByOrderNumber("#RRA89241")).thenReturn(Optional.of(order));

        OrderDto tracked = orderService.trackOrderByNumber("#RRA89241");
        assertNotNull(tracked);
        assertEquals("#RRA89241", tracked.getOrderNumber());
        assertEquals("Delivered", tracked.getStatus());
    }

    @Test
    @DisplayName("Update order status to Delivered updates timeline and completes payment")
    void testUpdateOrderStatus_Delivered_UpdatesTimeline() {
        Order order = Order.builder()
                .id("RRA-89105")
                .orderNumber("#RRA89105")
                .status("Processing")
                .paymentStatus("Captured")
                .timelineEvents(new ArrayList<>(List.of(
                        OrderTimelineEvent.builder().stepName("Dispatched from Hub").completed(false).build(),
                        OrderTimelineEvent.builder().stepName("Out for Delivery").completed(false).build(),
                        OrderTimelineEvent.builder().stepName("Delivered").completed(false).build()
                )))
                .items(new ArrayList<>())
                .build();

        when(orderRepository.findByIdOrOrderNumber("RRA-89105")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderStatusUpdateRequest updateReq = OrderStatusUpdateRequest.builder()
                .status("Delivered")
                .carrier("Bluedart Express")
                .build();

        OrderDto result = orderService.updateOrderStatus("RRA-89105", updateReq);

        assertEquals("Delivered", result.getStatus());
        assertEquals("Completed", result.getPaymentStatus());
        assertTrue(order.getTimelineEvents().stream().allMatch(OrderTimelineEvent::isCompleted));
    }

    @Test
    @DisplayName("Cancel order restores stock and sets status to Cancelled and Refunded")
    void testCancelOrder_RestoresStock() {
        OrderItem item = OrderItem.builder()
                .id("item-1")
                .product(testProduct)
                .variant(testVariant)
                .quantity(2)
                .build();

        Order order = Order.builder()
                .id("RRA-88940")
                .orderNumber("#RRA88940")
                .user(testUser)
                .status("Processing")
                .paymentStatus("Captured")
                .items(new ArrayList<>(List.of(item)))
                .timelineEvents(new ArrayList<>())
                .build();

        when(orderRepository.findByIdOrOrderNumber("RRA-88940")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int initialProductStock = testProduct.getStock();
        int initialVariantStock = testVariant.getStock();

        OrderDto cancelled = orderService.cancelOrder("RRA-88940", "user-100");

        assertEquals("Cancelled", cancelled.getStatus());
        assertEquals("Refunded", cancelled.getPaymentStatus());
        assertEquals(initialProductStock + 2, testProduct.getStock());
        assertEquals(initialVariantStock + 2, testVariant.getStock());
    }
}
