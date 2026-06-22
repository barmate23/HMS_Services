package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.SupplierDTO;
import java.util.List;

public interface SupplierService {
    StandardResponse<SupplierDTO> createSupplier(SupplierDTO dto);
    StandardResponse<SupplierDTO> updateSupplier(Long id, SupplierDTO dto);
    StandardResponse<SupplierDTO> getSupplierById(Long id);
    StandardResponse<List<SupplierDTO>> getAllSuppliers();
    StandardResponse<Void> deleteSupplier(Long id);
    StandardResponse<SupplierDTO> updateStatus(Long id, Long statusId);
}
