package com.rora.backend.qa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.auth.dto.LoginRequest;
import com.rora.backend.auth.dto.RegisterRequest;
import com.rora.backend.order.dto.AddressDto;
import com.rora.backend.catalog.entity.Category;
import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.repository.CategoryRepository;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.order.dto.CheckoutRequest;
import com.rora.backend.returns.dto.ApproveReturnRequest;
import com.rora.backend.returns.dto.CreateReturnRequest;
import com.rora.backend.returns.dto.ReturnItemRequest;
import com.rora.backend.review.dto.CreateReviewRequest;
import com.rora.backend.shipment.dto.CreateShipmentRequest;
import com.rora.backend.shipment.dto.UpdateShipmentStatusRequest;
import com.rora.backend.shipment.entity.ShipmentStatus;
import com.rora.backend.shopping.cart.dto.AddToCartRequest;
import com.rora.backend.shopping.coupon.entity.Coupon;
import com.rora.backend.shopping.coupon.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ============================================================================
 * Phase 15: Full Local QA — End-to-End Multi-Domain Journey & Security Test
 * ============================================================================
 * Validates the complete customer lifecycle, order fulfillment pipeline,
 * returns/refund workflows, reviews, admin RBAC guards, and error/failure edge cases.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EndToEndFullJourneyQaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private com.rora.backend.user.UserRepository userRepository;

    @Autowired
    private com.rora.backend.user.RoleRepository roleRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        com.rora.backend.user.Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(com.rora.backend.user.Role.builder()
                        .id("role-admin")
                        .name("ROLE_ADMIN")
                        .description("Super Administrator")
                        .build()));

        userRepository.findByEmailIgnoreCase("admin@rora-luxury.com").ifPresentOrElse(
                admin -> {
                    admin.setPasswordHash(passwordEncoder.encode("Password123!"));
                    admin.setRoles(new java.util.HashSet<>(java.util.Set.of(adminRole)));
                    userRepository.save(admin);
                },
                () -> userRepository.save(com.rora.backend.user.User.builder()
                        .id("user-admin-root")
                        .email("admin@rora-luxury.com")
                        .passwordHash(passwordEncoder.encode("Password123!"))
                        .name("RÓRA Administrator")
                        .status("ACTIVE")
                        .roles(new java.util.HashSet<>(java.util.Set.of(adminRole)))
                        .build())
        );

        if (couponRepository.findByCodeIgnoreCase("RORA10").isEmpty()) {
            Coupon coupon = Coupon.builder()
                    .id("coup-rora10-" + UUID.randomUUID())
                    .code("RORA10")
                    .description("10% off your entire order")
                    .discountType("PERCENTAGE")
                    .discountPercent(10)
                    .minimumSpend(BigDecimal.valueOf(1999.00))
                    .maxDiscountAmount(BigDecimal.valueOf(2000.00))
                    .isActive(true)
                    .startDate(Instant.now().minus(5, ChronoUnit.DAYS))
                    .expiryDate(Instant.now().plus(60, ChronoUnit.DAYS))
                    .build();
            couponRepository.save(coupon);
        }

        if (productRepository.findBySlug("the-nomad-backpack").isEmpty()) {
            Category cat = categoryRepository.findBySlug("backpacks").orElseGet(() ->
                    categoryRepository.save(Category.builder()
                            .id("cat-" + UUID.randomUUID())
                            .slug("backpacks")
                            .name("Backpacks")
                            .heroImage("https://images.unsplash.com/photo-1548036328-c9fa89d128fa?q=80&w=1200")
                            .build()));

            Product product = Product.builder()
                    .id("prod-nomad-" + UUID.randomUUID())
                    .slug("the-nomad-backpack")
                    .name("The Nomad Backpack")
                    .category(cat)
                    .price(BigDecimal.valueOf(3299.00))
                    .originalPrice(BigDecimal.valueOf(3999.00))
                    .stock(50)
                    .inStock(true)
                    .material("Full-Grain Italian Calfskin Leather")
                    .build();
            productRepository.save(product);
        }
    }

    @Test
    @DisplayName("QA-E2E: Complete Customer Journey (Register -> Cart -> Coupon -> Order -> Payment -> Shipment -> Return -> Refund -> Review)")
    void testCompleteCustomerLifecycleAndFulfillmentJourney() throws Exception {
        String uniqueEmail = "qa.patron." + UUID.randomUUID().toString().substring(0, 8) + "@rora-luxury.com";
        String guestSessionId = "sess-qa-" + UUID.randomUUID();

        // 1. Customer Self-Registration
        RegisterRequest regReq = RegisterRequest.builder()
                .name("Aria Montague")
                .email(uniqueEmail)
                .password("AtelierLuxury2026!")
                .phone("+919876543210")
                .build();

        MvcResult regRes = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn();

        JsonNode regNode = objectMapper.readTree(regRes.getResponse().getContentAsString());
        String customerJwt = regNode.path("data").path("token").asText();
        assertNotNull(customerJwt);
        assertFalse(customerJwt.isBlank());

        // 2. Customer Auth Login & Profile Verification
        LoginRequest loginReq = LoginRequest.builder()
                .email(uniqueEmail)
                .password("AtelierLuxury2026!")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value(uniqueEmail));

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + customerJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(uniqueEmail));

        // 3. Catalog Exploration
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/v1/products/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 4. Cart Operations with Authenticated User & Session Tracking
        AddToCartRequest cartReq = AddToCartRequest.builder()
                .productId("the-nomad-backpack")
                .quantity(1)
                .build();

        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + customerJwt)
                        .header("X-Session-ID", guestSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/v1/cart")
                        .header("Authorization", "Bearer " + customerJwt)
                        .header("X-Session-ID", guestSessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items").isArray());

        // 5. Coupon Validation
        mockMvc.perform(get("/api/v1/coupons/validate")
                        .param("code", "RORA10")
                        .param("subtotal", "12000.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true));

        // 6. Checkout / Place Order
        AddressDto shippingAddress = AddressDto.builder()
                .fullName("Aria Montague")
                .street("12 Altamount Road, Tower A")
                .city("Mumbai")
                .state("Maharashtra")
                .postalCode("400026")
                .country("India")
                .phone("+919876543210")
                .build();

        CheckoutRequest orderReq = CheckoutRequest.builder()
                .customerName("Aria Montague")
                .customerEmail(uniqueEmail)
                .customerPhone("+919876543210")
                .shippingAddress(shippingAddress)
                .billingAddress(shippingAddress)
                .paymentMethod("Razorpay")
                .sessionId(guestSessionId)
                .couponCode("RORA10")
                .build();

        MvcResult orderRes = mockMvc.perform(post("/api/v1/checkout/place-order")
                        .header("Authorization", "Bearer " + customerJwt)
                        .header("X-Session-ID", guestSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").exists())
                .andReturn();

        JsonNode orderNode = objectMapper.readTree(orderRes.getResponse().getContentAsString());
        String orderNumber = orderNode.path("data").path("orderNumber").asText();
        assertNotNull(orderNumber);

        // 7. Order & Payment Verification
        mockMvc.perform(get("/api/v1/orders/{idOrNumber}", orderNumber)
                        .header("Authorization", "Bearer " + customerJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.data.paymentStatus").value("Captured"));

        // Obtain Admin Token for fulfillment & inspection
        LoginRequest adminAuth = LoginRequest.builder()
                .email("admin@rora-luxury.com")
                .password("Password123!")
                .build();

        MvcResult adminRes = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminAuth)))
                .andExpect(status().isOk())
                .andReturn();

        String adminJwt = objectMapper.readTree(adminRes.getResponse().getContentAsString()).path("data").path("token").asText();

        // 8. Shipment Creation & Milestone Updates
        CreateShipmentRequest shipReq = CreateShipmentRequest.builder()
                .orderIdOrNumber(orderNumber)
                .courier("BlueDart Express")
                .destination("Mumbai, Maharashtra")
                .awbNumber("BLU-QA-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .build();

        MvcResult shipRes = mockMvc.perform(post("/api/v1/admin/shipments")
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shipReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        String shipmentId = objectMapper.readTree(shipRes.getResponse().getContentAsString()).path("data").path("id").asText();

        UpdateShipmentStatusRequest eventReq = UpdateShipmentStatusRequest.builder()
                .status(ShipmentStatus.DELIVERED)
                .location("Mumbai Hub")
                .activity("Delivered to patron - Signature verified")
                .notes("Delivered to patron - Signature verified")
                .build();

        mockMvc.perform(post("/api/v1/admin/shipments/" + shipmentId + "/events")
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 9. Customer Returns Request
        CreateReturnRequest retReq = CreateReturnRequest.builder()
                .orderIdOrNumber(orderNumber)
                .reason("Defective / Hardware imperfection")
                .items(List.of(ReturnItemRequest.builder()
                        .productName("The Nomad Backpack")
                        .quantity(1)
                        .colorName("Obsidian Black")
                        .reason("Zipper tension")
                        .build()))
                .customerNotes("Kindly inspect zipper slider tension")
                .build();

        MvcResult retRes = mockMvc.perform(post("/api/v1/returns")
                        .header("Authorization", "Bearer " + customerJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(retReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        String returnId = objectMapper.readTree(retRes.getResponse().getContentAsString()).path("data").path("id").asText();

        // 10. Admin Quality Inspection & Approval with Automatic Refund
        mockMvc.perform(put("/api/v1/admin/returns/" + returnId + "/inspection")
                        .header("Authorization", "Bearer " + adminJwt)
                        .param("status", "PASSED_PRISTINE")
                        .param("notes", "Item returned in pristine unblemished condition with dust bag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.inspectionStatusCode").value("PASSED_PRISTINE"));

        ApproveReturnRequest approveReq = ApproveReturnRequest.builder()
                .notes("Approved after physical QA inspection")
                .autoRefund(true)
                .restockInventory(true)
                .build();

        mockMvc.perform(post("/api/v1/admin/returns/" + returnId + "/approve")
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.statusCode").value("APPROVED_AND_REFUNDED"));

        // 11. Customer Review Submission
        CreateReviewRequest revReq = CreateReviewRequest.builder()
                .productIdOrSlug("the-nomad-backpack")
                .rating(5)
                .title("Unmatched Craftsmanship")
                .comment("The full-grain Italian leather texture and hardware feel exquisitely opulent.")
                .author("Aria Montague")
                .role("Verified Patron")
                .build();

        mockMvc.perform(post("/api/v1/reviews")
                        .header("Authorization", "Bearer " + customerJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(revReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(5));
    }

    @Test
    @DisplayName("QA-Security: RBAC Guards & Error Boundary Protections")
    void testSecurityAndFailureEdgeCases() throws Exception {
        // 1. Unauthorized Access to Admin Endpoints
        mockMvc.perform(get("/api/v1/admin/dashboard/summary"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/admin/audit-logs"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isUnauthorized());

        // 2. Non-Existent Entity Lookups
        mockMvc.perform(get("/api/v1/products/non-existent-luxury-item-999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/orders/non-existent-order-888"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/categories/non-existent-category-777"))
                .andExpect(status().isNotFound());

        // 3. Invalid Coupon Validation (Non-existent code)
        mockMvc.perform(get("/api/v1/coupons/validate")
                        .param("code", "INVALID_COUPON_CODE")
                        .param("subtotal", "5000.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false));

        // 4. Invalid Login Credentials
        LoginRequest badAuth = LoginRequest.builder()
                .email("admin@rora-luxury.com")
                .password("WrongPassword999!")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badAuth)))
                .andExpect(status().isUnauthorized());
    }
}
