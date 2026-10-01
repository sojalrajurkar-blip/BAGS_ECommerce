package com.rora.backend.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.catalog.dto.CategoryRequest;
import com.rora.backend.catalog.dto.ProductCreateRequest;
import com.rora.backend.catalog.dto.ProductVariantRequest;
import com.rora.backend.catalog.entity.Category;
import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.CategoryRepository;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.catalog.repository.ProductVariantRepository;
import com.rora.backend.security.JwtTokenProvider;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

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

    private String adminToken;
    private Category categoryBackpacks;
    private Product productNomad;

    @BeforeEach
    void setUp() {
        productVariantRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Ensure roles
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() ->
                roleRepository.save(Role.builder().id("role-admin").name("ROLE_ADMIN").description("Admin").build()));

        // Create Admin User & Token
        User adminUser = User.builder()
                .email("admin@rora-luxury.com")
                .passwordHash(passwordEncoder.encode("AdminPass123!"))
                .name("Admin User")
                .roles(Set.of(adminRole))
                .build();
        adminUser = userRepository.save(adminUser);
        adminToken = jwtTokenProvider.generateTokenFromUser(
                adminUser.getId(),
                adminUser.getEmail(),
                adminUser.getName(),
                List.of("ROLE_ADMIN")
        );

        // Create Sample Category
        categoryBackpacks = Category.builder()
                .id("backpacks")
                .slug("backpacks")
                .name("Backpacks")
                .headline("Everyday & Travel Backpacks")
                .description("Built for modern explorers")
                .heroImage("https://example.com/backpacks.jpg")
                .productCount(1)
                .build();
        categoryRepository.save(categoryBackpacks);

        // Create Sample Product
        ProductVariant variant1 = ProductVariant.builder()
                .sku("RRA-NMD-01-OLV")
                .name("Olive Green")
                .colorName("Olive Green")
                .colorHex("#555E48")
                .image("https://example.com/olive.jpg")
                .stock(10)
                .build();

        productNomad = Product.builder()
                .id("prod-1")
                .slug("the-nomad-backpack")
                .name("The Nomad Backpack")
                .tagline("Adventure-ready. Everyday style.")
                .category(categoryBackpacks)
                .categoryName("Backpacks")
                .price(BigDecimal.valueOf(4899))
                .originalPrice(BigDecimal.valueOf(5499))
                .discount(11)
                .currency("INR")
                .rating(BigDecimal.valueOf(4.8))
                .reviewCount(304)
                .badge("Best Seller")
                .stock(24)
                .inStock(true)
                .isFeatured(true)
                .isBestSeller(true)
                .isNewArrival(false)
                .material("900D Recycled Nylon")
                .sku("RRA-NMD-01")
                .specifications(Map.of("Volume", "20L"))
                .features(List.of("Dual-access zipper", "Padded laptop sleeve"))
                .variants(new ArrayList<>(List.of(variant1)))
                .build();
        variant1.setProduct(productNomad);
        productRepository.save(productNomad);
    }

    @Test
    void testGetCategories() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].slug", is("backpacks")));
    }

    @Test
    void testGetCategoryBySlug() throws Exception {
        mockMvc.perform(get("/api/v1/categories/backpacks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Backpacks")));
    }

    @Test
    void testGetProducts_WithFiltering() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .param("category", "backpacks")
                        .param("inStock", "true")
                        .param("sortBy", "popularity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].slug", is("the-nomad-backpack")))
                .andExpect(jsonPath("$.data.content[0].colors", hasSize(1)));
    }

    @Test
    void testGetProductDetail_BySlug() throws Exception {
        mockMvc.perform(get("/api/v1/products/the-nomad-backpack"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("The Nomad Backpack")))
                .andExpect(jsonPath("$.data.specifications.Volume", is("20L")));
    }

    @Test
    void testGetFeaturedProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].slug", is("the-nomad-backpack")));
    }

    @Test
    void testSearchProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products/search").param("q", "nomad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("The Nomad Backpack")));
    }

    @Test
    void testAdminCreateCategory_Authorized() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .slug("travel-bags")
                .name("Travel Bags")
                .headline("Weekender & Expedition Duffels")
                .build();

        mockMvc.perform(post("/api/v1/admin/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.slug", is("travel-bags")));
    }

    @Test
    void testAdminCreateCategory_UnauthorizedWithoutToken() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .slug("travel-bags")
                .name("Travel Bags")
                .build();

        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAdminCreateProduct_Authorized() throws Exception {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .slug("the-weekend-duffle")
                .name("The Weekend Duffle")
                .categoryId("backpacks")
                .price(BigDecimal.valueOf(6499))
                .originalPrice(BigDecimal.valueOf(7299))
                .stock(12)
                .variants(List.of(
                        ProductVariantRequest.builder()
                                .sku("RRA-DUF-04-SAF")
                                .colorName("Safari Olive")
                                .colorHex("#555E48")
                                .image("https://example.com/safari.jpg")
                                .stock(5)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.slug", is("the-weekend-duffle")));
    }
}
