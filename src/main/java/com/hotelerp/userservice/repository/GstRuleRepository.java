package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.GstRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GstRuleRepository extends JpaRepository<GstRule, Long> {
    Optional<GstRule> findByServiceCategoryIgnoreCaseAndHotelIdAndIsActiveTrue( String serviceCategory, Long hotelId);
}
