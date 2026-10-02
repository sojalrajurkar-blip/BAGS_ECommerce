package com.rora.backend.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFaqItemRequest {
    private String category;
    private String question;
    private String answer;
    private Integer displayOrder;
    private Boolean isActive;
}
