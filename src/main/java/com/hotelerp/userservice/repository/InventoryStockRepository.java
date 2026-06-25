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
    List<InventoryStock> findByItemConfigCategoryIdAndIsDeletedFalse(Long categoryId);
    
    long countByIsDeletedFalse();
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s) FROM InventoryStock s JOIN s.itemConfig ic WHERE s.onHand <= ic.reorderLevel AND s.isDeleted = false")
    long countLowStockItems();
    
    @org.springframework.data.jpa.repository.Query("SELECT s FROM InventoryStock s JOIN s.itemConfig ic WHERE s.onHand <= ic.reorderLevel AND s.isDeleted = false")
    List<InventoryStock> findLowStockItems();
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(s.onHand * ic.unitCost) FROM InventoryStock s JOIN s.itemConfig ic WHERE s.isDeleted = false")
    java.math.BigDecimal calculateTotalStockValue();
}
