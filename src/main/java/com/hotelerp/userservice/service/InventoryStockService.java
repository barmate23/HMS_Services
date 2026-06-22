package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.InventoryStockDTO;
import java.util.List;

public interface InventoryStockService {
    StandardResponse<InventoryStockDTO> createStockItem(InventoryStockDTO dto);
    StandardResponse<InventoryStockDTO> updateStockItem(Long id, InventoryStockDTO dto);
    StandardResponse<InventoryStockDTO> getStockItemById(Long id);
    StandardResponse<List<InventoryStockDTO>> getAllStockItems();
    StandardResponse<Void> deleteStockItem(Long id);
    StandardResponse<InventoryStockDTO> updateStockStatus(Long id, Long statusId);
}
