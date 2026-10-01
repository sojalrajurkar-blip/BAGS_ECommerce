package com.rora.backend.review.service;

import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderItem;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.review.dto.*;
import com.rora.backend.review.entity.Review;
import com.rora.backend.review.entity.ReviewStatus;
import com.rora.backend.review.repository.ReviewRepository;
import com.rora.backend.review.service.impl.ReviewServiceImpl;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Product testProduct;
    private User testUser;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("prod-1")
                .slug("the-nomad-backpack")
                .name("The Nomad Backpack")
                .price(BigDecimal.valueOf(4200.00))
                .rating(BigDecimal.valueOf(5.0))
                .reviewCount(1)
                .build();

        testUser = User.builder()
                .id("usr-1")
                .email("elena@example.com")
                .name("Elena Rostova")
                .build();

        testReview = Review.builder()
                .id("rev-1")
                .product(testProduct)
                .productName("The Nomad Backpack")
                .user(testUser)
                .author("Elena Rostova")
                .role("Architect & Traveler")
                .rating(5)
                .title("The cleanest backpack I have ever owned")
                .comment("Superb quality leather and clean lines.")
                .verifiedPurchase(true)
                .helpfulCount(24)
                .status(ReviewStatus.PUBLISHED)
                .isFeatured(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("createReview - Successfully creates review with verified buyer status")
    void testCreateReview_Success() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .productIdOrSlug("the-nomad-backpack")
                .rating(5)
                .title("Incredible craftsmanship")
                .comment("Worth every rupee spent.")
                .build();

        OrderItem item = OrderItem.builder()
                .product(testProduct)
                .quantity(1)
                .build();

        Order deliveredOrder = Order.builder()
                .id("ord-1")
                .status("Delivered")
                .items(List.of(item))
                .build();

        when(productRepository.findById("the-nomad-backpack")).thenReturn(Optional.empty());
        when(productRepository.findBySlug("the-nomad-backpack")).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("elena@example.com")).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of(deliveredOrder));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> {
            Review r = i.getArgument(0);
            r.setId("rev-new");
            r.setCreatedAt(Instant.now());
            return r;
        });
        when(reviewRepository.countByProductIdAndStatus("prod-1", ReviewStatus.PUBLISHED)).thenReturn(2L);
        when(reviewRepository.getAverageRatingForProduct("prod-1")).thenReturn(5.0);

        ReviewDto result = reviewService.createReview(request, "elena@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Incredible craftsmanship");
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.isVerifiedPurchase()).isTrue();
        assertThat(result.getAuthor()).isEqualTo("Elena Rostova");
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("createReview - Throws ResourceNotFoundException when product does not exist")
    void testCreateReview_ProductNotFound() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .productIdOrSlug("non-existent-product")
                .rating(5)
                .title("Nice")
                .comment("Good bag")
                .build();

        when(productRepository.findById("non-existent-product")).thenReturn(Optional.empty());
        when(productRepository.findBySlug("non-existent-product")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(request, "elena@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    @DisplayName("getReviewById - Returns review DTO when found")
    void testGetReviewById_Success() {
        when(reviewRepository.findById("rev-1")).thenReturn(Optional.of(testReview));

        ReviewDto result = reviewService.getReviewById("rev-1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("rev-1");
        assertThat(result.getAuthor()).isEqualTo("Elena Rostova");
        assertThat(result.getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("getProductReviews - Returns paginated list of published reviews")
    void testGetProductReviews_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> page = new PageImpl<>(List.of(testReview), pageable, 1);

        when(productRepository.findById("prod-1")).thenReturn(Optional.of(testProduct));
        when(reviewRepository.findByProductIdAndStatus("prod-1", ReviewStatus.PUBLISHED, pageable))
                .thenReturn(page);

        Page<ReviewDto> result = reviewService.getProductReviews("prod-1", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("The cleanest backpack I have ever owned");
    }

    @Test
    @DisplayName("getProductReviewSummary - Computes star breakdown and average score")
    void testGetProductReviewSummary_Success() {
        Review review2 = Review.builder()
                .id("rev-2")
                .product(testProduct)
                .rating(4)
                .status(ReviewStatus.PUBLISHED)
                .build();

        when(productRepository.findById("prod-1")).thenReturn(Optional.of(testProduct));
        when(reviewRepository.findByProductIdAndStatus("prod-1", ReviewStatus.PUBLISHED))
                .thenReturn(List.of(testReview, review2));

        ProductReviewSummaryDto summary = reviewService.getProductReviewSummary("prod-1");

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalReviews()).isEqualTo(2);
        assertThat(summary.getAverageRating()).isEqualTo(4.5);
        assertThat(summary.getRatingBreakdown().get(5)).isEqualTo(1L);
        assertThat(summary.getRatingBreakdown().get(4)).isEqualTo(1L);
        assertThat(summary.getRatingBreakdown().get(3)).isEqualTo(0L);
    }

    @Test
    @DisplayName("markReviewHelpful - Increments helpful count")
    void testMarkReviewHelpful_Success() {
        when(reviewRepository.findById("rev-1")).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        ReviewDto result = reviewService.markReviewHelpful("rev-1");

        assertThat(result).isNotNull();
        assertThat(result.getHelpfulCount()).isEqualTo(25);
    }

    @Test
    @DisplayName("moderateReview - Admin changes status and notes")
    void testModerateReview_Success() {
        UpdateReviewStatusRequest request = UpdateReviewStatusRequest.builder()
                .status(ReviewStatus.FLAGGED)
                .notes("Content flagged for moderation review")
                .isFeatured(false)
                .build();

        when(reviewRepository.findById("rev-1")).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));
        when(reviewRepository.countByProductIdAndStatus("prod-1", ReviewStatus.PUBLISHED)).thenReturn(0L);
        when(reviewRepository.getAverageRatingForProduct("prod-1")).thenReturn(null);

        ReviewDto result = reviewService.moderateReview("rev-1", request, "admin@rora-luxury.com");

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(ReviewStatus.FLAGGED);
        assertThat(result.getModerationNotes()).isEqualTo("Content flagged for moderation review");
        assertThat(result.getModeratedBy()).isEqualTo("admin@rora-luxury.com");
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("getReviewAdminSummary - Aggregates admin KPI metrics")
    void testGetReviewAdminSummary() {
        when(reviewRepository.count()).thenReturn(15L);
        when(reviewRepository.countByStatus(ReviewStatus.PUBLISHED)).thenReturn(12L);
        when(reviewRepository.countByStatus(ReviewStatus.PENDING_MODERATION)).thenReturn(1L);
        when(reviewRepository.countByStatus(ReviewStatus.FLAGGED)).thenReturn(1L);
        when(reviewRepository.countByStatus(ReviewStatus.ARCHIVED)).thenReturn(1L);
        when(reviewRepository.getGlobalAverageRating()).thenReturn(4.8);

        ReviewSummaryDto summary = reviewService.getReviewAdminSummary();

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalReviews()).isEqualTo(15L);
        assertThat(summary.getPublishedReviews()).isEqualTo(12L);
        assertThat(summary.getPendingReviews()).isEqualTo(1L);
        assertThat(summary.getFlaggedReviews()).isEqualTo(1L);
        assertThat(summary.getArchivedReviews()).isEqualTo(1L);
        assertThat(summary.getAverageRating()).isEqualTo(4.8);
    }

    @Test
    @DisplayName("deleteReview - Deletes review and re-syncs product rating metrics")
    void testDeleteReview() {
        when(reviewRepository.findById("rev-1")).thenReturn(Optional.of(testReview));
        when(reviewRepository.countByProductIdAndStatus("prod-1", ReviewStatus.PUBLISHED)).thenReturn(0L);
        when(reviewRepository.getAverageRatingForProduct("prod-1")).thenReturn(null);

        reviewService.deleteReview("rev-1", "admin@rora-luxury.com");

        verify(reviewRepository).delete(testReview);
        verify(productRepository).save(testProduct);
    }
}
