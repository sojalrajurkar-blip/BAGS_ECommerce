package com.rora.backend.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJournalArticleRequest {
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
}
