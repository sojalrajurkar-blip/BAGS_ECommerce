package com.rora.backend.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rora.backend.review.entity.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private String id;
    private String productId;
    private String productSlug;
    private String productName;
    private String author;
    private String role;
    private int rating;
    private String title;
    private String comment;
    private String content; // alias for comment to match frontend
    private String date;    // human readable formatted date
    private boolean verified;
    private boolean verifiedPurchase;
    private int helpfulCount;
    private String status;
    private ReviewStatus statusCode;

    @JsonProperty("isFeatured")
    private boolean isFeatured;

    @JsonProperty("featured")
    public boolean getFeatured() {
        return isFeatured;
    }

    private String moderatedBy;
    private Instant moderatedAt;
    private String moderationNotes;
    private Instant createdAt;
    private Instant updatedAt;
}
