package com.rora.backend.payment.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.payment.dto.PaymentDto;
import com.rora.backend.payment.dto.PaymentInitiateRequest;
import com.rora.backend.payment.dto.PaymentProcessRequest;
import com.rora.backend.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Customer Payments & Checkout Simulation", description = "Endpoints for initiating and processing payments during checkout")
public class PaymentController {

    private final PaymentService paymentService;
    private final com.rora.backend.payment.service.RazorpayService razorpayService;

    @PostMapping("/razorpay/create-order/{orderIdOrNumber}")
    @Operation(summary = "Create a Razorpay Order for online checkout")
    public ResponseEntity<ApiResponse<com.rora.backend.payment.dto.RazorpayOrderResponse>> createRazorpayOrder(
            @PathVariable String orderIdOrNumber) {
        com.rora.backend.payment.dto.RazorpayOrderResponse response = razorpayService.createOrder(orderIdOrNumber);
        return ResponseEntity.ok(ApiResponse.success("Razorpay order created successfully", response));
    }

    @PostMapping("/razorpay/verify")
    @Operation(summary = "Verify cryptographic signature of Razorpay payment and capture order")
    public ResponseEntity<ApiResponse<PaymentDto>> verifyRazorpayPayment(
            @Valid @RequestBody com.rora.backend.payment.dto.RazorpayVerifyRequest request) {
        PaymentDto payment = razorpayService.verifyPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Razorpay payment verified and captured successfully", payment));
    }

    @PostMapping("/initiate")
    @Operation(summary = "Initiate a payment transaction for an order")
    public ResponseEntity<ApiResponse<PaymentDto>> initiatePayment(
            @Valid @RequestBody PaymentInitiateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String customerEmail = userDetails != null ? userDetails.getUsername() : null;
        PaymentDto payment = paymentService.initiatePayment(request, customerEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment initiated successfully", payment));
    }

    @PostMapping("/process")
    @Operation(summary = "Process and simulate a payment transaction with mock gateway")
    public ResponseEntity<ApiResponse<PaymentDto>> processPayment(
            @Valid @RequestBody PaymentProcessRequest request) {

        PaymentDto processed = paymentService.processPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", processed));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details and transaction history by payment ID")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentById(@PathVariable String paymentId) {
        PaymentDto payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(ApiResponse.success("Payment details retrieved successfully", payment));
    }

    @GetMapping("/order/{orderIdOrNumber}")
    @Operation(summary = "Get payment status and details by order ID or order number")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentByOrderId(@PathVariable String orderIdOrNumber) {
        PaymentDto payment = paymentService.getPaymentByOrderId(orderIdOrNumber);
        return ResponseEntity.ok(ApiResponse.success("Order payment details retrieved successfully", payment));
    }
}
