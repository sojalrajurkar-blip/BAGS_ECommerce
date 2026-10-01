package com.rora.backend.returns.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "return_items")
public class ReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id", nullable = false)
    @JsonBackReference
    private ReturnRequest returnRequest;

    @Column(name = "order_item_id", length = 64)
    private String orderItemId;

    @Column(name = "product_id", length = 64)
    private String productId;

    @Column(name = "variant_id", length = 64)
    private String variantId;

    @Column(name = "product_name", length = 255, nullable = false)
    private String productName;

    @Column(name = "color_name", length = 64)
    private String colorName;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private int quantity = 1;

    @Column(name = "unit_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "reason", length = 512)
    private String reason;

    @Column(name = "condition_notes", columnDefinition = "TEXT")
    private String conditionNotes;
}
