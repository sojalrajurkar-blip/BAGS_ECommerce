package com.rora.backend.customer.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.customer.dto.CustomerDto;
import com.rora.backend.customer.dto.CustomerTierUpdateRequest;
import com.rora.backend.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/customers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@Tag(name = "Admin Customer 360", description = "Endpoints for customer relationship management, lifetime metrics, and VIP tier allocation")
public class AdminCustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get paginated customers with search and tier filtering")
    public ResponseEntity<ApiResponse<Page<CustomerDto>>> getCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tier,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CustomerDto> customers = customerService.searchCustomersAdmin(search, tier, pageable);
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full Customer 360 profile by customer ID or email")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable String id) {
        CustomerDto customer = customerService.getCustomerByIdAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("Customer profile retrieved successfully", customer));
    }

    @PutMapping("/{id}/tier")
    @Operation(summary = "Update customer VIP patronage tier")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomerTier(
            @PathVariable String id,
            @Valid @RequestBody CustomerTierUpdateRequest request) {
        CustomerDto updated = customerService.updateCustomerTierAdmin(id, request);
        return ResponseEntity.ok(ApiResponse.success("Customer tier updated successfully", updated));
    }
}
