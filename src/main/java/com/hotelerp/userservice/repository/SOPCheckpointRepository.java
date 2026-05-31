package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.SOPCheckpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SOPCheckpointRepository extends JpaRepository<SOPCheckpoint, Long> {
    List<SOPCheckpoint> findByFrequencyCode(String code);
}
