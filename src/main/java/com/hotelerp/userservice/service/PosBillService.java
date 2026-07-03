package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PosBillDTO;

import java.util.List;

public interface PosBillService {

    /** Create a new bill for a POS order. If postToFolio=true, charges are posted to room folio. */
    StandardResponse<PosBillDTO> createBill(PosBillDTO dto);

    /** Update an existing bill (discount, paymentMethod, notes, status). */
    StandardResponse<PosBillDTO> updateBill(Long id, PosBillDTO dto);

    /** Void / cancel a bill */
    StandardResponse<PosBillDTO> voidBill(Long id, Long compVoidReasonId);

    /** Get bill by ID */
    StandardResponse<PosBillDTO> getBillById(Long id);

    /** Get bill by Order ID */
    StandardResponse<PosBillDTO> getBillByOrderId(Long orderId);

    /** Get all bills (optionally filtered by outletId) */
    StandardResponse<List<PosBillDTO>> getAllBills(Long outletId);

    /** Get bills by status code, e.g. SETTLED, OPEN, VOID */
    StandardResponse<List<PosBillDTO>> getBillsByStatus(String statusCode);

    /** Soft-delete a bill */
    StandardResponse<Void> deleteBill(Long id);
}
