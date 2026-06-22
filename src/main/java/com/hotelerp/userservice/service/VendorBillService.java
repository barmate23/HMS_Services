package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.VendorBillDTO;
import java.util.List;

public interface VendorBillService {
    StandardResponse<VendorBillDTO> createVendorBill(VendorBillDTO dto);
    StandardResponse<VendorBillDTO> updateVendorBill(Long id, VendorBillDTO dto);
    StandardResponse<VendorBillDTO> getVendorBillById(Long id);
    StandardResponse<List<VendorBillDTO>> getAllVendorBills();
    StandardResponse<Void> deleteVendorBill(Long id);
    StandardResponse<VendorBillDTO> updateStatus(Long id, Long statusId);
}
