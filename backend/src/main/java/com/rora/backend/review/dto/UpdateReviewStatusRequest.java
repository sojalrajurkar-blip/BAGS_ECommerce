package com.rora.backend.review.dto;

import com.rora.backend.review.entity.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewStatusRequest {

    @NotNull(message = "Review status is required")
    private ReviewStatus status;

    private String notes;

    private Boolean isFeatured;
}
