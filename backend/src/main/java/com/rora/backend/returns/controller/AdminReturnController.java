package com.rora.backend.returns.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.returns.dto.*;
import com.rora.backend.returns.entity.InspectionStatus;
import com.rora.backend.returns.entity.ReturnStatus;
import com.rora.backend.returns.service.ReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/returns")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ORDER_MANAGER')")
@Tag(name = "Admin Returns", description = "Admin return request review, inspection, approval & rejection workflows")
public class AdminReturnController {

    private final ReturnService returnService;

    @GetMapping("/summary")
    @Operation(summary = "Get returns KPI overview and financial metrics")
    public ResponseEntity<ApiResponse<ReturnSummaryDto>> getSummary() {
        ReturnSummaryDto summary = returnService.getReturnSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping
    @Operation(summary = "Search and filter return requests with pagination")
    public ResponseEntity<ApiResponse<Page<ReturnRecordDto>>> searchReturns(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) ReturnStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReturnRecordDto> results = returnService.searchReturns(query, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get return request details by ID")
    public ResponseEntity<ApiResponse<ReturnRecordDto>> getReturnById(@PathVariable String id) {
        ReturnRecordDto returnRecord = returnService.getReturnById(id);
        return ResponseEntity.ok(ApiResponse.success(returnRecord));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve return request and optionally trigger automatic refund & restock")
    public ResponseEntity<ApiResponse<ReturnRecordDto>> approveReturn(
            @PathVariable String id,
            @Valid @RequestBody(required = false) ApproveReturnRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ApproveReturnRequest req = request != null ? request : ApproveReturnRequest.builder().build();
        String actor = userDetails != null ? userDetails.getUsername() : "system-admin";
        ReturnRecordDto approved = returnService.approveReturn(id, req, actor);
        return ResponseEntity.ok(ApiResponse.success("Return request approved successfully", approved));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject return request with policy reason")
    public ResponseEntity<ApiResponse<ReturnRecordDto>> rejectReturn(
            @PathVariable String id,
            @Valid @RequestBody RejectReturnRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String actor = userDetails != null ? userDetails.getUsername() : "system-admin";
        ReturnRecordDto rejected = returnService.rejectReturn(id, request, actor);
        return ResponseEntity.ok(ApiResponse.success("Return request rejected", rejected));
    }

    @PutMapping("/{id}/inspection")
    @Operation(summary = "Update return physical inspection status")
    public ResponseEntity<ApiResponse<ReturnRecordDto>> updateInspection(
            @PathVariable String id,
            @RequestParam InspectionStatus status,
            @RequestParam(required = false) String notes
    ) {
        ReturnRecordDto updated = returnService.updateInspectionStatus(id, status, notes);
        return ResponseEntity.ok(ApiResponse.success("Inspection status updated", updated));
    }
}
