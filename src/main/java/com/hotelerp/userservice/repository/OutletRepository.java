package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Outlet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutletRepository extends JpaRepository<Outlet, Long> {
    List<Outlet> findByIsActiveTrue();
    List<Outlet> findByIsActiveTrueAndIsDeletedFalse();
    List<Outlet> findByIsDeletedFalse();
    List<Outlet> findByHotel_IdAndIsDeletedFalse(Long hotelId);
    List<Outlet> findByHotel_IdAndIsActiveTrueAndIsDeletedFalse(Long hotelId);
}
