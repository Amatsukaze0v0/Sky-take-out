package com.skytakeout.repository;


import com.skytakeout.entity.SetMealDish;
import com.skytakeout.vo.SetmealVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetMealDishRepository extends JpaRepository<SetMealDish, Long> {

    List<SetMealDish> findByDishId(@Param("dishId") Long dishId);

    void deleteBySetmealId(Long id);

    List<SetMealDish> findBySetmealId(Long setmealId);
}
