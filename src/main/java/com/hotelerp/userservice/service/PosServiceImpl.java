package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.KitchenOrderCardDTO;
import com.hotelerp.userservice.dto.KitchenOrderItemDTO;
import com.hotelerp.userservice.dto.PosOrderDTO;
import com.hotelerp.userservice.dto.PosOrderItemDTO;
import com.hotelerp.userservice.dto.TableReservationDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import com.hotelerp.userservice.service.FolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosServiceImpl implements PosService {

    private final PosOrderRepository posOrderRepository;
    private final TableReservationRepository tableReservationRepository;
    private final OutletRepository outletRepository;
    private final DiningTableRepository diningTableRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final CommonMasterRepository commonMasterRepository;
    private final FolioService folioService;

    @Override
    @Transactional
    public StandardResponse<Void> createOrder(PosOrderDTO dto) {
        try {
            Outlet outlet = outletRepository.findById(dto.getOutletId())
                    .orElseThrow(() -> new ResourceNotFoundException("Outlet not found with ID: " + dto.getOutletId()));

            CommonMaster orderType = null;
            if (dto.getOrderTypeId() != null) {
                orderType = commonMasterRepository.findById(dto.getOrderTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Order type master data not found for ID: " + dto.getOrderTypeId()));
            }

            DiningTable table = null;
            if (dto.getTableId() != null) {
                table = diningTableRepository.findById(dto.getTableId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Dining table not found with ID: " + dto.getTableId()));

                // Block if table already has an active order
                List<String> activeCodes = List.of("OPEN");
                List<PosOrder> activeOrders = posOrderRepository
                        .findByDiningTableIdAndStatusCodeInAndIsDeletedFalse(dto.getTableId(), activeCodes);
                if (!activeOrders.isEmpty()) {
                    return StandardResponse.error(
                            "Table " + table.getTableNumber()
                                    + " already has an active order. Please close it before creating a new one.",
                            "ACTIVE_ORDER_EXISTS",
                            "Active order ID: " + activeOrders.get(0).getId());
                }

                CommonMaster tableStatus = commonMasterRepository.findByCategoryAndCode("TABLE_STATUS", "OCCUPIED")
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Status 'OCCUPIED' with category 'TABLE_STATUS' not found in master data"));
                table.setStatus(tableStatus);
                diningTableRepository.save(table);
            }

            Room room = null;
            if (dto.getRoomId() != null) {
                room = roomRepository.findById(dto.getRoomId())
                        .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + dto.getRoomId()));
            }

            User server = null;
            if (dto.getServerId() != null) {
                server = userRepository.findById(dto.getServerId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Server (User) not found with ID: " + dto.getServerId()));
            }

            CommonMaster status = commonMasterRepository.findByCategoryAndCode("ORDER_STATUS", "OPEN")
                    .orElseThrow(() -> new ResourceNotFoundException("Status 'OPEN' not found in master data"));

            CommonMaster defaultKotStatus = commonMasterRepository.findByCategoryAndCode("KOT_STATUS", "NOT_SENT")
                    .orElseThrow(() -> new ResourceNotFoundException("KOT status 'NOT_SENT' not found in master data"));

            PosOrder order = PosOrder.builder()
                    .outlet(outlet)
                    .orderType(orderType)
                    .diningTable(table)
                    .room(room)
                    .guestName(dto.getGuestName())
                    .server(server)
                    .covers(dto.getCovers())
                    .status(status)
                    .kotStatus(defaultKotStatus)
                    .notes(dto.getNotes())
                    .build();

            if (dto.getItems() != null && !dto.getItems().isEmpty()) {
                BigDecimal total = BigDecimal.ZERO;
                for (PosOrderItemDTO itemDto : dto.getItems()) {
                    MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Menu item not found with ID: " + itemDto.getMenuItemId()));
                    BigDecimal price = itemDto.getPrice() != null ? itemDto.getPrice() : menuItem.getPrice();
                    BigDecimal subtotal = price.multiply(new BigDecimal(itemDto.getQuantity()));
                    PosOrderItem orderItem = PosOrderItem.builder()
                            .order(order)
                            .menuItem(menuItem)
                            .quantity(itemDto.getQuantity())
                            .readyQuantity(itemDto.getReadyQuantity() != null ? itemDto.getReadyQuantity() : 0)
                            .price(price)
                            .subtotal(subtotal)
                            .build();
                    order.getItems().add(orderItem);
                    total = total.add(subtotal);
                }
                order.setTotalAmount(total);
            }

            posOrderRepository.save(order);

            if (order.getRoom() != null) {
                StandardResponse<Void> folioResponse = folioService.postChargeByRoom(order.getRoom().getId(),
                        order.getTotalAmount(),
                        "POS",
                        "POS Order: " + order.getId() + " - " + order.getOutlet().getName());
                if (!folioResponse.isSuccess()) {
                    throw new RuntimeException("Failed to post charge to folio: " + folioResponse.getMessage());
                }
            }

            return StandardResponse.success("Order created successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error creating order: ", e);
            return StandardResponse.error("Failed to create order", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<PosOrderDTO> updateOrder(Long id, PosOrderDTO dto) {
        try {
            PosOrder order = posOrderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

            if (dto.getStatusId() != null) {
                CommonMaster status = commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Status master data not found for ID: " + dto.getStatusId()));
                order.setStatus(status);
            }
            if (dto.getServerId() != null) {
                User server = userRepository.findById(dto.getServerId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Server (User) not found with ID: " + dto.getServerId()));
                order.setServer(server);
            }
            if (dto.getNotes() != null)
                order.setNotes(dto.getNotes());
            if (dto.getCovers() != null)
                order.setCovers(dto.getCovers());
            if (dto.getGuestName() != null)
                order.setGuestName(dto.getGuestName());

            if (dto.getItems() != null && !dto.getItems().isEmpty()) {
                Map<Long, Integer> existingById = order.getItems().stream()
                        .filter(i -> i.getId() != null)
                        .collect(Collectors.toMap(PosOrderItem::getId,
                                i -> i.getReadyQuantity() != null ? i.getReadyQuantity() : 0, (a, b) -> a));

                Map<Long, Integer> existingByMenuItemId = order.getItems().stream()
                        .filter(i -> i.getMenuItem() != null && i.getMenuItem().getId() != null)
                        .collect(Collectors.toMap(i -> i.getMenuItem().getId(),
                                i -> i.getReadyQuantity() != null ? i.getReadyQuantity() : 0, (a, b) -> a));

                boolean isKotReady = order.getKotStatus() != null
                        && ("KOT_READY".equalsIgnoreCase(order.getKotStatus().getCode()) ||
                                "KOT_READY".equalsIgnoreCase(order.getKotStatus().getValue()));

                order.getItems().clear();
                BigDecimal total = BigDecimal.ZERO;
                for (PosOrderItemDTO itemDto : dto.getItems()) {
                    MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Menu item not found with ID: " + itemDto.getMenuItemId()));
                    BigDecimal price = itemDto.getPrice() != null ? itemDto.getPrice() : menuItem.getPrice();
                    BigDecimal subtotal = price.multiply(new BigDecimal(itemDto.getQuantity()));

                    Integer readyQty = 0;
                    if (itemDto.getReadyQuantity() != null) {
                        readyQty = itemDto.getReadyQuantity();
                    } else if (itemDto.getId() != null && existingById.containsKey(itemDto.getId())) {
                        readyQty = existingById.get(itemDto.getId());
                    } else if (itemDto.getMenuItemId() != null
                            && existingByMenuItemId.containsKey(itemDto.getMenuItemId())) {
                        readyQty = existingByMenuItemId.get(itemDto.getMenuItemId());
                    }

                    if (isKotReady) {
                        readyQty = itemDto.getQuantity() != null ? itemDto.getQuantity() : 0;
                    }

                    PosOrderItem orderItem = PosOrderItem.builder()
                            .order(order)
                            .menuItem(menuItem)
                            .quantity(itemDto.getQuantity())
                            .readyQuantity(readyQty)
                            .price(price)
                            .subtotal(subtotal)
                            .build();
                    order.getItems().add(orderItem);
                    total = total.add(subtotal);
                }
                order.setTotalAmount(total);
            }

            PosOrder updatedOrder = posOrderRepository.save(order);
            return StandardResponse.success(convertToDTO(updatedOrder), "Order updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating order: ", e);
            return StandardResponse.error("Failed to update order", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<PosOrderDTO> updateOrderStatus(Long id, Long statusId) {
        try {
            PosOrder order = posOrderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

            CommonMaster status = commonMasterRepository.findById(statusId)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Status master data not found for ID: " + statusId));

            order.setStatus(status);
            PosOrder updatedOrder = posOrderRepository.save(order);
            return StandardResponse.success(convertToDTO(updatedOrder), "Order status updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating order status: ", e);
            return StandardResponse.error("Failed to update order status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<PosOrderDTO> updateKotStatus(Long id, Long kotStatusId) {
        try {
            PosOrder order = posOrderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

            CommonMaster kotStatus = commonMasterRepository.findById(kotStatusId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "KOT status master data not found for ID: " + kotStatusId));

            if (!"KOT_STATUS".equals(kotStatus.getCategory())) {
                return StandardResponse.error(
                        "Master ID " + kotStatusId + " does not belong to category 'KOT_STATUS'",
                        "INVALID_KOT_STATUS",
                        null);
            }

            order.setKotStatus(kotStatus);

            if (kotStatus != null && ("READY".equalsIgnoreCase(kotStatus.getCode())
                    || "READY".equalsIgnoreCase(kotStatus.getValue()) ||
                    "KOT_READY".equalsIgnoreCase(kotStatus.getCode())
                    || "KOT_READY".equalsIgnoreCase(kotStatus.getValue()) ||
                    "READY_FOR_SERVE".equalsIgnoreCase(kotStatus.getCode())
                    || "READY_FOR_SERVE".equalsIgnoreCase(kotStatus.getValue()) ||
                    "READY FOR SERVE".equalsIgnoreCase(kotStatus.getValue()))) {
                if (order.getItems() != null) {
                    for (PosOrderItem item : order.getItems()) {
                        item.setReadyQuantity(item.getQuantity() != null ? item.getQuantity() : 0);
                    }
                }
            }

            PosOrder updated = posOrderRepository.save(order);
            return StandardResponse.success(convertToDTO(updated), "KOT status updated to " + kotStatus.getValue());
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating KOT status: ", e);
            return StandardResponse.error("Failed to update KOT status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<PosOrderDTO> getOrderById(Long id) {
        try {
            PosOrder order = posOrderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
            return StandardResponse.success(convertToDTO(order), "Order fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching order: ", e);
            return StandardResponse.error("Failed to fetch order", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<PosOrderDTO>> getActiveOrders(Long tableId) {
        try {
            List<String> activeCodes = List.of("OPEN", "KOT_SENT");
            List<PosOrder> orders;
            if (tableId != null) {
                orders = posOrderRepository.findByDiningTableIdAndStatusCodeInAndIsDeletedFalse(tableId, activeCodes);
            } else {
                orders = posOrderRepository.findByStatusCodeInAndIsDeletedFalse(activeCodes);
            }
            List<PosOrderDTO> dtos = orders.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "Active orders fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching active orders: ", e);
            return StandardResponse.error("Failed to fetch active orders", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<PosOrderDTO>> getOrdersByOutlet(Long outletId) {
        try {
            List<PosOrder> orders;
            if (outletId != null) {
                orders = posOrderRepository.findByOutletId(outletId);
            } else {
                orders = posOrderRepository.findAll();
            }
            List<PosOrderDTO> dtos = orders.stream().map(this::convertToDTO).collect(Collectors.toList());
            return StandardResponse.success(dtos, "Orders fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching orders: ", e);
            return StandardResponse.error("Failed to fetch orders", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<PosOrderDTO>> getOpenOrders(Long outletId) {
        try {
            List<PosOrder> orders;
            if (outletId != null) {
                orders = posOrderRepository.findByOutletIdAndStatusCodeInAndIsDeletedFalse(outletId, List.of("OPEN"));
            } else {
                orders = posOrderRepository.findByStatusCodeInAndIsDeletedFalse(List.of("OPEN"));
            }
            List<PosOrderDTO> dtos = orders.stream().map(this::convertToDTO).collect(Collectors.toList());
            return StandardResponse.success(dtos, "Open POS orders fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching open POS orders: ", e);
            return StandardResponse.error("Failed to fetch open POS orders", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<KitchenOrderCardDTO>> getKitchenOrders(Long outletId, Boolean isClosed) {
        try {
            List<String> kotStatuses;
            if (Boolean.TRUE.equals(isClosed)) {
                kotStatuses = List.of("KOT_READY", "COMPLETED");
            } else {
                kotStatuses = List.of("KOT_SEND", "IN_PROGRESS", "IN PROGRESS", "KOT_READY");
            }
            List<PosOrder> orders;
            if (outletId != null) {
                orders = posOrderRepository.findByOutletIdAndKotStatusIn(outletId, kotStatuses);
            } else {
                orders = posOrderRepository.findByKotStatusIn(kotStatuses);
            }

            if (!Boolean.TRUE.equals(isClosed)) {
                // Show cards whose item quantity is greater than ready quantity only
                orders = orders.stream()
                        .filter(order -> order.getItems() == null || order.getItems().isEmpty() ||
                                order.getItems().stream()
                                        .anyMatch(i -> (i.getQuantity() != null ? i.getQuantity()
                                                : 0) > (i.getReadyQuantity() != null ? i.getReadyQuantity() : 0)))
                        .collect(Collectors.toList());
            }

            List<KitchenOrderCardDTO> dtos = orders.stream()
                    .map(order -> convertToKitchenCardDTO(order, isClosed))
                    .filter(card -> Boolean.TRUE.equals(isClosed)
                            || (card.getItems() != null && !card.getItems().isEmpty()))
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "Kitchen display orders fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching kitchen display orders: ", e);
            return StandardResponse.error("Failed to fetch kitchen display orders", "INTERNAL_SERVER_ERROR",
                    e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> bookTable(TableReservationDTO dto) {
        try {
            DiningTable table = diningTableRepository.findById(dto.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + dto.getTableId()));

            User server = null;
            if (dto.getServerId() != null) {
                server = userRepository.findById(dto.getServerId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Server (User) not found with ID: " + dto.getServerId()));
            }

            CommonMaster status = null;
            if (dto.getStatusId() != null) {
                status = commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Status master data not found for ID: " + dto.getStatusId()));
            }

            TableReservation reservation = TableReservation.builder()
                    .diningTable(table)
                    .guestName(dto.getGuestName())
                    .covers(dto.getCovers())
                    .server(server)
                    .bookingTime(dto.getBookingTime())
                    .status(status)
                    .build();

            tableReservationRepository.save(reservation);
            return StandardResponse.success("Table reserved successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error booking table: ", e);
            return StandardResponse.error("Failed to book table", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<TableReservationDTO>> getReservationsByTable(Long tableId) {
        try {
            List<TableReservationDTO> dtos = tableReservationRepository.findByDiningTableId(tableId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "Table reservations fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching reservations: ", e);
            return StandardResponse.error("Failed to fetch table reservations", "INTERNAL_SERVER_ERROR",
                    e.getMessage());
        }
    }

    private KitchenOrderCardDTO convertToKitchenCardDTO(PosOrder order, Boolean isClosed) {
        List<KitchenOrderItemDTO> itemDTOs = (order.getItems() != null) ? order.getItems().stream()
                .filter(i -> {
                    if (Boolean.TRUE.equals(isClosed)) {
                        return true;
                    }
                    int qty = i.getQuantity() != null ? i.getQuantity() : 0;
                    int readyQty = i.getReadyQuantity() != null ? i.getReadyQuantity() : 0;
                    return (qty - readyQty) > 0;
                })
                .map(i -> {
                    int qty = i.getQuantity() != null ? i.getQuantity() : 0;
                    int readyQty = i.getReadyQuantity() != null ? i.getReadyQuantity() : 0;
                    int remainingQty = Boolean.TRUE.equals(isClosed) ? qty : Math.max(0, qty - readyQty);
                    return KitchenOrderItemDTO.builder()
                            .id(i.getId())
                            .itemName(i.getMenuItem() != null ? i.getMenuItem().getItemName() : null)
                            .quantity(remainingQty)
                            .build();
                })
                .collect(Collectors.toList()) : List.of();

        return KitchenOrderCardDTO.builder()
                .id(order.getId())
                .orderNumber("ORD-" + order.getId())
                .orderType(order.getOrderType() != null ? order.getOrderType().getValue() : null)
                .outletId(order.getOutlet() != null ? order.getOutlet().getId() : null)
                .outletName(order.getOutlet() != null ? order.getOutlet().getName() : null)
                .tableNumber(order.getDiningTable() != null ? order.getDiningTable().getTableNumber() : null)
                .roomNumber(order.getRoom() != null ? order.getRoom().getRoomNumber() : null)
                .guestName(order.getGuestName())
                .serverName(order.getServer() != null ? order.getServer().getFullName() : null)
                .kotStatus(order.getKotStatus() != null ? order.getKotStatus().getValue() : null)
                .createdAt(order.getCreatedAt())
                .items(itemDTOs)
                .build();
    }

    private PosOrderDTO convertToDTO(PosOrder order) {
        List<PosOrderItemDTO> itemDTOs = order.getItems().stream()
                .map(i -> PosOrderItemDTO.builder()
                        .id(i.getId())
                        .menuItemId(i.getMenuItem().getId())
                        .itemName(i.getMenuItem().getItemName())
                        .quantity(i.getQuantity())
                        .readyQuantity(i.getReadyQuantity() != null ? i.getReadyQuantity() : 0)
                        .price(i.getPrice())
                        .subtotal(i.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return PosOrderDTO.builder()
                .id(order.getId())
                .outletId(order.getOutlet().getId())
                .outletName(order.getOutlet().getName())
                .orderTypeId(order.getOrderType() != null ? order.getOrderType().getId() : null)
                .orderTypeName(order.getOrderType() != null ? order.getOrderType().getValue() : null)
                .tableId(order.getDiningTable() != null ? order.getDiningTable().getId() : null)
                .tableNumber(order.getDiningTable() != null ? order.getDiningTable().getTableNumber() : null)
                .roomId(order.getRoom() != null ? order.getRoom().getId() : null)
                .roomNumber(order.getRoom() != null ? order.getRoom().getRoomNumber() : null)
                .guestName(order.getGuestName())
                .serverId(order.getServer() != null ? order.getServer().getId() : null)
                .serverName(order.getServer() != null ? order.getServer().getFullName() : null)
                .covers(order.getCovers())
                .statusId(order.getStatus() != null ? order.getStatus().getId() : null)
                .statusName(order.getStatus() != null ? order.getStatus().getValue() : null)
                .kotStatusId(order.getKotStatus() != null ? order.getKotStatus().getId() : null)
                .kotStatusName(order.getKotStatus() != null ? order.getKotStatus().getValue() : null)
                .notes(order.getNotes())
                .totalAmount(order.getTotalAmount())
                .items(itemDTOs)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private TableReservationDTO convertToDTO(TableReservation r) {
        return TableReservationDTO.builder()
                .id(r.getId())
                .tableId(r.getDiningTable().getId())
                .tableNumber(r.getDiningTable().getTableNumber())
                .guestName(r.getGuestName())
                .covers(r.getCovers())
                .serverId(r.getServer() != null ? r.getServer().getId() : null)
                .serverName(r.getServer() != null ? r.getServer().getFullName() : null)
                .bookingTime(r.getBookingTime())
                .statusId(r.getStatus() != null ? r.getStatus().getId() : null)
                .statusValue(r.getStatus() != null ? r.getStatus().getValue() : null)
                .build();
    }
}
