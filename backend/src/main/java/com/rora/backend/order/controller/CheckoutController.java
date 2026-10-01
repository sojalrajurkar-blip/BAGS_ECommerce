package com.rora.backend.order.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.order.dto.CheckoutRequest;
import com.rora.backend.order.dto.OrderDto;
import com.rora.backend.order.service.OrderService;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@Tag(name = "Checkout Engine", description = "Endpoints for placing orders and processing checkout")
public class CheckoutController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping("/place-order")
    @Operation(summary = "Complete checkout and place a new luxury order")
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(@Valid @RequestBody CheckoutRequest request) {
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                userId = user.getId();
            }
        }

        OrderDto orderDto = orderService.placeOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully with number " + orderDto.getOrderNumber(), orderDto));
    }
}
