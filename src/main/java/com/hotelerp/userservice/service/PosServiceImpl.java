package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.PosOrderDTO;
import com.hotelerp.userservice.dto.PosOrderItemDTO;
import com.hotelerp.userservice.dto.TableReservationDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PosServiceImpl implements PosService {

    private final PosOrderRepository posOrderRepository;
    private final TableReservationRepository tableReservationRepository;
    private final OutletRepository outletRepository;
    private final DiningTableRepository diningTableRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final CommonMasterRepository commonMasterRepository;

    // ─────────────────────────────────────────────
    //  ORDER APIs  (Table | Room Service | Takeaway)
    // ─────────────────────────────────────────────

    @Override
    @Transactional
    public PosOrderDTO createOrder(PosOrderDTO dto) {
        Outlet outlet = outletRepository.findById(dto.getOutletId())
                .orElseThrow(() -> new RuntimeException("Outlet not found"));

        CommonMaster orderType = null;
        if (dto.getOrderTypeId() != null) {
            orderType = commonMasterRepository.findById(dto.getOrderTypeId())
                    .orElseThrow(() -> new RuntimeException("Order type not found"));
        }

        DiningTable table = null;
        if (dto.getTableId() != null) {
            table = diningTableRepository.findById(dto.getTableId())
                    .orElseThrow(() -> new RuntimeException("Dining table not found"));
        }

        Room room = null;
        if (dto.getRoomId() != null) {
            room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
        }

        User server = null;
        if (dto.getServerId() != null) {
            server = userRepository.findById(dto.getServerId())
                    .orElseThrow(() -> new RuntimeException("Server not found"));
        }

        CommonMaster status = null;
        if (dto.getStatusId() != null) {
            status = commonMasterRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Status not found"));
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

        // Build order items and calculate total
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            for (PosOrderItemDTO itemDto : dto.getItems()) {
                MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                        .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemDto.getMenuItemId()));
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

        return convertToDTO(posOrderRepository.save(order));
    }

    @Override
    @Transactional
    public PosOrderDTO updateOrder(Long id, PosOrderDTO dto) {
        PosOrder order = posOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (dto.getStatusId() != null) {
            CommonMaster status = commonMasterRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            order.setStatus(status);
        }
        if (dto.getServerId() != null) {
            User server = userRepository.findById(dto.getServerId())
                    .orElseThrow(() -> new RuntimeException("Server not found"));
            order.setServer(server);
        }
        if (dto.getNotes() != null) order.setNotes(dto.getNotes());
        if (dto.getCovers() != null) order.setCovers(dto.getCovers());
        if (dto.getGuestName() != null) order.setGuestName(dto.getGuestName());

        // Replace items if provided
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            order.getItems().clear();
            BigDecimal total = BigDecimal.ZERO;
            for (PosOrderItemDTO itemDto : dto.getItems()) {
                MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                        .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemDto.getMenuItemId()));
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

        return convertToDTO(posOrderRepository.save(order));
    }

    @Override
    public PosOrderDTO getOrderById(Long id) {
        PosOrder order = posOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }

    @Override
    public List<PosOrderDTO> getOrdersByOutlet(Long outletId) {
        if (outletId != null) {
            return posOrderRepository.findByOutletId(outletId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        return posOrderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    //  TABLE BOOKING API
    // ─────────────────────────────────────────────

    @Override
    @Transactional
    public TableReservationDTO bookTable(TableReservationDTO dto) {
        DiningTable table = diningTableRepository.findById(dto.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        User server = null;
        if (dto.getServerId() != null) {
            server = userRepository.findById(dto.getServerId())
                    .orElseThrow(() -> new RuntimeException("Server not found"));
        }

        CommonMaster status = null;
        if (dto.getStatusId() != null) {
            status = commonMasterRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Status not found"));
        }

        TableReservation reservation = TableReservation.builder()
                .diningTable(table)
                .guestName(dto.getGuestName())
                .covers(dto.getCovers())
                .server(server)
                .bookingTime(dto.getBookingTime())
                .status(status)
                .build();

        return convertToDTO(tableReservationRepository.save(reservation));
    }

    @Override
    public List<TableReservationDTO> getReservationsByTable(Long tableId) {
        return tableReservationRepository.findByDiningTableId(tableId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    //  CONVERTERS
    // ─────────────────────────────────────────────

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
