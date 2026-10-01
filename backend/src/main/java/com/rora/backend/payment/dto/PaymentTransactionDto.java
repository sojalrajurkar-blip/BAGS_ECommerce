package com.rora.backend.payment.dto;

import com.rora.backend.payment.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionDto {
    private Long id;
    private String paymentId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String status;
    private String gatewayReference;
    private String gatewayResponseCode;
    private String gatewayMessage;
    private Map<String, Object> rawPayload;
    private Instant createdAt;
}
