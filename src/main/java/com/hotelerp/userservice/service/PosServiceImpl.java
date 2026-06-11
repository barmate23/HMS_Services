package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PosOrderDTO;
import com.hotelerp.userservice.dto.PosOrderItemDTO;
import com.hotelerp.userservice.dto.TableReservationDTO;
import com.hotelerp.common.entity.*;
import com.hotelerp.userservice.repository.*;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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

    @Override
    @Transactional
    public StandardResponse<Void> createOrder(PosOrderDTO dto) {
        try {
            Outlet outlet = outletRepository.findById(dto.getOutletId())
                    .orElseThrow(() -> new ResourceNotFoundException("Outlet not found with ID: " + dto.getOutletId()));

            CommonMaster orderType = null;
            if (dto.getOrderTypeId() != null) {
                orderType = commonMasterRepository.findById(dto.getOrderTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Order type master data not found for ID: " + dto.getOrderTypeId()));
            }

            DiningTable table = null;
            if (dto.getTableId() != null) {
                table = diningTableRepository.findById(dto.getTableId())
                        .orElseThrow(() -> new ResourceNotFoundException("Dining table not found with ID: " + dto.getTableId()));
            }

            Room room = null;
            if (dto.getRoomId() != null) {
                room = roomRepository.findById(dto.getRoomId())
                        .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + dto.getRoomId()));
            }

            User server = null;
            if (dto.getServerId() != null) {
                server = userRepository.findById(dto.getServerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Server (User) not found with ID: " + dto.getServerId()));
            }

            CommonMaster status = null;
            if (dto.getStatusId() != null) {
                status = commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException("Status master data not found for ID: " + dto.getStatusId()));
            }

            PosOrder order = PosOrder.builder()
                    .outlet(outlet)
                    .orderType(orderType)
                    .diningTable(table)
                    .room(room)
                    .guestName(dto.getGuestName())
                    .server(server)
                    .covers(dto.getCovers())
                    .status(status)
                    .notes(dto.getNotes())
                    .build();

            if (dto.getItems() != null && !dto.getItems().isEmpty()) {
                BigDecimal total = BigDecimal.ZERO;
                for (PosOrderItemDTO itemDto : dto.getItems()) {
                    MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + itemDto.getMenuItemId()));
                    BigDecimal price = itemDto.getPrice() != null ? itemDto.getPrice() : menuItem.getPrice();
                    BigDecimal subtotal = price.multiply(new BigDecimal(itemDto.getQuantity()));
                    PosOrderItem orderItem = PosOrderItem.builder()
                            .order(order)
                            .menuItem(menuItem)
                            .quantity(itemDto.getQuantity())
                            .price(price)
                            .subtotal(subtotal)
                            .build();
                    order.getItems().add(orderItem);
                    total = total.add(subtotal);
                }
                order.setTotalAmount(total);
            }

            posOrderRepository.save(order);
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
                        .orElseThrow(() -> new ResourceNotFoundException("Status master data not found for ID: " + dto.getStatusId()));
                order.setStatus(status);
            }
            if (dto.getServerId() != null) {
                User server = userRepository.findById(dto.getServerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Server (User) not found with ID: " + dto.getServerId()));
                order.setServer(server);
            }
            if (dto.getNotes() != null) order.setNotes(dto.getNotes());
            if (dto.getCovers() != null) order.setCovers(dto.getCovers());
            if (dto.getGuestName() != null) order.setGuestName(dto.getGuestName());

            if (dto.getItems() != null && !dto.getItems().isEmpty()) {
                order.getItems().clear();
                BigDecimal total = BigDecimal.ZERO;
                for (PosOrderItemDTO itemDto : dto.getItems()) {
                    MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + itemDto.getMenuItemId()));
                    BigDecimal price = itemDto.getPrice() != null ? itemDto.getPrice() : menuItem.getPrice();
                    BigDecimal subtotal = price.multiply(new BigDecimal(itemDto.getQuantity()));
                    PosOrderItem orderItem = PosOrderItem.builder()
                            .order(order)
                            .menuItem(menuItem)
                            .quantity(itemDto.getQuantity())
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
    @Transactional
    public StandardResponse<Void> bookTable(TableReservationDTO dto) {
        try {
            DiningTable table = diningTableRepository.findById(dto.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + dto.getTableId()));

            User server = null;
            if (dto.getServerId() != null) {
                server = userRepository.findById(dto.getServerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Server (User) not found with ID: " + dto.getServerId()));
            }

            CommonMaster status = null;
            if (dto.getStatusId() != null) {
                status = commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException("Status master data not found for ID: " + dto.getStatusId()));
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
            return StandardResponse.error("Failed to fetch table reservations", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private PosOrderDTO convertToDTO(PosOrder order) {
        List<PosOrderItemDTO> itemDTOs = order.getItems().stream()
                .map(i -> PosOrderItemDTO.builder()
                        .id(i.getId())
                        .menuItemId(i.getMenuItem().getId())
                        .itemName(i.getMenuItem().getItemName())
                        .quantity(i.getQuantity())
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
