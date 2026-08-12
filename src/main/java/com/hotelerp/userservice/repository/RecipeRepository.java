package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByIdAndIsDeletedFalse(Long id);

    /** Find the active recipe for a specific MenuItem. */
    Optional<Recipe> findByMenuItemIdAndIsDeletedFalse(Long menuItemId);

    /** Paginated list of all active recipes filtered by hotelId. */
    @Query("""
            SELECT r FROM Recipe r
            WHERE r.isDeleted = false
              AND (:hotelId IS NULL OR r.hotel.id = :hotelId)
            ORDER BY r.createdAt DESC
            """)
    Page<Recipe> findAllActive(@Param("hotelId") Long hotelId, Pageable pageable);

    /** List of all active recipes for summary calculations filtered by hotelId. */
    @Query("""
            SELECT r FROM Recipe r
            WHERE r.isDeleted = false
              AND (:hotelId IS NULL OR r.hotel.id = :hotelId)
            """)
    java.util.List<Recipe> findAllActiveList(@Param("hotelId") Long hotelId);

    /** Check if a recipe already exists for a given menu item (excluding a specific id on update). */
    boolean existsByMenuItemIdAndIsDeletedFalse(Long menuItemId);

    boolean existsByMenuItemIdAndIsDeletedFalseAndIdNot(Long menuItemId, Long id);
}
