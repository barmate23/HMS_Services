package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.UserRoomMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoomMapRepository extends JpaRepository<UserRoomMap, Long> {
    List<UserRoomMap> findByUserId(Long userId);
    Optional<UserRoomMap> findByRoomId(Long roomId);
    void deleteByUserId(Long userId);

    List<UserRoomMap> findByHotel_Id(Long hotelId);
    List<UserRoomMap> findByHotel_IdAndUserId(Long hotelId, Long userId);
}

