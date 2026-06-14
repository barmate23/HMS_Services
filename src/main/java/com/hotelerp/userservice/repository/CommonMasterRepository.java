package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.CommonMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonMasterRepository extends JpaRepository<CommonMaster, Long> {
    List<CommonMaster> findByCategoryAndIsActiveTrue(String category);
    List<CommonMaster> findByCategory(String category);
    java.util.Optional<CommonMaster> findByCategoryAndCode(String category, String code);
}
