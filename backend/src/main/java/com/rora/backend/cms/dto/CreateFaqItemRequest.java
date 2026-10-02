package com.rora.backend.cms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFaqItemRequest {

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Question is required")
    private String question;

    @NotBlank(message = "Answer is required")
    private String answer;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private Boolean isActive = true;
}
