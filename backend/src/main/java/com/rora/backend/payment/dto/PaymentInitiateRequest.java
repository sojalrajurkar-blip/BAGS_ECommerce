package com.rora.backend.payment.dto;

import com.rora.backend.payment.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiateRequest {

    @NotBlank(message = "Order ID or order number is required")
    @Schema(description = "Order ID or Order number (#RRA...)", example = "#RRA89241")
    private String orderIdOrNumber;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "1.00", message = "Minimum transaction amount is ₹1.00")
    @Schema(description = "Payment amount in INR", example = "4899.00")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    @Schema(description = "Selected payment method (UPI, CARD, NET_BANKING, etc.)", example = "UPI")
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.UPI;

    @Schema(description = "Client generated idempotency key", example = "idem-9921-abc")
    private String idempotencyKey;

    @Schema(description = "Optional payment initiation metadata", example = "{\"device\": \"mobile-web\"}")
    private Map<String, Object> metadata;
}
