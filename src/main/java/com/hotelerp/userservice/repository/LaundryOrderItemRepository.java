package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.LaundryOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaundryOrderItemRepository extends JpaRepository<LaundryOrderItem, Long> {
    List<LaundryOrderItem> findByLaundryOrderId(Long laundryOrderId);
    void deleteByLaundryOrderId(Long laundryOrderId);
}
