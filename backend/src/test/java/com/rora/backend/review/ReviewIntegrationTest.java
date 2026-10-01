package com.rora.backend.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.review.dto.CreateReviewRequest;
import com.rora.backend.review.dto.UpdateReviewStatusRequest;
import com.rora.backend.review.entity.Review;
import com.rora.backend.review.entity.ReviewStatus;
import com.rora.backend.review.repository.ReviewRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private Product testProduct;
    private User testUser;
    private Review testReview;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();

        testProduct = productRepository.findBySlug("the-nomad-backpack")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .id("prod-review-test-1")
                        .slug("the-nomad-backpack")
                        .name("The Nomad Backpack")
                        .price(BigDecimal.valueOf(4200.00))
                        .rating(BigDecimal.valueOf(5.0))
                        .reviewCount(1)
                        .build()));

        testUser = userRepository.findByEmail("customer@rora-luxury.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("customer@rora-luxury.com")
                        .passwordHash("hashed-pass")
                        .name("Elena Rostova")
                        .build()));

        testReview = Review.builder()
                .id("rev-test-1")
                .product(testProduct)
                .productName(testProduct.getName())
                .user(testUser)
                .author("Elena Rostova")
                .role("Architect & Traveler")
                .rating(5)
                .title("The cleanest backpack I have ever owned")
                .comment("The Nomad backpack has accompanied me through three countries and daily site visits.")
                .verifiedPurchase(true)
                .helpfulCount(24)
                .status(ReviewStatus.PUBLISHED)
                .isFeatured(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        testReview = reviewRepository.save(testReview);
    }

    @Test
    @DisplayName("POST /api/v1/reviews - Customer submits review")
    void testCreateReview_Success() throws Exception {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .productIdOrSlug(testProduct.getId())
                .rating(5)
                .title("Outstanding Italian leather")
                .comment("Incredible stitch quality and premium feel.")
                .author("Marcus Vance")
                .role("Creative Director")
                .build();

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Outstanding Italian leather"))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.author").value("Marcus Vance"));
    }

    @Test
    @DisplayName("GET /api/v1/reviews/product/{slug} - Get product reviews")
    void testGetProductReviews_Success() throws Exception {
        mockMvc.perform(get("/api/v1/reviews/product/the-nomad-backpack"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].productName").value("The Nomad Backpack"));
    }

    @Test
    @DisplayName("GET /api/v1/reviews/product/{slug}/summary - Get rating summary and breakdown")
    void testGetProductReviewSummary_Success() throws Exception {
        mockMvc.perform(get("/api/v1/reviews/product/the-nomad-backpack/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productName").value("The Nomad Backpack"))
                .andExpect(jsonPath("$.data.averageRating").value(5.0))
                .andExpect(jsonPath("$.data.totalReviews").value(1))
                .andExpect(jsonPath("$.data.ratingBreakdown.5").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/reviews/featured - Get featured editorial reviews")
    void testGetFeaturedReviews_Success() throws Exception {
        mockMvc.perform(get("/api/v1/reviews/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].isFeatured").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/reviews/{id}/helpful - Upvote review helpful count")
    void testMarkHelpful_Success() throws Exception {
        mockMvc.perform(post("/api/v1/reviews/" + testReview.getId() + "/helpful"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.helpfulCount").value(25));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/reviews - Admin searches reviews")
    void testAdminSearchReviews_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reviews")
                        .param("search", "backpack"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/reviews/summary - Admin gets dashboard summary")
    void testAdminGetSummary_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reviews/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalReviews").isNumber())
                .andExpect(jsonPath("$.data.publishedReviews").isNumber())
                .andExpect(jsonPath("$.data.averageRating").value(5.0));
    }

    @Test
    @WithMockUser(username = "customer@rora-luxury.com", roles = {"CUSTOMER"})
    @DisplayName("GET /api/v1/admin/reviews/summary - Customer gets 403 Forbidden")
    void testAdminGetSummary_CustomerForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reviews/summary"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("PUT /api/v1/admin/reviews/{id}/moderate - Admin moderates review status")
    void testAdminModerateReview_Success() throws Exception {
        UpdateReviewStatusRequest request = UpdateReviewStatusRequest.builder()
                .status(ReviewStatus.FLAGGED)
                .notes("Flagged for brand verification")
                .isFeatured(false)
                .build();

        mockMvc.perform(put("/api/v1/admin/reviews/" + testReview.getId() + "/moderate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("FLAGGED"))
                .andExpect(jsonPath("$.data.moderationNotes").value("Flagged for brand verification"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("DELETE /api/v1/admin/reviews/{id} - Admin deletes review")
    void testAdminDeleteReview_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/reviews/" + testReview.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
