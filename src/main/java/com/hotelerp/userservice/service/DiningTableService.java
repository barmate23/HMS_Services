package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.DiningTableDTO;
import com.hotelerp.userservice.dto.DiningTableWithoutOutletDTO;
import java.util.List;

public interface DiningTableService {
    StandardResponse<Void> createTable(DiningTableDTO dto);
    StandardResponse<DiningTableDTO> updateTable(Long id, DiningTableDTO dto);
    StandardResponse<DiningTableDTO> getTableById(Long id);
    StandardResponse<List<DiningTableWithoutOutletDTO>> getAllTables(Long outletId);
    StandardResponse<Void> deleteTable(Long id);
}
