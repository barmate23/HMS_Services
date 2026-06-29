package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.DiningTableDTO;
import com.hotelerp.userservice.dto.DiningTableWithoutOutletDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.DiningTable;
import com.hotelerp.userservice.entity.Outlet;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.DiningTableRepository;
import com.hotelerp.userservice.repository.OutletRepository;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiningTableServiceImpl implements DiningTableService {

    private final DiningTableRepository diningTableRepository;
    private final OutletRepository outletRepository;
    private final CommonMasterRepository commonMasterRepository;

    @Override
    @Transactional
    public StandardResponse<Void> createTable(DiningTableDTO dto) {
        try {
            Long outletId = dto.getOutletId();
            if (outletId == null)
                throw new IllegalArgumentException("Outlet ID must not be null");

            Outlet outlet = outletRepository.findById(outletId)
                    .orElseThrow(() -> new ResourceNotFoundException("Outlet not found with ID: " + outletId));

            CommonMaster section = null;
            if (dto.getSectionId() != null) {
                section = commonMasterRepository.findById(dto.getSectionId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Section master data not found for ID: " + dto.getSectionId()));
            }

            CommonMaster status = null;
            if (dto.getStatusId() != null) {
                status = commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Status master data not found for ID: " + dto.getStatusId()));
            }

            DiningTable table = DiningTable.builder()
                    .outlet(outlet)
                    .tableNumber(dto.getTableNumber())
                    .covers(dto.getCovers())
                    .section(section)
                    .status(status)
                    .build();

            diningTableRepository.save(table);
            return StandardResponse.success("Dining table created successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error creating dining table: ", e);
            return StandardResponse.error("Failed to create dining table", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<DiningTableDTO> updateTable(Long id, DiningTableDTO dto) {
        try {
            DiningTable table = diningTableRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Dining table not found with ID: " + id));

            if (dto.getOutletId() != null) {
                Outlet outlet = outletRepository.findById(dto.getOutletId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Outlet not found with ID: " + dto.getOutletId()));
                table.setOutlet(outlet);
            }

            table.setTableNumber(dto.getTableNumber());
            table.setCovers(dto.getCovers());

            if (dto.getSectionId() != null) {
                CommonMaster section = commonMasterRepository.findById(dto.getSectionId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Section master data not found for ID: " + dto.getSectionId()));
                table.setSection(section);
            }

            if (dto.getStatusId() != null) {
                CommonMaster status = commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Status master data not found for ID: " + dto.getStatusId()));
                table.setStatus(status);
            }

            DiningTable updatedTable = diningTableRepository.save(table);
            return StandardResponse.success(convertToDTO(updatedTable), "Dining table updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating dining table: ", e);
            return StandardResponse.error("Failed to update dining table", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<DiningTableDTO> getTableById(Long id) {
        try {
            DiningTable table = diningTableRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Dining table not found with ID: " + id));
            return StandardResponse.success(convertToDTO(table), "Dining table fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching dining table: ", e);
            return StandardResponse.error("Failed to fetch dining table", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<DiningTableWithoutOutletDTO>> getTablesByOutlet(Long outletId) {
        try {
            List<DiningTableWithoutOutletDTO> dtos = diningTableRepository.findByOutletId(outletId).stream()
                    .filter(t -> !Boolean.TRUE.equals(t.getIsDeleted()))
                    .map(this::convertToWithoutOutletDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "Dining tables fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching dining tables by outlet: ", e);
            return StandardResponse.error("Failed to fetch dining tables", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<DiningTableDTO>> getAllTables() {
        try {
            List<DiningTableDTO> dtos = diningTableRepository.findAll().stream()
                    .filter(t -> !Boolean.TRUE.equals(t.getIsDeleted()))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "All dining tables fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all dining tables: ", e);
            return StandardResponse.error("Failed to fetch all dining tables", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteTable(Long id) {
        try {
            DiningTable table = diningTableRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Dining table not found with ID: " + id));
            table.setIsDeleted(true);
            diningTableRepository.save(table);
            return StandardResponse.success("Dining table deleted successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting dining table: ", e);
            return StandardResponse.error("Failed to delete dining table", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private DiningTableDTO convertToDTO(DiningTable table) {
        return DiningTableDTO.builder()
                .id(table.getId())
                .outletId(table.getOutlet().getId())
                .outletName(table.getOutlet().getName())
                .tableNumber(table.getTableNumber())
                .covers(table.getCovers())
                .sectionId(table.getSection() != null ? table.getSection().getId() : null)
                .sectionName(table.getSection() != null ? table.getSection().getValue() : null)
                .statusId(table.getStatus() != null ? table.getStatus().getId() : null)
                .statusName(table.getStatus() != null ? table.getStatus().getValue() : null)
                .linkedTableId(table.getLinkedTable() != null ? table.getLinkedTable().getId() : null)
                .linkedTableNumber(table.getLinkedTable() != null ? table.getLinkedTable().getTableNumber() : null)
                .build();
    }

    private DiningTableWithoutOutletDTO convertToWithoutOutletDTO(DiningTable table) {
        return DiningTableWithoutOutletDTO.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .covers(table.getCovers())
                .sectionId(table.getSection() != null ? table.getSection().getId() : null)
                .sectionName(table.getSection() != null ? table.getSection().getValue() : null)
                .statusId(table.getStatus() != null ? table.getStatus().getId() : null)
                .statusName(table.getStatus() != null ? table.getStatus().getValue() : null)
                .build();
    }
}
