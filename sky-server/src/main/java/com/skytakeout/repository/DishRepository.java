package com.skytakeout.repository;

import com.skytakeout.entity.Dish;
import com.skytakeout.entity.Employee;
import com.skytakeout.vo.DishVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    @Query("select d from Dish d where (:name IS NULL OR d.name LIKE %:name%)")
    Page<Dish> findByNameLike(String name, Pageable pageable);

    // 如果需要联合查询，可以使用JPQL
    @Query("SELECT new com.skytakeout.vo.DishVO(d, c.name) FROM Dish d JOIN Category c ON d.categoryId = c.id WHERE d.id = :id")
    DishVO findDishVOById(@Param("id") Long id);
}
