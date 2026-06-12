package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.LaundryOrderDTO;
import com.hotelerp.userservice.dto.LaundryPriceMasterDTO;
import com.hotelerp.userservice.dto.LaundryServiceCatalogDTO;
import com.hotelerp.userservice.service.LaundryService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/laundry")
@RequiredArgsConstructor
public class LaundryController {

    private final LaundryService laundryService;

    // Price Master Endpoints

    @PostMapping(ServiceConstant.CREATE_LAUNDRY_PRICE_MASTER)
    public ResponseEntity<StandardResponse<LaundryPriceMasterDTO>> createPriceMaster(@RequestBody LaundryPriceMasterDTO dto) {
        StandardResponse<LaundryPriceMasterDTO> response = laundryService.createPriceMaster(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping(ServiceConstant.UPDATE_LAUNDRY_PRICE_MASTER)
    public ResponseEntity<StandardResponse<LaundryPriceMasterDTO>> updatePriceMaster(@PathVariable Long id, @RequestBody LaundryPriceMasterDTO dto) {
        StandardResponse<LaundryPriceMasterDTO> response = laundryService.updatePriceMaster(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_ALL_LAUNDRY_PRICE_MASTERS)
    public ResponseEntity<StandardResponse<List<LaundryPriceMasterDTO>>> getAllPriceMasters() {
        StandardResponse<List<LaundryPriceMasterDTO>> response = laundryService.getAllPriceMasters();
        return ResponseEntity.ok(response);
    }

    @GetMapping(ServiceConstant.GET_LAUNDRY_PRICE_MASTER_BY_ID)
    public ResponseEntity<StandardResponse<LaundryPriceMasterDTO>> getPriceMasterById(@PathVariable Long id) {
        StandardResponse<LaundryPriceMasterDTO> response = laundryService.getPriceMasterById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping(ServiceConstant.DELETE_LAUNDRY_PRICE_MASTER)
    public ResponseEntity<StandardResponse<Void>> deletePriceMaster(@PathVariable Long id) {
        StandardResponse<Void> response = laundryService.deletePriceMaster(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    // Service Catalog Endpoints

    @PostMapping(ServiceConstant.CREATE_LAUNDRY_SERVICE_CATALOG)
    public ResponseEntity<StandardResponse<LaundryServiceCatalogDTO>> createServiceCatalog(@RequestBody LaundryServiceCatalogDTO dto) {
        StandardResponse<LaundryServiceCatalogDTO> response = laundryService.createServiceCatalog(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping(ServiceConstant.UPDATE_LAUNDRY_SERVICE_CATALOG)
    public ResponseEntity<StandardResponse<LaundryServiceCatalogDTO>> updateServiceCatalog(@PathVariable Long id, @RequestBody LaundryServiceCatalogDTO dto) {
        StandardResponse<LaundryServiceCatalogDTO> response = laundryService.updateServiceCatalog(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_ALL_LAUNDRY_SERVICE_CATALOG)
    public ResponseEntity<StandardResponse<List<LaundryServiceCatalogDTO>>> getAllServiceCatalog() {
        StandardResponse<List<LaundryServiceCatalogDTO>> response = laundryService.getAllServiceCatalog();
        return ResponseEntity.ok(response);
    }

    @GetMapping(ServiceConstant.GET_ACTIVE_LAUNDRY_SERVICE_CATALOG)
    public ResponseEntity<StandardResponse<List<LaundryServiceCatalogDTO>>> getActiveServiceCatalog() {
        StandardResponse<List<LaundryServiceCatalogDTO>> response = laundryService.getActiveServiceCatalog();
        return ResponseEntity.ok(response);
    }

    @GetMapping(ServiceConstant.GET_LAUNDRY_SERVICE_CATALOG_BY_ID)
    public ResponseEntity<StandardResponse<LaundryServiceCatalogDTO>> getServiceCatalogById(@PathVariable Long id) {
        StandardResponse<LaundryServiceCatalogDTO> response = laundryService.getServiceCatalogById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping(ServiceConstant.DELETE_LAUNDRY_SERVICE_CATALOG)
    public ResponseEntity<StandardResponse<Void>> deleteServiceCatalog(@PathVariable Long id) {
        StandardResponse<Void> response = laundryService.deleteServiceCatalog(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    // Laundry Order Endpoints

    @PostMapping(ServiceConstant.CREATE_LAUNDRY_ORDER)
    public ResponseEntity<StandardResponse<LaundryOrderDTO>> createOrder(@RequestBody LaundryOrderDTO dto) {
        StandardResponse<LaundryOrderDTO> response = laundryService.createLaundryOrder(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping(ServiceConstant.UPDATE_LAUNDRY_ORDER)
    public ResponseEntity<StandardResponse<LaundryOrderDTO>> updateOrder(@PathVariable Long id, @RequestBody LaundryOrderDTO dto) {
        StandardResponse<LaundryOrderDTO> response = laundryService.updateLaundryOrder(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_ALL_LAUNDRY_ORDERS)
    public ResponseEntity<StandardResponse<List<LaundryOrderDTO>>> getAllOrders() {
        StandardResponse<List<LaundryOrderDTO>> response = laundryService.getAllLaundryOrders();
        return ResponseEntity.ok(response);
    }

    @GetMapping(ServiceConstant.GET_LAUNDRY_ORDER_BY_ID)
    public ResponseEntity<StandardResponse<LaundryOrderDTO>> getOrderById(@PathVariable Long id) {
        StandardResponse<LaundryOrderDTO> response = laundryService.getLaundryOrderById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping(ServiceConstant.UPDATE_LAUNDRY_ORDER_STATUS)
    public ResponseEntity<StandardResponse<LaundryOrderDTO>> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        StandardResponse<LaundryOrderDTO> response = laundryService.updateOrderStatus(id, status);
        HttpStatus httpStatus = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(httpStatus).body(response);
    }

    @DeleteMapping(ServiceConstant.DELETE_LAUNDRY_ORDER)
    public ResponseEntity<StandardResponse<Void>> deleteOrder(@PathVariable Long id) {
        StandardResponse<Void> response = laundryService.deleteLaundryOrder(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
