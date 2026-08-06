package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PosOrderDTO;
import com.hotelerp.userservice.dto.TableReservationDTO;
import java.util.List;

public interface PosService {
    // Order APIs
    StandardResponse<Void> createOrder(PosOrderDTO dto);
    StandardResponse<PosOrderDTO> updateOrder(Long id, PosOrderDTO dto);
    StandardResponse<PosOrderDTO> updateOrderStatus(Long id, Long statusId);
    StandardResponse<PosOrderDTO> updateKotStatus(Long id, Long kotStatusId);
    StandardResponse<PosOrderDTO> getOrderById(Long id);
    StandardResponse<List<PosOrderDTO>> getActiveOrders(Long tableId);
    StandardResponse<List<PosOrderDTO>> getOrdersByOutlet(Long outletId);
    StandardResponse<List<PosOrderDTO>> getOpenOrders(Long outletId);
    StandardResponse<List<PosOrderDTO>> getKitchenOrders(Long outletId, Boolean isClosed);
    
    // Booking API
    StandardResponse<Void> bookTable(TableReservationDTO dto);
    StandardResponse<List<TableReservationDTO>> getReservationsByTable(Long tableId);
}
