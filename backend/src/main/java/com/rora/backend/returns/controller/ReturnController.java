package com.rora.backend.returns.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.returns.dto.CreateReturnRequest;
import com.rora.backend.returns.dto.ReturnRecordDto;
import com.rora.backend.returns.service.ReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
@Tag(name = "Returns", description = "Customer return requests and status tracking APIs")
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    @Operation(summary = "Submit a return request for an order")
    public ResponseEntity<ApiResponse<ReturnRecordDto>> createReturnRequest(
            @Valid @RequestBody CreateReturnRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        ReturnRecordDto created = returnService.createReturnRequest(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Return request submitted successfully", created));
    }

    @GetMapping("/my-returns")
    @Operation(summary = "Get all return requests submitted by authenticated customer")
    public ResponseEntity<ApiResponse<List<ReturnRecordDto>>> getMyReturns(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ReturnRecordDto> returns = returnService.getReturnsByCustomer(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(returns));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get return request details by ID")
    public ResponseEntity<ApiResponse<ReturnRecordDto>> getReturnById(@PathVariable String id) {
        ReturnRecordDto returnRecord = returnService.getReturnById(id);
        return ResponseEntity.ok(ApiResponse.success(returnRecord));
    }

    @GetMapping("/order/{orderIdOrNumber}")
    @Operation(summary = "Get all return requests for a specific order")
    public ResponseEntity<ApiResponse<List<ReturnRecordDto>>> getReturnsByOrder(
            @PathVariable String orderIdOrNumber
    ) {
        List<ReturnRecordDto> list = returnService.getReturnsByOrder(orderIdOrNumber);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
