package com.rora.backend.payment.service;

import com.rora.backend.payment.dto.PaymentProcessRequest;
import com.rora.backend.payment.entity.Payment;
import com.rora.backend.payment.entity.PaymentStatus;
import com.rora.backend.payment.entity.SimulationAction;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class MockPaymentProvider {

    @Data
    @Builder
    public static class GatewayResult {
        private boolean success;
        private PaymentStatus status;
        private String gatewayReference;
        private String responseCode;
        private String message;
        private String failureReason;
        private Map<String, Object> metadata;
    }

    public GatewayResult executeTransaction(Payment payment, PaymentProcessRequest request) {
        SimulationAction action = request.getSimulationAction() != null ? request.getSimulationAction() : SimulationAction.FORCE_SUCCESS;
        String gwRef = "GW-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        Map<String, Object> meta = new HashMap<>();

        if (action == SimulationAction.FORCE_FAILURE) {
            return GatewayResult.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .gatewayReference(gwRef)
                    .responseCode("DECLINED_BY_ISSUER")
                    .message("Simulated card issuer or bank decline")
                    .failureReason("Transaction declined by customer's bank (simulated)")
                    .metadata(meta)
                    .build();
        }

        if (action == SimulationAction.FORCE_PENDING) {
            return GatewayResult.builder()
                    .success(false)
                    .status(PaymentStatus.PENDING)
                    .gatewayReference(gwRef)
                    .responseCode("PENDING_USER_ACTION")
                    .message("Awaiting customer 3D-Secure authentication or UPI app authorization")
                    .failureReason(null)
                    .metadata(meta)
                    .build();
        }

        // Default or FORCE_SUCCESS
        switch (payment.getPaymentMethod()) {
            case UPI -> {
                String vpa = request.getUpiVpa() != null ? request.getUpiVpa() : "sarah@okhdfcbank";
                meta.put("upiVpa", vpa);
                meta.put("rrn", String.valueOf(100000000000L + (long)(Math.random() * 899999999999L)));
                meta.put("flow", "UPI_COLLECT_INTENT");
            }
            case CARD -> {
                String cardNum = request.getCardNumber() != null ? request.getCardNumber().replaceAll("\\s+", "") : "4242424242424242";
                String last4 = cardNum.length() >= 4 ? cardNum.substring(cardNum.length() - 4) : "4242";
                meta.put("cardNetwork", cardNum.startsWith("4") ? "VISA" : (cardNum.startsWith("5") ? "MASTERCARD" : "RUPAY"));
                meta.put("last4", last4);
                meta.put("cardHolder", request.getCardHolder() != null ? request.getCardHolder() : "Sarah Johnson");
                meta.put("authCode", "AUTH-" + (10000 + (int)(Math.random() * 90000)));
            }
            case NET_BANKING -> {
                meta.put("bankCode", request.getBankCode() != null ? request.getBankCode() : "HDFC");
                meta.put("bankRef", "BANK-TXN-" + (100000 + (int)(Math.random() * 900000)));
            }
            case WALLET -> meta.put("walletProvider", "Paytm / AmazonPay");
            case CASH_ON_DELIVERY -> meta.put("codVerification", "OTP_VERIFIED_ON_DELIVERY");
        }

        return GatewayResult.builder()
                .success(true)
                .status(PaymentStatus.SUCCESS)
                .gatewayReference(gwRef)
                .responseCode("200_SUCCESS")
                .message("Payment captured and settled successfully")
                .failureReason(null)
                .metadata(meta)
                .build();
    }
}
