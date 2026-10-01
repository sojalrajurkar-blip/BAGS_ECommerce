package com.rora.backend.review.service.impl;

import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.review.dto.*;
import com.rora.backend.review.entity.Review;
import com.rora.backend.review.entity.ReviewStatus;
import com.rora.backend.review.repository.ReviewRepository;
import com.rora.backend.review.service.ReviewService;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, yyyy").withZone(ZoneId.of("UTC"));

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public ReviewDto createReview(CreateReviewRequest request, String userEmail) {
        log.info("Creating review for product: {} by user: {}", request.getProductIdOrSlug(), userEmail);

        Product product = resolveProduct(request.getProductIdOrSlug());

        User user = null;
        if (userEmail != null && !userEmail.equalsIgnoreCase("anonymousUser")) {
            user = userRepository.findByEmail(userEmail).orElse(null);
        }

        boolean verified = false;
        if (user != null) {
            verified = isVerifiedPurchase(user, product.getId(), request.getOrderId());
        }

        String author = request.getAuthor();
        if ((author == null || author.isBlank()) && user != null) {
            author = user.getName() != null && !user.getName().isBlank() ? user.getName() : user.getEmail();
        }
        if (author == null || author.isBlank()) {
            author = "Verified Customer";
        }

        String role = request.getRole();
        if (role == null || role.isBlank()) {
            role = verified ? "Verified Buyer" : "Customer";
        }

        String comment = request.getComment();
        if (comment == null || comment.isBlank()) {
            comment = request.getContent();
        }

        Review review = Review.builder()
                .product(product)
                .productName(product.getName())
                .user(user)
                .author(author)
                .role(role)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(comment)
                .verifiedPurchase(verified)
                .helpfulCount(0)
                .status(ReviewStatus.PUBLISHED)
                .isFeatured(false)
                .build();

        Review saved = reviewRepository.save(review);
        updateProductRatingMetrics(product);

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDto getReviewById(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return mapToDto(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> getProductReviews(String productIdOrSlug, Pageable pageable) {
        Product product = resolveProduct(productIdOrSlug);
        return reviewRepository.findByProductIdAndStatus(product.getId(), ReviewStatus.PUBLISHED, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getFeaturedEditorialReviews() {
        return reviewRepository.findByIsFeaturedTrueAndStatus(ReviewStatus.PUBLISHED)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getMyReviews(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        return reviewRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReviewSummaryDto getProductReviewSummary(String productIdOrSlug) {
        Product product = resolveProduct(productIdOrSlug);
        List<Review> reviews = reviewRepository.findByProductIdAndStatus(product.getId(), ReviewStatus.PUBLISHED);

        Map<Integer, Long> breakdown = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            breakdown.put(i, 0L);
        }

        double sum = 0.0;
        for (Review r : reviews) {
            int rating = Math.max(1, Math.min(5, r.getRating()));
            breakdown.put(rating, breakdown.get(rating) + 1);
            sum += rating;
        }

        double average = reviews.isEmpty() ? 0.0 : Math.round((sum / reviews.size()) * 10.0) / 10.0;

        return ProductReviewSummaryDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .averageRating(average)
                .totalReviews(reviews.size())
                .ratingBreakdown(breakdown)
                .build();
    }

    @Override
    @Transactional
    public ReviewDto markReviewHelpful(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        Review saved = reviewRepository.save(review);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ReviewDto moderateReview(String id, UpdateReviewStatusRequest request, String adminUser) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        review.setStatus(request.getStatus());
        if (request.getIsFeatured() != null) {
            review.setFeatured(request.getIsFeatured());
        }
        if (request.getNotes() != null) {
            review.setModerationNotes(request.getNotes());
        }
        review.setModeratedBy(adminUser);
        review.setModeratedAt(Instant.now());

        Review saved = reviewRepository.save(review);
        if (saved.getProduct() != null) {
            updateProductRatingMetrics(saved.getProduct());
        }

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> searchReviews(String query, ReviewStatus status, Pageable pageable) {
        return reviewRepository.searchReviews(query, status, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSummaryDto getReviewAdminSummary() {
        long total = reviewRepository.count();
        long published = reviewRepository.countByStatus(ReviewStatus.PUBLISHED);
        long pending = reviewRepository.countByStatus(ReviewStatus.PENDING_MODERATION);
        long flagged = reviewRepository.countByStatus(ReviewStatus.FLAGGED);
        long archived = reviewRepository.countByStatus(ReviewStatus.ARCHIVED);
        Double avg = reviewRepository.getGlobalAverageRating();
        double roundedAvg = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;

        return ReviewSummaryDto.builder()
                .totalReviews(total)
                .publishedReviews(published)
                .pendingReviews(pending)
                .flaggedReviews(flagged)
                .archivedReviews(archived)
                .averageRating(roundedAvg)
                .build();
    }

    @Override
    @Transactional
    public void deleteReview(String id, String adminUser) {
        log.info("Deleting review id: {} by admin: {}", id, adminUser);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        Product product = review.getProduct();
        reviewRepository.delete(review);
        if (product != null) {
            updateProductRatingMetrics(product);
        }
    }

    private Product resolveProduct(String productIdOrSlug) {
        return productRepository.findById(productIdOrSlug)
                .or(() -> productRepository.findBySlug(productIdOrSlug))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id or slug: " + productIdOrSlug));
    }

    private boolean isVerifiedPurchase(User user, String productId, String orderId) {
        if (orderId != null && !orderId.isBlank()) {
            Optional<Order> orderOpt = orderRepository.findByIdOrOrderNumber(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                return order.getItems().stream()
                        .anyMatch(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));
            }
        }
        List<Order> userOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return userOrders.stream()
                .flatMap(o -> o.getItems().stream())
                .anyMatch(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));
    }

    private void updateProductRatingMetrics(Product product) {
        long count = reviewRepository.countByProductIdAndStatus(product.getId(), ReviewStatus.PUBLISHED);
        Double avg = reviewRepository.getAverageRatingForProduct(product.getId());
        double roundedAvg = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;

        product.setReviewCount((int) count);
        product.setRating(BigDecimal.valueOf(roundedAvg));
        productRepository.save(product);
    }

    private ReviewDto mapToDto(Review review) {
        String formattedDate = review.getCreatedAt() != null
                ? DATE_FORMATTER.format(review.getCreatedAt())
                : "";

        return ReviewDto.builder()
                .id(review.getId())
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .productSlug(review.getProduct() != null ? review.getProduct().getSlug() : null)
                .productName(review.getProductName() != null ? review.getProductName() : (review.getProduct() != null ? review.getProduct().getName() : null))
                .author(review.getAuthor())
                .role(review.getRole())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .content(review.getComment())
                .date(formattedDate)
                .verified(review.isVerifiedPurchase())
                .verifiedPurchase(review.isVerifiedPurchase())
                .helpfulCount(review.getHelpfulCount())
                .status(review.getStatus() != null ? review.getStatus().name() : ReviewStatus.PUBLISHED.name())
                .statusCode(review.getStatus())
                .isFeatured(review.isFeatured())
                .moderatedBy(review.getModeratedBy())
                .moderatedAt(review.getModeratedAt())
                .moderationNotes(review.getModerationNotes())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
