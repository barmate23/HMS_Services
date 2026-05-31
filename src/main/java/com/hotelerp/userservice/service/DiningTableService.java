package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.DiningTableDTO;
import java.util.List;

public interface DiningTableService {
    DiningTableDTO createTable(DiningTableDTO dto);
    DiningTableDTO updateTable(Long id, DiningTableDTO dto);
    DiningTableDTO getTableById(Long id);
    List<DiningTableDTO> getTablesByOutlet(Long outletId);
    List<DiningTableDTO> getAllTables();
    void deleteTable(Long id);
}
