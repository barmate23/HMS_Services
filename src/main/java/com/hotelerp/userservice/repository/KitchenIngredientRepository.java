package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.KitchenIngredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KitchenIngredientRepository extends JpaRepository<KitchenIngredient, Long> {

    /** Paginated list – optionally filter by hotelId, category id and search keyword. */
    @Query("""
            SELECT k FROM KitchenIngredient k
            WHERE k.isDeleted = false
              AND (:hotelId IS NULL OR k.hotel.id = :hotelId)
              AND (:categoryId IS NULL OR k.category.id = :categoryId)
              AND (:search IS NULL OR :search = '' OR
                   LOWER(k.ingredientName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(k.ingredientCode) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(k.preferredSupplier) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(k.category.value) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY k.ingredientCode ASC
            """)
    Page<KitchenIngredient> findAllActive(
            @Param("hotelId") Long hotelId,
            @Param("categoryId") Long categoryId,
            @Param("search") String search,
            Pageable pageable);

    /** Total count of active ingredients. */
    long countByIsDeletedFalse();
    long countByHotel_IdAndIsDeletedFalse(Long hotelId);

    /**
     * Count of ingredients where currentStockLevel <= reorderThresholdLevel (low stock).
     * Both values must be non-null to participate in the comparison.
     */
    @Query("""
            SELECT COUNT(k) FROM KitchenIngredient k
            WHERE k.isDeleted = false
              AND (:hotelId IS NULL OR k.hotel.id = :hotelId)
              AND k.currentStockLevel IS NOT NULL
              AND k.reorderThresholdLevel IS NOT NULL
              AND k.currentStockLevel <= k.reorderThresholdLevel
            """)
    long countLowStock(@Param("hotelId") Long hotelId);

    /** Count of distinct categories that have at least one active ingredient. */
    @Query("""
            SELECT COUNT(DISTINCT k.category.id) FROM KitchenIngredient k
            WHERE k.isDeleted = false AND (:hotelId IS NULL OR k.hotel.id = :hotelId) AND k.category IS NOT NULL
            """)
    long countDistinctCategories(@Param("hotelId") Long hotelId);

    /** Find next ingredient code sequence number. */
    @Query("SELECT COUNT(k) FROM KitchenIngredient k")
    long countAll();

    /** Case-insensitive name check to prevent duplicates. */
    boolean existsByIngredientNameIgnoreCaseAndIsDeletedFalse(String ingredientName);

    boolean existsByIngredientNameIgnoreCaseAndIsDeletedFalseAndIdNot(String ingredientName, Long id);

    boolean existsByHotel_IdAndIngredientNameIgnoreCaseAndIsDeletedFalse(Long hotelId, String ingredientName);

    boolean existsByHotel_IdAndIngredientNameIgnoreCaseAndIsDeletedFalseAndIdNot(Long hotelId, String ingredientName, Long id);

    Optional<KitchenIngredient> findByIdAndIsDeletedFalse(Long id);
}
