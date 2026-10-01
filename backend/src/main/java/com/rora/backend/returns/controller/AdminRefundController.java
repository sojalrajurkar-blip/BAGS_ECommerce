package com.rora.backend.returns.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.returns.dto.CreateRefundRequest;
import com.rora.backend.returns.dto.RefundRecordDto;
import com.rora.backend.returns.entity.RefundStatus;
import com.rora.backend.returns.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/refunds")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ORDER_MANAGER')")
@Tag(name = "Admin Refunds", description = "Admin financial reimbursement and refund transaction management APIs")
public class AdminRefundController {

    private final RefundService refundService;

    @GetMapping
    @Operation(summary = "Search and list all refund transactions with pagination")
    public ResponseEntity<ApiResponse<Page<RefundRecordDto>>> searchRefunds(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) RefundStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RefundRecordDto> results = refundService.searchRefunds(query, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get refund record details by ID")
    public ResponseEntity<ApiResponse<RefundRecordDto>> getRefundById(@PathVariable String id) {
        RefundRecordDto refund = refundService.getRefundById(id);
        return ResponseEntity.ok(ApiResponse.success(refund));
    }

    @GetMapping("/return/{returnId}")
    @Operation(summary = "Get refund record associated with a return request")
    public ResponseEntity<ApiResponse<RefundRecordDto>> getRefundByReturn(@PathVariable String returnId) {
        RefundRecordDto refund = refundService.getRefundByReturnId(returnId);
        return ResponseEntity.ok(ApiResponse.success(refund));
    }

    @GetMapping("/order/{orderIdOrNumber}")
    @Operation(summary = "Get all refunds for a specific order")
    public ResponseEntity<ApiResponse<List<RefundRecordDto>>> getRefundsByOrder(@PathVariable String orderIdOrNumber) {
        List<RefundRecordDto> list = refundService.getRefundsByOrder(orderIdOrNumber);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping
    @Operation(summary = "Issue a manual or custom refund transaction")
    public ResponseEntity<ApiResponse<RefundRecordDto>> createRefund(
            @Valid @RequestBody CreateRefundRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String actor = userDetails != null ? userDetails.getUsername() : "system-admin";
        RefundRecordDto created = refundService.createRefund(request, actor);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Refund transaction executed successfully", created));
    }
}
