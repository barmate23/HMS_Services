package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.MaintenanceDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.MaintenanceRequest;
import com.hotelerp.userservice.entity.Room;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.MaintenanceRepository;
import com.hotelerp.userservice.repository.RoomRepository;
import com.hotelerp.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final CommonMasterRepository masterRepository;

    @Override
    @Transactional
    public MaintenanceDTO reportIssue(MaintenanceDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        User reportedBy = userRepository.findById(dto.getReportedById())
                .orElseThrow(() -> new RuntimeException("User not found"));
        CommonMaster category = masterRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        CommonMaster priority = masterRepository.findById(dto.getPriorityId())
                .orElseThrow(() -> new RuntimeException("Priority not found"));

        User assignedTo = null;
        if (dto.getAssignedToId() != null) {
            assignedTo = userRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
        }

        MaintenanceRequest request = MaintenanceRequest.builder()
                .room(room)
                .repairIssue(dto.getRepairIssue())
                .category(category)
                .priority(priority)
                .reportedBy(reportedBy)
                .assignedTo(assignedTo)
                .repairNotes(dto.getRepairNotes())
                .status(MaintenanceRequest.MaintenanceStatus.OPEN) // Default to OPEN
                .build();

        return convertToDTO(maintenanceRepository.save(request));
    }

    @Override
    @Transactional
    public MaintenanceDTO updateIssue(Long id, MaintenanceDTO dto) {
        MaintenanceRequest request = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        if (dto.getRoomId() != null) {
            Room room = roomRepository.findById(dto.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found"));
            request.setRoom(room);
        }
        if (dto.getReportedById() != null) {
            User user = userRepository.findById(dto.getReportedById()).orElseThrow(() -> new RuntimeException("User not found"));
            request.setReportedBy(user);
        }
        if (dto.getAssignedToId() != null) {
            User user = userRepository.findById(dto.getAssignedToId()).orElseThrow(() -> new RuntimeException("Assigned user not found"));
            request.setAssignedTo(user);
        }
        if (dto.getCategoryId() != null) {
            CommonMaster cat = masterRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
            request.setCategory(cat);
        }
        if (dto.getPriorityId() != null) {
            CommonMaster prio = masterRepository.findById(dto.getPriorityId()).orElseThrow(() -> new RuntimeException("Priority not found"));
            request.setPriority(prio);
        }

        request.setRepairIssue(dto.getRepairIssue());
        request.setRepairNotes(dto.getRepairNotes());
        if (dto.getStatus() != null) {
            request.setStatus(dto.getStatus());
        }

        return convertToDTO(maintenanceRepository.save(request));
    }

    @Override
    public MaintenanceDTO getIssueById(Long id) {
        MaintenanceRequest request = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));
        return convertToDTO(request);
    }

    @Override
    public List<MaintenanceDTO> getAllIssues() {
        return maintenanceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteIssue(Long id) {
        maintenanceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MaintenanceDTO updateStatus(Long id, String status) {
        MaintenanceRequest request = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));
        request.setStatus(MaintenanceRequest.MaintenanceStatus.valueOf(status.toUpperCase().replace(" ", "_")));
        return convertToDTO(maintenanceRepository.save(request));
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
                .status(request.getStatus())
                .reportedAt(request.getReportedAt())
                .build();
    }
}
