package com.hotelerp.userservice.repository;

import com.hotelerp.common.entity.UserRoomMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoomMapRepository extends JpaRepository<UserRoomMap, Long> {
    List<UserRoomMap> findByUserId(Long userId);
    Optional<UserRoomMap> findByRoomId(Long roomId);
    void deleteByUserId(Long userId);
}

