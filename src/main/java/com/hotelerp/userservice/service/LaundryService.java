package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.LaundryOrderDTO;
import com.hotelerp.userservice.dto.LaundryPriceMasterDTO;
import com.hotelerp.userservice.dto.LaundryServiceCatalogDTO;
import java.util.List;

public interface LaundryService {
    // Price Master APIs
    StandardResponse<LaundryPriceMasterDTO> createPriceMaster(LaundryPriceMasterDTO dto);
    StandardResponse<LaundryPriceMasterDTO> updatePriceMaster(Long id, LaundryPriceMasterDTO dto);
    StandardResponse<List<LaundryPriceMasterDTO>> getAllPriceMasters();
    StandardResponse<LaundryPriceMasterDTO> getPriceMasterById(Long id);
    StandardResponse<Void> deletePriceMaster(Long id);

    // Service Catalog APIs
    StandardResponse<LaundryServiceCatalogDTO> createServiceCatalog(LaundryServiceCatalogDTO dto);
    StandardResponse<LaundryServiceCatalogDTO> updateServiceCatalog(Long id, LaundryServiceCatalogDTO dto);
    StandardResponse<List<LaundryServiceCatalogDTO>> getAllServiceCatalog();
    StandardResponse<List<LaundryServiceCatalogDTO>> getActiveServiceCatalog();
    StandardResponse<LaundryServiceCatalogDTO> getServiceCatalogById(Long id);
    StandardResponse<Void> deleteServiceCatalog(Long id);

    // Laundry Order APIs
    StandardResponse<LaundryOrderDTO> createLaundryOrder(LaundryOrderDTO dto);
    StandardResponse<LaundryOrderDTO> updateLaundryOrder(Long id, LaundryOrderDTO dto);
    StandardResponse<LaundryOrderDTO> getLaundryOrderById(Long id);
    StandardResponse<List<LaundryOrderDTO>> getAllLaundryOrders();
    StandardResponse<LaundryOrderDTO> updateOrderStatus(Long id, String status);
    StandardResponse<Void> deleteLaundryOrder(Long id);
}
