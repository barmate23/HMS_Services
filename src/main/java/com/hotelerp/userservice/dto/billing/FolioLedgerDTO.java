package com.hotelerp.userservice.dto.billing;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolioLedgerDTO {
    private Long folioId;
    private String folioNumber;
    private String reservationNumber;
    private String guestName;
    private String roomNumber;
    private BigDecimal totalCharges;
    private BigDecimal totalPayments;
    private BigDecimal taxAmount;
    private BigDecimal balance;
    private String status;
    private List<LedgerEntryDTO> entries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LedgerEntryDTO {
        private LocalDateTime date;
        private String source;
        private String description;
        private BigDecimal debit;
        private BigDecimal tax;
        private BigDecimal paid;
        private BigDecimal credit;
    }
}
