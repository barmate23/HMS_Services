package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.VendorBillLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorBillLineRepository extends JpaRepository<VendorBillLine, Long> {
    List<VendorBillLine> findByVendorBillIdAndIsDeletedFalse(Long vendorBillId);
}
