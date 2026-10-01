package com.rora.backend.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private String id;
    private String slug;
    private String name;
    private String title;
    private String headline;
    private String subtitle;
    private String description;
    private String image;
    private String heroImage;
    private int count;
    private Instant createdAt;
    private Instant updatedAt;
}
