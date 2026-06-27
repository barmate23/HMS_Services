package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByIsDeletedFalse();

    Optional<Supplier> findByIdAndIsDeletedFalse(Long id);

    long countByIsDeletedFalse();

    // Count suppliers grouped by category (for Supplier Categories section)
    @Query("""
            SELECT s.category.value, COUNT(s)
            FROM Supplier s
            WHERE s.isDeleted = false AND s.category IS NOT NULL
            GROUP BY s.category.id, s.category.value
            """)
    List<Object[]> countSuppliersByCategory();
}
