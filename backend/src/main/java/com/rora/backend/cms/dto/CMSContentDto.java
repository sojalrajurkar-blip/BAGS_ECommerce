package com.rora.backend.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CMSContentDto {
    private AnnouncementBarDto announcementBar;
    private HeroBannerDto heroBanner;
    private CraftsmanshipFeatureDto craftsmanshipFeature;
    private Map<String, Object> additionalBlocks;
}
