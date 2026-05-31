package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.LostAndFoundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostAndFoundRepository extends JpaRepository<LostAndFoundItem, Long> {
    List<LostAndFoundItem> findByStatus(LostAndFoundItem.ItemStatus status);
    List<LostAndFoundItem> findByRoomId(Long roomId);
}
