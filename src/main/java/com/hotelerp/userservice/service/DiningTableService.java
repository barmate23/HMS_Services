package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.DiningTableDTO;
import java.util.List;

public interface DiningTableService {
    StandardResponse<Void> createTable(DiningTableDTO dto);
    StandardResponse<DiningTableDTO> updateTable(Long id, DiningTableDTO dto);
    StandardResponse<DiningTableDTO> getTableById(Long id);
    StandardResponse<List<DiningTableDTO>> getTablesByOutlet(Long outletId);
    StandardResponse<List<DiningTableDTO>> getAllTables();
    StandardResponse<Void> deleteTable(Long id);
}
