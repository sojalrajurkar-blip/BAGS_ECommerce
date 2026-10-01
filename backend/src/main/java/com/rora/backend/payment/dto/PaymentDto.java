package com.rora.backend.payment.dto;

import com.rora.backend.payment.entity.PaymentMethod;
import com.rora.backend.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private String id;
    private String orderId;
    private String orderNumber;
    private String customerId;
    private String customerEmail;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private String paymentProvider;
    private PaymentStatus status;
    private String transactionReference;
    private String idempotencyKey;
    private String failureReason;
    private Map<String, Object> metadata;
    private List<PaymentTransactionDto> transactions;
    private Instant createdAt;
    private Instant updatedAt;
}
