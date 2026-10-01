package com.rora.backend.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotBlank(message = "Product ID or slug is required")
    private String productIdOrSlug;

    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    @Builder.Default
    private int rating = 5;

    @NotBlank(message = "Review headline/title is required")
    private String title;

    @NotBlank(message = "Review content or comment is required")
    private String comment;

    private String content; // alias

    private String author;

    private String role;

    private String orderId;
}
