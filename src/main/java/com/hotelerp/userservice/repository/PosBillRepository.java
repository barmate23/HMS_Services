package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.PosBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PosBillRepository extends JpaRepository<PosBill, Long> {
}
