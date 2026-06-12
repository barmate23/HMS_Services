package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.LaundryOrderDTO;
import com.hotelerp.userservice.dto.LaundryOrderItemDTO;
import com.hotelerp.userservice.dto.LaundryPriceMasterDTO;
import com.hotelerp.userservice.dto.LaundryServiceCatalogDTO;
import com.hotelerp.userservice.entity.LaundryOrder;
import com.hotelerp.userservice.entity.LaundryOrderItem;
import com.hotelerp.userservice.entity.LaundryPriceMaster;
import com.hotelerp.userservice.entity.LaundryServiceCatalog;
import com.hotelerp.userservice.entity.Room;
import com.hotelerp.userservice.repository.LaundryOrderItemRepository;
import com.hotelerp.userservice.repository.LaundryOrderRepository;
import com.hotelerp.userservice.repository.LaundryPriceMasterRepository;
import com.hotelerp.userservice.repository.LaundryServiceCatalogRepository;
import com.hotelerp.userservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LaundryServiceImpl implements LaundryService {

    private final LaundryPriceMasterRepository priceMasterRepository;
    private final LaundryOrderRepository orderRepository;
    private final LaundryOrderItemRepository orderItemRepository;
    private final RoomRepository roomRepository;
    private final LaundryServiceCatalogRepository serviceCatalogRepository;

    // Price Master APIs

    @Override
    public StandardResponse<LaundryPriceMasterDTO> createPriceMaster(LaundryPriceMasterDTO dto) {
        try {
            LaundryPriceMaster entity = LaundryPriceMaster.builder()
                    .category(dto.getCategory())
                    .itemName(dto.getItemName())
                    .washFoldPrice(dto.getWashFoldPrice())
                    .washPressPrice(dto.getWashPressPrice())
                    .dryCleanPrice(dto.getDryCleanPrice())
                    .expressSurchargePercentage(dto.getExpressSurchargePercentage())
                    .servicePrices(normalizeServicePrices(dto.getServicePrices()))
                    .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                    .build();
            entity = priceMasterRepository.save(entity);
            return StandardResponse.success(convertToDTO(entity), "Price Master item created successfully");
        } catch (Exception e) {
            log.error("Error creating Price Master: ", e);
            return StandardResponse.error("Failed to create Price Master item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<LaundryPriceMasterDTO> updatePriceMaster(Long id, LaundryPriceMasterDTO dto) {
        try {
            LaundryPriceMaster entity = priceMasterRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Price Master item not found"));
            entity.setCategory(dto.getCategory());
            entity.setItemName(dto.getItemName());
            entity.setWashFoldPrice(dto.getWashFoldPrice());
            entity.setWashPressPrice(dto.getWashPressPrice());
            entity.setDryCleanPrice(dto.getDryCleanPrice());
            entity.setExpressSurchargePercentage(dto.getExpressSurchargePercentage());
            entity.setServicePrices(normalizeServicePrices(dto.getServicePrices()));
            entity.setStatus(dto.getStatus() != null ? dto.getStatus() : entity.getStatus());
            entity = priceMasterRepository.save(entity);
            return StandardResponse.success(convertToDTO(entity), "Price Master item updated successfully");
        } catch (Exception e) {
            log.error("Error updating Price Master: ", e);
            return StandardResponse.error("Failed to update Price Master item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<LaundryPriceMasterDTO>> getAllPriceMasters() {
        List<LaundryPriceMasterDTO> dtos = priceMasterRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return StandardResponse.success(dtos, "Price Master items fetched successfully");
    }

    @Override
    public StandardResponse<LaundryPriceMasterDTO> getPriceMasterById(Long id) {
        return priceMasterRepository.findById(id)
                .map(entity -> StandardResponse.success(convertToDTO(entity), "Price Master item fetched"))
                .orElse(StandardResponse.error("Price Master item not found", "NOT_FOUND", null));
    }

    @Override
    public StandardResponse<Void> deletePriceMaster(Long id) {
        priceMasterRepository.deleteById(id);
        return StandardResponse.success("Price Master item deleted successfully");
    }

    // Service Catalog APIs

    @Override
    public StandardResponse<LaundryServiceCatalogDTO> createServiceCatalog(LaundryServiceCatalogDTO dto) {
        try {
            if (dto.getServiceName() == null || dto.getServiceName().trim().isEmpty()) {
                return StandardResponse.error("Service name is required", "VALIDATION_ERROR", "serviceName", "Service name cannot be blank");
            }
            if (serviceCatalogRepository.existsByServiceNameIgnoreCase(dto.getServiceName().trim())) {
                return StandardResponse.error("Service already exists", "DUPLICATE_SERVICE", "serviceName", dto.getServiceName());
            }

            LaundryServiceCatalog entity = LaundryServiceCatalog.builder()
                    .serviceName(dto.getServiceName().trim())
                    .pricingBasis(defaultString(dto.getPricingBasis(), "washPress"))
                    .description(dto.getDescription())
                    .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                    .status(defaultString(dto.getStatus(), "ACTIVE"))
                    .build();
            entity = serviceCatalogRepository.save(entity);
            return StandardResponse.success(convertToDTO(entity), "Laundry service created successfully");
        } catch (Exception e) {
            log.error("Error creating laundry service catalog: ", e);
            return StandardResponse.error("Failed to create laundry service", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<LaundryServiceCatalogDTO> updateServiceCatalog(Long id, LaundryServiceCatalogDTO dto) {
        try {
            LaundryServiceCatalog entity = serviceCatalogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Laundry service not found"));
            String serviceName = dto.getServiceName() != null ? dto.getServiceName().trim() : entity.getServiceName();
            serviceCatalogRepository.findByServiceNameIgnoreCase(serviceName)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("Service name already exists");
                    });

            entity.setServiceName(serviceName);
            entity.setPricingBasis(defaultString(dto.getPricingBasis(), entity.getPricingBasis()));
            entity.setDescription(dto.getDescription());
            entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : entity.getDisplayOrder());
            entity.setStatus(defaultString(dto.getStatus(), entity.getStatus()));
            entity = serviceCatalogRepository.save(entity);
            return StandardResponse.success(convertToDTO(entity), "Laundry service updated successfully");
        } catch (Exception e) {
            log.error("Error updating laundry service catalog: ", e);
            return StandardResponse.error("Failed to update laundry service", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<LaundryServiceCatalogDTO>> getAllServiceCatalog() {
        seedDefaultServiceCatalogIfEmpty();
        List<LaundryServiceCatalogDTO> dtos = serviceCatalogRepository.findAllByOrderByDisplayOrderAscServiceNameAsc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return StandardResponse.success(dtos, "Laundry service catalog fetched successfully");
    }

    @Override
    public StandardResponse<List<LaundryServiceCatalogDTO>> getActiveServiceCatalog() {
        seedDefaultServiceCatalogIfEmpty();
        List<LaundryServiceCatalogDTO> dtos = serviceCatalogRepository.findByStatusOrderByDisplayOrderAscServiceNameAsc("ACTIVE").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return StandardResponse.success(dtos, "Active laundry services fetched successfully");
    }

    @Override
    public StandardResponse<LaundryServiceCatalogDTO> getServiceCatalogById(Long id) {
        return serviceCatalogRepository.findById(id)
                .map(entity -> StandardResponse.success(convertToDTO(entity), "Laundry service fetched"))
                .orElse(StandardResponse.error("Laundry service not found", "NOT_FOUND", null));
    }

    @Override
    public StandardResponse<Void> deleteServiceCatalog(Long id) {
        serviceCatalogRepository.deleteById(id);
        return StandardResponse.success("Laundry service deleted successfully");
    }

    // Laundry Order APIs

    @Override
    @Transactional
    public StandardResponse<LaundryOrderDTO> createLaundryOrder(LaundryOrderDTO dto) {
        try {
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            String orderId = generateOrderId();

            LaundryOrder order = LaundryOrder.builder()
                    .orderId(orderId)
                    .room(room)
                    .guestName(dto.getGuestName())
                    .serviceType(dto.getServiceType())
                    .billingOption(dto.getBillingOption())
                    .pickupDatetime(dto.getPickupDatetime())
                    .expectedDelivery(dto.getExpectedDelivery())
                    .specialInstructions(dto.getSpecialInstructions())
                    .status(dto.getStatus() != null ? dto.getStatus() : "PENDING")
                    .build();

            double totalAmount = 0;
            for (LaundryOrderItemDTO itemDto : dto.getItems()) {
                LaundryPriceMaster priceMaster = priceMasterRepository.findById(itemDto.getPriceMasterId())
                        .orElseThrow(() -> new RuntimeException("Item not found in Price Master: " + itemDto.getPriceMasterId()));
                double unitPrice = getPriceForService(priceMaster, dto.getServiceType());
                totalAmount += unitPrice * itemDto.getQuantity();
            }

            order.setTotalAmount(totalAmount);
            order = orderRepository.save(order);

            // Save items using repository
            for (LaundryOrderItemDTO itemDto : dto.getItems()) {
                LaundryPriceMaster priceMaster = priceMasterRepository.findById(itemDto.getPriceMasterId())
                        .orElseThrow(() -> new RuntimeException("Item not found in Price Master"));

                double unitPrice = getPriceForService(priceMaster, dto.getServiceType());
                double itemTotal = unitPrice * itemDto.getQuantity();

                LaundryOrderItem item = LaundryOrderItem.builder()
                        .laundryOrder(order)
                        .priceMaster(priceMaster)
                        .quantity(itemDto.getQuantity())
                        .unitPrice(unitPrice)
                        .total(itemTotal)
                        .notes(itemDto.getNotes())
                        .build();
                orderItemRepository.save(item);
            }

            return StandardResponse.success(convertToDTO(order), "Laundry order created successfully");
        } catch (Exception e) {
            log.error("Error creating laundry order: ", e);
            return StandardResponse.error("Failed to create laundry order", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<LaundryOrderDTO> updateLaundryOrder(Long id, LaundryOrderDTO dto) {
        try {
            LaundryOrder order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            order.setServiceType(dto.getServiceType());
            order.setBillingOption(dto.getBillingOption());
            order.setPickupDatetime(dto.getPickupDatetime());
            order.setExpectedDelivery(dto.getExpectedDelivery());
            order.setSpecialInstructions(dto.getSpecialInstructions());
            order.setGuestName(dto.getGuestName());
            if (dto.getStatus() != null) order.setStatus(dto.getStatus());

            double totalAmount = 0;
            for (LaundryOrderItemDTO itemDto : dto.getItems()) {
                LaundryPriceMaster priceMaster = priceMasterRepository.findById(itemDto.getPriceMasterId())
                        .orElseThrow(() -> new RuntimeException("Price master item not found"));
                double unitPrice = getPriceForService(priceMaster, dto.getServiceType());
                totalAmount += unitPrice * itemDto.getQuantity();
            }

            // Update total and save order first
            order.setTotalAmount(totalAmount);
            order = orderRepository.save(order);

            // Clear old items and save new ones using repository
            orderItemRepository.deleteByLaundryOrderId(order.getId());
            for (LaundryOrderItemDTO itemDto : dto.getItems()) {
                LaundryPriceMaster priceMaster = priceMasterRepository.findById(itemDto.getPriceMasterId())
                        .orElseThrow(() -> new RuntimeException("Price master item not found"));
                double unitPrice = getPriceForService(priceMaster, dto.getServiceType());
                double itemTotal = unitPrice * itemDto.getQuantity();

                LaundryOrderItem item = LaundryOrderItem.builder()
                        .laundryOrder(order)
                        .priceMaster(priceMaster)
                        .quantity(itemDto.getQuantity())
                        .unitPrice(unitPrice)
                        .total(itemTotal)
                        .notes(itemDto.getNotes())
                        .build();
                orderItemRepository.save(item);
            }

            return StandardResponse.success(convertToDTO(order), "Laundry order updated successfully");
        } catch (Exception e) {
            log.error("Error updating laundry order: ", e);
            return StandardResponse.error("Failed to update laundry order", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<LaundryOrderDTO> getLaundryOrderById(Long id) {
        return orderRepository.findById(id)
                .map(order -> StandardResponse.success(convertToDTO(order), "Order fetched"))
                .orElse(StandardResponse.error("Order not found", "NOT_FOUND", null));
    }

    @Override
    public StandardResponse<List<LaundryOrderDTO>> getAllLaundryOrders() {
        List<LaundryOrderDTO> dtos = orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return StandardResponse.success(dtos, "Orders fetched successfully");
    }

    @Override
    public StandardResponse<LaundryOrderDTO> updateOrderStatus(Long id, String status) {
        try {
            LaundryOrder order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            order.setStatus(status);
            order = orderRepository.save(order);
            return StandardResponse.success(convertToDTO(order), "Order status updated");
        } catch (Exception e) {
            return StandardResponse.error("Failed to update status", "ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<Void> deleteLaundryOrder(Long id) {
        orderRepository.deleteById(id);
        return StandardResponse.success("Order deleted successfully");
    }

    // Helper methods

    private LaundryPriceMasterDTO convertToDTO(LaundryPriceMaster entity) {
        return LaundryPriceMasterDTO.builder()
                .id(entity.getId())
                .category(entity.getCategory())
                .itemName(entity.getItemName())
                .washFoldPrice(entity.getWashFoldPrice())
                .washPressPrice(entity.getWashPressPrice())
                .dryCleanPrice(entity.getDryCleanPrice())
                .expressSurchargePercentage(entity.getExpressSurchargePercentage())
                .servicePrices(servicePricesForDTO(entity))
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private LaundryServiceCatalogDTO convertToDTO(LaundryServiceCatalog entity) {
        return LaundryServiceCatalogDTO.builder()
                .id(entity.getId())
                .serviceName(entity.getServiceName())
                .pricingBasis(entity.getPricingBasis())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private LaundryOrderDTO convertToDTO(LaundryOrder entity) {
        List<LaundryOrderItem> items = orderItemRepository.findByLaundryOrderId(entity.getId());
        return LaundryOrderDTO.builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .roomId(entity.getRoom().getId())
                .roomNumber(entity.getRoom().getRoomNumber())
                .floorNumber(entity.getRoom().getFloor() != null ? entity.getRoom().getFloor().getFloorNumber() : null)
                .guestName(entity.getGuestName())
                .serviceType(entity.getServiceType())
                .billingOption(entity.getBillingOption())
                .pickupDatetime(entity.getPickupDatetime())
                .expectedDelivery(entity.getExpectedDelivery())
                .specialInstructions(entity.getSpecialInstructions())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .items(items.stream().map(this::convertToDTO).collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private LaundryOrderItemDTO convertToDTO(LaundryOrderItem item) {
        return LaundryOrderItemDTO.builder()
                .id(item.getId())
                .priceMasterId(item.getPriceMaster().getId())
                .itemName(item.getPriceMaster().getItemName())
                .category(item.getPriceMaster().getCategory())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .total(item.getTotal())
                .notes(item.getNotes())
                .build();
    }

    private String generateOrderId() {
        Long maxId = orderRepository.findMaxOrderNumber();
        long nextId = (maxId == null ? 1000 : maxId) + 1;
        return "LND-" + nextId;
    }

    private Double getPriceForService(LaundryPriceMaster item, String serviceType) {
        String normalized = normalizeServiceName(serviceType);
        if (item.getServicePrices() != null && item.getServicePrices().containsKey(normalized)) {
            return item.getServicePrices().get(normalized) != null ? item.getServicePrices().get(normalized) : 0.0;
        }
        if ("Wash & Fold".equalsIgnoreCase(serviceType)) return item.getWashFoldPrice() != null ? item.getWashFoldPrice() : 0.0;
        if ("Wash & Press".equalsIgnoreCase(serviceType)) return item.getWashPressPrice() != null ? item.getWashPressPrice() : 0.0;
        if ("Dry Clean".equalsIgnoreCase(serviceType)) return item.getDryCleanPrice() != null ? item.getDryCleanPrice() : 0.0;
        if ("Express".equalsIgnoreCase(serviceType)) {
            double base = item.getWashFoldPrice() != null ? item.getWashFoldPrice() : 0.0;
            double surcharge = item.getExpressSurchargePercentage() != null ? item.getExpressSurchargePercentage() : 0.0;
            return base * (1 + surcharge / 100);
        }
        return 0.0;
    }

    private Map<String, Double> normalizeServicePrices(Map<String, Double> servicePrices) {
        Map<String, Double> normalized = new LinkedHashMap<>();
        if (servicePrices == null) return normalized;
        servicePrices.forEach((serviceName, price) -> {
            String key = normalizeServiceName(serviceName);
            if (!key.isEmpty()) normalized.put(key, price != null ? price : 0.0);
        });
        return normalized;
    }

    private Map<String, Double> servicePricesForDTO(LaundryPriceMaster entity) {
        Map<String, Double> prices = new LinkedHashMap<>();
        if (entity.getServicePrices() != null) prices.putAll(entity.getServicePrices());
        putIfMissing(prices, "Wash & Fold", entity.getWashFoldPrice());
        putIfMissing(prices, "Wash & Press", entity.getWashPressPrice());
        putIfMissing(prices, "Dry Clean", entity.getDryCleanPrice());
        return prices;
    }

    private void putIfMissing(Map<String, Double> prices, String serviceName, Double price) {
        String key = normalizeServiceName(serviceName);
        if (!prices.containsKey(key) && price != null) prices.put(key, price);
    }

    private String normalizeServiceName(String serviceName) {
        return String.valueOf(serviceName == null ? "" : serviceName)
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    private String defaultString(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private void seedDefaultServiceCatalogIfEmpty() {
        if (serviceCatalogRepository.count() > 0) return;
        List<LaundryServiceCatalog> defaults = List.of(
                LaundryServiceCatalog.builder().serviceName("Wash & Fold").pricingBasis("washFold").description("Standard wash, dry and folded packaging.").displayOrder(1).status("ACTIVE").build(),
                LaundryServiceCatalog.builder().serviceName("Wash & Press").pricingBasis("washPress").description("Washed garments with pressed finish.").displayOrder(2).status("ACTIVE").build(),
                LaundryServiceCatalog.builder().serviceName("Dry Clean").pricingBasis("dryClean").description("Premium care for delicate garments.").displayOrder(3).status("ACTIVE").build(),
                LaundryServiceCatalog.builder().serviceName("Express").pricingBasis("express").description("Priority room pickup and delivery surcharge.").displayOrder(4).status("ACTIVE").build()
        );
        serviceCatalogRepository.saveAll(defaults);
    }
}
