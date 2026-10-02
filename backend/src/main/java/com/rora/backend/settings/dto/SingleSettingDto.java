package com.rora.backend.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleSettingDto {
    private String id;
    private String key;
    private String value;
    private String type;
    private String description;
    private Instant updatedAt;
}
