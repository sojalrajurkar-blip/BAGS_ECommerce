package com.rora.backend.shopping.wishlist.dto;

import com.rora.backend.catalog.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemDto {

    private String id;
    private ProductDto product;
    private Instant addedAt;
}
