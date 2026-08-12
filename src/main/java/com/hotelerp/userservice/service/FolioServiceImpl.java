package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;

import com.hotelerp.userservice.config.LoginUser;
import com.hotelerp.userservice.dto.billing.*;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FolioServiceImpl implements FolioService {

    private final FolioRepository folioRepository;
    private final CommonMasterRepository commonMasterRepository;
    private final FolioPostingRepository folioPostingRepository;
    private final FolioPaymentRepository folioPaymentRepository;
    private final ReservationRepository reservationRepository;
    private final BookingRepository bookingRepository;
    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private final LoginUser loginUser;

    @Override
    public StandardResponse<FolioLedgerDTO> getLedger(Long folioId) {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            Folio folio = folioRepository.findByIdAndHotelId(folioId, hotelId)
                    .orElseThrow(() -> new RuntimeException("Folio not found"));

            List<FolioPosting> postings = folioPostingRepository.findByFolioIdAndIsDeletedFalse(folioId);
            List<FolioPayment> payments = folioPaymentRepository.findByFolioIdAndIsDeletedFalse(folioId);

            List<FolioLedgerDTO.LedgerEntryDTO> entries = new ArrayList<>();

            for (FolioPosting p : postings) {
                entries.add(FolioLedgerDTO.LedgerEntryDTO.builder()
                        .date(p.getPostingDate())
                        .source(p.getSource())
                        .description(p.getDescription())
                        .grossAmount(p.getChargeAmount())
                        .taxAmount(p.getTaxAmount())
                        .build());
            }

            entries.sort(Comparator.comparing(FolioLedgerDTO.LedgerEntryDTO::getDate));

            Reservation res = folio.getReservation();
            String guestName = res != null && res.getGuest() != null
                    ? res.getGuest().getFirstName() + " " + res.getGuest().getLastName()
                    : "Unknown";

            FolioLedgerDTO ledger = FolioLedgerDTO.builder()
                    .folioId(folio.getId())
                    .folioNumber(folio.getFolioNumber())
                    .reservationNumber(res != null ? res.getId().toString() : "")
                    .guestName(guestName)
                    .totalCharges(folio.getTotalCharges())
                    .totalPayments(folio.getTotalPayments())
                    .taxAmount(folio.getTaxAmount())
                    .balance(folio.getBalance())
                    .status(folio.getStatus() != null ? folio.getStatus().getValue() : "OPEN")
                    .entries(entries)
                    .build();
            return StandardResponse.success(ledger, "Folio ledger fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching folio ledger: ", e);
            return StandardResponse.error("Failed to fetch folio ledger", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> postCharge(FolioPostingRequest request) {
        try {
            Long folioId = request.getFolioId();
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;

            if (request.getRoomId() != null) {
                LocalDate today = LocalDate.now();
                Booking booking = bookingRepository.findActiveByRoomAndDate(request.getRoomId(), today)
                        .orElseThrow(() -> new RuntimeException(
                                "No active booking found for room " + request.getRoomId() + " on " + today));

                Folio folioByRoom = folioRepository
                        .findByReservationIdAndHotelId(booking.getReservation().getId(), hotelId)
                        .orElseThrow(() -> new RuntimeException(
                                "Folio not found for reservation of room " + request.getRoomId()));

                folioId = folioByRoom.getId();
            }

            if (folioId == null) {
                throw new RuntimeException("Folio ID or Room ID is required");
            }

            Folio folio = folioRepository.findByIdAndHotelId(folioId, hotelId)
                    .orElseThrow(() -> new RuntimeException("Folio not found"));

            // Basic tax calculation logic for demonstration
            BigDecimal taxRate = BigDecimal.ZERO;
            if (request.getTaxType() != null && request.getTaxType().contains("12")) {
                taxRate = new BigDecimal("0.12");
            } else if (request.getTaxType() != null && request.getTaxType().contains("5")) {
                taxRate = new BigDecimal("0.05");
            }

            BigDecimal taxAmount = request.getAmount().multiply(taxRate);
            BigDecimal totalAmount = request.getAmount().add(taxAmount);

            FolioPosting posting = FolioPosting.builder()
                    .folio(folio)
                    .postingDate(LocalDateTime.now())
                    .source(request.getSource())
                    .description(request.getDescription())
                    .chargeAmount(request.getAmount())
                    .taxAmount(taxAmount)
                    .totalAmount(totalAmount)
                    .build();

            folioPostingRepository.save(posting);

            folio.setTotalCharges(folio.getTotalCharges().add(totalAmount));
            folio.setTaxAmount(folio.getTaxAmount().add(taxAmount));
            folio.setBalance(folio.getTotalCharges().subtract(folio.getTotalPayments()));
            folioRepository.save(folio);

            return StandardResponse.success("Charge posted successfully");
        } catch (Exception e) {
            log.error("Error posting charge: ", e);
            return StandardResponse.error("Failed to post charge", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> postToFolio(PostToFolioRequest request) {
        try {
            Long roomId = request.getRoomId();
            if (roomId == null) {
                throw new RuntimeException("Room ID is required");
            }

            LocalDate today = LocalDate.now();
            Booking booking = bookingRepository.findActiveByRoomAndDate(roomId, today)
                    .orElseThrow(() -> new RuntimeException(
                            "No active booking found for room " + roomId + " on " + today));

            Folio folio = folioRepository
                    .findByReservationIdAndIsDeletedFalse(booking.getReservation().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Folio not found for reservation of room " + roomId));

            // Delegate to postCharge with the resolved folioId
            return postCharge(FolioPostingRequest.builder()
                    .folioId(folio.getId())
                    .source(request.getSource())
                    .amount(request.getAmount())
                    .taxType(request.getTaxType())
                    .description(request.getDescription())
                    .build());
        } catch (Exception e) {
            log.error("Error posting to folio: ", e);
            return StandardResponse.error("Failed to post to folio", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> postChargeByRoom(Long roomId, java.math.BigDecimal amount, String source,
            String description) {
        try {
            Booking booking = bookingRepository.findByRoomIdAndIsDeletedFalse(roomId).stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No active booking found for room ID: " + roomId));

            Folio folio = folioRepository.findByReservationIdAndIsDeletedFalse(booking.getReservation().getId())
                    .orElseGet(() -> {
                        StandardResponse<Long> response = createFolioForReservation(booking.getReservation().getId());
                        if (!response.isSuccess()) {
                            throw new RuntimeException("Failed to create folio: " + response.getMessage());
                        }
                        return folioRepository.findById(response.getData()).get();
                    });

            return postCharge(FolioPostingRequest.builder()
                    .folioId(folio.getId())
                    .source(source)
                    .amount(amount)
                    .description(description)
                    .build());
        } catch (Exception e) {
            log.error("Error posting charge by room: ", e);
            return StandardResponse.error("Failed to post charge by room", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> collectPayment(FolioPaymentRequest request) {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            Folio folio = folioRepository.findByIdAndHotelId(request.getFolioId(), hotelId)
                    .orElseThrow(() -> new RuntimeException("Folio not found"));

            // Check if an invoice has already been generated for this folio
            Optional<Invoice> existingInvoice = invoiceRepository.findByFolioIdAndIsDeletedFalse(request.getFolioId());
            if (existingInvoice.isPresent()) {
                Invoice inv = existingInvoice.get();
                return StandardResponse.error(
                        "Invoice '" + inv.getInvoiceNumber() + "' has already been generated for this folio. Payment cannot be collected again.",
                        "INVOICE_ALREADY_GENERATED",
                        "Invoice already exists for folio ID: " + request.getFolioId()
                );
            }

            Optional<CommonMaster> folioStatus = commonMasterRepository.findByCategoryAndCode("FOLIO_STATUS", "CLOSE");
            FolioPayment payment = FolioPayment.builder()
                    .folio(folio)
                    .paymentDate(LocalDateTime.now())
                    .paymentMode(request.getMode())
                    .amount(request.getAmount())
                    .referenceNumber(request.getReferenceNumber())
                    .notes(request.getNotes())
                    .build();

            folioPaymentRepository.save(payment);

            folio.setTotalPayments(folio.getTotalPayments().add(request.getAmount()));
            folio.setBalance(folio.getTotalCharges().subtract(folio.getTotalPayments()));
            folio.setStatus(folioStatus.get());
            folioRepository.save(folio);


            StandardResponse<InvoiceDTO> invoiceResponse = invoiceService.generateInvoice(request.getFolioId());
            if (!invoiceResponse.isSuccess()) {
                throw new RuntimeException("Invoice generation failed: " + invoiceResponse.getMessage());
            }

            return StandardResponse.success("Payment collected successfully");
        } catch (Exception e) {
            log.error("Error collecting payment: ", e);
            return StandardResponse.error("Failed to collect payment", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Long> createFolioForReservation(Long reservationId) {
        try {
            Reservation res = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));

            Folio folio = Folio.builder()
                    .folioNumber("FOL-" + (1000 + reservationId))
                    .reservation(res)
                    .totalCharges(BigDecimal.ZERO)
                    .totalPayments(BigDecimal.ZERO)
                    .balance(BigDecimal.ZERO)
                    .taxAmount(BigDecimal.ZERO)
                    .build();

            Long id = folioRepository.save(folio).getId();

            List<Booking> bookings = bookingRepository.findByReservationId(reservationId);
            BigDecimal totalBookingAmount = bookings.stream()
                    .map(Booking::getFinalPrice)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalBookingAmount.compareTo(BigDecimal.ZERO) > 0) {
                postCharge(FolioPostingRequest.builder()
                        .folioId(id)
                        .source("Reservation")
                        .amount(totalBookingAmount)
                        .description("Room stay charges")
                        .build());
            }

            return StandardResponse.success(id, "Folio created successfully");
        } catch (Exception e) {
            log.error("Error creating folio: ", e);
            return StandardResponse.error("Failed to create folio", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<FolioLedgerDTO>> getActiveFolios() {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            List<FolioLedgerDTO> list = folioRepository.findAllOpenFoliosByHotel(java.time.LocalDate.now(), hotelId).stream()
                    .map(this::convertToSummaryDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(list, "Active folios fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching active folios: ", e);
            return StandardResponse.error("Failed to fetch active folios", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private FolioLedgerDTO convertToSummaryDTO(Folio folio) {
        Reservation res = folio.getReservation();
        String guestName = res != null && res.getGuest() != null
                ? res.getGuest().getFirstName() + " " + res.getGuest().getLastName()
                : "Unknown";

        return FolioLedgerDTO.builder()
                .folioId(folio.getId())
                .folioNumber(folio.getFolioNumber())
                .reservationNumber(res != null ? res.getId().toString() : "")
                .guestName(guestName)
                .totalCharges(folio.getTotalCharges())
                .totalPayments(folio.getTotalPayments())
                .taxAmount(folio.getTaxAmount())
                .balance(folio.getBalance())
                .status(folio.getStatus() != null ? folio.getStatus().getValue() : "OPEN")
                .build();
    }

    @Override
    public StandardResponse<List<FolioPaymentDTO>> getAllPayments() {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            List<FolioPaymentDTO> list = folioPaymentRepository.findByHotelIdAndIsDeletedFalse(hotelId).stream()
                    .map(this::convertToPaymentDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(list, "Folio payments fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching folio payments: ", e);
            return StandardResponse.error("Failed to fetch folio payments", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private FolioPaymentDTO convertToPaymentDTO(FolioPayment payment) {
        Folio folio = payment.getFolio();
        Reservation res = folio != null ? folio.getReservation() : null;
        String guestName = "Unknown";
        String roomNumber = "";

        if (res != null) {
            if (res.getGuest() != null) {
                guestName = res.getGuest().getFirstName() + " " + res.getGuest().getLastName();
            }
            List<Booking> bookings = bookingRepository.findByReservationId(res.getId());
            roomNumber = bookings.stream()
                    .map(b -> b.getRoom() != null ? b.getRoom().getRoomNumber() : "")
                    .filter(r -> !r.isEmpty())
                    .collect(Collectors.joining(", "));
        }

        return FolioPaymentDTO.builder()
                .id(payment.getId())
                .folioId(folio != null ? folio.getId() : null)
                .folioNumber(folio != null ? folio.getFolioNumber() : "")
                .guestName(guestName)
                .roomNumber(roomNumber)
                .mode(payment.getPaymentMode())
                .referenceNumber(payment.getReferenceNumber())
                .notes(payment.getNotes())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}
