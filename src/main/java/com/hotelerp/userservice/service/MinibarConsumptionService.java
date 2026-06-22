package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.MinibarConsumptionDTO;
import java.util.List;

public interface MinibarConsumptionService {
    StandardResponse<MinibarConsumptionDTO> postConsumption(MinibarConsumptionDTO dto);
    StandardResponse<MinibarConsumptionDTO> updateConsumption(Long id, MinibarConsumptionDTO dto);
    StandardResponse<MinibarConsumptionDTO> getConsumptionById(Long id);
    StandardResponse<List<MinibarConsumptionDTO>> getAllConsumptions();
    StandardResponse<Void> deleteConsumption(Long id);
}
