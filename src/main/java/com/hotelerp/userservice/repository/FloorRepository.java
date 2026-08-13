package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {
    java.util.List<Floor> findByHotel_Id(Long hotelId);
}
