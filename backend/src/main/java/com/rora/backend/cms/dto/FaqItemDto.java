package com.rora.backend.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqItemDto {
    private String id;
    private String category;
    private String question;
    private String answer;
    // For frontend compatibility where fields are named 'q' and 'a'
    private String q;
    private String a;
    private Integer displayOrder;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
