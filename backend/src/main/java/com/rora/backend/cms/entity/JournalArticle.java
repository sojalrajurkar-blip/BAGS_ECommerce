package com.rora.backend.cms.entity;

import com.rora.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "journal_articles")
public class JournalArticle extends BaseEntity {

    @Column(name = "slug", length = 255, nullable = false, unique = true)
    private String slug;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    @Column(name = "category", length = 128, nullable = false)
    private String category;

    @Column(name = "read_time", length = 64, nullable = false)
    private String readTime;

    @Column(name = "author", length = 128)
    private String author;

    @Column(name = "image", columnDefinition = "TEXT", nullable = false)
    private String image;

    @Column(name = "excerpt", columnDefinition = "TEXT", nullable = false)
    private String excerpt;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Column(name = "published_at", length = 128)
    private String publishedAt;
}
