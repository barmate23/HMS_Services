package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.OutletDTO;
import java.util.List;

public interface OutletService {
    StandardResponse<Void> createOutlet(OutletDTO dto);
    StandardResponse<OutletDTO> updateOutlet(Long id, OutletDTO dto);
    StandardResponse<OutletDTO> getOutletById(Long id);
    StandardResponse<List<OutletDTO>> getAllOutlets();
    StandardResponse<Void> deleteOutlet(Long id);
}
