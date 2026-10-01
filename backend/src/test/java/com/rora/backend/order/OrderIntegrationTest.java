package com.rora.backend.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.catalog.entity.Category;
import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.CategoryRepository;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.catalog.repository.ProductVariantRepository;
import com.rora.backend.order.dto.AddressDto;
import com.rora.backend.order.dto.CheckoutItemDto;
import com.rora.backend.order.dto.CheckoutRequest;
import com.rora.backend.order.dto.OrderStatusUpdateRequest;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderItem;
import com.rora.backend.order.entity.OrderTimelineEvent;
import com.rora.backend.order.repository.OrderItemRepository;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.order.repository.OrderTimelineEventRepository;
import com.rora.backend.user.Role;
import com.rora.backend.user.RoleRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Product testProduct;
    private ProductVariant testVariant;
    private Order seededOrder;
    private Order seededOrder2;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        Category category = categoryRepository.findById("backpacks")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .id("backpacks")
                        .slug("backpacks")
                        .name("Backpacks")
                        .title("Everyday Backpacks")
                        .headline("Luxury Backpacks")
                        .subtitle("Ergonomic luxury")
                        .description("Built for modern explorers.")
                        .heroImage("https://images.unsplash.com/photo-1553062407-98eeb64c6a62")
                        .image("https://images.unsplash.com/photo-1553062407-98eeb64c6a62")
                        .build()));

        testProduct = productRepository.findById("prod-1")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .id("prod-1")
                        .slug("the-nomad-backpack")
                        .name("The Nomad Backpack")
                        .category(category)
                        .categoryName("Backpacks")
                        .price(BigDecimal.valueOf(4899.00))
                        .currency("INR")
                        .stock(20)
                        .inStock(true)
                        .description("Built for modern explorers.")
                        .images(new ArrayList<>())
                        .build()));

        testVariant = productVariantRepository.findBySku("RRA-NMD-01-OLV")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .id("var-1-1")
                        .product(testProduct)
                        .sku("RRA-NMD-01-OLV")
                        .colorName("Olive Green")
                        .colorHex("#556B2F")
                        .image("https://images.unsplash.com/photo-1553062407-98eeb64c6a62")
                        .stock(10)
                        .build()));

        AddressDto address = AddressDto.builder()
                .fullName("Sarah Johnson")
                .street("142 Bandra West, Hill Road")
                .city("Mumbai")
                .state("MH")
                .postalCode("400050")
                .country("India")
                .phone("+91 98200 12345")
                .build();

        seededOrder = orderRepository.findByOrderNumber("#RRA89241")
                .orElseGet(() -> {
                    Order o = Order.builder()
                            .id("RRA-89241")
                            .orderNumber("#RRA89241")
                            .customerName("Sarah Johnson")
                            .customerEmail("admin@rora-luxury.com")
                            .customerPhone("+91 98200 12345")
                            .shippingAddress(address)
                            .billingAddress(address)
                            .paymentMethod("UPI / Card ending in 4242")
                            .paymentStatus("Captured")
                            .status("Delivered")
                            .subtotal(BigDecimal.valueOf(8798.00))
                            .total(BigDecimal.valueOf(8798.00))
                            .carrier("Bluedart Express")
                            .trackingNumber("BLU-88239014")
                            .items(new ArrayList<>())
                            .timelineEvents(new ArrayList<>())
                            .build();

                    o.addItem(OrderItem.builder()
                            .product(testProduct)
                            .variant(testVariant)
                            .productName("The Nomad Backpack")
                            .colorName("Olive Green")
                            .unitPrice(BigDecimal.valueOf(4899.00))
                            .quantity(1)
                            .totalPrice(BigDecimal.valueOf(4899.00))
                            .build());

                    o.addTimelineEvent(OrderTimelineEvent.builder()
                            .stepName("Order Placed")
                            .completed(true)
                            .displayOrder(1)
                            .build());

                    return orderRepository.save(o);
                });

        seededOrder2 = orderRepository.findByOrderNumber("#RRA88940")
                .orElseGet(() -> {
                    Order o = Order.builder()
                            .id("RRA-88940")
                            .orderNumber("#RRA88940")
                            .customerName("Priya Sundaram")
                            .customerEmail("priya@example.com")
                            .shippingAddress(address)
                            .paymentMethod("UPI")
                            .paymentStatus("Captured")
                            .status("Processing")
                            .subtotal(BigDecimal.valueOf(4899.00))
                            .total(BigDecimal.valueOf(4899.00))
                            .items(new ArrayList<>())
                            .timelineEvents(new ArrayList<>())
                            .build();
                    return orderRepository.save(o);
                });
    }

    private AddressDto createTestAddress() {
        return AddressDto.builder()
                .fullName("Sarah Johnson")
                .street("142 Bandra West, Hill Road")
                .city("Mumbai")
                .state("MH")
                .postalCode("400050")
                .country("India")
                .phone("+91 98200 12345")
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/checkout/place-order - Successfully places an order with direct items")
    void testPlaceOrder_DirectItems_Success() throws Exception {
        CheckoutRequest request = CheckoutRequest.builder()
                .customerName("Sarah Johnson")
                .customerEmail("sarah@rora-luxury.com")
                .customerPhone("+91 98200 12345")
                .shippingAddress(createTestAddress())
                .items(List.of(
                        CheckoutItemDto.builder()
                                .productId(testProduct.getId())
                                .variantId(testVariant.getId())
                                .quantity(1)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/checkout/place-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber", startsWith("#RRA")))
                .andExpect(jsonPath("$.data.status").value("Processing"))
                .andExpect(jsonPath("$.data.items", hasSize(1)))
                .andExpect(jsonPath("$.data.timeline", hasSize(5)));
    }

    @Test
    @DisplayName("GET /api/v1/orders/track/{orderNumber} - Public tracking of seeded order")
    void testTrackOrder_PublicEndpoint() throws Exception {
        String trackNumber = seededOrder.getOrderNumber().replace("#", "");
        mockMvc.perform(get("/api/v1/orders/track/" + trackNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("#RRA89241"))
                .andExpect(jsonPath("$.data.status").value("Delivered"));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{idOrNumber} - Get seeded order by ID")
    void testGetOrderByIdOrNumber() throws Exception {
        mockMvc.perform(get("/api/v1/orders/RRA-89241"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("RRA-89241"))
                .andExpect(jsonPath("$.data.customerName").value("Sarah Johnson"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/orders - Admin retrieves paginated orders")
    void testAdminGetOrders() throws Exception {
        mockMvc.perform(get("/api/v1/admin/orders")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("PUT /api/v1/admin/orders/{id}/status - Admin updates order status to Delivered")
    void testAdminUpdateOrderStatus() throws Exception {
        OrderStatusUpdateRequest updateReq = OrderStatusUpdateRequest.builder()
                .status("Delivered")
                .carrier("Bluedart Express")
                .trackingNumber("BLU-99001122")
                .build();

        mockMvc.perform(put("/api/v1/admin/orders/RRA-88940/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("Delivered"))
                .andExpect(jsonPath("$.data.paymentStatus").value("Completed"));
    }
}
