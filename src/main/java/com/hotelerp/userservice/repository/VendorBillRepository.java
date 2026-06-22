package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.VendorBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {
    List<VendorBill> findByIsDeletedFalse();
    Optional<VendorBill> findByIdAndIsDeletedFalse(Long id);
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(vb.totalAmount) FROM VendorBill vb WHERE vb.status.code != :statusCode AND vb.isDeleted = false")
    java.math.BigDecimal sumTotalAmountByStatus_CodeNotAndIsDeletedFalse(@org.springframework.data.repository.query.Param("statusCode") String statusCode);
}
