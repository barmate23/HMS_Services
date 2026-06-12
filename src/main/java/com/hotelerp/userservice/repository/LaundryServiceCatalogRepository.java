package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.LaundryServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaundryServiceCatalogRepository extends JpaRepository<LaundryServiceCatalog, Long> {
    List<LaundryServiceCatalog> findAllByOrderByDisplayOrderAscServiceNameAsc();
    List<LaundryServiceCatalog> findByStatusOrderByDisplayOrderAscServiceNameAsc(String status);
    Optional<LaundryServiceCatalog> findByServiceNameIgnoreCase(String serviceName);
    boolean existsByServiceNameIgnoreCase(String serviceName);
}
