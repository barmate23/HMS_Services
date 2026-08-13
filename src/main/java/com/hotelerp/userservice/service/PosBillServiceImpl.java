package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.config.LoginUser;
import com.hotelerp.userservice.dto.PosBillDTO;
import com.hotelerp.userservice.dto.PosOrderItemDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosBillServiceImpl implements PosBillService {

    private final PosBillRepository posBillRepository;
    private final PosOrderRepository posOrderRepository;
    private final CommonMasterRepository commonMasterRepository;
    private final DiningTableRepository diningTableRepository;
    private final FolioService folioService;
    private final FolioPostingRepository folioPostingRepository;
    private final RecipeRepository recipeRepository;
    private final KitchenIngredientRepository kitchenIngredientRepository;
    private final HotelRepository hotelRepository;
    private final LoginUser loginUser;

    // ─────────────────────────────────────────────────────────────────────────
    //  CREATE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StandardResponse<PosBillDTO> createBill(PosBillDTO dto) {
        try {
            // 1. Validate order exists
            PosOrder order = posOrderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + dto.getOrderId()));

            // 2. Prevent duplicate bill for same order
            if (posBillRepository.findByOrderIdAndIsDeletedFalse(dto.getOrderId()).isPresent()) {
                return StandardResponse.error(
                        "A bill already exists for order ID: " + dto.getOrderId(),
                        "DUPLICATE_BILL", null);
            }

            // 3. Payment method (optional)
            CommonMaster paymentMethod = null;
            if (dto.getPaymentMethodId() != null) {
                paymentMethod = commonMasterRepository.findById(dto.getPaymentMethodId())
                        .orElseThrow(() -> new ResourceNotFoundException("Payment method not found: " + dto.getPaymentMethodId()));
            }

            // 4. Comp/Void reason (optional)
            CommonMaster compVoidReason = null;
            if (dto.getCompVoidReasonId() != null) {
                compVoidReason = commonMasterRepository.findById(dto.getCompVoidReasonId())
                        .orElseThrow(() -> new ResourceNotFoundException("Comp/Void reason not found: " + dto.getCompVoidReasonId()));
            }

            // 5. Default bill status → OPEN
            CommonMaster billStatus = commonMasterRepository.findByCategoryAndCode("BILL_STATUS", "OPEN")
                    .orElseThrow(() -> new ResourceNotFoundException("BILL_STATUS 'OPEN' not found in master data"));

            // 6. Amount calculations
            BigDecimal gross = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal discount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;
            BigDecimal baseAmount = gross.subtract(discount);
            BigDecimal gstPercent = dto.getGstPercent() != null ? dto.getGstPercent() : BigDecimal.ZERO;
            BigDecimal gstAmount = baseAmount.multiply(gstPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal net = baseAmount.add(gstAmount);
            BigDecimal paid = dto.getPaidAmount() != null ? dto.getPaidAmount() : BigDecimal.ZERO;

            // 7. Generate bill number
            String billNumber = generateBillNumber();

            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId() : dto.getHotelId();
            if (hotelId == null && order.getHotel() != null) {
                hotelId = order.getHotel().getId();
            }
            Hotel hotel = null;
            if (hotelId != null) {
                final Long finalHotelId = hotelId;
                hotel = hotelRepository.findById(finalHotelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + finalHotelId));
            }

            // 8. Build and save the bill
            PosBill bill = PosBill.builder()
                    .hotel(hotel)
                    .order(order)
                    .billNumber(billNumber)
                    .paymentMethod(paymentMethod)
                    .compVoidReason(compVoidReason)
                    .status(billStatus)
                    .grossAmount(gross)
                    .discount(discount)
                    .gstPercent(gstPercent)
                    .gstAmount(gstAmount)
                    .netAmount(net)
                    .paidAmount(paid)
                    .postToFolio(Boolean.TRUE.equals(dto.getPostToFolio()))
                    .notes(dto.getNotes())
                    .build();

            PosBill savedBill = posBillRepository.save(bill);

            // 9. If "Post to Folio" is checked → post charge to room's active folio
            if (Boolean.TRUE.equals(dto.getPostToFolio())) {
                if (order.getRoom() == null) {
                    return StandardResponse.error(
                            "Cannot post to folio: order is not linked to a room.",
                            "NO_ROOM_LINKED", null);
                }

                final Long roomId = order.getRoom().getId();
                final String description = "POS Bill " + billNumber
                        + " | Order: " + order.getId()
                        + (order.getGuestName() != null ? " | Guest: " + order.getGuestName() : "");

                StandardResponse<Void> folioResponse = folioService.postChargeByRoom(
                        roomId, net, "POS", description);

                if (!folioResponse.isSuccess()) {
                    throw new RuntimeException("Folio posting failed: " + folioResponse.getMessage());
                }

                final PosBill billToUpdate = savedBill;
                folioPostingRepository.findByFolioIdAndIsDeletedFalse(
                                resolveActiveFolioId(roomId)
                        ).stream()
                        .reduce((first, second) -> second)
                        .ifPresent(posting -> {
                            billToUpdate.setFolioPosting(posting);
                            posBillRepository.save(billToUpdate);
                        });
            }

            // 10. Update order status to BILLED
            CommonMaster billedStatus = commonMasterRepository.findByCategoryAndCode("ORDER_STATUS", "BILLED")
                    .orElseThrow(() -> new ResourceNotFoundException("ORDER_STATUS 'BILLED' not found in master data"));
            order.setStatus(billedStatus);
            posOrderRepository.save(order);

            // set table status to available
            DiningTable diningTable = order.getDiningTable();
            if (diningTable != null) {
                commonMasterRepository.findByCategoryAndCode("TABLE_STATUS", "AVAILABLE")
                        .ifPresent(availableStatus -> {
                            diningTable.setStatus(availableStatus);
                            diningTableRepository.save(diningTable);
                        });
            }

            // 11. Deduct kitchen ingredient inventory stock based on recipe BOMs
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                for (PosOrderItem item : order.getItems()) {
                    if (item.getMenuItem() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                        recipeRepository.findByMenuItemIdAndIsDeletedFalse(item.getMenuItem().getId())
                                .ifPresent(recipe -> {
                                    if (recipe.getIngredients() != null) {
                                        for (RecipeIngredient ri : recipe.getIngredients()) {
                                            if (ri.getIngredient() != null) {
                                                BigDecimal qtyPerPortion = ri.getGrossQty() != null ? ri.getGrossQty() : (ri.getNetQty() != null ? ri.getNetQty() : BigDecimal.ZERO);
                                                BigDecimal totalConsumed = qtyPerPortion.multiply(BigDecimal.valueOf(item.getQuantity()));

                                                KitchenIngredient ing = ri.getIngredient();
                                                BigDecimal currentStock = ing.getCurrentStockLevel() != null ? ing.getCurrentStockLevel() : BigDecimal.ZERO;
                                                ing.setCurrentStockLevel(currentStock.subtract(totalConsumed));
                                                kitchenIngredientRepository.save(ing);
                                            }
                                        }
                                    }
                                });
                    }
                }
            }

            return StandardResponse.success(convertToDTO(savedBill), "Bill created successfully");

        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error creating bill: ", e);
            return StandardResponse.error("Failed to create bill", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StandardResponse<PosBillDTO> updateBill(Long id, PosBillDTO dto) {
        try {
            PosBill bill = posBillRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id));

            if (dto.getDiscount() != null) bill.setDiscount(dto.getDiscount());
            if (dto.getGstPercent() != null) bill.setGstPercent(dto.getGstPercent());

            // Recalculate net amount
            BigDecimal gross = bill.getGrossAmount() != null ? bill.getGrossAmount() : BigDecimal.ZERO;
            BigDecimal discount = bill.getDiscount() != null ? bill.getDiscount() : BigDecimal.ZERO;
            BigDecimal baseAmount = gross.subtract(discount);
            BigDecimal gstPercent = bill.getGstPercent() != null ? bill.getGstPercent() : BigDecimal.ZERO;
            BigDecimal gstAmount = baseAmount.multiply(gstPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal net = baseAmount.add(gstAmount);

            bill.setGstAmount(gstAmount);
            bill.setNetAmount(net);

            if (dto.getPaidAmount() != null) bill.setPaidAmount(dto.getPaidAmount());
            if (dto.getNotes() != null) bill.setNotes(dto.getNotes());

            if (dto.getPaymentMethodId() != null) {
                CommonMaster paymentMethod = commonMasterRepository.findById(dto.getPaymentMethodId())
                        .orElseThrow(() -> new ResourceNotFoundException("Payment method not found: " + dto.getPaymentMethodId()));
                bill.setPaymentMethod(paymentMethod);
            }

            if (dto.getStatusId() != null) {
                CommonMaster status = commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException("Bill status not found: " + dto.getStatusId()));
                bill.setStatus(status);
            }

            if (dto.getCompVoidReasonId() != null) {
                CommonMaster compVoidReason = commonMasterRepository.findById(dto.getCompVoidReasonId())
                        .orElseThrow(() -> new ResourceNotFoundException("Comp/Void reason not found: " + dto.getCompVoidReasonId()));
                bill.setCompVoidReason(compVoidReason);
            }

            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId() : dto.getHotelId();
            if (hotelId != null && bill.getHotel() == null) {
                Hotel hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));
                bill.setHotel(hotel);
            }

            PosBill updated = posBillRepository.save(bill);
            return StandardResponse.success(convertToDTO(updated), "Bill updated successfully");

        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating bill: ", e);
            return StandardResponse.error("Failed to update bill", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  VOID
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StandardResponse<PosBillDTO> voidBill(Long id, Long compVoidReasonId) {
        try {
            PosBill bill = posBillRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id));

            CommonMaster voidStatus = commonMasterRepository.findByCategoryAndCode("BILL_STATUS", "VOID")
                    .orElseThrow(() -> new ResourceNotFoundException("BILL_STATUS 'VOID' not found"));
            bill.setStatus(voidStatus);

            if (compVoidReasonId != null) {
                CommonMaster reason = commonMasterRepository.findById(compVoidReasonId)
                        .orElseThrow(() -> new ResourceNotFoundException("Comp/Void reason not found: " + compVoidReasonId));
                bill.setCompVoidReason(reason);
            }

            PosBill saved = posBillRepository.save(bill);
            return StandardResponse.success(convertToDTO(saved), "Bill voided successfully");

        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error voiding bill: ", e);
            return StandardResponse.error("Failed to void bill", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  READ
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public StandardResponse<PosBillDTO> getBillById(Long id) {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            PosBill bill = (hotelId != null)
                    ? posBillRepository.findByIdAndHotelId(id, hotelId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id))
                    : posBillRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id));
            return StandardResponse.success(convertToDTO(bill), "Bill fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching bill: ", e);
            return StandardResponse.error("Failed to fetch bill", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<PosBillDTO> getBillByOrderId(Long orderId) {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            PosBill bill = (hotelId != null)
                    ? posBillRepository.findByOrderIdAndHotelId(orderId, hotelId)
                    .orElseThrow(() -> new ResourceNotFoundException("No bill found for order ID: " + orderId))
                    : posBillRepository.findByOrderIdAndIsDeletedFalse(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("No bill found for order ID: " + orderId));
            return StandardResponse.success(convertToDTO(bill), "Bill fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching bill by order: ", e);
            return StandardResponse.error("Failed to fetch bill", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<PosBillDTO>> getAllBills(Long outletId, int page, int size) {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            Page<PosBill> billPage;
            if (hotelId != null) {
                billPage = (outletId != null)
                        ? posBillRepository.findByHotelIdAndOutletId(hotelId, outletId, pageable)
                        : posBillRepository.findByHotel_IdAndIsDeletedFalse(hotelId, pageable);
            } else {
                billPage = (outletId != null)
                        ? posBillRepository.findByOutletId(outletId, pageable)
                        : posBillRepository.findByIsDeletedFalse(pageable);
            }

            List<PosBillDTO> dtos = billPage.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            StandardResponse.ResponseMetadata meta = StandardResponse.ResponseMetadata.builder()
                    .totalRecords(billPage.getTotalElements())
                    .currentPage(billPage.getNumber())
                    .pageSize(billPage.getSize())
                    .totalPages(billPage.getTotalPages())
                    .build();

            return StandardResponse.success(dtos, "Bills fetched successfully", meta);
        } catch (Exception e) {
            log.error("Error fetching bills: ", e);
            return StandardResponse.error("Failed to fetch bills", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<PosBillDTO>> getBillsByStatus(String statusCode) {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            List<PosBillDTO> dtos = ((hotelId != null)
                    ? posBillRepository.findByHotelIdAndStatusCode(hotelId, statusCode)
                    : posBillRepository.findByStatusCode(statusCode))
                    .stream().map(this::convertToDTO).collect(Collectors.toList());
            return StandardResponse.success(dtos, "Bills fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching bills by status: ", e);
            return StandardResponse.error("Failed to fetch bills", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE (soft)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StandardResponse<Void> deleteBill(Long id) {
        try {
            PosBill bill = posBillRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id));
            bill.setIsDeleted(true);
            posBillRepository.save(bill);
            return StandardResponse.success("Bill deleted successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting bill: ", e);
            return StandardResponse.error("Failed to delete bill", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private String generateBillNumber() {
        long count = posBillRepository.countAll() + 1;
        return String.format("BILL-%04d", count);
    }

    private Long resolveActiveFolioId(Long roomId) {
        try {
            return folioService.getActiveFolios().getData().stream()
                    .filter(f -> f.getFolioId() != null)
                    .mapToLong(f -> f.getFolioId())
                    .findFirst()
                    .orElse(-1L);
        } catch (Exception e) {
            log.warn("Could not resolve active folio id for room {}: {}", roomId, e.getMessage());
            return -1L;
        }
    }

    private PosBillDTO convertToDTO(PosBill bill) {
        PosOrder order = bill.getOrder();

        String orderFrom = "TAKEAWAY";
        if (order != null) {
            if (order.getDiningTable() != null) orderFrom = "TABLE";
            else if (order.getRoom() != null) orderFrom = "ROOM";
        }

        List<PosOrderItemDTO> itemDTOs = (order != null && order.getItems() != null)
                ? order.getItems().stream()
                .map(i -> PosOrderItemDTO.builder()
                        .id(i.getId())
                        .hotelId(i.getHotel() != null ? i.getHotel().getId() : (bill.getHotel() != null ? bill.getHotel().getId() : null))
                        .hotelName(i.getHotel() != null ? i.getHotel().getName() : (bill.getHotel() != null ? bill.getHotel().getName() : null))
                        .menuItemId(i.getMenuItem() != null ? i.getMenuItem().getId() : null)
                        .itemName(i.getMenuItem() != null ? i.getMenuItem().getItemName() : null)
                        .quantity(i.getQuantity())
                        .readyQuantity(i.getReadyQuantity() != null ? i.getReadyQuantity() : 0)
                        .price(i.getPrice())
                        .subtotal(i.getSubtotal())
                        .kotStatusId(i.getKotStatus() != null ? i.getKotStatus().getId() : null)
                        .kotStatusCode(i.getKotStatus() != null ? i.getKotStatus().getCode() : null)
                        .kotStatusName(i.getKotStatus() != null ? i.getKotStatus().getValue() : null)
                        .build())
                .collect(Collectors.toList())
                : List.of();

        return PosBillDTO.builder()
                .id(bill.getId())
                .hotelId(bill.getHotel() != null ? bill.getHotel().getId() : (order != null && order.getHotel() != null ? order.getHotel().getId() : null))
                .hotelName(bill.getHotel() != null ? bill.getHotel().getName() : (order != null && order.getHotel() != null ? order.getHotel().getName() : null))
                .billNumber(bill.getBillNumber())
                .orderId(order != null ? order.getId() : null)
                .orderRef(order != null ? "ORD-" + order.getId() : null)
                .orderFrom(orderFrom)
                .tableId((order != null && order.getDiningTable() != null) ? order.getDiningTable().getId() : null)
                .tableNumber((order != null && order.getDiningTable() != null) ? order.getDiningTable().getTableNumber() : null)
                .roomId((order != null && order.getRoom() != null) ? order.getRoom().getId() : null)
                .roomNumber((order != null && order.getRoom() != null) ? order.getRoom().getRoomNumber() : null)
                .guestName(order != null ? order.getGuestName() : null)
                .isRoomOrder(order != null && order.getRoom() != null)
                .grossAmount(bill.getGrossAmount())
                .discount(bill.getDiscount())
                .netAmount(bill.getNetAmount())
                .paidAmount(bill.getPaidAmount())
                .gstPercent(bill.getGstPercent())
                .gstAmount(bill.getGstAmount())
                .paymentMethodId(bill.getPaymentMethod() != null ? bill.getPaymentMethod().getId() : null)
                .paymentMethodName(bill.getPaymentMethod() != null ? bill.getPaymentMethod().getValue() : null)
                .statusId(bill.getStatus() != null ? bill.getStatus().getId() : null)
                .statusName(bill.getStatus() != null ? bill.getStatus().getValue() : null)
                .compVoidReasonId(bill.getCompVoidReason() != null ? bill.getCompVoidReason().getId() : null)
                .compVoidReasonName(bill.getCompVoidReason() != null ? bill.getCompVoidReason().getValue() : null)
                .postToFolio(bill.getPostToFolio())
                .folioPostingId(bill.getFolioPosting() != null ? bill.getFolioPosting().getId() : null)
                .items(itemDTOs)
                .notes(bill.getNotes())
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .build();
    }
}
