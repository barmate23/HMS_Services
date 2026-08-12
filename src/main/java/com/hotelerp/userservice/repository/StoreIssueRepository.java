package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.StoreIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreIssueRepository extends JpaRepository<StoreIssue, Long> {
    List<StoreIssue> findByIsDeletedFalse();
    Optional<StoreIssue> findByIdAndIsDeletedFalse(Long id);
    boolean existsByIssueNumber(String issueNumber);
    List<StoreIssue> findByIssueDateAndIsDeletedFalse(java.time.LocalDate issueDate);

    List<StoreIssue> findByHotel_IdAndIsDeletedFalse(Long hotelId);
    Optional<StoreIssue> findByIdAndHotel_IdAndIsDeletedFalse(Long id, Long hotelId);
    List<StoreIssue> findByHotel_IdAndIssueDateAndIsDeletedFalse(Long hotelId, java.time.LocalDate issueDate);
}
