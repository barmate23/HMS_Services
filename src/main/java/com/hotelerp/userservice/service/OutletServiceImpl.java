package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.OutletDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.Outlet;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.OutletRepository;
import com.hotelerp.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutletServiceImpl implements OutletService {

    private final OutletRepository outletRepository;
    private final CommonMasterRepository masterRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OutletDTO createOutlet(OutletDTO dto) {
        CommonMaster type = masterRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new RuntimeException("Outlet Type not found"));
        
        User manager = null;
        if (dto.getManagerId() != null) {
            manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
        }

        Outlet outlet = Outlet.builder()
                .name(dto.getName())
                .type(type)
                .location(dto.getLocation())
                .timing(dto.getTiming())
                .taxProfile(dto.getTaxProfile())
                .manager(manager)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        return convertToDTO(outletRepository.save(outlet));
    }

    @Override
    @Transactional
    public OutletDTO updateOutlet(Long id, OutletDTO dto) {
        Outlet outlet = outletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outlet not found"));

        if (dto.getTypeId() != null) {
            CommonMaster type = masterRepository.findById(dto.getTypeId()).orElseThrow(() -> new RuntimeException("Outlet Type not found"));
            outlet.setType(type);
        }
        
        if (dto.getManagerId() != null) {
            User manager = userRepository.findById(dto.getManagerId()).orElseThrow(() -> new RuntimeException("Manager not found"));
            outlet.setManager(manager);
        }

        outlet.setName(dto.getName());
        outlet.setLocation(dto.getLocation());
        outlet.setTiming(dto.getTiming());
        outlet.setTaxProfile(dto.getTaxProfile());
        if (dto.getIsActive() != null) {
            outlet.setIsActive(dto.getIsActive());
        }

        return convertToDTO(outletRepository.save(outlet));
    }

    @Override
    public OutletDTO getOutletById(Long id) {
        Outlet outlet = outletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outlet not found"));
        return convertToDTO(outlet);
    }

    @Override
    public List<OutletDTO> getAllOutlets() {
        return outletRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOutlet(Long id) {
        outletRepository.deleteById(id);
    }

    private OutletDTO convertToDTO(Outlet outlet) {
        return OutletDTO.builder()
                .id(outlet.getId())
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
