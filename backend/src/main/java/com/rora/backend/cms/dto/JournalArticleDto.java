package com.rora.backend.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalArticleDto {
    private String id;
    private String slug;
    private String title;
    private String subtitle;
    private String category;
    private String readTime;
    private String author;
    private String image;
    private String excerpt;
    private String content;
    private List<String> tags;
    private String publishedAt;
    private String date;
    private Instant createdAt;
    private Instant updatedAt;
}
