package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.constant.ServiceConstant;
import com.hotelerp.userservice.dto.PosOrderDTO;
import com.hotelerp.userservice.dto.TableReservationDTO;
import com.hotelerp.userservice.service.PosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * POS Controller handles four order flows:
 *  1. Table (Dine-In) orders  – tableId must be provided in the body
 *  2. Room Service orders     – roomId must be provided in the body
 *  3. Takeaway orders         – neither tableId nor roomId required
 *  4. Table Reservations / Bookings
 */
@RestController
@RequestMapping("/api/hmsService/v1/pos/orders")
@RequiredArgsConstructor
public class PosController {

    private final PosService posService;

    // ──────────────────────────────────────────
    //  ORDER APIs  (Table | Room | Takeaway)
    // ──────────────────────────────────────────

    /** POST /createOrder
     *  Creates a Table order (dine-in), Room Service order, or Takeaway order
     *  depending on the orderTypeId and presence of tableId / roomId in the body.
     */
    @PostMapping(ServiceConstant.CREATE_ORDER)
    public ResponseEntity<StandardResponse<Void>> createOrder(@RequestBody PosOrderDTO dto) {
        StandardResponse<Void> response = posService.createOrder(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    /** PUT /updateOrder/{id} - update status, items, notes etc. */
    @PutMapping(ServiceConstant.UPDATE_ORDER)
    public ResponseEntity<StandardResponse<PosOrderDTO>> updateOrder(@PathVariable Long id, @RequestBody PosOrderDTO dto) {
        StandardResponse<PosOrderDTO> response = posService.updateOrder(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    /** GET /getOrderById/{id} */
    @GetMapping(ServiceConstant.GET_ORDER_BY_ID)
    public ResponseEntity<StandardResponse<PosOrderDTO>> getOrderById(@PathVariable Long id) {
        StandardResponse<PosOrderDTO> response = posService.getOrderById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    /** GET /getAllOrders?outletId= */
    @GetMapping(ServiceConstant.GET_ALL_ORDERS)
    public ResponseEntity<StandardResponse<List<PosOrderDTO>>> getAllOrders(
            @RequestParam(required = false) Long outletId) {
        StandardResponse<List<PosOrderDTO>> response = posService.getOrdersByOutlet(outletId);
        return ResponseEntity.ok(response);
    }

    // ──────────────────────────────────────────
    //  TABLE BOOKING API
    // ──────────────────────────────────────────

    /** POST /bookTable - reserve a dining table */
    @PostMapping(ServiceConstant.BOOK_TABLE)
    public ResponseEntity<StandardResponse<Void>> bookTable(@RequestBody TableReservationDTO dto) {
        StandardResponse<Void> response = posService.bookTable(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    /** GET /getTableReservations/{tableId} */
    @GetMapping(ServiceConstant.GET_TABLE_RESERVATIONS)
    public ResponseEntity<StandardResponse<List<TableReservationDTO>>> getTableReservations(@PathVariable Long tableId) {
        StandardResponse<List<TableReservationDTO>> response = posService.getReservationsByTable(tableId);
        return ResponseEntity.ok(response);
    }
}
