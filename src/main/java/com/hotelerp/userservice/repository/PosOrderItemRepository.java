package com.hotelerp.userservice.repository;

import com.hotelerp.common.entity.PosOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosOrderItemRepository extends JpaRepository<PosOrderItem, Long> {
}
