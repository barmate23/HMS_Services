package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.MaintenanceDTO;
import com.hotelerp.userservice.entity.Room;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.MaintenanceRequest;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.repository.RoomRepository;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.MaintenanceRepository;
import com.hotelerp.userservice.repository.UserRepository;
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
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final CommonMasterRepository commonMasterRepository;

    @Override
    @Transactional
    public StandardResponse<Void> reportIssue(MaintenanceDTO dto) {
        try {
            Long roomId = dto.getRoomId();
            if (roomId == null) throw new IllegalArgumentException("Room ID must not be null");

            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

            CommonMaster category = commonMasterRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category master data not found for ID: " + dto.getCategoryId()));

            CommonMaster priority = commonMasterRepository.findById(dto.getPriorityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Priority master data not found for ID: " + dto.getPriorityId()));

            User reportedBy = userRepository.findById(dto.getReportedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User (Reporter) not found with ID: " + dto.getReportedById()));

            CommonMaster status = null;
            if (dto.getStatusId() != null) {
                status = commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException("Status master data not found for ID: " + dto.getStatusId()));
            } else {
                status = commonMasterRepository.findByCategoryAndCode("MAINTENANCE_STATUS", "OPEN")
                        .orElseThrow(() -> new ResourceNotFoundException("Default OPEN status not found in master data"));
            }

            MaintenanceRequest request = MaintenanceRequest.builder()
                    .room(room)
                    .repairIssue(dto.getRepairIssue())
                    .category(category)
                    .priority(priority)
                    .reportedBy(reportedBy)
                    .status(status)
                    .build();

            maintenanceRepository.save(request);
            return StandardResponse.success("Maintenance issue reported successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error reporting maintenance issue: ", e);
            return StandardResponse.error("Failed to report maintenance issue", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<MaintenanceDTO> updateIssue(Long id, MaintenanceDTO dto) {
        try {
            MaintenanceRequest request = maintenanceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with ID: " + id));

            if (dto.getRoomId() != null) {
                Room room = roomRepository.findById(dto.getRoomId())
                        .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + dto.getRoomId()));
                request.setRoom(room);
            }

            if (dto.getCategoryId() != null) {
                CommonMaster category = commonMasterRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category master data not found for ID: " + dto.getCategoryId()));
                request.setCategory(category);
            }

            if (dto.getPriorityId() != null) {
                CommonMaster priority = commonMasterRepository.findById(dto.getPriorityId())
                        .orElseThrow(() -> new ResourceNotFoundException("Priority master data not found for ID: " + dto.getPriorityId()));
                request.setPriority(priority);
            }

            request.setRepairIssue(dto.getRepairIssue());
            request.setRepairNotes(dto.getRepairNotes());
            
            if (dto.getStatusId() != null) {
                CommonMaster status = commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException("Status master data not found for ID: " + dto.getStatusId()));
                request.setStatus(status);
            }

            if (dto.getAssignedToId() != null) {
                User assignedTo = userRepository.findById(dto.getAssignedToId())
                        .orElseThrow(() -> new ResourceNotFoundException("User (Assigned Technician) not found with ID: " + dto.getAssignedToId()));
                request.setAssignedTo(assignedTo);
            }

            MaintenanceRequest updatedRequest = maintenanceRepository.save(request);
            return StandardResponse.success(convertToDTO(updatedRequest), "Maintenance issue updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating maintenance issue: ", e);
            return StandardResponse.error("Failed to update maintenance issue", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<MaintenanceDTO> getIssueById(Long id) {
        try {
            MaintenanceRequest request = maintenanceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with ID: " + id));
            return StandardResponse.success(convertToDTO(request), "Maintenance issue fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching maintenance issue: ", e);
            return StandardResponse.error("Failed to fetch maintenance issue", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<MaintenanceDTO>> getAllIssues() {
        try {
            List<MaintenanceDTO> dtos = maintenanceRepository.findAll().stream()
                    .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "All maintenance issues fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all maintenance issues: ", e);
            return StandardResponse.error("Failed to fetch maintenance issues", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteIssue(Long id) {
        try {
            MaintenanceRequest request = maintenanceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with ID: " + id));
            request.setIsDeleted(true);
            maintenanceRepository.save(request);
            return StandardResponse.success("Maintenance issue deleted successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting maintenance issue: ", e);
            return StandardResponse.error("Failed to delete maintenance issue", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<MaintenanceDTO> updateStatus(Long id, String status) {
        try {
            MaintenanceRequest request = maintenanceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with ID: " + id));
            
            CommonMaster statusEntry = commonMasterRepository.findById(Long.parseLong(status))
                    .orElseThrow(() -> new ResourceNotFoundException("Status master data not found for ID: " + status));
            
            request.setStatus(statusEntry);
            
            MaintenanceRequest updatedRequest = maintenanceRepository.save(request);
            return StandardResponse.success(convertToDTO(updatedRequest), "Maintenance issue status updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating maintenance status: ", e);
            return StandardResponse.error("Failed to update status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private MaintenanceDTO convertToDTO(MaintenanceRequest request) {
        return MaintenanceDTO.builder()
                .id(request.getId())
                .roomId(request.getRoom().getId())
                .roomNumber(request.getRoom().getRoomNumber())
                .repairIssue(request.getRepairIssue())
                .categoryId(request.getCategory().getId())
                .categoryValue(request.getCategory().getValue())
                .priorityId(request.getPriority().getId())
                .priorityValue(request.getPriority().getValue())
                .reportedById(request.getReportedBy().getId())
                .reportedByName(request.getReportedBy().getFullName())
                .assignedToId(request.getAssignedTo() != null ? request.getAssignedTo().getId() : null)
                .assignedToName(request.getAssignedTo() != null ? request.getAssignedTo().getFullName() : null)
                .repairNotes(request.getRepairNotes())
                .statusId(request.getStatus() != null ? request.getStatus().getId() : null)
                .statusName(request.getStatus() != null ? request.getStatus().getValue() : null)
                .reportedAt(request.getReportedAt())
                .build();
    }
}
