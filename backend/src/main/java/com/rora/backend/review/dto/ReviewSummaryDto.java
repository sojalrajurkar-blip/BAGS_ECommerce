package com.rora.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryDto {

    private long totalReviews;
    private long publishedReviews;
    private long pendingReviews;
    private long flaggedReviews;
    private long archivedReviews;
    private double averageRating;
}
