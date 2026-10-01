package com.rora.backend.returns.dto;

import com.rora.backend.returns.entity.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRecordDto {

    private String id;
    private String returnRef;
    private String returnId;
    private String orderId;
    private String orderNumber;
    private String customer;
    private String customerName;
    private String customerEmail;
    private String method;
    private String transactionRef;
    private BigDecimal amount;
    private String date;
    private Instant processedDate;
    private String status;
    private RefundStatus statusCode;
    private String reason;
    private String notes;
}
