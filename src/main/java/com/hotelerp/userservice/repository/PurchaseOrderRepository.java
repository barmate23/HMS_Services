package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByIsDeletedFalse();
    Optional<PurchaseOrder> findByIdAndIsDeletedFalse(Long id);
    Optional<PurchaseOrder> findByPoNumberAndIsDeletedFalse(String poNumber);
    long countByStatus_CodeNotInAndIsDeletedFalse(java.util.List<String> statusCodes);
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(po.totalAmount) FROM PurchaseOrder po WHERE po.isDeleted = false")
    java.math.BigDecimal sumTotalAmountByIsDeletedFalse();
}
