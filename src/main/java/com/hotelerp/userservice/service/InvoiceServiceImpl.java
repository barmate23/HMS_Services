package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.billing.InvoiceDTO;
import com.hotelerp.userservice.dto.billing.InvoiceDetailDTO;
import com.hotelerp.userservice.dto.billing.InvoiceDetailDTO.*;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final FolioRepository folioRepository;
    private final FolioPostingRepository folioPostingRepository;
    private final FolioPaymentRepository folioPaymentRepository;
    private final BookingRepository bookingRepository;

    // ── Static hotel info (update from DB/config if Hotel entity is used) ──
    private static final HotelInfoDTO HOTEL_INFO = HotelInfoDTO.builder()
            .name("HMS Cloud Hotels & Resorts")
            .address("123 Hospitality Way, Sector 62, Noida, UP - 201301")
            .gstin("09AAAAI111AIZ0")
            .pan("AAAA0000A")
            .email("billing@hmscloud.com")
            .tel("+91 120 444555")
            .build();

    @Override
    public StandardResponse<InvoiceDTO> generateInvoice(Long folioId) {
        try {
            Folio folio = folioRepository.findById(folioId)
                    .orElseThrow(() -> new RuntimeException("Folio not found"));

            Invoice invoice = Invoice.builder()
                    .folio(folio)
                    .invoiceNumber("INV-2026-" + (1000 + folioId))
                    .status("PAID")
                    .issuedAt(LocalDateTime.now())
                    .totalAmount(folio.getTotalCharges())
                    .taxAmount(folio.getTaxAmount())
                    .build();

            invoice = invoiceRepository.save(invoice);

            return StandardResponse.success(mapToDTO(invoice), "Invoice generated successfully");
        } catch (Exception e) {
            return StandardResponse.error(e.getMessage(), "INVOICE_GENERATE_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<InvoiceDTO>> getAllInvoices() {
        try {
            List<InvoiceDTO> invoices = invoiceRepository.findAll().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(invoices, "Invoices fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error(e.getMessage(), "INVOICE_FETCH_ERROR", e.getMessage());
        }
    }

    @Override
    public byte[] downloadInvoice(Long invoiceId) {
        return "Mock Invoice PDF Content".getBytes();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET Invoice Detail — full data for the PDF
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public StandardResponse<InvoiceDetailDTO> getInvoiceDetail(Long invoiceId) {
        try {
            // 1. Fetch invoice
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

            Folio folio = invoice.getFolio();
            Reservation res = folio.getReservation();
            Guest guest = res != null ? res.getGuest() : null;

            // 2. Booking(s) → room & stay details
            List<Booking> bookings = res != null
                    ? bookingRepository.findByReservationId(res.getId())
                    : List.of();

            Booking primaryBooking = bookings.isEmpty() ? null : bookings.get(0);
            Room room = primaryBooking != null ? primaryBooking.getRoom() : null;

            // 3. Stay Record
            StayRecordDTO stayRecord = buildStayRecord(res, room, primaryBooking);

            // 4. Billing To
            BillingToDTO billingTo = buildBillingTo(guest, res);

            // 5. Line Items from FolioPostings
            List<FolioPosting> postings = folioPostingRepository.findByFolioIdAndIsDeletedFalse(folio.getId());
            AtomicInteger srCounter = new AtomicInteger(1);
            List<LineItemDTO> lineItems = postings.stream()
                    .map(p -> buildLineItem(srCounter.getAndIncrement(), p))
                    .collect(Collectors.toList());

            // 6. GST Breakdown — group postings by source/SAC
            List<GstBreakdownDTO> gstBreakdown = buildGstBreakdown(postings);

            // 7. Summary totals
            BigDecimal netTaxableBase = postings.stream()
                    .map(FolioPosting::getChargeAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalTax = postings.stream()
                    .map(FolioPosting::getTaxAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal cgstSubtotal = totalTax.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            BigDecimal sgstSubtotal = totalTax.subtract(cgstSubtotal);
            BigDecimal grandTotal = netTaxableBase.add(totalTax);

            // 8. Payments received
            List<FolioPayment> payments = folioPaymentRepository.findByFolioIdAndIsDeletedFalse(folio.getId());
            BigDecimal paymentsReceived = payments.stream()
                    .map(FolioPayment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal balanceDue = grandTotal.subtract(paymentsReceived);

            // 9. Assemble final DTO
            InvoiceDetailDTO detail = InvoiceDetailDTO.builder()
                    .invoiceNumber(invoice.getInvoiceNumber())
                    .invoiceDate(invoice.getIssuedAt())
                    .folioNumber(folio.getFolioNumber())
                    .invoiceStatus(invoice.getStatus())
                    .hotelInfo(HOTEL_INFO)
                    .billingTo(billingTo)
                    .stayRecord(stayRecord)
                    .lineItems(lineItems)
                    .gstBreakdown(gstBreakdown)
                    .netTaxableBase(netTaxableBase)
                    .cgstSubtotal(cgstSubtotal)
                    .sgstSubtotal(sgstSubtotal)
                    .totalTax(totalTax)
                    .grandTotal(grandTotal)
                    .paymentsReceived(paymentsReceived)
                    .balanceDue(balanceDue)
                    .build();

            return StandardResponse.success(detail, "Invoice detail fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch invoice detail", "INVOICE_DETAIL_ERROR", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private StayRecordDTO buildStayRecord(Reservation res, Room room, Booking booking) {
        if (res == null) return StayRecordDTO.builder().build();

        String roomNumber = room != null ? room.getRoomNumber() : "";
        String roomTypeName = (room != null && room.getRoomType() != null) ? room.getRoomType().getName() : "";

        LocalDateTime checkIn = res.getCheckInDate() != null
                ? res.getCheckInDate().atTime(res.getCheckInTime() != null ? res.getCheckInTime() : java.time.LocalTime.of(14, 0))
                : null;

        LocalDateTime checkOut = res.getCheckOutDate() != null
                ? res.getCheckOutDate().atTime(res.getCheckOutTime() != null ? res.getCheckOutTime() : java.time.LocalTime.of(11, 0))
                : null;

        return StayRecordDTO.builder()
                .roomNumber(roomNumber)
                .roomTypeName(roomTypeName)
                .checkInDateTime(checkIn)
                .checkOutDateTime(checkOut)
                .numberOfNights(res.getNumberOfNights())
                .numberOfAdults(res.getNumberOfAdults())
                .numberOfChildren(res.getNumberOfChildren())
                .build();
    }

    private BillingToDTO buildBillingTo(Guest guest, Reservation res) {
        if (guest == null) return BillingToDTO.builder().build();

        String fullName = guest.getFirstName() + " " + guest.getLastName();
        // Place of supply = state (India convention)
        String placeOfSupply = guest.getState() != null ? guest.getState() : "";

        return BillingToDTO.builder()
                .guestName(fullName)
                .addressLine1(guest.getAddressLine1())
                .addressLine2(guest.getAddressLine2())
                .city(guest.getCity())
                .state(guest.getState())
                .postCode(guest.getPostCode())
                .placeOfSupply(placeOfSupply)
                .organisationName(res != null ? res.getOrganisationName() : null)
                .gstNumber(res != null ? res.getGstNumber() : null)
                .build();
    }

    /**
     * Maps a source type to its SAC code and a human-readable service title.
     * SAC codes match the PDF prototype.
     */
    private LineItemDTO buildLineItem(int srNo, FolioPosting posting) {
        SacInfo sacInfo = resolveSac(posting.getSource());
        return LineItemDTO.builder()
                .srNo(srNo)
                .date(posting.getPostingDate())
                .sacCode(sacInfo.code)
                .serviceTitle(sacInfo.title)
                .serviceDescription(posting.getDescription())
                .baseValue(posting.getChargeAmount())
                .taxAmount(posting.getTaxAmount())
                .totalAmount(posting.getTotalAmount())
                .build();
    }

    /**
     * Builds statutory GST breakdown grouped by source/SAC category.
     * Tax is split 50/50 into CGST and SGST (intra-state supply).
     */
    private List<GstBreakdownDTO> buildGstBreakdown(List<FolioPosting> postings) {
        // Group postings by source
        Map<String, List<FolioPosting>> grouped = postings.stream()
                .collect(Collectors.groupingBy(FolioPosting::getSource));

        List<GstBreakdownDTO> breakdown = new ArrayList<>();
        for (Map.Entry<String, List<FolioPosting>> entry : grouped.entrySet()) {
            String source = entry.getKey();
            List<FolioPosting> group = entry.getValue();

            BigDecimal taxable = group.stream().map(FolioPosting::getChargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal taxTotal = group.stream().map(FolioPosting::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal cgst = taxTotal.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            BigDecimal sgst = taxTotal.subtract(cgst);

            // Derive effective tax rate (for display)
            BigDecimal cgstRate = taxable.compareTo(BigDecimal.ZERO) > 0
                    ? cgst.divide(taxable, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            SacInfo sacInfo = resolveSac(source);

            breakdown.add(GstBreakdownDTO.builder()
                    .sacCode(sacInfo.code)
                    .category(sacInfo.category)
                    .taxableAmount(taxable)
                    .cgstRate(cgstRate)
                    .cgstAmount(cgst)
                    .sgstRate(cgstRate) // symmetric intra-state
                    .sgstAmount(sgst)
                    .totalTax(taxTotal)
                    .build());
        }
        return breakdown;
    }

    /** SAC code mapping per the PDF prototype. */
    private SacInfo resolveSac(String source) {
        if (source == null) return new SacInfo("999999", "Other Charges", "Other");
        return switch (source.toLowerCase()) {
            case "reservation", "room" -> new SacInfo("996311", "Room Accommodation Charges", "Room");
            case "pos", "food", "f&b", "fnb" -> new SacInfo("996331", "Food & Beverage (POS Billing)", "F&B");
            case "laundry" -> new SacInfo("996322", "Laundry Services", "Laundry");
            default -> new SacInfo("996399", source, "Other Fee");
        };
    }

    private record SacInfo(String code, String title, String category) {}

    private InvoiceDTO mapToDTO(Invoice invoice) {
        Folio folio = invoice.getFolio();
        String guestName = "Unknown";
        if (folio != null && folio.getReservation() != null && folio.getReservation().getGuest() != null) {
            guestName = folio.getReservation().getGuest().getFirstName() + " " + folio.getReservation().getGuest().getLastName();
        }

        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .folioNumber(folio != null ? folio.getFolioNumber() : "")
                .guestName(guestName)
                .amount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .date(invoice.getIssuedAt())
                .build();
    }
}

