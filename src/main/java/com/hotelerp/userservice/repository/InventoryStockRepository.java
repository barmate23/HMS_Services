package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {
    List<InventoryStock> findByIsDeletedFalse();
    List<InventoryStock> findByHotel_IdAndIsDeletedFalse(Long hotelId);
    Optional<InventoryStock> findByIdAndIsDeletedFalse(Long id);
    List<InventoryStock> findByItemConfigIdAndIsDeletedFalse(Long itemConfigId);
    List<InventoryStock> findByHotel_IdAndItemConfigIdAndIsDeletedFalse(Long hotelId, Long itemConfigId);
    List<InventoryStock> findByStoreIdAndIsDeletedFalse(Long storeId);
    List<InventoryStock> findByHotel_IdAndStoreIdAndIsDeletedFalse(Long hotelId, Long storeId);
    List<InventoryStock> findByItemConfigCategoryIdAndIsDeletedFalse(Long categoryId);
    List<InventoryStock> findByHotel_IdAndItemConfigCategoryIdAndIsDeletedFalse(Long hotelId, Long categoryId);
    
    long countByIsDeletedFalse();
    long countByHotel_IdAndIsDeletedFalse(Long hotelId);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s) FROM InventoryStock s JOIN s.itemConfig ic WHERE s.onHand <= ic.reorderLevel AND s.isDeleted = false AND (:hotelId IS NULL OR s.hotel.id = :hotelId)")
    long countLowStockItems(@org.springframework.data.repository.query.Param("hotelId") Long hotelId);
    
    @org.springframework.data.jpa.repository.Query("SELECT s FROM InventoryStock s JOIN s.itemConfig ic WHERE s.onHand <= ic.reorderLevel AND s.isDeleted = false AND (:hotelId IS NULL OR s.hotel.id = :hotelId)")
    List<InventoryStock> findLowStockItems(@org.springframework.data.repository.query.Param("hotelId") Long hotelId);
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(s.onHand * ic.unitCost) FROM InventoryStock s JOIN s.itemConfig ic WHERE s.isDeleted = false AND (:hotelId IS NULL OR s.hotel.id = :hotelId)")
    java.math.BigDecimal calculateTotalStockValue(@org.springframework.data.repository.query.Param("hotelId") Long hotelId);
}
