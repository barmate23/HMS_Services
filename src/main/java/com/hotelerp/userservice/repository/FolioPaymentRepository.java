package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.FolioPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolioPaymentRepository extends JpaRepository<FolioPayment, Long> {
    List<FolioPayment> findByFolioIdAndIsDeletedFalse(Long folioId);
    List<FolioPayment> findByIsDeletedFalse();

    @Query("SELECT fp FROM FolioPayment fp JOIN fp.folio f JOIN f.reservation r WHERE fp.isDeleted = false AND (:hotelId IS NULL OR r.hotel.id = :hotelId)")
    List<FolioPayment> findByHotelIdAndIsDeletedFalse(@Param("hotelId") Long hotelId);
}
