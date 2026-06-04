package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.PosOrderDTO;
import com.hotelerp.userservice.dto.TableReservationDTO;
import java.util.List;

public interface PosService {
    // Order APIs
    PosOrderDTO createOrder(PosOrderDTO dto);
    PosOrderDTO updateOrder(Long id, PosOrderDTO dto);
    PosOrderDTO getOrderById(Long id);
    List<PosOrderDTO> getOrdersByOutlet(Long outletId);
    
    // Booking API
    TableReservationDTO bookTable(TableReservationDTO dto);
    List<TableReservationDTO> getReservationsByTable(Long tableId);
}
