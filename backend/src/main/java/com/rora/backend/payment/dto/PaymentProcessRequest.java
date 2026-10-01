package com.rora.backend.payment.dto;

import com.rora.backend.payment.entity.SimulationAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessRequest {

    @NotBlank(message = "Payment ID is required")
    @Schema(description = "Payment ID generated from initiation", example = "pay-89241")
    private String paymentId;

    @Schema(description = "Simulation control for QA/testing", example = "FORCE_SUCCESS")
    @Builder.Default
    private SimulationAction simulationAction = SimulationAction.FORCE_SUCCESS;

    @Schema(description = "Customer Virtual Payment Address (VPA)", example = "sarah@okhdfcbank")
    private String upiVpa;

    @Schema(description = "Card number (Mock)", example = "4242424242424242")
    private String cardNumber;

    @Schema(description = "Name on card", example = "Sarah Johnson")
    private String cardHolder;

    @Schema(description = "Card expiry month", example = "12")
    private String expiryMonth;

    @Schema(description = "Card expiry year", example = "2028")
    private String expiryYear;

    @Schema(description = "Card CVV", example = "888")
    private String cvv;

    @Schema(description = "Net banking bank code (e.g., HDFC, ICICI, SBI)", example = "HDFC")
    private String bankCode;
}
