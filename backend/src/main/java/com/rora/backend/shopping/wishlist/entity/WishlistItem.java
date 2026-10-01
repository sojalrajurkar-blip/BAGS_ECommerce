package com.rora.backend.shopping.wishlist.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rora.backend.catalog.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wishlist_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"wishlist_id", "product_id"})
})
public class WishlistItem {

    @Id
    @Column(name = "id", length = 64, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    @JsonBackReference
    private Wishlist wishlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "added_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant addedAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.addedAt == null) {
            this.addedAt = Instant.now();
        }
    }
}
