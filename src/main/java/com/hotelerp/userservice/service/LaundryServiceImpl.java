package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.config.LoginUser;
import com.hotelerp.userservice.dto.LaundryOrderDTO;
import com.hotelerp.userservice.dto.LaundryOrderItemDTO;
import com.hotelerp.userservice.dto.LaundryPriceMasterDTO;
import com.hotelerp.userservice.dto.LaundryServiceCatalogDTO;
import com.hotelerp.userservice.entity.Hotel;
import com.hotelerp.userservice.entity.LaundryOrder;
import com.hotelerp.userservice.entity.LaundryOrderItem;
import com.hotelerp.userservice.entity.LaundryPriceMaster;
import com.hotelerp.userservice.entity.LaundryServiceCatalog;
import com.hotelerp.userservice.entity.Room;
import com.hotelerp.userservice.repository.HotelRepository;
import com.hotelerp.userservice.repository.LaundryOrderItemRepository;
import com.hotelerp.userservice.repository.LaundryOrderRepository;
import com.hotelerp.userservice.repository.LaundryPriceMasterRepository;
import com.hotelerp.userservice.repository.LaundryServiceCatalogRepository;
import com.hotelerp.userservice.repository.RoomRepository;
import com.hotelerp.userservice.repository.BookingRepository;
import com.hotelerp.userservice.repository.GstRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final FolioService folioService;
    private final BookingRepository bookingRepository;
    private final GstRuleRepository gstRuleRepository;
    private final HotelRepository hotelRepository;
    private final LoginUser loginUser;

    // Price Master APIs

    @Override
    public StandardResponse<LaundryPriceMasterDTO> createPriceMaster(LaundryPriceMasterDTO dto) {
        try {
            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId()
                    : dto.getHotelId();
            Hotel hotel = null;
            if (hotelId != null) {
                hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
            }

            LaundryPriceMaster entity = LaundryPriceMaster.builder()
                    .hotel(hotel)
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
            return StandardResponse.error("Failed to create Price Master item", "INTERNAL_SERVER_ERROR",
                    e.getMessage());
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

            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId()
                    : dto.getHotelId();
            if (hotelId != null && entity.getHotel() == null) {
                Hotel hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
                entity.setHotel(hotel);
            }

            entity = priceMasterRepository.save(entity);
            return StandardResponse.success(convertToDTO(entity), "Price Master item updated successfully");
        } catch (Exception e) {
            log.error("Error updating Price Master: ", e);
            return StandardResponse.error("Failed to update Price Master item", "INTERNAL_SERVER_ERROR",
                    e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<LaundryPriceMasterDTO>> getAllPriceMasters() {
        Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
        List<LaundryPriceMaster> list = (hotelId != null)
                ? priceMasterRepository.findByHotel_Id(hotelId)
                : priceMasterRepository.findAll();
        List<LaundryPriceMasterDTO> dtos = list.stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
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
        LaundryPriceMaster entity = priceMasterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Price Master item not found with ID: " + id));
        entity.setIsDeleted(true);
        priceMasterRepository.save(entity);
        return StandardResponse.success("Price Master item deleted successfully");
    }

    // Service Catalog APIs

    @Override
    public StandardResponse<LaundryServiceCatalogDTO> createServiceCatalog(LaundryServiceCatalogDTO dto) {
        try {
            if (dto.getServiceName() == null || dto.getServiceName().trim().isEmpty()) {
                return StandardResponse.error("Service name is required", "VALIDATION_ERROR", "serviceName",
                        "Service name cannot be blank");
            }

            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId()
                    : dto.getHotelId();

            boolean exists = (hotelId != null)
                    ? serviceCatalogRepository.existsByServiceNameIgnoreCaseAndHotel_Id(dto.getServiceName().trim(), hotelId)
                    : serviceCatalogRepository.existsByServiceNameIgnoreCaseAndHotelIsNull(dto.getServiceName().trim());

            if (exists) {
                return StandardResponse.error("Service already exists", "DUPLICATE_SERVICE", "serviceName",
                        dto.getServiceName());
            }

            Hotel hotel = null;
            if (hotelId != null) {
                hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
            }

            LaundryServiceCatalog entity = LaundryServiceCatalog.builder()
                    .hotel(hotel)
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

            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId()
                    : dto.getHotelId();

            String serviceName = dto.getServiceName() != null ? dto.getServiceName().trim() : entity.getServiceName();
            
            Optional<LaundryServiceCatalog> existingCatalog = (hotelId != null)
                    ? serviceCatalogRepository.findByServiceNameIgnoreCaseAndHotel_Id(serviceName, hotelId)
                    : serviceCatalogRepository.findByServiceNameIgnoreCaseAndHotelIsNull(serviceName);

            existingCatalog.filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("Service name already exists");
                    });

            entity.setServiceName(serviceName);
            entity.setPricingBasis(defaultString(dto.getPricingBasis(), entity.getPricingBasis()));
            entity.setDescription(dto.getDescription());
            entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : entity.getDisplayOrder());
            entity.setStatus(defaultString(dto.getStatus(), entity.getStatus()));

            if (hotelId != null && entity.getHotel() == null) {
                Hotel hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
                entity.setHotel(hotel);
            }

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
        Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
        List<LaundryServiceCatalog> list = (hotelId != null)
                ? serviceCatalogRepository.findByHotel_IdOrderByDisplayOrderAscServiceNameAsc(hotelId)
                : serviceCatalogRepository.findAllByOrderByDisplayOrderAscServiceNameAsc();
        List<LaundryServiceCatalogDTO> dtos = list.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return StandardResponse.success(dtos, "Laundry service catalog fetched successfully");
    }

    @Override
    public StandardResponse<List<LaundryServiceCatalogDTO>> getActiveServiceCatalog() {
        seedDefaultServiceCatalogIfEmpty();
        Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
        List<LaundryServiceCatalog> list = (hotelId != null)
                ? serviceCatalogRepository.findByHotel_IdAndStatusOrderByDisplayOrderAscServiceNameAsc(hotelId,
                        "ACTIVE")
                : serviceCatalogRepository.findByStatusOrderByDisplayOrderAscServiceNameAsc("ACTIVE");
        List<LaundryServiceCatalogDTO> dtos = list.stream()
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

            double gstPercent = gstRuleRepository
                    .findByServiceCategoryIgnoreCaseAndHotelIdAndIsActiveTrue("Laundry", loginUser.getHotelId())
                    .map(r -> r.getIgstRate().doubleValue())
                    .orElse(0.0);

            String orderId = generateOrderId();

            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId()
                    : dto.getHotelId();
            Hotel hotel = null;
            if (hotelId != null) {
                hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
            }

            double totalAmount = 0;
            List<String> allOrderServices = new ArrayList<>();

            for (LaundryOrderItemDTO itemDto : dto.getItems()) {
                LaundryPriceMaster priceMaster = priceMasterRepository.findById(itemDto.getPriceMasterId())
                        .orElseThrow(() -> new RuntimeException(
                                "Item not found in Price Master: " + itemDto.getPriceMasterId()));
                List<String> itemServices = selectedServicesForItem(itemDto, dto);
                if (itemServices.isEmpty()) {
                    throw new RuntimeException("At least one service must be selected for item: " + priceMaster.getItemName());
                }
                allOrderServices.addAll(itemServices);
                double basePrice = getPriceForServices(priceMaster, itemServices);
                double unitPrice = basePrice * (1 + gstPercent / 100.0);
                totalAmount += unitPrice * itemDto.getQuantity();
            }

            String summaryServiceType = joinServices(allOrderServices.stream().distinct().collect(Collectors.toList()));

            LaundryOrder order = LaundryOrder.builder()
                    .hotel(hotel)
                    .orderId(orderId)
                    .room(room)
                    .guestName(dto.getGuestName())
                    .serviceType(summaryServiceType)
                    .billingOption(dto.getBillingOption())
                    .pickupDatetime(dto.getPickupDatetime())
                    .expectedDelivery(dto.getExpectedDelivery())
                    .specialInstructions(dto.getSpecialInstructions())
                    .status(dto.getStatus() != null ? dto.getStatus() : "PENDING")
                    .gstPercent(gstPercent)
                    .totalAmount(totalAmount)
                    .build();

            order = orderRepository.save(order);

            // Save items using repository
            for (LaundryOrderItemDTO itemDto : dto.getItems()) {
                LaundryPriceMaster priceMaster = priceMasterRepository.findById(itemDto.getPriceMasterId())
                        .orElseThrow(() -> new RuntimeException("Item not found in Price Master"));

                List<String> itemServices = selectedServicesForItem(itemDto, dto);
                String itemServiceType = joinServices(itemServices);
                double basePrice = getPriceForServices(priceMaster, itemServices);
                double unitPrice = basePrice ;
                double itemTotal = unitPrice * itemDto.getQuantity();

                LaundryOrderItem item = LaundryOrderItem.builder()
                        .hotel(hotel)
                        .laundryOrder(order)
                        .priceMaster(priceMaster)
                        .quantity(itemDto.getQuantity())
                        .unitPrice(unitPrice)
                        .total(itemTotal)
                        .serviceType(itemServiceType)
                        .notes(itemDto.getNotes())
                        .build();
                orderItemRepository.save(item);
            }

            if ("Room".equalsIgnoreCase(order.getBillingOption())) {
                StandardResponse<Void> folioResponse = folioService.postChargeByRoom(order.getRoom().getId(),
                        java.math.BigDecimal.valueOf(order.getTotalAmount()),
                        "Laundry",
                        "Laundry Order: " + order.getOrderId());
                if (!folioResponse.isSuccess()) {
                    throw new RuntimeException("Failed to post charge to folio: " + folioResponse.getMessage());
                }
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

            double gstPercent = gstRuleRepository
                    .findByServiceCategoryIgnoreCaseAndHotelIdAndIsActiveTrue("Laundry", loginUser.getHotelId())
                    .map(r -> r.getIgstRate().doubleValue())
                    .orElse(0.0);

            double totalAmount = 0;
            List<String> allOrderServices = new ArrayList<>();
            for (LaundryOrderItemDTO itemDto : dto.getItems()) {
                LaundryPriceMaster priceMaster = priceMasterRepository.findById(itemDto.getPriceMasterId())
                        .orElseThrow(() -> new RuntimeException("Price master item not found"));
                List<String> itemServices = selectedServicesForItem(itemDto, dto);
                if (itemServices.isEmpty()) {
                    throw new RuntimeException("At least one service must be selected for item: " + priceMaster.getItemName());
                }
                allOrderServices.addAll(itemServices);
                double basePrice = getPriceForServices(priceMaster, itemServices);
                double unitPrice = basePrice * (1 + gstPercent / 100.0);
                totalAmount += unitPrice * itemDto.getQuantity();
            }

            String summaryServiceType = joinServices(allOrderServices.stream().distinct().collect(Collectors.toList()));
            order.setServiceType(summaryServiceType);
            order.setBillingOption(dto.getBillingOption());
            order.setPickupDatetime(dto.getPickupDatetime());
            order.setExpectedDelivery(dto.getExpectedDelivery());
            order.setSpecialInstructions(dto.getSpecialInstructions());
            order.setGuestName(dto.getGuestName());
            if (dto.getStatus() != null)
                order.setStatus(dto.getStatus());

            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId()
                    : dto.getHotelId();
            if (hotelId != null && order.getHotel() == null) {
                Hotel hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
                order.setHotel(hotel);
            }

            // Update total and save order first
            order.setTotalAmount(totalAmount);
            order.setGstPercent(gstPercent);
            order = orderRepository.save(order);

            Hotel orderHotel = order.getHotel();

            // Clear old items and save new ones using repository
            orderItemRepository.deleteByLaundryOrderId(order.getId());
            for (LaundryOrderItemDTO itemDto : dto.getItems()) {
                LaundryPriceMaster priceMaster = priceMasterRepository.findById(itemDto.getPriceMasterId())
                        .orElseThrow(() -> new RuntimeException("Price master item not found"));
                List<String> itemServices = selectedServicesForItem(itemDto, dto);
                String itemServiceType = joinServices(itemServices);
                double basePrice = getPriceForServices(priceMaster, itemServices);
                double unitPrice = basePrice * (1 + gstPercent / 100.0);
                double itemTotal = unitPrice * itemDto.getQuantity();

                LaundryOrderItem item = LaundryOrderItem.builder()
                        .hotel(orderHotel)
                        .laundryOrder(order)
                        .priceMaster(priceMaster)
                        .quantity(itemDto.getQuantity())
                        .unitPrice(unitPrice)
                        .total(itemTotal)
                        .serviceType(itemServiceType)
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
        Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
        List<LaundryOrder> list = (hotelId != null)
                ? orderRepository.findByHotel_IdAndIsDeletedFalse(hotelId)
                : orderRepository.findByIsDeletedFalse();
        List<LaundryOrderDTO> dtos = list.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return StandardResponse.success(dtos, "Orders fetched successfully");
    }

    @Override
    public StandardResponse<List<LaundryOrderDTO>> getNonDeliveredOrders() {
        Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
        List<LaundryOrder> list = (hotelId != null)
                ? orderRepository.findByHotel_IdAndStatusNotAndIsDeletedFalse(hotelId, "DELIVERED")
                : orderRepository.findByStatusNotAndIsDeletedFalse("DELIVERED");
        List<LaundryOrderDTO> dtos = list.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return StandardResponse.success(dtos, "Non-delivered orders fetched successfully");
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
        LaundryOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laundry order not found with ID: " + id));
        order.setIsDeleted(true);
        orderRepository.save(order);
        return StandardResponse.success("Order deleted successfully");
    }

    // Helper methods

    private LaundryPriceMasterDTO convertToDTO(LaundryPriceMaster entity) {
        return LaundryPriceMasterDTO.builder()
                .id(entity.getId())
                .hotelId(entity.getHotel() != null ? entity.getHotel().getId() : null)
                .hotelName(entity.getHotel() != null ? entity.getHotel().getName() : null)
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
                .hotelId(entity.getHotel() != null ? entity.getHotel().getId() : null)
                .hotelName(entity.getHotel() != null ? entity.getHotel().getName() : null)
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
                .hotelId(entity.getHotel() != null ? entity.getHotel().getId() : null)
                .hotelName(entity.getHotel() != null ? entity.getHotel().getName() : null)
                .orderId(entity.getOrderId())
                .roomId(entity.getRoom().getId())
                .roomNumber(entity.getRoom().getRoomNumber())
                .floorNumber(entity.getRoom().getFloor() != null ? entity.getRoom().getFloor().getFloorNumber() : null)
                .guestName(entity.getGuestName())
                .serviceType(entity.getServiceType())
                .serviceTypes(splitServices(entity.getServiceType()))
                .billingOption(entity.getBillingOption())
                .pickupDatetime(entity.getPickupDatetime())
                .expectedDelivery(entity.getExpectedDelivery())
                .specialInstructions(entity.getSpecialInstructions())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .gstPercent(entity.getGstPercent())
                .items(items.stream().map(this::convertToDTO).collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private LaundryOrderItemDTO convertToDTO(LaundryOrderItem item) {
        return LaundryOrderItemDTO.builder()
                .id(item.getId())
                .hotelId(item.getHotel() != null ? item.getHotel().getId() : null)
                .hotelName(item.getHotel() != null ? item.getHotel().getName() : null)
                .priceMasterId(item.getPriceMaster().getId())
                .itemName(item.getPriceMaster().getItemName())
                .category(item.getPriceMaster().getCategory())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .total(item.getTotal())
                .serviceType(item.getServiceType())
                .serviceTypes(splitServices(item.getServiceType()))
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
            Double configuredPrice = item.getServicePrices().get(normalized);
            if (configuredPrice != null && configuredPrice > 0)
                return configuredPrice;
        }
        String pricingBasis = pricingBasisForService(serviceType);
        if ("washFold".equals(pricingBasis))
            return item.getWashFoldPrice() != null ? item.getWashFoldPrice() : 0.0;
        if ("washPress".equals(pricingBasis))
            return item.getWashPressPrice() != null ? item.getWashPressPrice() : 0.0;
        if ("dryClean".equals(pricingBasis))
            return item.getDryCleanPrice() != null ? item.getDryCleanPrice() : 0.0;
        if ("express".equals(pricingBasis)) {
            double base = item.getWashPressPrice() != null ? item.getWashPressPrice()
                    : item.getWashFoldPrice() != null ? item.getWashFoldPrice() : 0.0;
            double surcharge = item.getExpressSurchargePercentage() != null ? item.getExpressSurchargePercentage()
                    : 0.0;
            return base * (1 + surcharge / 100);
        }
        if ("Wash & Fold".equalsIgnoreCase(serviceType))
            return item.getWashFoldPrice() != null ? item.getWashFoldPrice() : 0.0;
        if ("Wash & Press".equalsIgnoreCase(serviceType))
            return item.getWashPressPrice() != null ? item.getWashPressPrice() : 0.0;
        if ("Dry Clean".equalsIgnoreCase(serviceType))
            return item.getDryCleanPrice() != null ? item.getDryCleanPrice() : 0.0;
        if ("Express".equalsIgnoreCase(serviceType)) {
            double base = item.getWashFoldPrice() != null ? item.getWashFoldPrice() : 0.0;
            double surcharge = item.getExpressSurchargePercentage() != null ? item.getExpressSurchargePercentage()
                    : 0.0;
            return base * (1 + surcharge / 100);
        }
        return 0.0;
    }

    private Double getPriceForServices(LaundryPriceMaster item, List<String> serviceTypes) {
        return serviceTypes.stream()
                .mapToDouble(service -> getPriceForService(item, service))
                .sum();
    }

    private List<String> selectedServicesForItem(LaundryOrderItemDTO itemDto, LaundryOrderDTO orderDto) {
        if (itemDto != null) {
            if (itemDto.getServiceTypes() != null && !itemDto.getServiceTypes().isEmpty()) {
                List<String> services = itemDto.getServiceTypes().stream()
                        .filter(service -> service != null && !service.trim().isEmpty())
                        .map(String::trim)
                        .distinct()
                        .collect(Collectors.toList());
                if (!services.isEmpty()) return services;
            }
            if (itemDto.getServiceType() != null && !itemDto.getServiceType().trim().isEmpty()) {
                List<String> services = splitServices(itemDto.getServiceType());
                if (!services.isEmpty()) return services;
            }
        }
        if (orderDto != null) {
            if (orderDto.getServiceTypes() != null && !orderDto.getServiceTypes().isEmpty()) {
                List<String> services = orderDto.getServiceTypes().stream()
                        .filter(service -> service != null && !service.trim().isEmpty())
                        .map(String::trim)
                        .distinct()
                        .collect(Collectors.toList());
                if (!services.isEmpty()) return services;
            }
            if (orderDto.getServiceType() != null && !orderDto.getServiceType().trim().isEmpty()) {
                List<String> services = splitServices(orderDto.getServiceType());
                if (!services.isEmpty()) return services;
            }
        }
        return List.of();
    }

    private List<String> selectedServices(LaundryOrderDTO dto) {
        List<String> services;
        if (dto.getServiceTypes() != null && !dto.getServiceTypes().isEmpty()) {
            services = dto.getServiceTypes().stream()
                    .filter(service -> service != null && !service.trim().isEmpty())
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());
        } else {
            services = splitServices(dto.getServiceType());
        }
        if (services.isEmpty()) {
            throw new RuntimeException("At least one laundry service must be selected");
        }
        return services;
    }

    private List<String> splitServices(String serviceType) {
        if (serviceType == null || serviceType.trim().isEmpty())
            return List.of();
        return List.of(serviceType.split(","))
                .stream()
                .map(String::trim)
                .filter(service -> !service.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private String joinServices(List<String> serviceTypes) {
        return String.join(", ", serviceTypes);
    }

    private String pricingBasisForService(String serviceType) {
        String normalized = normalizeServiceName(serviceType);
        return serviceCatalogRepository.findAll().stream()
                .filter(service -> normalizeServiceName(service.getServiceName()).equals(normalized))
                .map(LaundryServiceCatalog::getPricingBasis)
                .findFirst()
                .orElse("");
    }

    private Map<String, Double> normalizeServicePrices(Map<String, Double> servicePrices) {
        Map<String, Double> normalized = new LinkedHashMap<>();
        if (servicePrices == null)
            return normalized;
        servicePrices.forEach((serviceName, price) -> {
            String key = normalizeServiceName(serviceName);
            if (!key.isEmpty())
                normalized.put(key, price != null ? price : 0.0);
        });
        return normalized;
    }

    private Map<String, Double> servicePricesForDTO(LaundryPriceMaster entity) {
        Map<String, Double> prices = new LinkedHashMap<>();
        if (entity.getServicePrices() != null)
            prices.putAll(entity.getServicePrices());
        putIfMissing(prices, "Wash & Fold", entity.getWashFoldPrice());
        putIfMissing(prices, "Wash & Press", entity.getWashPressPrice());
        putIfMissing(prices, "Dry Clean", entity.getDryCleanPrice());
        return prices;
    }

    private void putIfMissing(Map<String, Double> prices, String serviceName, Double price) {
        String key = normalizeServiceName(serviceName);
        if (!prices.containsKey(key) && price != null)
            prices.put(key, price);
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
        if (serviceCatalogRepository.count() > 0)
            return;
        List<LaundryServiceCatalog> defaults = List.of(
                LaundryServiceCatalog.builder().serviceName("Wash & Fold").pricingBasis("washFold")
                        .description("Standard wash, dry and folded packaging.").displayOrder(1).status("ACTIVE")
                        .build(),
                LaundryServiceCatalog.builder().serviceName("Wash & Press").pricingBasis("washPress")
                        .description("Washed garments with pressed finish.").displayOrder(2).status("ACTIVE").build(),
                LaundryServiceCatalog.builder().serviceName("Dry Clean").pricingBasis("dryClean")
                        .description("Premium care for delicate garments.").displayOrder(3).status("ACTIVE").build(),
                LaundryServiceCatalog.builder().serviceName("Express").pricingBasis("express")
                        .description("Priority room pickup and delivery surcharge.").displayOrder(4).status("ACTIVE")
                        .build());
        serviceCatalogRepository.saveAll(defaults);
    }
}
