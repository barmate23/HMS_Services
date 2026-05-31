package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.OutletDTO;
import java.util.List;

public interface OutletService {
    OutletDTO createOutlet(OutletDTO dto);
    OutletDTO updateOutlet(Long id, OutletDTO dto);
    OutletDTO getOutletById(Long id);
    List<OutletDTO> getAllOutlets();
    void deleteOutlet(Long id);
}
