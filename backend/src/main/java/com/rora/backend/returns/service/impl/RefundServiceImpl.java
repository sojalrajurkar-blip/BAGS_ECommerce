package com.rora.backend.returns.service.impl;

import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.payment.entity.Payment;
import com.rora.backend.payment.entity.PaymentStatus;
import com.rora.backend.payment.entity.PaymentTransaction;
import com.rora.backend.payment.entity.TransactionType;
import com.rora.backend.payment.repository.PaymentRepository;
import com.rora.backend.payment.repository.PaymentTransactionRepository;
import com.rora.backend.returns.dto.CreateRefundRequest;
import com.rora.backend.returns.dto.RefundRecordDto;
import com.rora.backend.returns.entity.RefundRecord;
import com.rora.backend.returns.entity.RefundStatus;
import com.rora.backend.returns.entity.ReturnRequest;
import com.rora.backend.returns.repository.RefundRepository;
import com.rora.backend.returns.repository.ReturnRepository;
import com.rora.backend.returns.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
    private final ReturnRepository returnRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.of("Asia/Kolkata"));

    @Override
    @Transactional
    public RefundRecordDto createRefund(CreateRefundRequest request, String adminUser) {
        log.info("Processing refund request for amount: {}", request.getAmount());

        ReturnRequest returnRequest = null;
        if (request.getReturnId() != null && !request.getReturnId().trim().isEmpty()) {
            returnRequest = returnRepository.findById(request.getReturnId().trim()).orElse(null);
        }

        Order order = null;
        if (returnRequest != null) {
            order = returnRequest.getOrder();
        } else if (request.getOrderIdOrNumber() != null && !request.getOrderIdOrNumber().trim().isEmpty()) {
            String clean = request.getOrderIdOrNumber().trim();
            order = orderRepository.findById(clean)
                    .or(() -> orderRepository.findByOrderNumber(clean))
                    .or(() -> orderRepository.findByOrderNumber("#" + clean))
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with identifier: " + request.getOrderIdOrNumber()));
        } else {
            throw new BadRequestException("Either returnId or orderIdOrNumber must be provided for refund");
        }

        if (order == null) {
            throw new BadRequestException("Order associated with refund could not be resolved");
        }

        // Check if refund already exists for this return
        if (returnRequest != null) {
            Optional<RefundRecord> existing = refundRepository.findByReturnRequestId(returnRequest.getId());
            if (existing.isPresent()) {
                log.info("Returning existing refund record ID: {} for return: {}", existing.get().getId(), returnRequest.getId());
                return mapToDto(existing.get());
            }
        }

        // Resolve Payment if exists
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);

        String method = request.getMethod() != null && !request.getMethod().trim().isEmpty()
                ? request.getMethod().trim()
                : (payment != null ? "Source (" + payment.getPaymentMethod().name() + ")" : "Original Payment Source");

        String transactionRef = "REF-" + (payment != null ? payment.getPaymentMethod().name() : "MOCK") + "-" + (100000 + new Random().nextInt(900000));

        RefundRecord refund = RefundRecord.builder()
                .returnRequest(returnRequest)
                .returnRef(returnRequest != null ? returnRequest.getId() : null)
                .order(order)
                .orderNumber(order.getOrderNumber())
                .payment(payment)
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .method(method)
                .transactionRef(transactionRef)
                .amount(request.getAmount())
                .status(RefundStatus.COMPLETED)
                .processedDate(Instant.now())
                .reason(request.getReason())
                .notes(request.getNotes())
                .build();

        RefundRecord savedRefund = refundRepository.save(refund);

        // Update Payment record & record transaction ledger if available
        if (payment != null) {
            payment.setStatus(PaymentStatus.REFUNDED);
            PaymentTransaction txn = PaymentTransaction.builder()
                    .payment(payment)
                    .transactionType(TransactionType.REFUND)
                    .amount(request.getAmount())
                    .status("SUCCESS")
                    .gatewayReference(transactionRef)
                    .gatewayMessage("Refund of ₹" + request.getAmount() + " processed for return " + (returnRequest != null ? returnRequest.getId() : order.getOrderNumber()))
                    .createdAt(Instant.now())
                    .build();
            paymentTransactionRepository.save(txn);
            paymentRepository.save(payment);
        }

        order.setPaymentStatus("Refunded");
        orderRepository.save(order);

        log.info("Successfully processed refund ID: {} (Txn: {}) for Order: {}", savedRefund.getId(), transactionRef, order.getOrderNumber());
        return mapToDto(savedRefund);
    }

    @Override
    @Transactional(readOnly = true)
    public RefundRecordDto getRefundById(String id) {
        RefundRecord refund = refundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund record not found with ID: " + id));
        return mapToDto(refund);
    }

    @Override
    @Transactional(readOnly = true)
    public RefundRecordDto getRefundByReturnId(String returnId) {
        RefundRecord refund = refundRepository.findByReturnRequestId(returnId)
                .or(() -> refundRepository.findByReturnRef(returnId))
                .orElseThrow(() -> new ResourceNotFoundException("Refund record not found for return: " + returnId));
        return mapToDto(refund);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundRecordDto> getRefundsByOrder(String orderIdOrNumber) {
        String clean = orderIdOrNumber.trim();
        List<RefundRecord> list = refundRepository.findByOrderNumber(clean);
        if (list.isEmpty() && !clean.startsWith("#")) {
            list = refundRepository.findByOrderNumber("#" + clean);
        }
        if (list.isEmpty()) {
            list = refundRepository.findByOrderId(clean);
        }
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RefundRecordDto> searchRefunds(String query, RefundStatus status, Pageable pageable) {
        String cleanQuery = query != null ? query.trim() : null;
        Page<RefundRecord> page = refundRepository.searchRefunds(cleanQuery, status, pageable);
        return page.map(this::mapToDto);
    }

    private RefundRecordDto mapToDto(RefundRecord refund) {
        return RefundRecordDto.builder()
                .id(refund.getId())
                .returnRef(refund.getReturnRef())
                .returnId(refund.getReturnRequest() != null ? refund.getReturnRequest().getId() : null)
                .orderId(refund.getOrder() != null ? refund.getOrder().getId() : null)
                .orderNumber(refund.getOrderNumber())
                .customer(refund.getCustomerName())
                .customerName(refund.getCustomerName())
                .customerEmail(refund.getCustomerEmail())
                .method(refund.getMethod())
                .transactionRef(refund.getTransactionRef())
                .amount(refund.getAmount())
                .date(DATE_FORMATTER.format(refund.getProcessedDate()))
                .processedDate(refund.getProcessedDate())
                .status(refund.getStatus() != null ? refund.getStatus().getDisplayName() : "Completed")
                .statusCode(refund.getStatus())
                .reason(refund.getReason())
                .notes(refund.getNotes())
                .build();
    }
}
