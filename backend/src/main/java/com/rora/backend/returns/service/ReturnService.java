package com.rora.backend.returns.service;

import com.rora.backend.returns.dto.*;
import com.rora.backend.returns.entity.InspectionStatus;
import com.rora.backend.returns.entity.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReturnService {

    ReturnRecordDto createReturnRequest(CreateReturnRequest request, String authenticatedEmail);

    ReturnRecordDto getReturnById(String id);

    List<ReturnRecordDto> getReturnsByCustomer(String customerEmail);

    List<ReturnRecordDto> getReturnsByOrder(String orderIdOrNumber);

    Page<ReturnRecordDto> searchReturns(String query, ReturnStatus status, Pageable pageable);

    ReturnRecordDto approveReturn(String returnId, ApproveReturnRequest request, String adminUser);

    ReturnRecordDto rejectReturn(String returnId, RejectReturnRequest request, String adminUser);

    ReturnRecordDto updateInspectionStatus(String returnId, InspectionStatus status, String notes);

    ReturnSummaryDto getReturnSummary();
}
