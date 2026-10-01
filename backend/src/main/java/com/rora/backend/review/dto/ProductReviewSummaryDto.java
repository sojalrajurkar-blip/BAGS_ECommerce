package com.rora.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewSummaryDto {

    private String productId;
    private String productName;
    private double averageRating;
    private long totalReviews;

    @Builder.Default
    private Map<Integer, Long> ratingBreakdown = new HashMap<>();
}
