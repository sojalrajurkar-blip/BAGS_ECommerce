package com.rora.backend.order.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.order.dto.OrderDto;
import com.rora.backend.order.service.OrderService;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Customer Orders", description = "Endpoints for viewing customer orders and tracking shipments")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @GetMapping("/my-orders")
    @Operation(summary = "Get current authenticated customer order history")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getMyOrders(@RequestParam(required = false) String email) {
        String userId = null;
        String userEmail = email;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String authEmail = authentication.getName();
            User user = userRepository.findByEmail(authEmail).orElse(null);
            if (user != null) {
                userId = user.getId();
                userEmail = user.getEmail();
            }
        }

        List<OrderDto> orders = orderService.getCustomerOrders(userId, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Fetched customer orders successfully", orders));
    }

    @GetMapping("/track/{orderNumber}")
    @Operation(summary = "Track shipment milestone and delivery status by order number")
    public ResponseEntity<ApiResponse<OrderDto>> trackOrder(@PathVariable String orderNumber) {
        OrderDto order = orderService.trackOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success("Tracked order details successfully", order));
    }

    @GetMapping("/{idOrNumber}")
    @Operation(summary = "Get order details by order ID or order number")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderByIdOrNumber(@PathVariable String idOrNumber) {
        OrderDto order = orderService.getOrderByIdOrNumber(idOrNumber);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
    }

    @PostMapping("/{idOrNumber}/cancel")
    @Operation(summary = "Cancel an order before dispatch")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(@PathVariable String idOrNumber) {
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String authEmail = authentication.getName();
            User user = userRepository.findByEmail(authEmail).orElse(null);
            if (user != null) {
                userId = user.getId();
            }
        }

        OrderDto order = orderService.cancelOrder(idOrNumber, userId);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
    }
}
