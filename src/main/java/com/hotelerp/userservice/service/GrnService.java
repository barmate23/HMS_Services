package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.GrnDTO;
import java.util.List;

public interface GrnService {
    StandardResponse<GrnDTO> createGrn(GrnDTO dto);
    StandardResponse<GrnDTO> updateGrn(Long id, GrnDTO dto);
    StandardResponse<GrnDTO> getGrnById(Long id);
    StandardResponse<List<GrnDTO>> getAllGrns();
    StandardResponse<Void> deleteGrn(Long id);
}
