package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.ItemConfigDTO;
import java.util.List;

public interface ItemConfigService {
    StandardResponse<ItemConfigDTO> createItem(ItemConfigDTO dto);
    StandardResponse<ItemConfigDTO> updateItem(Long id, ItemConfigDTO dto);
    StandardResponse<ItemConfigDTO> getItemById(Long id);
    StandardResponse<List<ItemConfigDTO>> getAllItems();
    StandardResponse<Void> deleteItem(Long id);
    StandardResponse<ItemConfigDTO> updateItemStatus(Long id, Boolean isActive);
}
