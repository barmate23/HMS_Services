package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PosBillDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosBillServiceImpl implements PosBillService {

    private final PosBillRepository       posBillRepository;
    private final PosOrderRepository      posOrderRepository;
    private final CommonMasterRepository  commonMasterRepository;
    private final FolioService            folioService;
    private final FolioPostingRepository  folioPostingRepository;

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
            BigDecimal gross      = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal discount   = dto.getDiscount()      != null ? dto.getDiscount()      : BigDecimal.ZERO;
            BigDecimal baseAmount = gross.subtract(discount);
            BigDecimal gstPercent = dto.getGstPercent()    != null ? dto.getGstPercent()    : BigDecimal.ZERO;
            BigDecimal gstAmount  = baseAmount.multiply(gstPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal net        = baseAmount.add(gstAmount);
            BigDecimal paid       = dto.getPaidAmount()    != null ? dto.getPaidAmount()    : BigDecimal.ZERO;

            // 7. Generate bill number
            String billNumber = generateBillNumber();

            // 8. Build and save the bill (use a wrapper so lambda can reference it)
            PosBill bill = PosBill.builder()
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

            bill = posBillRepository.save(bill);

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
                    // Roll back the whole transaction
                    throw new RuntimeException("Folio posting failed: " + folioResponse.getMessage());
                }

                // Attach the latest folio posting back to the bill
                final PosBill savedBill = bill;
                folioPostingRepository.findByFolioIdAndIsDeletedFalse(
                                resolveActiveFolioId(roomId)
                        ).stream()
                        .reduce((first, second) -> second)   // last inserted = most recent
                        .ifPresent(posting -> {
                            savedBill.setFolioPosting(posting);
                            posBillRepository.save(savedBill);
                        });

                bill = posBillRepository.findById(bill.getId()).orElse(bill);
            }

            // 10. Flip order status to BILLED
            commonMasterRepository.findByCategoryAndCode("ORDER_STATUS", "BILLED")
                    .ifPresent(billedStatus -> {
                        order.setStatus(billedStatus);
                        posOrderRepository.save(order);
                    });

            return StandardResponse.success(convertToDTO(bill), "Bill created successfully");

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

            if (dto.getPaymentMethodId() != null) {
                CommonMaster pm = commonMasterRepository.findById(dto.getPaymentMethodId())
                        .orElseThrow(() -> new ResourceNotFoundException("Payment method not found: " + dto.getPaymentMethodId()));
                bill.setPaymentMethod(pm);
            }

            if (dto.getStatusId() != null) {
                CommonMaster status = commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException("Bill status not found: " + dto.getStatusId()));
                bill.setStatus(status);
            }

            if (dto.getCompVoidReasonId() != null) {
                CommonMaster reason = commonMasterRepository.findById(dto.getCompVoidReasonId())
                        .orElseThrow(() -> new ResourceNotFoundException("Void reason not found: " + dto.getCompVoidReasonId()));
                bill.setCompVoidReason(reason);
            }

            if (dto.getDiscount() != null || dto.getGstPercent() != null) {
                BigDecimal discount = dto.getDiscount() != null ? dto.getDiscount() : bill.getDiscount();
                if (discount == null) discount = BigDecimal.ZERO;

                BigDecimal gstPercent = dto.getGstPercent() != null ? dto.getGstPercent() : bill.getGstPercent();
                if (gstPercent == null) gstPercent = BigDecimal.ZERO;

                bill.setDiscount(discount);
                bill.setGstPercent(gstPercent);

                BigDecimal baseAmount = bill.getGrossAmount().subtract(discount);
                BigDecimal gstAmount = baseAmount.multiply(gstPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                bill.setGstAmount(gstAmount);
                bill.setNetAmount(baseAmount.add(gstAmount));
            }

            if (dto.getPaidAmount() != null) bill.setPaidAmount(dto.getPaidAmount());
            if (dto.getNotes()      != null) bill.setNotes(dto.getNotes());

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
                    .orElseThrow(() -> new ResourceNotFoundException("BILL_STATUS 'VOID' not found in master data"));
            bill.setStatus(voidStatus);

            if (compVoidReasonId != null) {
                CommonMaster reason = commonMasterRepository.findById(compVoidReasonId)
                        .orElseThrow(() -> new ResourceNotFoundException("Comp/Void reason not found: " + compVoidReasonId));
                bill.setCompVoidReason(reason);
            }

            PosBill updated = posBillRepository.save(bill);
            return StandardResponse.success(convertToDTO(updated), "Bill voided successfully");

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
            PosBill bill = posBillRepository.findById(id)
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
            PosBill bill = posBillRepository.findByOrderIdAndIsDeletedFalse(orderId)
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
    public StandardResponse<List<PosBillDTO>> getAllBills(Long outletId) {
        try {
            List<PosBill> bills = (outletId != null)
                    ? posBillRepository.findByOutletId(outletId)
                    : posBillRepository.findByIsDeletedFalse();

            List<PosBillDTO> dtos = bills.stream().map(this::convertToDTO).collect(Collectors.toList());
            return StandardResponse.success(dtos, "Bills fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching bills: ", e);
            return StandardResponse.error("Failed to fetch bills", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<PosBillDTO>> getBillsByStatus(String statusCode) {
        try {
            List<PosBillDTO> dtos = posBillRepository.findByStatusCode(statusCode)
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

    /**
     * Resolves the active Folio ID for a room by looking up the booking/reservation chain.
     * Used to attach the FolioPosting back-link on the bill.
     * Returns -1L if resolution fails (charge is already posted; back-link is cosmetic).
     */
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
        if (order.getDiningTable() != null) orderFrom = "TABLE";
        else if (order.getRoom()   != null) orderFrom = "ROOM";

        return PosBillDTO.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .orderId(order.getId())
                .orderRef("ORD-" + order.getId())
                .orderFrom(orderFrom)
                .tableId(order.getDiningTable() != null ? order.getDiningTable().getId()           : null)
                .tableNumber(order.getDiningTable() != null ? order.getDiningTable().getTableNumber() : null)
                .roomId(order.getRoom() != null ? order.getRoom().getId()         : null)
                .roomNumber(order.getRoom() != null ? order.getRoom().getRoomNumber() : null)
                .guestName(order.getGuestName())
                .grossAmount(bill.getGrossAmount())
                .discount(bill.getDiscount())
                .netAmount(bill.getNetAmount())
                .paidAmount(bill.getPaidAmount())
                .gstPercent(bill.getGstPercent())
                .gstAmount(bill.getGstAmount())
                .paymentMethodId(bill.getPaymentMethod()   != null ? bill.getPaymentMethod().getId()   : null)
                .paymentMethodName(bill.getPaymentMethod() != null ? bill.getPaymentMethod().getValue() : null)
                .statusId(bill.getStatus()   != null ? bill.getStatus().getId()   : null)
                .statusName(bill.getStatus() != null ? bill.getStatus().getValue() : null)
                .compVoidReasonId(bill.getCompVoidReason()   != null ? bill.getCompVoidReason().getId()   : null)
                .compVoidReasonName(bill.getCompVoidReason() != null ? bill.getCompVoidReason().getValue() : null)
                .postToFolio(bill.getPostToFolio())
                .folioPostingId(bill.getFolioPosting() != null ? bill.getFolioPosting().getId() : null)
                .notes(bill.getNotes())
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .build();
    }
}
