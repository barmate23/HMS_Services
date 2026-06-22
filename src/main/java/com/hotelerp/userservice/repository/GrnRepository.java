package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Grn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrnRepository extends JpaRepository<Grn, Long> {
    List<Grn> findByIsDeletedFalse();
    Optional<Grn> findByIdAndIsDeletedFalse(Long id);
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(g.acceptedValue) FROM Grn g WHERE g.isDeleted = false")
    java.math.BigDecimal sumAcceptedValueByIsDeletedFalse();
}
