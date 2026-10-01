package com.rora.backend.shopping.wishlist.dto;

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
public class WishlistDto {

    private String id;
    private String userId;
    private int itemCount;
    private List<WishlistItemDto> items;
    private Instant updatedAt;
}
