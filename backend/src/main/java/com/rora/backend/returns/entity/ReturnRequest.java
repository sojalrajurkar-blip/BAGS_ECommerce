package com.rora.backend.returns.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rora.backend.common.BaseEntity;
import com.rora.backend.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "returns")
public class ReturnRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "order_number", length = 64, nullable = false)
    private String orderNumber;

    @Column(name = "customer_id", length = 64)
    private String customerId;

    @Column(name = "customer_name", length = 255, nullable = false)
    private String customerName;

    @Column(name = "customer_email", length = 255, nullable = false)
    private String customerEmail;

    @Column(name = "customer_phone", length = 32)
    private String customerPhone;

    @Column(name = "item", length = 255, nullable = false)
    private String item;

    @Column(name = "reason", length = 512, nullable = false)
    private String reason;

    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;

    @Column(name = "request_date", nullable = false)
    @Builder.Default
    private Instant requestDate = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_status", length = 64, nullable = false)
    @Builder.Default
    private InspectionStatus inspectionStatus = InspectionStatus.AWAITING_HUB_DELIVERY;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 64, nullable = false)
    @Builder.Default
    private ReturnStatus status = ReturnStatus.UNDER_REVIEW;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @OneToMany(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<ReturnItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "returnRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RefundRecord refund;

    public void addItem(ReturnItem returnItem) {
        items.add(returnItem);
        returnItem.setReturnRequest(this);
    }
}
