package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PosOrderDTO;
import com.hotelerp.userservice.dto.TableReservationDTO;
import java.util.List;

public interface PosService {
    // Order APIs
    StandardResponse<Void> createOrder(PosOrderDTO dto);
    StandardResponse<PosOrderDTO> updateOrder(Long id, PosOrderDTO dto);
    StandardResponse<PosOrderDTO> getOrderById(Long id);
    StandardResponse<List<PosOrderDTO>> getOrdersByOutlet(Long outletId);
    
    // Booking API
    StandardResponse<Void> bookTable(TableReservationDTO dto);
    StandardResponse<List<TableReservationDTO>> getReservationsByTable(Long tableId);
}
