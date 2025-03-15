package com.skytakeout.repository;

import com.skytakeout.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c where (:name IS NULL OR c.name LIKE %:name%)")
    Page<Category> findByNameLike(String name, Pageable pageable);
    @Query("update Category c set c.status = :status where c.id = :id")
    @Modifying
    @Transactional
    void setStatus(@Param("status") Integer status,@Param("id") Long id);
    @Query("select c from Category c where (:type is null or c.type = :type)")
    List<Category> findByType(Integer type);
}
