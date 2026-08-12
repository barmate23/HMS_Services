package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    List<PurchaseRequest> findByIsDeletedFalse();
    List<PurchaseRequest> findByHotel_IdAndIsDeletedFalse(Long hotelId);
    Optional<PurchaseRequest> findByIdAndIsDeletedFalse(Long id);
    Optional<PurchaseRequest> findByIdAndHotel_IdAndIsDeletedFalse(Long id, Long hotelId);
    boolean existsByPrNumber(String prNumber);
    long countByStatus_CodeInAndIsDeletedFalse(java.util.List<String> statusCodes);
    long countByHotel_IdAndStatus_CodeInAndIsDeletedFalse(Long hotelId, java.util.List<String> statusCodes);
}
