package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.FolioPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FolioPostingRepository extends JpaRepository<FolioPosting, Long> {
    List<FolioPosting> findByFolioIdAndIsDeletedFalse(Long folioId);
}
