package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByIsDeletedFalse();
    Optional<Supplier> findByIdAndIsDeletedFalse(Long id);
    long countByIsDeletedFalse();
}
