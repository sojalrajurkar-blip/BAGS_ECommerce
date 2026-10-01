package com.rora.backend.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefundRequest {

    @NotBlank(message = "Payment ID is required")
    @Schema(description = "Payment ID to refund", example = "pay-89241")
    private String paymentId;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "1.00", message = "Minimum refund amount is ₹1.00")
    @Schema(description = "Refund amount in INR", example = "4899.00")
    private BigDecimal amount;

    @NotBlank(message = "Refund reason is required")
    @Schema(description = "Business justification for issuing refund", example = "Customer cancellation before dispatch")
    private String reason;
}
