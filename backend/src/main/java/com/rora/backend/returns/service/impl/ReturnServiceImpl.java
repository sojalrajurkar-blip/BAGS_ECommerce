package com.rora.backend.returns.service.impl;

import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderItem;
import com.rora.backend.order.entity.OrderTimelineEvent;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.returns.dto.*;
import com.rora.backend.returns.entity.*;
import com.rora.backend.returns.repository.RefundRepository;
import com.rora.backend.returns.repository.ReturnItemRepository;
import com.rora.backend.returns.repository.ReturnRepository;
import com.rora.backend.returns.service.RefundService;
import com.rora.backend.returns.service.ReturnService;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;
    private final ReturnItemRepository returnItemRepository;
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final RefundService refundService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.of("Asia/Kolkata"));

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a").withZone(ZoneId.of("Asia/Kolkata"));

    @Override
    @Transactional
    public ReturnRecordDto createReturnRequest(CreateReturnRequest request, String authenticatedEmail) {
        log.info("Creating return request for order: {}", request.getOrderIdOrNumber());

        Order order = findOrder(request.getOrderIdOrNumber());

        // Validate order status
        if ("Cancelled".equalsIgnoreCase(order.getStatus())) {
            throw new BadRequestException("Cannot request a return for a cancelled order");
        }

        // Build items and calculate amount
        List<ReturnItem> returnItems = new ArrayList<>();
        BigDecimal returnAmount = BigDecimal.ZERO;
        String primaryItemName = null;

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (ReturnItemRequest itemReq : request.getItems()) {
                OrderItem orderItem = null;
                if (itemReq.getOrderItemId() != null && order.getItems() != null) {
                    orderItem = order.getItems().stream()
                            .filter(oi -> oi.getId().equals(itemReq.getOrderItemId()))
                            .findFirst()
                            .orElse(null);
                }

                BigDecimal unitPrice = orderItem != null ? orderItem.getUnitPrice() : BigDecimal.valueOf(2899.00);
                BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                returnAmount = returnAmount.add(totalPrice);

                String name = (itemReq.getProductName() != null && !itemReq.getProductName().trim().isEmpty())
                        ? itemReq.getProductName().trim()
                        : (orderItem != null && orderItem.getProductName() != null ? orderItem.getProductName() : "Luxury Item");
                String color = (itemReq.getColorName() != null && !itemReq.getColorName().trim().isEmpty())
                        ? itemReq.getColorName().trim()
                        : (orderItem != null ? orderItem.getColorName() : null);

                if (color != null && !color.isEmpty()) {
                    name += " (" + color + ")";
                }
                if (primaryItemName == null) {
                    primaryItemName = name;
                }

                ReturnItem ri = ReturnItem.builder()
                        .orderItemId(itemReq.getOrderItemId())
                        .productId(orderItem != null && orderItem.getProduct() != null ? orderItem.getProduct().getId() : null)
                        .variantId(orderItem != null && orderItem.getVariant() != null ? orderItem.getVariant().getId() : null)
                        .productName(name)
                        .colorName(color)
                        .quantity(itemReq.getQuantity())
                        .unitPrice(unitPrice)
                        .totalPrice(totalPrice)
                        .reason(itemReq.getReason() != null ? itemReq.getReason() : request.getReason())
                        .build();
                returnItems.add(ri);
            }
        } else {
            // Default to entire order items
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                for (OrderItem oi : order.getItems()) {
                    String name = oi.getProductName();
                    if (oi.getColorName() != null && !oi.getColorName().trim().isEmpty()) {
                        name += " (" + oi.getColorName() + ")";
                    }
                    if (primaryItemName == null) {
                        primaryItemName = name;
                    }

                    ReturnItem ri = ReturnItem.builder()
                            .orderItemId(oi.getId())
                            .productId(oi.getProduct() != null ? oi.getProduct().getId() : null)
                            .variantId(oi.getVariant() != null ? oi.getVariant().getId() : null)
                            .productName(oi.getProductName())
                            .colorName(oi.getColorName())
                            .quantity(oi.getQuantity())
                            .unitPrice(oi.getUnitPrice())
                            .totalPrice(oi.getTotalPrice())
                            .reason(request.getReason())
                            .build();
                    returnItems.add(ri);
                    returnAmount = returnAmount.add(oi.getTotalPrice());
                }
            } else {
                primaryItemName = "Luxury Item";
                returnAmount = order.getTotal() != null ? order.getTotal() : BigDecimal.valueOf(4899.00);
            }
        }

        if (primaryItemName == null) {
            primaryItemName = "Luxury Leather Goods";
        }

        ReturnRequest returnRequest = ReturnRequest.builder()
                .order(order)
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .item(primaryItemName)
                .reason(request.getReason())
                .customerNotes(request.getCustomerNotes())
                .requestDate(Instant.now())
                .inspectionStatus(InspectionStatus.AWAITING_HUB_DELIVERY)
                .status(ReturnStatus.UNDER_REVIEW)
                .amount(returnAmount)
                .build();

        for (ReturnItem ri : returnItems) {
            returnRequest.addItem(ri);
        }

        ReturnRequest savedReturn = returnRepository.save(returnRequest);

        // Add Order Timeline Event
        OrderTimelineEvent timelineEvent = OrderTimelineEvent.builder()
                .order(order)
                .stepName("Return Requested")
                .title("Return Requested")
                .description("Return request (" + savedReturn.getId() + ") initiated for " + primaryItemName + ". Reason: " + request.getReason())
                .eventTime(DATE_TIME_FORMATTER.format(Instant.now()))
                .completed(true)
                .displayOrder(6)
                .build();
        order.addTimelineEvent(timelineEvent);
        orderRepository.save(order);

        log.info("Successfully created return request ID: {} for order: {}", savedReturn.getId(), order.getOrderNumber());
        return mapToDto(savedReturn);
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnRecordDto getReturnById(String id) {
        ReturnRequest returnRequest = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with ID: " + id));
        return mapToDto(returnRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRecordDto> getReturnsByCustomer(String customerEmail) {
        List<ReturnRequest> list = returnRepository.findByCustomerEmailIgnoreCase(customerEmail.trim());
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRecordDto> getReturnsByOrder(String orderIdOrNumber) {
        String clean = orderIdOrNumber.trim();
        List<ReturnRequest> list = returnRepository.findByOrderNumber(clean);
        if (list.isEmpty() && !clean.startsWith("#")) {
            list = returnRepository.findByOrderNumber("#" + clean);
        }
        if (list.isEmpty()) {
            list = returnRepository.findByOrderId(clean);
        }
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReturnRecordDto> searchReturns(String query, ReturnStatus status, Pageable pageable) {
        String cleanQuery = query != null ? query.trim() : null;
        Page<ReturnRequest> page = returnRepository.searchReturns(cleanQuery, status, pageable);
        return page.map(this::mapToDto);
    }

    @Override
    @Transactional
    public ReturnRecordDto approveReturn(String returnId, ApproveReturnRequest request, String adminUser) {
        ReturnRequest returnRequest = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with ID: " + returnId));

        // State transition validation
        if (returnRequest.getStatus() == ReturnStatus.APPROVED_AND_REFUNDED ||
            returnRequest.getStatus() == ReturnStatus.REJECTED ||
            returnRequest.getStatus() == ReturnStatus.CANCELLED) {
            throw new BadRequestException("Cannot approve return in status: " + returnRequest.getStatus());
        }

        InspectionStatus inspection = request.getInspectionStatus() != null
                ? request.getInspectionStatus()
                : InspectionStatus.PASSED_PRISTINE;

        returnRequest.setInspectionStatus(inspection);
        returnRequest.setAdminNotes(request.getNotes());

        BigDecimal refundAmount = request.getCustomRefundAmount() != null
                ? request.getCustomRefundAmount()
                : returnRequest.getAmount();

        if (request.isAutoRefund()) {
            returnRequest.setStatus(ReturnStatus.APPROVED_AND_REFUNDED);

            // Execute refund via refund service
            CreateRefundRequest refundReq = CreateRefundRequest.builder()
                    .returnId(returnRequest.getId())
                    .orderIdOrNumber(returnRequest.getOrderNumber())
                    .amount(refundAmount)
                    .method(request.getRefundMethod() != null ? request.getRefundMethod() : "Original Payment Source")
                    .reason("Return " + returnRequest.getId() + " approved: " + inspection.getDisplayName())
                    .notes(request.getNotes())
                    .build();

            refundService.createRefund(refundReq, adminUser);
        } else {
            returnRequest.setStatus(ReturnStatus.APPROVED);
        }

        ReturnRequest saved = returnRepository.save(returnRequest);

        // Update Order Timeline
        Order order = returnRequest.getOrder();
        if (order != null) {
            OrderTimelineEvent event = OrderTimelineEvent.builder()
                    .order(order)
                    .stepName("Return Approved")
                    .title("Return Approved")
                    .description("Return " + returnRequest.getId() + " approved by concierge team (" + inspection.getDisplayName() + ").")
                    .eventTime(DATE_TIME_FORMATTER.format(Instant.now()))
                    .completed(true)
                    .displayOrder(7)
                    .build();
            order.addTimelineEvent(event);
            orderRepository.save(order);
        }

        log.info("Admin {} approved return ID: {} (Status: {})", adminUser, saved.getId(), saved.getStatus());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ReturnRecordDto rejectReturn(String returnId, RejectReturnRequest request, String adminUser) {
        ReturnRequest returnRequest = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with ID: " + returnId));

        // State transition validation
        if (returnRequest.getStatus() == ReturnStatus.APPROVED_AND_REFUNDED ||
            returnRequest.getStatus() == ReturnStatus.REJECTED ||
            returnRequest.getStatus() == ReturnStatus.CANCELLED) {
            throw new BadRequestException("Cannot reject return in status: " + returnRequest.getStatus());
        }

        InspectionStatus inspection = request.getInspectionStatus() != null
                ? request.getInspectionStatus()
                : InspectionStatus.FAILED_POLICY_CHECK;

        returnRequest.setStatus(ReturnStatus.REJECTED);
        returnRequest.setInspectionStatus(inspection);
        returnRequest.setAdminNotes(request.getReason() + (request.getNotes() != null ? " - " + request.getNotes() : ""));

        ReturnRequest saved = returnRepository.save(returnRequest);

        // Update Order Timeline
        Order order = returnRequest.getOrder();
        if (order != null) {
            OrderTimelineEvent event = OrderTimelineEvent.builder()
                    .order(order)
                    .stepName("Return Rejected")
                    .title("Return Rejected")
                    .description("Return " + returnRequest.getId() + " rejected. Reason: " + request.getReason())
                    .eventTime(DATE_TIME_FORMATTER.format(Instant.now()))
                    .completed(true)
                    .displayOrder(7)
                    .build();
            order.addTimelineEvent(event);
            orderRepository.save(order);
        }

        log.info("Admin {} rejected return ID: {}. Reason: {}", adminUser, saved.getId(), request.getReason());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ReturnRecordDto updateInspectionStatus(String returnId, InspectionStatus status, String notes) {
        ReturnRequest returnRequest = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found with ID: " + returnId));

        returnRequest.setInspectionStatus(status);
        if (notes != null && !notes.trim().isEmpty()) {
            returnRequest.setAdminNotes(notes);
        }
        ReturnRequest saved = returnRepository.save(returnRequest);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnSummaryDto getReturnSummary() {
        long total = returnRepository.count();
        long requested = returnRepository.countByStatus(ReturnStatus.REQUESTED);
        long underReview = returnRepository.countByStatus(ReturnStatus.UNDER_REVIEW);
        long approved = returnRepository.countByStatus(ReturnStatus.APPROVED);
        long receivedAtHub = returnRepository.countByStatus(ReturnStatus.RECEIVED_AT_HUB);
        long approvedAndRefunded = returnRepository.countByStatus(ReturnStatus.APPROVED_AND_REFUNDED);
        long rejected = returnRepository.countByStatus(ReturnStatus.REJECTED);
        long cancelled = returnRepository.countByStatus(ReturnStatus.CANCELLED);
        BigDecimal totalRefunded = refundRepository.sumTotalRefundedAmount();

        return ReturnSummaryDto.builder()
                .totalReturns(total)
                .requested(requested)
                .underReview(underReview)
                .approved(approved)
                .receivedAtHub(receivedAtHub)
                .approvedAndRefunded(approvedAndRefunded)
                .rejected(rejected)
                .cancelled(cancelled)
                .totalRefundedAmount(totalRefunded != null ? totalRefunded : BigDecimal.ZERO)
                .build();
    }

    private Order findOrder(String orderIdOrNumber) {
        String clean = orderIdOrNumber.trim();
        return orderRepository.findById(clean)
                .or(() -> orderRepository.findByOrderNumber(clean))
                .or(() -> orderRepository.findByOrderNumber("#" + clean))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with identifier: " + orderIdOrNumber));
    }

    private ReturnRecordDto mapToDto(ReturnRequest returnRequest) {
        List<ReturnItemDto> itemDtos = returnRequest.getItems() != null
                ? returnRequest.getItems().stream().map(this::mapItemToDto).collect(Collectors.toList())
                : new ArrayList<>();

        Optional<RefundRecord> refundOpt = refundRepository.findByReturnRequestId(returnRequest.getId());
        String refundId = refundOpt.map(RefundRecord::getId).orElse(null);

        return ReturnRecordDto.builder()
                .id(returnRequest.getId())
                .orderId(returnRequest.getOrder() != null ? returnRequest.getOrder().getId() : null)
                .orderNumber(returnRequest.getOrderNumber())
                .customer(returnRequest.getCustomerName())
                .customerName(returnRequest.getCustomerName())
                .customerEmail(returnRequest.getCustomerEmail())
                .customerPhone(returnRequest.getCustomerPhone())
                .item(returnRequest.getItem())
                .reason(returnRequest.getReason())
                .customerNotes(returnRequest.getCustomerNotes())
                .inspectionStatus(returnRequest.getInspectionStatus() != null ? returnRequest.getInspectionStatus().getDisplayName() : "Pending Delivery")
                .inspectionStatusCode(returnRequest.getInspectionStatus())
                .amount(returnRequest.getAmount())
                .status(returnRequest.getStatus() != null ? returnRequest.getStatus().getDisplayName() : "Under Review")
                .statusCode(returnRequest.getStatus())
                .requestDate(returnRequest.getRequestDate())
                .formattedRequestDate(returnRequest.getRequestDate() != null ? DATE_FORMATTER.format(returnRequest.getRequestDate()) : null)
                .adminNotes(returnRequest.getAdminNotes())
                .refundId(refundId)
                .createdAt(returnRequest.getCreatedAt())
                .updatedAt(returnRequest.getUpdatedAt())
                .items(itemDtos)
                .build();
    }

    private ReturnItemDto mapItemToDto(ReturnItem item) {
        return ReturnItemDto.builder()
                .id(item.getId())
                .orderItemId(item.getOrderItemId())
                .productId(item.getProductId())
                .variantId(item.getVariantId())
                .productName(item.getProductName())
                .colorName(item.getColorName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .reason(item.getReason())
                .conditionNotes(item.getConditionNotes())
                .build();
    }
}
