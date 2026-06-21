package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    java.util.List<Room> findByFloorId(Long floorId);
    java.util.List<Room> findByIsDeletedFalse();
}

