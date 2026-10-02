package com.rora.backend.cms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJournalArticleRequest {

    private String slug;

    @NotBlank(message = "Title is required")
    private String title;

    private String subtitle;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Read time is required")
    private String readTime;

    private String author;

    @NotBlank(message = "Featured image URL is required")
    private String image;

    @NotBlank(message = "Excerpt is required")
    private String excerpt;

    @NotBlank(message = "Article content is required")
    private String content;

    private List<String> tags;

    private String publishedAt;
}
