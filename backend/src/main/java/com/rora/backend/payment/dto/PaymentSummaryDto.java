package com.rora.backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryDto {
    private long totalPayments;
    private long successfulPayments;
    private long pendingPayments;
    private long failedPayments;
    private long refundedPayments;
    private BigDecimal totalRevenue;
    private BigDecimal totalRefunded;
    private List<PaymentTransactionDto> recentTransactions;
}
