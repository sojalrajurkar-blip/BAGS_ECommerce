package com.rora.backend.catalog.entity;

import com.rora.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(name = "slug", length = 128, nullable = false, unique = true)
    private String slug;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "headline", length = 255)
    private String headline;

    @Column(name = "subtitle", columnDefinition = "TEXT")
    private String subtitle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "hero_image", columnDefinition = "TEXT")
    private String heroImage;

    @Column(name = "product_count", nullable = false)
    private int productCount;
}
