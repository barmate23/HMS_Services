package com.hotelerp.userservice.controller;

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
    public ResponseEntity<Void> createOrder(@RequestBody PosOrderDTO dto) {
        posService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** PUT /updateOrder/{id} - update status, items, notes etc. */
    @PutMapping(ServiceConstant.UPDATE_ORDER)
    public ResponseEntity<PosOrderDTO> updateOrder(@PathVariable Long id, @RequestBody PosOrderDTO dto) {
        return ResponseEntity.ok(posService.updateOrder(id, dto));
    }

    /** GET /getOrderById/{id} */
    @GetMapping(ServiceConstant.GET_ORDER_BY_ID)
    public ResponseEntity<PosOrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(posService.getOrderById(id));
    }

    /** GET /getAllOrders?outletId= */
    @GetMapping(ServiceConstant.GET_ALL_ORDERS)
    public ResponseEntity<List<PosOrderDTO>> getAllOrders(
            @RequestParam(required = false) Long outletId) {
        return ResponseEntity.ok(posService.getOrdersByOutlet(outletId));
    }

    // ──────────────────────────────────────────
    //  TABLE BOOKING API
    // ──────────────────────────────────────────

    /** POST /bookTable - reserve a dining table */
    @PostMapping(ServiceConstant.BOOK_TABLE)
    public ResponseEntity<Void> bookTable(@RequestBody TableReservationDTO dto) {
        posService.bookTable(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** GET /getTableReservations/{tableId} */
    @GetMapping(ServiceConstant.GET_TABLE_RESERVATIONS)
    public ResponseEntity<List<TableReservationDTO>> getTableReservations(@PathVariable Long tableId) {
        return ResponseEntity.ok(posService.getReservationsByTable(tableId));
    }
}
