package com.rora.backend.returns.service;

import com.rora.backend.returns.dto.CreateRefundRequest;
import com.rora.backend.returns.dto.RefundRecordDto;
import com.rora.backend.returns.entity.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RefundService {

    RefundRecordDto createRefund(CreateRefundRequest request, String adminUser);

    RefundRecordDto getRefundById(String id);

    RefundRecordDto getRefundByReturnId(String returnId);

    List<RefundRecordDto> getRefundsByOrder(String orderIdOrNumber);

    Page<RefundRecordDto> searchRefunds(String query, RefundStatus status, Pageable pageable);
}
