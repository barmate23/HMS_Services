package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.CommonMasterDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonMasterServiceImpl implements CommonMasterService {

    private final CommonMasterRepository repository;

    @Override
    public StandardResponse<CommonMasterDTO> saveCommonMaster(CommonMasterDTO dto) {
        try {
            CommonMaster entity;
            if (dto.getId() != null) {
                entity = repository.findById(dto.getId())
                        .orElseThrow(() -> new RuntimeException("Common Master not found"));
            } else {
                entity = new CommonMaster();
            }

            entity.setCategory(dto.getCategory());
            entity.setCode(dto.getCode());
            entity.setValue(dto.getValue());
            entity.setDescription(dto.getDescription());
            entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

            entity = repository.save(entity);
            return StandardResponse.success(convertToDTO(entity), "Common Master saved successfully");
        } catch (Exception e) {
            log.error("Error saving Common Master: ", e);
            return StandardResponse.error("Failed to save Common Master", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<CommonMasterDTO> updateCommonMasterData(CommonMasterDTO dto) {
        try {
            if (dto.getId() == null) {
                return StandardResponse.error("Common Master id is required", "BAD_REQUEST", "id", "id must be provided");
            }

            CommonMaster entity = repository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Common Master not found"));

            entity.setCategory(dto.getCategory());
            entity.setCode(dto.getCode());
            entity.setValue(dto.getValue());
            entity.setDescription(dto.getDescription());
            if (dto.getIsActive() != null) {
                entity.setIsActive(dto.getIsActive());
            }

            entity = repository.save(entity);
            return StandardResponse.success(convertToDTO(entity), "Common Master updated successfully");
        } catch (Exception e) {
            log.error("Error updating Common Master: ", e);
            return StandardResponse.error("Failed to update Common Master", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<CommonMasterDTO>> getMastersByCategory(String category) {
        try {
            List<CommonMasterDTO> dtos = repository.findByCategoryAndIsActiveTrue(category).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "Masters fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching masters: ", e);
            return StandardResponse.error("Failed to fetch masters", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<Void> deleteCommonMaster(Long id) {
        try {
            repository.deleteById(id);
            return StandardResponse.success("Common Master deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting master: ", e);
            return StandardResponse.error("Failed to delete master", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private CommonMasterDTO convertToDTO(CommonMaster entity) {
        CommonMasterDTO dto = new CommonMasterDTO();
        dto.setId(entity.getId());
        dto.setCategory(entity.getCategory());
        dto.setCode(entity.getCode());
        dto.setValue(entity.getValue());
        dto.setDescription(entity.getDescription());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }
}
