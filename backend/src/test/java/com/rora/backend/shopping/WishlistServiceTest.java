package com.rora.backend.shopping;

import com.rora.backend.catalog.dto.ProductDto;
import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.service.ProductService;
import com.rora.backend.shopping.wishlist.dto.WishlistDto;
import com.rora.backend.shopping.wishlist.entity.Wishlist;
import com.rora.backend.shopping.wishlist.entity.WishlistItem;
import com.rora.backend.shopping.wishlist.repository.WishlistItemRepository;
import com.rora.backend.shopping.wishlist.repository.WishlistRepository;
import com.rora.backend.shopping.wishlist.service.WishlistService;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private WishlistService wishlistService;

    private User mockUser;
    private Product mockProduct;
    private Wishlist mockWishlist;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id("user-1")
                .email("user@example.com")
                .name("Test User")
                .build();

        mockProduct = Product.builder()
                .id("prod-1")
                .slug("the-nomad-backpack")
                .name("The Nomad Backpack")
                .price(BigDecimal.valueOf(4899))
                .build();

        mockWishlist = Wishlist.builder()
                .id("wish-1")
                .user(mockUser)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void testToggleWishlistItem_AddWhenAbsent() {
        when(wishlistRepository.findByUserId("user-1")).thenReturn(Optional.of(mockWishlist));
        when(productService.findEntityByIdOrSlug("prod-1")).thenReturn(mockProduct);
        when(wishlistItemRepository.findByWishlistIdAndProductId("wish-1", "prod-1")).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(mockWishlist);
        when(wishlistItemRepository.findByWishlistIdOrderByAddedAtDesc("wish-1")).thenReturn(List.of(
                WishlistItem.builder().id("item-1").product(mockProduct).build()
        ));
        when(productService.mapToSummaryDto(mockProduct)).thenReturn(ProductDto.builder().id("prod-1").name("The Nomad Backpack").build());

        WishlistDto result = wishlistService.toggleWishlistItem("user-1", "prod-1");

        assertNotNull(result);
        assertEquals(1, result.getItemCount());
        verify(wishlistItemRepository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testToggleWishlistItem_RemoveWhenPresent() {
        WishlistItem existing = WishlistItem.builder().id("item-1").wishlist(mockWishlist).product(mockProduct).build();
        mockWishlist.addItem(existing);

        when(wishlistRepository.findByUserId("user-1")).thenReturn(Optional.of(mockWishlist));
        when(productService.findEntityByIdOrSlug("prod-1")).thenReturn(mockProduct);
        when(wishlistItemRepository.findByWishlistIdAndProductId("wish-1", "prod-1")).thenReturn(Optional.of(existing));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(mockWishlist);
        when(wishlistItemRepository.findByWishlistIdOrderByAddedAtDesc("wish-1")).thenReturn(List.of());

        WishlistDto result = wishlistService.toggleWishlistItem("user-1", "prod-1");

        assertNotNull(result);
        assertEquals(0, result.getItemCount());
        verify(wishlistItemRepository, times(1)).delete(existing);
    }
}
