package com.rora.backend.payment.service;

import com.rora.backend.payment.dto.*;
import com.rora.backend.payment.entity.PaymentMethod;
import com.rora.backend.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentDto initiatePayment(PaymentInitiateRequest request, String customerEmail);

    PaymentDto processPayment(PaymentProcessRequest request);

    PaymentDto getPaymentById(String paymentId);

    PaymentDto getPaymentByOrderId(String orderIdOrNumber);

    PaymentDto processRefund(PaymentRefundRequest request, String actor);

    PaymentSummaryDto getPaymentSummary();

    Page<PaymentDto> searchPaymentsAdmin(String search, PaymentStatus status, PaymentMethod method, Pageable pageable);
}
