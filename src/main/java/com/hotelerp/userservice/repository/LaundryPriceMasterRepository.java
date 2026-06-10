package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.LaundryPriceMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaundryPriceMasterRepository extends JpaRepository<LaundryPriceMaster, Long> {
    List<LaundryPriceMaster> findByStatus(String status);
}
