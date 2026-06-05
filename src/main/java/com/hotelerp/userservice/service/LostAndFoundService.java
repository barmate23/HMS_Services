package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.LostAndFoundDTO;
import java.util.List;

public interface LostAndFoundService {
    StandardResponse<Void> logItem(LostAndFoundDTO dto);
    StandardResponse<LostAndFoundDTO> updateItem(Long id, LostAndFoundDTO dto);
    StandardResponse<LostAndFoundDTO> getItemById(Long id);
    StandardResponse<List<LostAndFoundDTO>> getAllItems();
    StandardResponse<Void> deleteItem(Long id);
    StandardResponse<LostAndFoundDTO> updateStatus(Long id, String status);
}
