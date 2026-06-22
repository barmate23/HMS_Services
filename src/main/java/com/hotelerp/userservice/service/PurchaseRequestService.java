package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PurchaseRequestDTO;
import java.util.List;

public interface PurchaseRequestService {
    StandardResponse<PurchaseRequestDTO> createPurchaseRequest(PurchaseRequestDTO dto);
    StandardResponse<PurchaseRequestDTO> updatePurchaseRequest(Long id, PurchaseRequestDTO dto);
    StandardResponse<PurchaseRequestDTO> getPurchaseRequestById(Long id);
    StandardResponse<List<PurchaseRequestDTO>> getAllPurchaseRequests();
    StandardResponse<Void> deletePurchaseRequest(Long id);
    StandardResponse<PurchaseRequestDTO> updateStatus(Long id, Long statusId);
}
