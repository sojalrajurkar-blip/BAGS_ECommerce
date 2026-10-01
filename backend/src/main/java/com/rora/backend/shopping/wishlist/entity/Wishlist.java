package com.rora.backend.shopping.wishlist.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rora.backend.common.BaseEntity;
import com.rora.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wishlists")
public class Wishlist extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<WishlistItem> items = new ArrayList<>();

    public void addItem(WishlistItem item) {
        items.add(item);
        item.setWishlist(this);
    }

    public void removeItem(WishlistItem item) {
        items.remove(item);
        item.setWishlist(null);
    }
}
