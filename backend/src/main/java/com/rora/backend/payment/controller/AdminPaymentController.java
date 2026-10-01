package com.rora.backend.payment.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.payment.dto.PaymentDto;
import com.rora.backend.payment.dto.PaymentRefundRequest;
import com.rora.backend.payment.dto.PaymentSummaryDto;
import com.rora.backend.payment.entity.PaymentMethod;
import com.rora.backend.payment.entity.PaymentStatus;
import com.rora.backend.payment.service.PaymentService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ORDER_MANAGER')")
@Tag(name = "Admin Payments & Ledger", description = "Endpoints for payment ledger auditing, transaction tracking, and issuing refunds")
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping("/summary")
    @Operation(summary = "Get payment transaction KPI overview")
    public ResponseEntity<ApiResponse<PaymentSummaryDto>> getPaymentSummary() {
        PaymentSummaryDto summary = paymentService.getPaymentSummary();
        return ResponseEntity.ok(ApiResponse.success("Payment summary retrieved successfully", summary));
    }

    @GetMapping
    @Operation(summary = "Search and filter all payment transactions across the store")
    public ResponseEntity<ApiResponse<Page<PaymentDto>>> searchPayments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentMethod method,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaymentDto> payments = paymentService.searchPaymentsAdmin(search, status, method, pageable);
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", payments));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details and transaction history by payment ID")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentById(@PathVariable String paymentId) {
        PaymentDto payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(ApiResponse.success("Payment details retrieved successfully", payment));
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Issue a partial or full refund for a successful payment")
    public ResponseEntity<ApiResponse<PaymentDto>> processRefund(
            @PathVariable String paymentId,
            @Valid @RequestBody PaymentRefundRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        request.setPaymentId(paymentId);
        String actor = userDetails != null ? userDetails.getUsername() : "admin";
        PaymentDto refunded = paymentService.processRefund(request, actor);
        return ResponseEntity.ok(ApiResponse.success("Refund processed successfully", refunded));
    }
}
