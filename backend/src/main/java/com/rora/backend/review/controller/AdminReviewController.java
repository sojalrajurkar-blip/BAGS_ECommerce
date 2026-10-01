package com.rora.backend.review.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.common.PagedResponse;
import com.rora.backend.review.dto.ReviewDto;
import com.rora.backend.review.dto.ReviewSummaryDto;
import com.rora.backend.review.dto.UpdateReviewStatusRequest;
import com.rora.backend.review.entity.ReviewStatus;
import com.rora.backend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGER', 'PRODUCT_MANAGER', 'MARKETING_MANAGER')")
@Tag(name = "Admin Reviews", description = "Backoffice customer review moderation, status transitions, curation, and analytics")
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Search & list reviews", description = "Filter reviews by search term, status, with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDto>>> searchReviews(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ReviewStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<ReviewDto> reviewPage = reviewService.searchReviews(search, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.of(reviewPage)));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get review dashboard summary", description = "Aggregates total, published, pending, flagged, and average ratings")
    public ResponseEntity<ApiResponse<ReviewSummaryDto>> getReviewSummary() {
        ReviewSummaryDto summary = reviewService.getReviewAdminSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review details", description = "Retrieve complete review information for moderation")
    public ResponseEntity<ApiResponse<ReviewDto>> getReviewById(@PathVariable String id) {
        ReviewDto review = reviewService.getReviewById(id);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @PutMapping("/{id}/moderate")
    @Operation(summary = "Moderate review", description = "Update review status (PUBLISHED, PENDING_MODERATION, FLAGGED, ARCHIVED), featured state, and moderation notes")
    public ResponseEntity<ApiResponse<ReviewDto>> moderateReview(
            @PathVariable String id,
            @Valid @RequestBody UpdateReviewStatusRequest request,
            Authentication authentication
    ) {
        String adminUser = authentication != null ? authentication.getName() : "system-admin";
        ReviewDto review = reviewService.moderateReview(id, request, adminUser);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update review status", description = "Quick status change shortcut for moderation workflows")
    public ResponseEntity<ApiResponse<ReviewDto>> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateReviewStatusRequest request,
            Authentication authentication
    ) {
        String adminUser = authentication != null ? authentication.getName() : "system-admin";
        ReviewDto review = reviewService.moderateReview(id, request, adminUser);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review", description = "Permanently removes a review and adjusts product rating statistics")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable String id,
            Authentication authentication
    ) {
        String adminUser = authentication != null ? authentication.getName() : "system-admin";
        reviewService.deleteReview(id, adminUser);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
