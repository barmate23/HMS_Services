package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Optional<Invoice> findByFolioIdAndIsDeletedFalse(Long folioId);

    List<Invoice> findByIsDeletedFalse();
    List<Invoice> findByHotel_IdAndIsDeletedFalse(Long hotelId);
    Optional<Invoice> findByIdAndHotel_IdAndIsDeletedFalse(Long id, Long hotelId);

    @Query("SELECT i FROM Invoice i WHERE i.folio.id = :folioId AND i.isDeleted = false AND (:hotelId IS NULL OR i.hotel.id = :hotelId)")
    Optional<Invoice> findByFolioIdAndHotelIdAndIsDeletedFalse(@Param("folioId") Long folioId, @Param("hotelId") Long hotelId);
}
