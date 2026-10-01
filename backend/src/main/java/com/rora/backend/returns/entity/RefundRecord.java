package com.rora.backend.returns.entity;

import com.rora.backend.common.BaseEntity;
import com.rora.backend.order.entity.Order;
import com.rora.backend.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refunds")
public class RefundRecord extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id")
    private ReturnRequest returnRequest;

    @Column(name = "return_ref", length = 64)
    private String returnRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "order_number", length = 64, nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "customer_name", length = 255, nullable = false)
    private String customerName;

    @Column(name = "customer_email", length = 255, nullable = false)
    private String customerEmail;

    @Column(name = "method", length = 128, nullable = false)
    @Builder.Default
    private String method = "Original Payment Source";

    @Column(name = "transaction_ref", length = 128, unique = true)
    private String transactionRef;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 64, nullable = false)
    @Builder.Default
    private RefundStatus status = RefundStatus.COMPLETED;

    @Column(name = "processed_date", nullable = false)
    @Builder.Default
    private Instant processedDate = Instant.now();

    @Column(name = "reason", length = 512)
    private String reason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
