package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.SupplierDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.Supplier;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final CommonMasterRepository commonMasterRepository;

    @Override
    @Transactional
    public StandardResponse<SupplierDTO> createSupplier(SupplierDTO dto) {
        try {
            Supplier supplier = buildFromDTO(new Supplier(), dto);

            // Default status to ACTIVE if none provided
            if (dto.getStatusId() == null) {
                commonMasterRepository.findByCategoryAndCode("SUPPLIER_STATUS", "ACTIVE")
                        .ifPresent(supplier::setStatus);
            }

            supplier = supplierRepository.save(supplier);
            return StandardResponse.success(convertToDTO(supplier), "Supplier created successfully");
        } catch (Exception e) {
            log.error("Error creating supplier: ", e);
            return StandardResponse.error("Failed to create supplier", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<SupplierDTO> updateSupplier(Long id, SupplierDTO dto) {
        try {
            Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            supplier = buildFromDTO(supplier, dto);
            supplier = supplierRepository.save(supplier);
            return StandardResponse.success(convertToDTO(supplier), "Supplier updated successfully");
        } catch (Exception e) {
            log.error("Error updating supplier: ", e);
            return StandardResponse.error("Failed to update supplier", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<SupplierDTO> getSupplierById(Long id) {
        try {
            Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            return StandardResponse.success(convertToDTO(supplier), "Supplier fetched");
        } catch (Exception e) {
            return StandardResponse.error("Not found", "NOT_FOUND", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<SupplierDTO>> getAllSuppliers() {
        try {
            List<SupplierDTO> list = supplierRepository.findByIsDeletedFalse().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(list, "Suppliers fetched");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch suppliers", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteSupplier(Long id) {
        try {
            Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            supplier.setIsDeleted(true);
            supplierRepository.save(supplier);
            return StandardResponse.success("Supplier deleted");
        } catch (Exception e) {
            return StandardResponse.error("Failed to delete supplier", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<SupplierDTO> updateStatus(Long id, Long statusId) {
        try {
            Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            CommonMaster status = commonMasterRepository.findById(statusId)
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            supplier.setStatus(status);
            supplier = supplierRepository.save(supplier);
            return StandardResponse.success(convertToDTO(supplier), "Status updated successfully");
        } catch (Exception e) {
            log.error("Error updating supplier status: ", e);
            return StandardResponse.error("Failed to update status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private Supplier buildFromDTO(Supplier supplier, SupplierDTO dto) {
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setSupplierAddress(dto.getSupplierAddress());
        supplier.setCity(dto.getCity());
        supplier.setState(dto.getState());
        supplier.setPinCode(dto.getPinCode());
        supplier.setGstin(dto.getGstin());
        supplier.setPan(dto.getPan());
        supplier.setCreditLimit(dto.getCreditLimit());
        supplier.setBankName(dto.getBankName());
        supplier.setAccountNumber(dto.getAccountNumber());
        supplier.setIfscCode(dto.getIfscCode());

        if (dto.getCategoryId() != null) {
            supplier.setCategory(commonMasterRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found")));
        }

        if (dto.getPaymentTermsId() != null) {
            supplier.setPaymentTerms(commonMasterRepository.findById(dto.getPaymentTermsId())
                    .orElseThrow(() -> new RuntimeException("Payment terms not found")));
        }

        if (dto.getStatusId() != null) {
            supplier.setStatus(commonMasterRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Status not found")));
        }

        return supplier;
    }

    private SupplierDTO convertToDTO(Supplier s) {
        return SupplierDTO.builder()
                .id(s.getId())
                .supplierName(s.getSupplierName())
                .categoryId(s.getCategory() != null ? s.getCategory().getId() : null)
                .categoryName(s.getCategory() != null ? s.getCategory().getValue() : null)
                .contactPerson(s.getContactPerson())
                .phone(s.getPhone())
                .email(s.getEmail())
                .paymentTermsId(s.getPaymentTerms() != null ? s.getPaymentTerms().getId() : null)
                .paymentTermsName(s.getPaymentTerms() != null ? s.getPaymentTerms().getValue() : null)
                .supplierAddress(s.getSupplierAddress())
                .city(s.getCity())
                .state(s.getState())
                .pinCode(s.getPinCode())
                .gstin(s.getGstin())
                .pan(s.getPan())
                .creditLimit(s.getCreditLimit())
                .bankName(s.getBankName())
                .accountNumber(s.getAccountNumber())
                .ifscCode(s.getIfscCode())
                .statusId(s.getStatus() != null ? s.getStatus().getId() : null)
                .statusName(s.getStatus() != null ? s.getStatus().getValue() : null)
                .statusCode(s.getStatus() != null ? s.getStatus().getCode() : null)
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
