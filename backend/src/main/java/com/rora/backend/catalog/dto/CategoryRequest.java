package com.rora.backend.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Slug is required")
    private String slug;

    @NotBlank(message = "Name is required")
    private String name;

    private String title;
    private String headline;
    private String subtitle;
    private String description;
    private String image;
    private String heroImage;
}
