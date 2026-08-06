package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.constant.ServiceConstant;
import com.hotelerp.userservice.dto.KitchenOrderCardDTO;
import com.hotelerp.userservice.dto.PosOrderDTO;
import com.hotelerp.userservice.dto.TableReservationDTO;
import com.hotelerp.userservice.service.KitchenSseService;
import com.hotelerp.userservice.service.PosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    private final KitchenSseService kitchenSseService;

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

    /** PATCH /updateOrderStatus/{id}?statusId= – update Order Status (OPEN → BILLED / CLOSED) */
    @PatchMapping(ServiceConstant.UPDATE_POS_ORDER_STATUS)
    public ResponseEntity<StandardResponse<PosOrderDTO>> updateOrderStatus(@PathVariable Long id, @RequestParam Long statusId) {
        StandardResponse<PosOrderDTO> response = posService.updateOrderStatus(id, statusId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    /** PATCH /updateKotStatus/{id}?kotStatusId= – update KOT Status via CommonMaster ID (KOT_STATUS category) */
    @PatchMapping(ServiceConstant.UPDATE_KOT_STATUS)
    public ResponseEntity<StandardResponse<PosOrderDTO>> updateKotStatus(@PathVariable Long id,
                                                                          @RequestParam Long kotStatusId) {
        StandardResponse<PosOrderDTO> response = posService.updateKotStatus(id, kotStatusId);
        HttpStatus httpStatus = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * PATCH /updateItemKotStatus/{orderId}/item/{itemId}?kotStatusId=
     *
     * Updates the KOT status of a single item within an order.
     * After the update:
     *   - If KOT_READY → readyQuantity is set to item quantity automatically.
     *   - Order-level kotStatus is recalculated using the least-status rule:
     *       KOT_SEND < IN_PROGRESS < KOT_READY
     *     (if ANY item still has KOT_SEND, order stays KOT_SEND)
     *   - SSE broadcast is sent to all KDS screens.
     */
    @PatchMapping(ServiceConstant.UPDATE_ITEM_KOT_STATUS)
    public ResponseEntity<StandardResponse<PosOrderDTO>> updateItemKotStatus(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestParam Long kotStatusId) {
        StandardResponse<PosOrderDTO> response = posService.updateItemKotStatus(orderId, itemId, kotStatusId);
        HttpStatus httpStatus = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(httpStatus).body(response);
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

    @GetMapping(ServiceConstant.GET_ACTIVE_POS_ORDERS)
    public ResponseEntity<StandardResponse<List<PosOrderDTO>>> getActiveOrders(
            @RequestParam(required = false) Long tableId) {
        StandardResponse<List<PosOrderDTO>> response = posService.getActiveOrders(tableId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(ServiceConstant.GET_OPEN_POS_ORDERS)
    public ResponseEntity<StandardResponse<List<PosOrderDTO>>> getOpenOrders(
            @RequestParam(required = false) Long outletId) {
        StandardResponse<List<PosOrderDTO>> response = posService.getOpenOrders(outletId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(ServiceConstant.GET_KITCHEN_ORDERS)
    public ResponseEntity<StandardResponse<List<KitchenOrderCardDTO>>> getKitchenOrders(
            @RequestParam(required = false) Long outletId,
            @RequestParam(required = false, defaultValue = "false") Boolean isClosed) {
        StandardResponse<List<KitchenOrderCardDTO>> response = posService.getKitchenOrders(outletId, isClosed);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /getKitchenOrdersStream?outletId=
     *
     * Server-Sent Events (SSE) endpoint for the Kitchen Display System.
     * Connect once and receive real-time push events:
     *   - "connected"           – initial handshake
     *   - "NEW_ORDER"           – a new order was placed
     *   - "KOT_STATUS_CHANGED"  – KOT status updated on an existing order
     *   - "ORDER_UPDATED"       – order items / details modified
     *
     * Each event data payload is the same JSON array returned by GET /getKitchenOrders
     * (active cards only, isClosed=false).
     *
     * Usage (JavaScript):
     *   const es = new EventSource('/api/hmsService/v1/pos/orders/getKitchenOrdersStream?outletId=1');
     *   es.addEventListener('NEW_ORDER', e => setCards(JSON.parse(e.data)));
     *   es.addEventListener('KOT_STATUS_CHANGED', e => setCards(JSON.parse(e.data)));
     */
    @GetMapping(value = ServiceConstant.GET_KITCHEN_ORDERS_STREAM, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamKitchenOrders(@RequestParam(required = false) Long outletId) {
        return kitchenSseService.subscribe(outletId);
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
