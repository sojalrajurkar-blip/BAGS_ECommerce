package com.rora.backend.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeroBannerDto {
    private String eyebrow;
    private String title;
    private String subtitle;
    private String primaryButtonText;
    private String secondaryButtonText;
    private String primaryButtonLink;
    private String secondaryButtonLink;
    private String backgroundImage;
}
