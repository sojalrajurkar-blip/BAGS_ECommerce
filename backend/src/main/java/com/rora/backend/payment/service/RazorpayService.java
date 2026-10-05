package com.rora.backend.payment.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderTimelineEvent;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.payment.dto.PaymentDto;
import com.rora.backend.payment.dto.PaymentTransactionDto;
import com.rora.backend.payment.dto.RazorpayOrderResponse;
import com.rora.backend.payment.dto.RazorpayVerifyRequest;
import com.rora.backend.payment.entity.*;
import com.rora.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
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
public class RazorpayService {

    private static final DateTimeFormatter TIMELINE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy · hh:mm a").withZone(ZoneId.of("Asia/Kolkata"));

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${rora.razorpay.key-id:rzp_test_TkFa9wOUkFRBDH}")
    private String keyId;

    @Value("${rora.razorpay.key-secret:gJlmuC6OqY5Bv9c2q3Muc7zN}")
    private String keySecret;

    /**
     * Creates a Razorpay Order and initializes a Payment record in the local database.
     */
    @Transactional
    public RazorpayOrderResponse createOrder(String orderIdOrNumber) {
        Order order = orderRepository.findByIdOrOrderNumber(orderIdOrNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for ID/Number: " + orderIdOrNumber));

        if ("Captured".equalsIgnoreCase(order.getPaymentStatus()) || "Completed".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new BadRequestException("Order " + order.getOrderNumber() + " is already paid.");
        }

        BigDecimal totalAmount = order.getTotal();
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            totalAmount = BigDecimal.valueOf(1.00); // minimum 1 INR for test
        }

        // Amount in paise (e.g. ₹3,999.00 -> 399900 paise)
        long amountInPaise = totalAmount.multiply(BigDecimal.valueOf(100)).longValue();

        String razorpayOrderId;
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", order.getOrderNumber());

            JSONObject notes = new JSONObject();
            notes.put("orderId", order.getId());
            notes.put("orderNumber", order.getOrderNumber());
            notes.put("customerEmail", order.getCustomerEmail() != null ? order.getCustomerEmail() : "");
            orderRequest.put("notes", notes);

            com.razorpay.Order rzpOrder = client.orders.create(orderRequest);
            razorpayOrderId = rzpOrder.get("id");
            log.info("Razorpay Order created: {} for Order #{}", razorpayOrderId, order.getOrderNumber());
        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay Order: {}", e.getMessage(), e);
            throw new BadRequestException("Razorpay order creation failed: " + e.getMessage());
        }

        // Create or update local Payment record
        Optional<Payment> existingPaymentOpt = paymentRepository.findByOrderId(order.getId());
        Payment payment;
        if (existingPaymentOpt.isPresent()) {
            payment = existingPaymentOpt.get();
            payment.setAmount(totalAmount);
            payment.setPaymentProvider("RAZORPAY");
            payment.setPaymentMethod(PaymentMethod.UPI);
            payment.setStatus(PaymentStatus.INITIATED);
            payment.setTransactionReference(razorpayOrderId);
            if (payment.getMetadata() == null) {
                payment.setMetadata(new HashMap<>());
            }
            payment.getMetadata().put("razorpayOrderId", razorpayOrderId);
        } else {
            Map<String, Object> meta = new HashMap<>();
            meta.put("razorpayOrderId", razorpayOrderId);

            payment = Payment.builder()
                    .order(order)
                    .orderNumber(order.getOrderNumber())
                    .customerId(order.getCustomerId())
                    .customerEmail(order.getCustomerEmail())
                    .amount(totalAmount)
                    .currency("INR")
                    .paymentMethod(PaymentMethod.UPI)
                    .paymentProvider("RAZORPAY")
                    .status(PaymentStatus.INITIATED)
                    .transactionReference(razorpayOrderId)
                    .metadata(meta)
                    .build();
        }
        paymentRepository.save(payment);

        return RazorpayOrderResponse.builder()
                .razorpayOrderId(razorpayOrderId)
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .amount(totalAmount)
                .amountInPaise(amountInPaise)
                .currency("INR")
                .keyId(keyId)
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .description("RÓRA Luxury Leather Goods - Order " + order.getOrderNumber())
                .build();
    }

    /**
     * Verifies the cryptographic HMAC SHA256 signature returned by Razorpay Checkout.
     */
    @Transactional
    public PaymentDto verifyPayment(RazorpayVerifyRequest request) {
        log.info("Verifying Razorpay payment for order: {}, RzpOrderId: {}, RzpPaymentId: {}",
                request.getOrderIdOrNumber(), request.getRazorpayOrderId(), request.getRazorpayPaymentId());

        boolean isSignatureValid = false;
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            isSignatureValid = Utils.verifyPaymentSignature(options, keySecret);
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            isSignatureValid = false;
        }

        if (!isSignatureValid) {
            log.error("Razorpay signature verification FAILED for payment ID: {}", request.getRazorpayPaymentId());
            throw new BadRequestException("Invalid payment signature from Razorpay. Verification failed.");
        }

        Order order = orderRepository.findByIdOrOrderNumber(request.getOrderIdOrNumber().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.getOrderIdOrNumber()));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseGet(() -> Payment.builder()
                        .order(order)
                        .orderNumber(order.getOrderNumber())
                        .customerId(order.getCustomerId())
                        .customerEmail(order.getCustomerEmail())
                        .amount(order.getTotal())
                        .currency("INR")
                        .paymentMethod(PaymentMethod.UPI)
                        .paymentProvider("RAZORPAY")
                        .build());

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentProvider("RAZORPAY");
        payment.setTransactionReference(request.getRazorpayPaymentId());
        payment.setFailureReason(null);

        if (payment.getMetadata() == null) {
            payment.setMetadata(new HashMap<>());
        }
        payment.getMetadata().put("razorpayOrderId", request.getRazorpayOrderId());
        payment.getMetadata().put("razorpayPaymentId", request.getRazorpayPaymentId());
        payment.getMetadata().put("razorpaySignature", request.getRazorpaySignature());
        payment.getMetadata().put("verifiedAt", Instant.now().toString());

        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .transactionType(TransactionType.PAYMENT_ATTEMPT)
                .amount(payment.getAmount())
                .status("SUCCESS")
                .gatewayReference(request.getRazorpayPaymentId())
                .gatewayResponseCode("200_RAZORPAY_CAPTURED")
                .gatewayMessage("Razorpay verified payment captured successfully")
                .rawPayload(payment.getMetadata())
                .build();

        payment.addTransaction(transaction);
        Payment savedPayment = paymentRepository.save(payment);

        // Update Order State
        order.setPaymentStatus("Captured");
        order.setStatus("CONFIRMED");

        if (order.getTimelineEvents() != null) {
            String nowStr = TIMELINE_FORMATTER.format(Instant.now());
            boolean found = false;
            for (OrderTimelineEvent event : order.getTimelineEvents()) {
                if (event.getStepName() != null && event.getStepName().equalsIgnoreCase("Payment Verified")) {
                    event.setCompleted(true);
                    event.setEventTime(nowStr);
                    found = true;
                    break;
                }
            }
            if (!found) {
                order.addTimelineEvent(OrderTimelineEvent.builder()
                        .stepName("Payment Verified")
                        .title("Payment Verified via Razorpay")
                        .completed(true)
                        .eventTime(nowStr)
                        .displayOrder(1)
                        .build());
            }
        }

        orderRepository.save(order);
        log.info("Order #{} marked as CONFIRMED with Captured payment {}", order.getOrderNumber(), request.getRazorpayPaymentId());

        return mapToDto(savedPayment);
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
