package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PurchaseOrderDTO;
import java.util.List;

public interface PurchaseOrderService {
    StandardResponse<PurchaseOrderDTO> createPurchaseOrder(PurchaseOrderDTO dto);
    StandardResponse<PurchaseOrderDTO> updatePurchaseOrder(Long id, PurchaseOrderDTO dto);
    StandardResponse<PurchaseOrderDTO> getPurchaseOrderById(Long id);
    StandardResponse<List<PurchaseOrderDTO>> getAllPurchaseOrders();
    StandardResponse<Void> deletePurchaseOrder(Long id);
    StandardResponse<PurchaseOrderDTO> updateStatus(Long id, Long statusId);
}
