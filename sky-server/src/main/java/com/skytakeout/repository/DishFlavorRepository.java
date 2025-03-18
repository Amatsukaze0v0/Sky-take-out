package com.skytakeout.repository;

import com.skytakeout.entity.DishFlavor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishFlavorRepository extends JpaRepository<DishFlavor, Long> {

    @Query("delete from DishFlavor df where df.dishId = :dishId")
    @Modifying
    @Transactional
    void deleteByDishId(@Param("dishId") Long dishId);

    List<DishFlavor> findByDishId(Long id);
}
