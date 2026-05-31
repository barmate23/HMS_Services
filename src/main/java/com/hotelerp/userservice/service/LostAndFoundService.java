package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.LostAndFoundDTO;
import java.util.List;

public interface LostAndFoundService {
    LostAndFoundDTO logItem(LostAndFoundDTO dto);
    LostAndFoundDTO updateItem(Long id, LostAndFoundDTO dto);
    LostAndFoundDTO getItemById(Long id);
    List<LostAndFoundDTO> getAllItems();
    void deleteItem(Long id);
    LostAndFoundDTO updateStatus(Long id, String status);
}
