package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findByIsDeletedFalse();

    Optional<PurchaseOrder> findByIdAndIsDeletedFalse(Long id);

    Optional<PurchaseOrder> findByPoNumberAndIsDeletedFalse(String poNumber);

    // Count POs whose status code is NOT in the given list
    long countByStatus_CodeNotInAndIsDeletedFalse(List<String> statusCodes);

    // Count POs by exact status code
    long countByStatus_CodeAndIsDeletedFalse(String statusCode);

    // Sum total PO amount (active records)
    @Query("SELECT SUM(po.totalAmount) FROM PurchaseOrder po WHERE po.isDeleted = false")
    BigDecimal sumTotalAmountByIsDeletedFalse();

    // Sum PO amount grouped by supplier category (for Supplier Categories section)
    @Query("""
            SELECT po.supplier.category.value, SUM(po.totalAmount)
            FROM PurchaseOrder po
            WHERE po.isDeleted = false AND po.supplier.category IS NOT NULL
            GROUP BY po.supplier.category.id, po.supplier.category.value
            """)
    List<Object[]> sumPoValueBySupplierCategory();
}
