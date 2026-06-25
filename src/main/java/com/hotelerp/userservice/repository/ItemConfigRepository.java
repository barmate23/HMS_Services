package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.ItemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemConfigRepository extends JpaRepository<ItemConfig, Long> {
    Optional<ItemConfig> findByItemCode(String itemCode);
    List<ItemConfig> findAllByIsActiveTrue();
}
