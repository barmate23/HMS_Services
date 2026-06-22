package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.GrnDTO;
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
public class GrnServiceImpl implements GrnService {

    private final GrnRepository grnRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    @Transactional
    public StandardResponse<GrnDTO> createGrn(GrnDTO dto) {
        try {
            PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(dto.getPurchaseOrderId())
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

            Grn grn = Grn.builder()
                    .grnNumber(dto.getGrnNumber())
                    .purchaseOrder(po)
                    .receivedBy(dto.getReceivedBy())
                    .receivedDate(dto.getReceivedDate())
                    .acceptedValue(dto.getAcceptedValue())
                    .varianceNote(dto.getVarianceNote())
                    .build();

            grn = grnRepository.save(grn);
            return StandardResponse.success(convertToDTO(grn), "GRN created successfully");
        } catch (Exception e) {
            log.error("Error creating GRN: ", e);
            return StandardResponse.error("Failed to create GRN", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<GrnDTO> updateGrn(Long id, GrnDTO dto) {
        try {
            Grn grn = grnRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("GRN not found"));

            grn.setGrnNumber(dto.getGrnNumber());
            grn.setReceivedBy(dto.getReceivedBy());
            grn.setReceivedDate(dto.getReceivedDate());
            grn.setAcceptedValue(dto.getAcceptedValue());
            grn.setVarianceNote(dto.getVarianceNote());

            if (dto.getPurchaseOrderId() != null) {
                PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(dto.getPurchaseOrderId())
                        .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
                grn.setPurchaseOrder(po);
            }

            grn = grnRepository.save(grn);
            return StandardResponse.success(convertToDTO(grn), "GRN updated successfully");
        } catch (Exception e) {
            log.error("Error updating GRN: ", e);
            return StandardResponse.error("Failed to update GRN", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<GrnDTO> getGrnById(Long id) {
        try {
            Grn grn = grnRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("GRN not found"));
            return StandardResponse.success(convertToDTO(grn), "GRN fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Not found", "NOT_FOUND", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<GrnDTO>> getAllGrns() {
        try {
            List<GrnDTO> list = grnRepository.findByIsDeletedFalse().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(list, "GRNs fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch GRNs", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteGrn(Long id) {
        try {
            Grn grn = grnRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("GRN not found"));
            grn.setIsDeleted(true);
            grnRepository.save(grn);
            return StandardResponse.success("GRN deleted successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to delete GRN", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private GrnDTO convertToDTO(Grn grn) {
        return GrnDTO.builder()
                .id(grn.getId())
                .grnNumber(grn.getGrnNumber())
                .purchaseOrderId(grn.getPurchaseOrder().getId())
                .poNumber(grn.getPurchaseOrder().getPoNumber())
                .supplierName(grn.getPurchaseOrder().getSupplier().getSupplierName())
                .receivedBy(grn.getReceivedBy())
                .receivedDate(grn.getReceivedDate())
                .acceptedValue(grn.getAcceptedValue())
                .varianceNote(grn.getVarianceNote())
                .createdAt(grn.getCreatedAt())
                .updatedAt(grn.getUpdatedAt())
                .build();
    }
}
