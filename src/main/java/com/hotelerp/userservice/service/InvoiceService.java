package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.billing.InvoiceDTO;

import java.util.List;

public interface InvoiceService {
    StandardResponse<InvoiceDTO> generateInvoice(Long folioId);
    StandardResponse<List<InvoiceDTO>> getAllInvoices();
    byte[] downloadInvoice(Long invoiceId);
}
