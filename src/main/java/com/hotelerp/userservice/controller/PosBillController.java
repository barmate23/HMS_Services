package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.constant.ServiceConstant;
import com.hotelerp.userservice.dto.PosBillDTO;
import com.hotelerp.userservice.service.PosBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for POS Billing.
 *
 * Base URL : /api/hmsService/v1/pos/billing
 *
 * Supported flows:
 *  - Create a bill for a POS order (with optional folio posting)
 *  - Update bill details (discount, payment method, notes)
 *  - Void a bill with a comp/void reason
 *  - Soft-delete a bill
 *  - Fetch bill(s) by various criteria
 *
 * CommonMaster seeding required:
 *  category=BILL_STATUS      → codes: OPEN, SETTLED, VOID
 *  category=PAYMENT_MODE     → codes: CASH, CARD, UPI, ROOM_CHARGE, COMPLIMENTARY
 *  category=COMP_VOID_REASON → codes: COMP, MANAGER_VOID, WRONG_ORDER, …
 */
@RestController
@RequestMapping("/api/hmsService/v1/pos/billing")
@RequiredArgsConstructor
public class PosBillController {

    private final PosBillService posBillService;

    // ──────────────────────────────────────────────────────────────────────────
    //  CREATE
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * POST /createBill
     * Body: { orderId, paymentMethodId, compVoidReasonId, discount, paidAmount,
     *         postToFolio, notes }
     * - grossAmount is taken from the POS Order's totalAmount.
     * - netAmount  = grossAmount - discount.
     * - If postToFolio = true, netAmount is posted to the room's active folio.
     * - Order status is automatically flipped to BILLED on success.
     */
    @PostMapping(ServiceConstant.CREATE_BILL)
    public ResponseEntity<StandardResponse<PosBillDTO>> createBill(@RequestBody PosBillDTO dto) {
        StandardResponse<PosBillDTO> response = posBillService.createBill(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  READ
    // ──────────────────────────────────────────────────────────────────────────

    /** GET /getBillById/{id} */
    @GetMapping(ServiceConstant.GET_BILL_BY_ID)
    public ResponseEntity<StandardResponse<PosBillDTO>> getBillById(@PathVariable Long id) {
        StandardResponse<PosBillDTO> response = posBillService.getBillById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    /** GET /getBillByOrderId/{orderId} */
    @GetMapping(ServiceConstant.GET_BILL_BY_ORDER_ID)
    public ResponseEntity<StandardResponse<PosBillDTO>> getBillByOrderId(@PathVariable Long orderId) {
        StandardResponse<PosBillDTO> response = posBillService.getBillByOrderId(orderId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * GET /getAllBills?outletId=
     * Returns all non-deleted bills. Optionally filtered by outlet.
     */
    @GetMapping(ServiceConstant.GET_ALL_BILLS)
    public ResponseEntity<StandardResponse<List<PosBillDTO>>> getAllBills(
            @RequestParam(required = false) Long outletId) {
        StandardResponse<List<PosBillDTO>> response = posBillService.getAllBills(outletId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /getBillsByStatus?statusCode=OPEN|SETTLED|VOID
     */
    @GetMapping(ServiceConstant.GET_BILLS_BY_STATUS)
    public ResponseEntity<StandardResponse<List<PosBillDTO>>> getBillsByStatus(
            @RequestParam String statusCode) {
        StandardResponse<List<PosBillDTO>> response = posBillService.getBillsByStatus(statusCode);
        return ResponseEntity.ok(response);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  UPDATE
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * PUT /updateBill/{id}
     * Updatable fields: paymentMethodId, statusId, compVoidReasonId, discount, paidAmount, notes
     */
    @PutMapping(ServiceConstant.UPDATE_BILL)
    public ResponseEntity<StandardResponse<PosBillDTO>> updateBill(
            @PathVariable Long id, @RequestBody PosBillDTO dto) {
        StandardResponse<PosBillDTO> response = posBillService.updateBill(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  VOID
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * PATCH /voidBill/{id}?compVoidReasonId=
     * Sets bill status to VOID and attaches the comp/void reason.
     */
    @PatchMapping(ServiceConstant.VOID_BILL)
    public ResponseEntity<StandardResponse<PosBillDTO>> voidBill(
            @PathVariable Long id,
            @RequestParam(required = false) Long compVoidReasonId) {
        StandardResponse<PosBillDTO> response = posBillService.voidBill(id, compVoidReasonId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  DELETE
    // ──────────────────────────────────────────────────────────────────────────

    /** DELETE /deleteBill/{id} – soft deletes the bill */
    @DeleteMapping(ServiceConstant.DELETE_BILL)
    public ResponseEntity<StandardResponse<Void>> deleteBill(@PathVariable Long id) {
        StandardResponse<Void> response = posBillService.deleteBill(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
