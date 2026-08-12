package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.config.LoginUser;
import com.hotelerp.userservice.dto.OutletDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.Hotel;
import com.hotelerp.userservice.entity.Outlet;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.HotelRepository;
import com.hotelerp.userservice.repository.OutletRepository;
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
public class OutletServiceImpl implements OutletService {

    private final OutletRepository outletRepository;
    private final CommonMasterRepository commonMasterRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final LoginUser loginUser;

    @Override
    @Transactional
    public StandardResponse<Void> createOutlet(OutletDTO dto) {
        try {
            Long typeId = dto.getTypeId();
            if (typeId == null) throw new IllegalArgumentException("Outlet Type ID must not be null");

            CommonMaster type = commonMasterRepository.findById(typeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Outlet type master data not found for ID: " + typeId));

            User manager = null;
            if (dto.getManagerId() != null) {
                manager = userRepository.findById(dto.getManagerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Manager (User) not found with ID: " + dto.getManagerId()));
            }

            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId() : dto.getHotelId();
            Hotel hotel = null;
            if (hotelId != null) {
                hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));
            }

            Outlet outlet = Outlet.builder()
                    .name(dto.getName())
                    .hotel(hotel)
                    .type(type)
                    .location(dto.getLocation())
                    .timing(dto.getTiming())
                    .taxProfile(dto.getTaxProfile())
                    .manager(manager)
                    .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                    .build();
            
            outletRepository.save(outlet);
            return StandardResponse.success("Outlet created successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error creating outlet: ", e);
            return StandardResponse.error("Failed to create outlet", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<OutletDTO> updateOutlet(Long id, OutletDTO dto) {
        try {
            Outlet outlet = outletRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Outlet not found with ID: " + id));
            
            outlet.setName(dto.getName());
            
            if (dto.getTypeId() != null) {
                CommonMaster type = commonMasterRepository.findById(dto.getTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Outlet type master data not found for ID: " + dto.getTypeId()));
                outlet.setType(type);
            }

            if (dto.getManagerId() != null) {
                User manager = userRepository.findById(dto.getManagerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Manager (User) not found with ID: " + dto.getManagerId()));
                outlet.setManager(manager);
            }

            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId() : dto.getHotelId();
            if (hotelId != null) {
                Hotel hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));
                outlet.setHotel(hotel);
            }

            outlet.setLocation(dto.getLocation());
            outlet.setTiming(dto.getTiming());
            outlet.setTaxProfile(dto.getTaxProfile());
            if (dto.getIsActive() != null) {
                outlet.setIsActive(dto.getIsActive());
            }

            Outlet updatedOutlet = outletRepository.save(outlet);
            return StandardResponse.success(convertToDTO(updatedOutlet), "Outlet updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating outlet: ", e);
            return StandardResponse.error("Failed to update outlet", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<OutletDTO> getOutletById(Long id) {
        try {
            Outlet outlet = outletRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Outlet not found with ID: " + id));
            return StandardResponse.success(convertToDTO(outlet), "Outlet fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching outlet: ", e);
            return StandardResponse.error("Failed to fetch outlet", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<OutletDTO>> getAllOutlets() {
        try {
            Long hotelId = (loginUser != null) ? loginUser.getHotelId() : null;
            List<Outlet> outlets;
            if (hotelId != null) {
                outlets = outletRepository.findByHotel_IdAndIsDeletedFalse(hotelId);
            } else {
                outlets = outletRepository.findByIsDeletedFalse();
            }

            List<OutletDTO> dtos = outlets.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "All outlets fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all outlets: ", e);
            return StandardResponse.error("Failed to fetch outlets", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteOutlet(Long id) {
        try {
            Outlet outlet = outletRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Outlet not found with ID: " + id));
            outlet.setIsDeleted(true);
            outletRepository.save(outlet);
            return StandardResponse.success("Outlet deleted successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting outlet: ", e);
            return StandardResponse.error("Failed to delete outlet", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private OutletDTO convertToDTO(Outlet outlet) {
        return OutletDTO.builder()
                .id(outlet.getId())
                .hotelId(outlet.getHotel() != null ? outlet.getHotel().getId() : null)
                .hotelName(outlet.getHotel() != null ? outlet.getHotel().getName() : null)
                .name(outlet.getName())
                .typeId(outlet.getType().getId())
                .typeValue(outlet.getType().getValue())
                .location(outlet.getLocation())
                .timing(outlet.getTiming())
                .taxProfile(outlet.getTaxProfile())
                .managerId(outlet.getManager() != null ? outlet.getManager().getId() : null)
                .managerName(outlet.getManager() != null ? outlet.getManager().getFullName() : null)
                .isActive(outlet.getIsActive())
                .build();
    }
}
