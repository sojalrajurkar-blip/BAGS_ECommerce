package com.rora.backend.payment.service.impl;

import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderTimelineEvent;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.payment.dto.*;
import com.rora.backend.payment.entity.*;
import com.rora.backend.payment.repository.PaymentRepository;
import com.rora.backend.payment.repository.PaymentTransactionRepository;
import com.rora.backend.payment.service.MockPaymentProvider;
import com.rora.backend.payment.service.PaymentService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final DateTimeFormatter TIMELINE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy · hh:mm a").withZone(ZoneId.of("Asia/Kolkata"));

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderRepository orderRepository;
    private final MockPaymentProvider mockPaymentProvider;

    @Override
    @Transactional
    public PaymentDto initiatePayment(PaymentInitiateRequest request, String customerEmail) {
        Order order = orderRepository.findByIdOrOrderNumber(request.getOrderIdOrNumber().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for ID/Number: " + request.getOrderIdOrNumber()));

        if ("Captured".equalsIgnoreCase(order.getPaymentStatus()) || "Completed".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new BadRequestException("Order " + order.getOrderNumber() + " is already paid.");
        }

        // Idempotency check
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().trim().isEmpty()) {
            Optional<Payment> existing = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey().trim());
            if (existing.isPresent()) {
                log.info("Returning idempotent payment for key: {}", request.getIdempotencyKey());
                return mapToDto(existing.get());
            }
        }

        Optional<Payment> existingPaymentOpt = paymentRepository.findByOrderId(order.getId());
        Payment payment;
        String txnRef = "TXN-RRA-" + System.currentTimeMillis() + "-" + (1000 + (int)(Math.random() * 9000));

        if (existingPaymentOpt.isPresent()) {
            payment = existingPaymentOpt.get();
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                throw new BadRequestException("Payment has already succeeded for order: " + order.getOrderNumber());
            }
            payment.setAmount(request.getAmount());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setStatus(PaymentStatus.INITIATED);
            payment.setTransactionReference(txnRef);
            payment.setFailureReason(null);
            payment.setIdempotencyKey(request.getIdempotencyKey());
            payment.setMetadata(request.getMetadata());
        } else {
            payment = Payment.builder()
                    .order(order)
                    .orderNumber(order.getOrderNumber())
                    .customerId(order.getCustomerId())
                    .customerEmail(order.getCustomerEmail())
                    .amount(request.getAmount())
                    .currency("INR")
                    .paymentMethod(request.getPaymentMethod())
                    .paymentProvider("MOCK_GATEWAY")
                    .status(PaymentStatus.INITIATED)
                    .transactionReference(txnRef)
                    .idempotencyKey(request.getIdempotencyKey())
                    .metadata(request.getMetadata())
                    .build();
        }

        Payment saved = paymentRepository.save(payment);
        log.info("Initiated payment {} for order {} with amount ₹{}", saved.getId(), order.getOrderNumber(), saved.getAmount());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public PaymentDto processPayment(PaymentProcessRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found: " + request.getPaymentId()));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment is already in SUCCESS status.");
        }

        // Execute simulated gateway transaction
        MockPaymentProvider.GatewayResult result = mockPaymentProvider.executeTransaction(payment, request);

        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .transactionType(TransactionType.PAYMENT_ATTEMPT)
                .amount(payment.getAmount())
                .status(result.getStatus().name())
                .gatewayReference(result.getGatewayReference())
                .gatewayResponseCode(result.getResponseCode())
                .gatewayMessage(result.getMessage())
                .rawPayload(result.getMetadata())
                .build();

        payment.addTransaction(transaction);
        payment.setStatus(result.getStatus());

        Order order = payment.getOrder();

        if (result.isSuccess()) {
            payment.setFailureReason(null);
            if (result.getMetadata() != null) {
                if (payment.getMetadata() == null) {
                    payment.setMetadata(new HashMap<>());
                }
                payment.getMetadata().putAll(result.getMetadata());
            }

            // Synchronize with Order
            if (order != null) {
                order.setPaymentStatus("Captured");
                order.setPaymentMethod(payment.getPaymentMethod().name());
                updateOrderTimelineStep(order, "Payment Verified", true);
                orderRepository.save(order);
            }
            log.info("Payment {} marked SUCCESS for order {}", payment.getId(), payment.getOrderNumber());
        } else {
            payment.setFailureReason(result.getFailureReason() != null ? result.getFailureReason() : result.getMessage());
            if (order != null && result.getStatus() == PaymentStatus.FAILED) {
                order.setPaymentStatus("Failed");
                orderRepository.save(order);
            }
            log.warn("Payment {} failed with reason: {}", payment.getId(), payment.getFailureReason());
        }

        Payment updated = paymentRepository.save(payment);
        return mapToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        return mapToDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByOrderId(String orderIdOrNumber) {
        String query = orderIdOrNumber.trim();
        Payment payment = paymentRepository.findByOrderId(query)
                .or(() -> paymentRepository.findByOrderNumber(query))
                .or(() -> paymentRepository.findByOrderNumber("#" + query))
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for order: " + query));
        return mapToDto(payment);
    }

    @Override
    @Transactional
    public PaymentDto processRefund(PaymentRefundRequest request, String actor) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found: " + request.getPaymentId()));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Cannot refund payment with status: " + payment.getStatus());
        }

        if (request.getAmount().compareTo(payment.getAmount()) > 0) {
            throw new BadRequestException("Refund amount (₹" + request.getAmount() + ") cannot exceed original payment amount (₹" + payment.getAmount() + ")");
        }

        PaymentStatus newStatus = (request.getAmount().compareTo(payment.getAmount()) == 0)
                ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIALLY_REFUNDED;

        String refundGwRef = "REF-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        PaymentTransaction refundTxn = PaymentTransaction.builder()
                .payment(payment)
                .transactionType(TransactionType.REFUND)
                .amount(request.getAmount())
                .status("SUCCESS")
                .gatewayReference(refundGwRef)
                .gatewayResponseCode("200_REFUND_SETTLED")
                .gatewayMessage("Refund settled by " + (actor != null ? actor : "admin") + " — Reason: " + request.getReason())
                .build();

        payment.addTransaction(refundTxn);
        payment.setStatus(newStatus);

        Order order = payment.getOrder();
        if (order != null) {
            order.setPaymentStatus("Refunded");
            orderRepository.save(order);
        }

        Payment updated = paymentRepository.save(payment);
        log.info("Processed refund of ₹{} for payment {} (Order: {}) by {}", request.getAmount(), payment.getId(), payment.getOrderNumber(), actor);
        return mapToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentSummaryDto getPaymentSummary() {
        long totalPayments = paymentRepository.count();
        long successful = paymentRepository.countByStatus(PaymentStatus.SUCCESS);
        long pending = paymentRepository.countByStatus(PaymentStatus.PENDING) + paymentRepository.countByStatus(PaymentStatus.INITIATED);
        long failed = paymentRepository.countByStatus(PaymentStatus.FAILED) + paymentRepository.countByStatus(PaymentStatus.CANCELLED);
        long refunded = paymentRepository.countByStatus(PaymentStatus.REFUNDED) + paymentRepository.countByStatus(PaymentStatus.PARTIALLY_REFUNDED);

        BigDecimal totalRevenue = paymentRepository.sumSuccessfulVolume();
        BigDecimal totalRefunded = paymentRepository.sumRefundedVolume();

        List<PaymentTransactionDto> recentTxns = paymentTransactionRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToTransactionDto)
                .toList();

        return PaymentSummaryDto.builder()
                .totalPayments(totalPayments)
                .successfulPayments(successful)
                .pendingPayments(pending)
                .failedPayments(failed)
                .refundedPayments(refunded)
                .totalRevenue(totalRevenue)
                .totalRefunded(totalRefunded)
                .recentTransactions(recentTxns)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> searchPaymentsAdmin(String search, PaymentStatus status, PaymentMethod method, Pageable pageable) {
        Specification<Payment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate orderNum = cb.like(cb.lower(root.get("orderNumber")), pattern);
                Predicate email = cb.like(cb.lower(root.get("customerEmail")), pattern);
                Predicate txnRef = cb.like(cb.lower(root.get("transactionReference")), pattern);
                Predicate payId = cb.like(cb.lower(root.get("id")), pattern);
                predicates.add(cb.or(orderNum, email, txnRef, payId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (method != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), method));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return paymentRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    private void updateOrderTimelineStep(Order order, String stepName, boolean completed) {
        if (order.getTimelineEvents() == null) return;
        String nowStr = TIMELINE_FORMATTER.format(Instant.now());
        for (OrderTimelineEvent event : order.getTimelineEvents()) {
            if (event.getStepName().equalsIgnoreCase(stepName.trim())) {
                event.setCompleted(completed);
                event.setEventTime(nowStr);
                return;
            }
        }
    }

    private PaymentDto mapToDto(Payment entity) {
        List<PaymentTransactionDto> txns = entity.getTransactions() != null
                ? entity.getTransactions().stream().map(this::mapToTransactionDto).toList()
                : new ArrayList<>();

        return PaymentDto.builder()
                .id(entity.getId())
                .orderId(entity.getOrder() != null ? entity.getOrder().getId() : null)
                .orderNumber(entity.getOrderNumber())
                .customerId(entity.getCustomerId())
                .customerEmail(entity.getCustomerEmail())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .paymentMethod(entity.getPaymentMethod())
                .paymentProvider(entity.getPaymentProvider())
                .status(entity.getStatus())
                .transactionReference(entity.getTransactionReference())
                .idempotencyKey(entity.getIdempotencyKey())
                .failureReason(entity.getFailureReason())
                .metadata(entity.getMetadata())
                .transactions(txns)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private PaymentTransactionDto mapToTransactionDto(PaymentTransaction txn) {
        return PaymentTransactionDto.builder()
                .id(txn.getId())
                .paymentId(txn.getPayment() != null ? txn.getPayment().getId() : null)
                .transactionType(txn.getTransactionType())
                .amount(txn.getAmount())
                .status(txn.getStatus())
                .gatewayReference(txn.getGatewayReference())
                .gatewayResponseCode(txn.getGatewayResponseCode())
                .gatewayMessage(txn.getGatewayMessage())
                .rawPayload(txn.getRawPayload())
                .createdAt(txn.getCreatedAt())
                .build();
    }
}
