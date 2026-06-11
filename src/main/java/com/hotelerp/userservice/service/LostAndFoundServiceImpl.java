package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.LostAndFoundDTO;
import com.hotelerp.common.entity.CommonMaster;
import com.hotelerp.common.entity.LostAndFoundItem;
import com.hotelerp.common.entity.Room;
import com.hotelerp.common.entity.User;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.LostAndFoundRepository;
import com.hotelerp.userservice.repository.RoomRepository;
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
public class LostAndFoundServiceImpl implements LostAndFoundService {

    private final LostAndFoundRepository lostAndFoundRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final CommonMasterRepository masterRepository;

    @Override
    @Transactional
    public StandardResponse<Void> logItem(LostAndFoundDTO dto) {
        try {
            Long roomId = dto.getRoomId();
            if (roomId == null) throw new IllegalArgumentException("Room ID must not be null");

            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));
            
            User foundBy = userRepository.findById(dto.getFoundById())
                    .orElseThrow(() -> new ResourceNotFoundException("User (Finder) not found with ID: " + dto.getFoundById()));
            
            CommonMaster category = masterRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category master data not found for ID: " + dto.getCategoryId()));

            LostAndFoundItem item = LostAndFoundItem.builder()
                    .room(room)
                    .itemDescription(dto.getItemDescription())
                    .category(category)
                    .foundBy(foundBy)
                    .storageLocation(dto.getStorageLocation())
                    .foundDate(dto.getFoundDate())
                    .guestName(dto.getGuestName())
                    .guestContact(dto.getGuestContact())
                    .storageNotes(dto.getStorageNotes())
                    .status(LostAndFoundItem.ItemStatus.STORED)
                    .build();

            lostAndFoundRepository.save(item);
            return StandardResponse.success("Lost and found item logged successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error logging lost and found item: ", e);
            return StandardResponse.error("Failed to log item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<LostAndFoundDTO> updateItem(Long id, LostAndFoundDTO dto) {
        try {
            LostAndFoundItem item = lostAndFoundRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Lost and found item not found with ID: " + id));

            if (dto.getRoomId() != null) {
                Room room = roomRepository.findById(dto.getRoomId())
                        .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + dto.getRoomId()));
                item.setRoom(room);
            }
            if (dto.getFoundById() != null) {
                User user = userRepository.findById(dto.getFoundById())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getFoundById()));
                item.setFoundBy(user);
            }
            if (dto.getCategoryId() != null) {
                CommonMaster cat = masterRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));
                item.setCategory(cat);
            }

            item.setItemDescription(dto.getItemDescription());
            item.setStorageLocation(dto.getStorageLocation());
            item.setFoundDate(dto.getFoundDate());
            item.setGuestName(dto.getGuestName());
            item.setGuestContact(dto.getGuestContact());
            item.setStorageNotes(dto.getStorageNotes());
            if (dto.getStatus() != null) {
                item.setStatus(dto.getStatus());
            }

            LostAndFoundItem updatedItem = lostAndFoundRepository.save(item);
            return StandardResponse.success(convertToDTO(updatedItem), "Item updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating lost and found item: ", e);
            return StandardResponse.error("Failed to update item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<LostAndFoundDTO> getItemById(Long id) {
        try {
            LostAndFoundItem item = lostAndFoundRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));
            return StandardResponse.success(convertToDTO(item), "Item fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching lost and found item: ", e);
            return StandardResponse.error("Failed to fetch item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<LostAndFoundDTO>> getAllItems() {
        try {
            List<LostAndFoundDTO> dtos = lostAndFoundRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "All items fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all lost and found items: ", e);
            return StandardResponse.error("Failed to fetch items", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteItem(Long id) {
        try {
            if (!lostAndFoundRepository.existsById(id)) {
                throw new ResourceNotFoundException("Item not found with ID: " + id);
            }
            lostAndFoundRepository.deleteById(id);
            return StandardResponse.success("Item deleted successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting lost and found item: ", e);
            return StandardResponse.error("Failed to delete item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<LostAndFoundDTO> updateStatus(Long id, String status) {
        try {
            LostAndFoundItem item = lostAndFoundRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));
            
            try {
                item.setStatus(LostAndFoundItem.ItemStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return StandardResponse.error("Invalid status: " + status, "INVALID_INPUT", "Allowed statuses: STORED, CLAIMED, DISPOSED");
            }
            
            LostAndFoundItem updatedItem = lostAndFoundRepository.save(item);
            return StandardResponse.success(convertToDTO(updatedItem), "Item status updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating lost and found status: ", e);
            return StandardResponse.error("Failed to update status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private LostAndFoundDTO convertToDTO(LostAndFoundItem item) {
        return LostAndFoundDTO.builder()
                .id(item.getId())
                .roomId(item.getRoom().getId())
                .roomNumber(item.getRoom().getRoomNumber())
                .itemDescription(item.getItemDescription())
                .categoryId(item.getCategory().getId())
                .categoryValue(item.getCategory().getValue())
                .foundById(item.getFoundBy().getId())
                .foundByName(item.getFoundBy().getFullName())
                .storageLocation(item.getStorageLocation())
                .foundDate(item.getFoundDate())
                .guestName(item.getGuestName())
                .guestContact(item.getGuestContact())
                .storageNotes(item.getStorageNotes())
                .status(item.getStatus())
                .build();
    }
}
