package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.FolioPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FolioPaymentRepository extends JpaRepository<FolioPayment, Long> {
    List<FolioPayment> findByFolioIdAndIsDeletedFalse(Long folioId);
}
