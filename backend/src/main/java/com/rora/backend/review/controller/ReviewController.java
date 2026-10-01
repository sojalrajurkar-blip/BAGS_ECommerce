package com.rora.backend.review.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.common.PagedResponse;
import com.rora.backend.review.dto.CreateReviewRequest;
import com.rora.backend.review.dto.ProductReviewSummaryDto;
import com.rora.backend.review.dto.ReviewDto;
import com.rora.backend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Customer reviews, product ratings, star summaries, and helpful votes")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Submit a product review", description = "Creates a new customer review with rating, headline, and commentary")
    public ResponseEntity<ApiResponse<ReviewDto>> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {
        String userEmail = authentication != null ? authentication.getName() : null;
        ReviewDto review = reviewService.createReview(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(review));
    }

    @GetMapping("/product/{productIdOrSlug}")
    @Operation(summary = "Get reviews for a product", description = "Paginated list of approved published reviews for PDP")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDto>>> getProductReviews(
            @PathVariable String productIdOrSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<ReviewDto> reviewPage = reviewService.getProductReviews(productIdOrSlug, pageable);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.of(reviewPage)));
    }

    @GetMapping("/product/{productIdOrSlug}/summary")
    @Operation(summary = "Get product review summary", description = "Aggregate rating breakdown and average score for PDP")
    public ResponseEntity<ApiResponse<ProductReviewSummaryDto>> getProductReviewSummary(
            @PathVariable String productIdOrSlug
    ) {
        ProductReviewSummaryDto summary = reviewService.getProductReviewSummary(productIdOrSlug);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured editorial reviews", description = "Returns top curated editorial customer testimonials")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getFeaturedReviews() {
        List<ReviewDto> featured = reviewService.getFeaturedEditorialReviews();
        return ResponseEntity.ok(ApiResponse.success(featured));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current customer's reviews", description = "List all reviews authored by the authenticated customer")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getMyReviews(Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        List<ReviewDto> reviews = reviewService.getMyReviews(userEmail);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID", description = "Fetch a single review by its unique identifier")
    public ResponseEntity<ApiResponse<ReviewDto>> getReviewById(@PathVariable String id) {
        ReviewDto review = reviewService.getReviewById(id);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @PostMapping("/{id}/helpful")
    @Operation(summary = "Mark review helpful", description = "Upvotes the helpfulness counter on a review")
    public ResponseEntity<ApiResponse<ReviewDto>> markHelpful(@PathVariable String id) {
        ReviewDto review = reviewService.markReviewHelpful(id);
        return ResponseEntity.ok(ApiResponse.success(review));
    }
}
