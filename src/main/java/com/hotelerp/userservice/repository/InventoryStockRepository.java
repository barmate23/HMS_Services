package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {
    List<InventoryStock> findByIsDeletedFalse();
    Optional<InventoryStock> findByIdAndIsDeletedFalse(Long id);
    List<InventoryStock> findByStoreIdAndIsDeletedFalse(Long storeId);
    List<InventoryStock> findByCategoryIdAndIsDeletedFalse(Long categoryId);
    
    long countByIsDeletedFalse();
    
    // Low stock: onHand <= reorderLevel
    List<InventoryStock> findByOnHandLessThanEqualAndIsDeletedFalse(java.math.BigDecimal threshold); 
    // Actually the logic is item-specific. Jpa doesn't easily compare two columns without @Query.
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s) FROM InventoryStock s WHERE s.onHand <= s.reorderLevel AND s.isDeleted = false")
    long countLowStockItems();
    
    @org.springframework.data.jpa.repository.Query("SELECT s FROM InventoryStock s WHERE s.onHand <= s.reorderLevel AND s.isDeleted = false")
    List<InventoryStock> findLowStockItems();
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(s.onHand * s.unitCost) FROM InventoryStock s WHERE s.isDeleted = false")
    java.math.BigDecimal calculateTotalStockValue();
}
