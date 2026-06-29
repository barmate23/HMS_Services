package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByOutletId(Long outletId);
    List<MenuItem> findByCategoryId(Long categoryId);
    List<MenuItem> findByCategoryValue(String categoryValue);
    List<MenuItem> findByCategoryIdAndIsDeletedFalse(Long categoryId);
    List<MenuItem> findBySubcategoryIdAndIsDeletedFalse(Long subcategoryId);
    List<MenuItem> findByCategoryIdAndSubcategoryIdAndIsDeletedFalse(Long categoryId, Long subcategoryId);
    List<MenuItem> findByIsDeletedFalse();
}
