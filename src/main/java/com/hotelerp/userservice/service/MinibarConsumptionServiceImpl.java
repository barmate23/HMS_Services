package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.MinibarConsumptionDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinibarConsumptionServiceImpl implements MinibarConsumptionService {

    private final MinibarConsumptionRepository minibarConsumptionRepository;
    private final RoomRepository roomRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final CommonMasterRepository commonMasterRepository;
    private final FolioService folioService;

    @Override
    @Transactional
    public StandardResponse<MinibarConsumptionDTO> postConsumption(MinibarConsumptionDTO dto) {
        try {
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            InventoryStock item = inventoryStockRepository.findByIdAndIsDeletedFalse(dto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Inventory item not found"));

            MinibarConsumption consumption = MinibarConsumption.builder()
                    .room(room)
                    .item(item)
                    .parLevel(dto.getParLevel())
                    .currentQty(dto.getCurrentQty())
                    .consumedQty(dto.getConsumedQty())
                    .chargeAmount(dto.getChargeAmount())
                    .remarks(dto.getRemarks())
                    .consumptionDate(java.time.LocalDateTime.now())
                    .build();

            if (dto.getStatusId() != null) {
                consumption.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            }

            consumption = minibarConsumptionRepository.save(consumption);

            // Post to room folio if consumption is greater than 0
            if (dto.getConsumedQty() != null && dto.getConsumedQty() > 0 && dto.getChargeAmount() != null) {
                StandardResponse<Void> folioResponse = folioService.postChargeByRoom(
                        room.getId(),
                        dto.getChargeAmount(),
                        "Minibar",
                        "Minibar Consumption: " + item.getItemConfig().getItemName() + " x " + dto.getConsumedQty()
                );
                
                if (!folioResponse.isSuccess()) {
                    log.error("Failed to post minibar charge to folio: {}", folioResponse.getMessage());
                    // We might not want to roll back the consumption record just because folio posting failed,
                    // but in many cases, financial accuracy is critical.
                    // For now, let's just log it or throw to rollback if preferred.
                    throw new RuntimeException("Failed to post charge to folio: " + folioResponse.getMessage());
                }
            }

            return StandardResponse.success(convertToDTO(consumption), "Minibar consumption posted successfully");
        } catch (Exception e) {
            log.error("Error posting minibar consumption: ", e);
            return StandardResponse.error("Failed to post consumption", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<MinibarConsumptionDTO> updateConsumption(Long id, MinibarConsumptionDTO dto) {
        try {
            MinibarConsumption consumption = minibarConsumptionRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Consumption record not found"));

            consumption.setParLevel(dto.getParLevel());
            consumption.setCurrentQty(dto.getCurrentQty());
            consumption.setConsumedQty(dto.getConsumedQty());
            consumption.setChargeAmount(dto.getChargeAmount());
            consumption.setRemarks(dto.getRemarks());

            if (dto.getStatusId() != null) {
                consumption.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            }

            consumption = minibarConsumptionRepository.save(consumption);
            return StandardResponse.success(convertToDTO(consumption), "Minibar consumption updated successfully");
        } catch (Exception e) {
            log.error("Error updating minibar consumption: ", e);
            return StandardResponse.error("Failed to update consumption", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<MinibarConsumptionDTO> getConsumptionById(Long id) {
        try {
            MinibarConsumption consumption = minibarConsumptionRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Consumption record not found"));
            return StandardResponse.success(convertToDTO(consumption), "Consumption record fetched");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch record", "NOT_FOUND", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<MinibarConsumptionDTO>> getAllConsumptions() {
        try {
            List<MinibarConsumptionDTO> list = minibarConsumptionRepository.findByIsDeletedFalse().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(list, "Minibar consumption records fetched");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch records", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteConsumption(Long id) {
        try {
            MinibarConsumption consumption = minibarConsumptionRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Consumption record not found"));
            consumption.setIsDeleted(true);
            minibarConsumptionRepository.save(consumption);
            return StandardResponse.success("Record deleted successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to delete record", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private MinibarConsumptionDTO convertToDTO(MinibarConsumption c) {
        return MinibarConsumptionDTO.builder()
                .id(c.getId())
                .roomId(c.getRoom().getId())
                .roomNumber(c.getRoom().getRoomNumber())
                .itemId(c.getItem().getId())
                .itemName(c.getItem().getItemConfig().getItemName())
                .itemCode(c.getItem().getItemConfig().getItemCode())
                .parLevel(c.getParLevel())
                .currentQty(c.getCurrentQty())
                .consumedQty(c.getConsumedQty())
                .chargeAmount(c.getChargeAmount())
                .statusId(c.getStatus() != null ? c.getStatus().getId() : null)
                .statusName(c.getStatus() != null ? c.getStatus().getValue() : null)
                .statusCode(c.getStatus() != null ? c.getStatus().getCode() : null)
                .remarks(c.getRemarks())

                .consumptionDate(c.getConsumptionDate())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
