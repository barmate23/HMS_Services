package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.billing.*;
import java.util.List;

public interface FolioService {
    StandardResponse<FolioLedgerDTO> getLedger(Long folioId);
    StandardResponse<Void> postCharge(FolioPostingRequest request);
    StandardResponse<Void> postToFolio(PostToFolioRequest request);
    StandardResponse<Void> postChargeByRoom(Long roomId, java.math.BigDecimal amount, String source, String description);
    StandardResponse<Void> collectPayment(FolioPaymentRequest request);

    StandardResponse<Long> createFolioForReservation(Long reservationId);
    StandardResponse<List<FolioLedgerDTO>> getActiveFolios();
    StandardResponse<List<FolioPaymentDTO>> getAllPayments();
}

