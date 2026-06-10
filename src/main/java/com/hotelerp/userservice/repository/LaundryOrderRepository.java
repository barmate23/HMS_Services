package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.LaundryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LaundryOrderRepository extends JpaRepository<LaundryOrder, Long> {
    Optional<LaundryOrder> findByOrderId(String orderId);

    @Query("SELECT MAX(CAST(SUBSTRING(l.orderId, 5) AS long)) FROM LaundryOrder l WHERE l.orderId LIKE 'LND-%'")
    Long findMaxOrderNumber();
}
