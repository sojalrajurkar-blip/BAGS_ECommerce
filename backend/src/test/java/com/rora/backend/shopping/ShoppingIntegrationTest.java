package com.rora.backend.shopping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.catalog.entity.Category;
import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.CategoryRepository;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.catalog.repository.ProductVariantRepository;
import com.rora.backend.security.JwtTokenProvider;
import com.rora.backend.shopping.cart.dto.AddToCartRequest;
import com.rora.backend.shopping.cart.dto.ApplyCouponRequest;
import com.rora.backend.shopping.cart.dto.UpdateCartItemRequest;
import com.rora.backend.shopping.cart.repository.CartItemRepository;
import com.rora.backend.shopping.cart.repository.CartRepository;
import com.rora.backend.shopping.coupon.entity.Coupon;
import com.rora.backend.shopping.coupon.repository.CouponRepository;
import com.rora.backend.shopping.wishlist.repository.WishlistItemRepository;
import com.rora.backend.shopping.wishlist.repository.WishlistRepository;
import com.rora.backend.user.Role;
import com.rora.backend.user.RoleRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ShoppingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private User testUser;
    private Product product;
    private ProductVariant variant;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        wishlistItemRepository.deleteAll();
        wishlistRepository.deleteAll();
        couponRepository.deleteAll();
        productVariantRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Roles
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER").orElseGet(() ->
                roleRepository.save(Role.builder().id("role-cust").name("ROLE_CUSTOMER").description("Customer").build()));

        // User
        testUser = User.builder()
                .email("shopper@rora-luxury.com")
                .passwordHash(passwordEncoder.encode("SecretPass123!"))
                .name("Luxury Shopper")
                .roles(Set.of(customerRole))
                .build();
        testUser = userRepository.save(testUser);
        userToken = jwtTokenProvider.generateTokenFromUser(testUser.getId(), testUser.getEmail(), testUser.getName(), List.of("ROLE_CUSTOMER"));

        // Category & Product
        Category cat = Category.builder().id("backpacks").slug("backpacks").name("Backpacks").build();
        categoryRepository.save(cat);

        variant = ProductVariant.builder()
                .id("var-1")
                .sku("RRA-NMD-01-OLV")
                .name("Olive Green")
                .colorName("Olive Green")
                .colorHex("#555E48")
                .image("https://example.com/olive.jpg")
                .stock(10)
                .build();

        product = Product.builder()
                .id("prod-1")
                .slug("the-nomad-backpack")
                .name("The Nomad Backpack")
                .category(cat)
                .price(BigDecimal.valueOf(4899))
                .originalPrice(BigDecimal.valueOf(5499))
                .stock(20)
                .inStock(true)
                .variants(new ArrayList<>(List.of(variant)))
                .build();
        variant.setProduct(product);
        productRepository.save(product);

        // Coupon
        coupon = Coupon.builder()
                .id("coup-1")
                .code("RORA10")
                .description("10% off")
                .discountType("PERCENTAGE")
                .discountPercent(10)
                .minimumSpend(BigDecimal.valueOf(1999.00))
                .maxDiscountAmount(BigDecimal.valueOf(2000.00))
                .isActive(true)
                .startDate(Instant.now().minus(5, ChronoUnit.DAYS))
                .expiryDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();
        couponRepository.save(coupon);
    }

    @Test
    void testWishlistFlow_ToggleAddAndRemove() throws Exception {
        // 1. Initial Wishlist is empty
        mockMvc.perform(get("/api/v1/wishlist")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.itemCount", is(0)));

        // 2. Toggle item into Wishlist
        mockMvc.perform(post("/api/v1/wishlist/toggle/" + product.getSlug())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.itemCount", is(1)))
                .andExpect(jsonPath("$.data.items[0].product.slug", is("the-nomad-backpack")));

        // 3. Toggle same item again -> removes it
        mockMvc.perform(post("/api/v1/wishlist/toggle/" + product.getSlug())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.itemCount", is(0)));
    }

    @Test
    void testCartFlow_AddUpdateApplyCouponAndClear() throws Exception {
        // 1. Add item to cart
        AddToCartRequest addReq = AddToCartRequest.builder()
                .productId(product.getSlug())
                .variantId(variant.getId())
                .quantity(1)
                .build();

        MvcResult addResult = mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.itemCount", is(1)))
                .andExpect(jsonPath("$.data.subtotal", is(4899.0)))
                .andExpect(jsonPath("$.data.shippingFee").value(0)) // subtotal >= 1999 -> Free shipping
                .andReturn();

        String cartJson = addResult.getResponse().getContentAsString();
        String itemId = objectMapper.readTree(cartJson).get("data").get("items").get(0).get("id").asText();

        // 2. Update item quantity to 2
        UpdateCartItemRequest updateReq = UpdateCartItemRequest.builder().quantity(2).build();
        mockMvc.perform(put("/api/v1/cart/items/" + itemId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.itemCount", is(2)))
                .andExpect(jsonPath("$.data.subtotal", is(9798.0)));

        // 3. Apply coupon RORA10 (10% discount on 9798 = 979.80)
        ApplyCouponRequest couponReq = ApplyCouponRequest.builder().code("RORA10").build();
        mockMvc.perform(post("/api/v1/cart/apply-coupon")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.appliedCoupon", is("RORA10")))
                .andExpect(jsonPath("$.data.discountAmount", is(979.80)))
                .andExpect(jsonPath("$.data.total", is(8818.20)));

        // 4. Remove coupon
        mockMvc.perform(delete("/api/v1/cart/remove-coupon")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.appliedCoupon", nullValue()))
                .andExpect(jsonPath("$.data.total", is(9798.0)));

        // 5. Clear cart
        mockMvc.perform(delete("/api/v1/cart")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.itemCount", is(0)));
    }

    @Test
    void testCouponValidation_PublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/coupons/validate")
                        .param("code", "RORA10")
                        .param("subtotal", "3000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.valid", is(true)))
                .andExpect(jsonPath("$.data.discountAmount", is(300.0)));
    }
}
