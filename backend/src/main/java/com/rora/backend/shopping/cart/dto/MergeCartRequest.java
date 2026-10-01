package com.rora.backend.shopping.cart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MergeCartRequest {

    @NotBlank(message = "Guest Session ID is required to merge")
    private String sessionId;
}
