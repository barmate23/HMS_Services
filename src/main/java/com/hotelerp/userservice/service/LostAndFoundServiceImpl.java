package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.LostAndFoundDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.LostAndFoundItem;
import com.hotelerp.userservice.entity.Room;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.LostAndFoundRepository;
import com.hotelerp.userservice.repository.RoomRepository;
import com.hotelerp.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LostAndFoundServiceImpl implements LostAndFoundService {

    private final LostAndFoundRepository lostAndFoundRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final CommonMasterRepository masterRepository;

    @Override
    @Transactional
    public LostAndFoundDTO logItem(LostAndFoundDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        User foundBy = userRepository.findById(dto.getFoundById())
                .orElseThrow(() -> new RuntimeException("User not found"));
        CommonMaster category = masterRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

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
                .status(LostAndFoundItem.ItemStatus.STORED) // Default to STORED
                .build();

        return convertToDTO(lostAndFoundRepository.save(item));
    }

    @Override
    @Transactional
    public LostAndFoundDTO updateItem(Long id, LostAndFoundDTO dto) {
        LostAndFoundItem item = lostAndFoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (dto.getRoomId() != null) {
            Room room = roomRepository.findById(dto.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found"));
            item.setRoom(room);
        }
        if (dto.getFoundById() != null) {
            User user = userRepository.findById(dto.getFoundById()).orElseThrow(() -> new RuntimeException("User not found"));
            item.setFoundBy(user);
        }
        if (dto.getCategoryId() != null) {
            CommonMaster cat = masterRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
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

        return convertToDTO(lostAndFoundRepository.save(item));
    }

    @Override
    public LostAndFoundDTO getItemById(Long id) {
        LostAndFoundItem item = lostAndFoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return convertToDTO(item);
    }

    @Override
    public List<LostAndFoundDTO> getAllItems() {
        return lostAndFoundRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        lostAndFoundRepository.deleteById(id);
    }

    @Override
    @Transactional
    public LostAndFoundDTO updateStatus(Long id, String status) {
        LostAndFoundItem item = lostAndFoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setStatus(LostAndFoundItem.ItemStatus.valueOf(status.toUpperCase()));
        return convertToDTO(lostAndFoundRepository.save(item));
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
