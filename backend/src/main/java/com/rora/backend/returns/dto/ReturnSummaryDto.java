package com.rora.backend.returns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnSummaryDto {

    private long totalReturns;
    private long requested;
    private long underReview;
    private long approved;
    private long receivedAtHub;
    private long approvedAndRefunded;
    private long rejected;
    private long cancelled;
    private BigDecimal totalRefundedAmount;
}
