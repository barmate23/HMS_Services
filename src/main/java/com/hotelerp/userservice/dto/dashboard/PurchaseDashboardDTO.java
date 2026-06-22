package com.hotelerp.userservice.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDashboardDTO {
    private PurchaseStats stats;
    private List<PendingProcurementDTO> pendingProcurement;
    private List<VendorBillQueueDTO> vendorBillQueue;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseStats {
        private Long suppliersCount;
        private Long openPosCount;
        private BigDecimal poValue;
        private BigDecimal inwardValue;
        private BigDecimal payables;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingProcurementDTO {
        private String poNumber;
        private String supplierName;
        private LocalDate expectedDate;
        private String status;
        private String statusCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorBillQueueDTO {
        private String billNumber;
        private String supplierName;
        private LocalDate dueDate;
        private BigDecimal amount;
        private String status;
        private String statusCode;
    }
}
