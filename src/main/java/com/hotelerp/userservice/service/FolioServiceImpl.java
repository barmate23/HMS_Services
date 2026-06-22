package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;

import com.hotelerp.userservice.dto.billing.*;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FolioServiceImpl implements FolioService {


    private final FolioRepository folioRepository;
    private final FolioPostingRepository folioPostingRepository;
    private final FolioPaymentRepository folioPaymentRepository;
    private final ReservationRepository reservationRepository;
    private final BookingRepository bookingRepository;
    private final InvoiceService invoiceService;

    @Override
    public StandardResponse<FolioLedgerDTO> getLedger(Long folioId) {
        try {
            Folio folio = folioRepository.findById(folioId)
                    .orElseThrow(() -> new RuntimeException("Folio not found"));

            List<FolioPosting> postings = folioPostingRepository.findByFolioIdAndIsDeletedFalse(folioId);
            List<FolioPayment> payments = folioPaymentRepository.findByFolioIdAndIsDeletedFalse(folioId);

            List<FolioLedgerDTO.LedgerEntryDTO> entries = new ArrayList<>();

            for (FolioPosting p : postings) {
                entries.add(FolioLedgerDTO.LedgerEntryDTO.builder()
                        .date(p.getPostingDate())
                        .source(p.getSource())
                        .description(p.getDescription())
                        .debit(p.getTotalAmount())
                        .tax(p.getTaxAmount())
                        .paid(p.getPaidAmount())
                        .credit(BigDecimal.ZERO)
                        .build());
            }

            for (FolioPayment p : payments) {
                entries.add(FolioLedgerDTO.LedgerEntryDTO.builder()
                        .date(p.getPaymentDate())
                        .source("Payment")
                        .description(p.getPaymentMode() + (p.getReferenceNumber() != null ? " - " + p.getReferenceNumber() : ""))
                        .debit(BigDecimal.ZERO)
                        .tax(BigDecimal.ZERO)
                        .credit(p.getAmount())
                        .build());
            }

            entries.sort(Comparator.comparing(FolioLedgerDTO.LedgerEntryDTO::getDate));

            Reservation res = folio.getReservation();
            String guestName = res != null && res.getGuest() != null ? res.getGuest().getFirstName() + " " + res.getGuest().getLastName() : "Unknown";

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
            Folio folio = folioRepository.findById(request.getFolioId())
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
            BigDecimal paidAmount = request.getPaidAmount() != null ? request.getPaidAmount() : BigDecimal.ZERO;

            FolioPosting posting = FolioPosting.builder()
                    .folio(folio)
                    .postingDate(LocalDateTime.now())
                    .source(request.getSource())
                    .description(request.getDescription())
                    .debitAmount(request.getAmount())
                    .taxAmount(taxAmount)
                    .totalAmount(totalAmount)
                    .paidAmount(paidAmount)
                    .build();

            folioPostingRepository.save(posting);

            folio.setTotalCharges(folio.getTotalCharges().add(totalAmount));
            folio.setTaxAmount(folio.getTaxAmount().add(taxAmount));
            folio.setTotalPayments(folio.getTotalPayments().add(paidAmount));
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
    public StandardResponse<Void> postChargeByRoom(Long roomId, java.math.BigDecimal amount, String source, String description) {
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
            Folio folio = folioRepository.findById(request.getFolioId())
                    .orElseThrow(() -> new RuntimeException("Folio not found"));

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
            folioRepository.save(folio);
            
            return StandardResponse.success("Payment collected successfully");
        } catch (Exception e) {
            log.error("Error collecting payment: ", e);
            return StandardResponse.error("Failed to collect payment", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }


    @Override
    @Transactional
    public StandardResponse<Void> settledFolio(Long folioId) {
        try {
            if (!folioRepository.existsById(folioId)) {
                throw new RuntimeException("Folio not found");
            }

            invoiceService.generateInvoice(folioId);
            return StandardResponse.success("Folio settled and invoice generated successfully");
        } catch (Exception e) {
            log.error("Error settling folio: ", e);
            return StandardResponse.error("Failed to settle folio", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }



    @Override
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
            return StandardResponse.success(id, "Folio created successfully");
        } catch (Exception e) {
            log.error("Error creating folio: ", e);
            return StandardResponse.error("Failed to create folio", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }


    @Override
    public StandardResponse<List<FolioLedgerDTO>> getActiveFolios() {
        try {
            List<FolioLedgerDTO> list = folioRepository.findActiveByDate(java.time.LocalDate.now()).stream()
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
        String guestName = res != null && res.getGuest() != null ? 
                res.getGuest().getFirstName() + " " + res.getGuest().getLastName() : "Unknown";
        
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
}
