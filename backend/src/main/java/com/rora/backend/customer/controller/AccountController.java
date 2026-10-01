package com.rora.backend.customer.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.customer.dto.*;
import com.rora.backend.customer.service.CustomerService;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "Customer Account & Profiles", description = "Endpoints for managing customer personal profiles, passwords, and saved addresses")
public class AccountController {

    private final CustomerService customerService;
    private final UserRepository userRepository;

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResourceNotFoundException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
        return user.getId();
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current authenticated customer profile")
    public ResponseEntity<ApiResponse<CustomerDto>> getProfile() {
        String userId = getCurrentUserId();
        CustomerDto profile = customerService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update customer personal profile information")
    public ResponseEntity<ApiResponse<CustomerDto>> updateProfile(@RequestBody CustomerProfileUpdateRequest request) {
        String userId = getCurrentUserId();
        CustomerDto updated = customerService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PutMapping("/password")
    @Operation(summary = "Change customer password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String userId = getCurrentUserId();
        customerService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @GetMapping("/addresses")
    @Operation(summary = "Get all saved shipping and billing addresses")
    public ResponseEntity<ApiResponse<List<CustomerAddressDto>>> getAddresses() {
        String userId = getCurrentUserId();
        List<CustomerAddressDto> addresses = customerService.getAddresses(userId);
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", addresses));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Save a new shipping or billing address")
    public ResponseEntity<ApiResponse<CustomerAddressDto>> addAddress(@Valid @RequestBody AddressRequest request) {
        String userId = getCurrentUserId();
        CustomerAddressDto created = customerService.addAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added successfully", created));
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "Update an existing saved address")
    public ResponseEntity<ApiResponse<CustomerAddressDto>> updateAddress(
            @PathVariable String id,
            @Valid @RequestBody AddressRequest request) {
        String userId = getCurrentUserId();
        CustomerAddressDto updated = customerService.updateAddress(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", updated));
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Delete a saved address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable String id) {
        String userId = getCurrentUserId();
        customerService.deleteAddress(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }

    @PutMapping("/addresses/{id}/default")
    @Operation(summary = "Set an address as the default shipping address")
    public ResponseEntity<ApiResponse<CustomerAddressDto>> setDefaultAddress(@PathVariable String id) {
        String userId = getCurrentUserId();
        CustomerAddressDto updated = customerService.setDefaultAddress(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Default address updated successfully", updated));
    }
}
