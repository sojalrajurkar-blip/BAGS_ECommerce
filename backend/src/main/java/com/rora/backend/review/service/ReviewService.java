package com.rora.backend.review.service;

import com.rora.backend.review.dto.*;
import com.rora.backend.review.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    ReviewDto createReview(CreateReviewRequest request, String userEmail);

    ReviewDto getReviewById(String id);

    Page<ReviewDto> getProductReviews(String productIdOrSlug, Pageable pageable);

    List<ReviewDto> getFeaturedEditorialReviews();

    List<ReviewDto> getMyReviews(String userEmail);

    ProductReviewSummaryDto getProductReviewSummary(String productIdOrSlug);

    ReviewDto markReviewHelpful(String id);

    ReviewDto moderateReview(String id, UpdateReviewStatusRequest request, String adminUser);

    Page<ReviewDto> searchReviews(String query, ReviewStatus status, Pageable pageable);

    ReviewSummaryDto getReviewAdminSummary();

    void deleteReview(String id, String adminUser);
}
