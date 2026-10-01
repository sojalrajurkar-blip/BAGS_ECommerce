package com.rora.backend.order.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.order.dto.OrderDto;
import com.rora.backend.order.dto.OrderStatusUpdateRequest;
import com.rora.backend.order.dto.OrderTrackingUpdateRequest;
import com.rora.backend.order.service.OrderService;
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
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ORDER_MANAGER')")
@Tag(name = "Admin Order Management", description = "Endpoints for managing store orders, dispatches, and tracking updates")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get paginated orders with optional search and status filter")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderDto> orders = orderService.getAllOrdersAdmin(search, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Retrieved orders successfully", orders));
    }

    @GetMapping("/{idOrNumber}")
    @Operation(summary = "Get full order details by ID or order number")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable String idOrNumber) {
        OrderDto order = orderService.getOrderByIdOrNumber(idOrNumber);
        return ResponseEntity.ok(ApiResponse.success("Order details retrieved successfully", order));
    }

    @PutMapping("/{idOrNumber}/status")
    @Operation(summary = "Update order status and milestone progression")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable String idOrNumber,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderDto updated = orderService.updateOrderStatus(idOrNumber, request);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", updated));
    }

    @PutMapping("/{idOrNumber}/tracking")
    @Operation(summary = "Update carrier, tracking number, and milestone completion")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderTracking(
            @PathVariable String idOrNumber,
            @RequestBody OrderTrackingUpdateRequest request) {
        OrderDto updated = orderService.updateOrderTracking(idOrNumber, request);
        return ResponseEntity.ok(ApiResponse.success("Order tracking updated successfully", updated));
    }
}
