package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.DiningTableDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.DiningTable;
import com.hotelerp.userservice.entity.Outlet;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.DiningTableRepository;
import com.hotelerp.userservice.repository.OutletRepository;
import com.hotelerp.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiningTableServiceImpl implements DiningTableService {

    private final DiningTableRepository tableRepository;
    private final OutletRepository outletRepository;
    private final UserRepository userRepository;
    private final CommonMasterRepository commonMasterRepository;

    @Override
    @Transactional
    public DiningTableDTO createTable(DiningTableDTO dto) {
        Outlet outlet = outletRepository.findById(dto.getOutletId())
                .orElseThrow(() -> new RuntimeException("Outlet not found"));

        CommonMaster section = null;
        if (dto.getSectionId() != null) {
            section = commonMasterRepository.findById(dto.getSectionId())
                    .orElseThrow(() -> new RuntimeException("Section not found"));
        }

        CommonMaster status = null;
        if (dto.getStatusId() != null) {
            status = commonMasterRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Status not found"));
        }

        User server = null;
        if (dto.getServerId() != null) {
            server = userRepository.findById(dto.getServerId())
                    .orElseThrow(() -> new RuntimeException("Server not found"));
        }

        DiningTable linkedTable = null;
        if (dto.getLinkedTableId() != null) {
            linkedTable = tableRepository.findById(dto.getLinkedTableId())
                    .orElseThrow(() -> new RuntimeException("Linked table not found"));
        }

        DiningTable table = DiningTable.builder()
                .outlet(outlet)
                .tableNumber(dto.getTableNumber())
                .section(section)
                .status(status)
                .covers(dto.getCovers())
                .server(server)
                .linkedTable(linkedTable)
                .build();

        return convertToDTO(tableRepository.save(table));
    }

    @Override
    @Transactional
    public DiningTableDTO updateTable(Long id, DiningTableDTO dto) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        if (dto.getOutletId() != null) {
            Outlet outlet = outletRepository.findById(dto.getOutletId()).orElseThrow(() -> new RuntimeException("Outlet not found"));
            table.setOutlet(outlet);
        }
        if (dto.getServerId() != null) {
            User server = userRepository.findById(dto.getServerId()).orElseThrow(() -> new RuntimeException("Server not found"));
            table.setServer(server);
        }
        if (dto.getLinkedTableId() != null) {
            DiningTable linkedTable = tableRepository.findById(dto.getLinkedTableId()).orElseThrow(() -> new RuntimeException("Linked table not found"));
            table.setLinkedTable(linkedTable);
        }
        if (dto.getSectionId() != null) {
            CommonMaster section = commonMasterRepository.findById(dto.getSectionId()).orElseThrow(() -> new RuntimeException("Section not found"));
            table.setSection(section);
        }
        if (dto.getStatusId() != null) {
            CommonMaster status = commonMasterRepository.findById(dto.getStatusId()).orElseThrow(() -> new RuntimeException("Status not found"));
            table.setStatus(status);
        }

        table.setTableNumber(dto.getTableNumber());
        table.setCovers(dto.getCovers());

        return convertToDTO(tableRepository.save(table));
    }

    @Override
    public DiningTableDTO getTableById(Long id) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));
        return convertToDTO(table);
    }

    @Override
    public List<DiningTableDTO> getTablesByOutlet(Long outletId) {
        return tableRepository.findByOutletId(outletId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DiningTableDTO> getAllTables() {
        return tableRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    private DiningTableDTO convertToDTO(DiningTable table) {
        return DiningTableDTO.builder()
                .id(table.getId())
                .outletId(table.getOutlet().getId())
                .outletName(table.getOutlet().getName())
                .tableNumber(table.getTableNumber())
                .sectionId(table.getSection() != null ? table.getSection().getId() : null)
                .sectionName(table.getSection() != null ? table.getSection().getValue() : null)
                .statusId(table.getStatus() != null ? table.getStatus().getId() : null)
                .statusName(table.getStatus() != null ? table.getStatus().getValue() : null)
                .covers(table.getCovers())
                .serverId(table.getServer() != null ? table.getServer().getId() : null)
                .serverName(table.getServer() != null ? table.getServer().getFullName() : null)
                .linkedTableId(table.getLinkedTable() != null ? table.getLinkedTable().getId() : null)
                .linkedTableNumber(table.getLinkedTable() != null ? table.getLinkedTable().getTableNumber() : null)
                .build();
    }
}
