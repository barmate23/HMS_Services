package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.billing.InvoiceDTO;
import java.util.List;

public interface InvoiceService {
    InvoiceDTO generateInvoice(Long folioId);
    List<InvoiceDTO> getAllInvoices();
    byte[] downloadInvoice(Long invoiceId);
}
