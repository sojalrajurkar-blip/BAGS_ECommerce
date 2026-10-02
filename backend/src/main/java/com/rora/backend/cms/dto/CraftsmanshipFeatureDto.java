package com.rora.backend.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CraftsmanshipFeatureDto {
    private String heading;
    private String paragraph1;
    private String paragraph2;
    private String image;
}
