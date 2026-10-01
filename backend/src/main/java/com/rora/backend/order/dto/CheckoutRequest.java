package com.rora.backend.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    private String customerPhone;

    @NotNull(message = "Shipping address is required")
    @Valid
    private AddressDto shippingAddress;

    @Valid
    private AddressDto billingAddress;

    @Builder.Default
    private String paymentMethod = "Mock Gateway";

    private String couponCode;

    private String sessionId;

    // Optional direct items if bypassing cart
    private List<CheckoutItemDto> items;
}
