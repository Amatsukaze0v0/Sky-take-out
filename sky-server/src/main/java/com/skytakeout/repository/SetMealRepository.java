package com.skytakeout.repository;

import com.skytakeout.entity.SetMeal;
import com.skytakeout.vo.SetmealVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SetMealRepository extends JpaRepository<SetMeal, Long> {

    @Query("select sm from SetMeal sm where (:name IS NULL OR sm.name LIKE %:name%)")
    Page<SetMeal> findByNameLike(String name, Pageable pageable);

    List<SetMeal> findByCategoryIdAndStatus(Long categoryId, int enable);

    @Query("select count(sm.id) from SetMeal sm where sm.status = :status")
    Integer countByStatus(Integer status);

}
