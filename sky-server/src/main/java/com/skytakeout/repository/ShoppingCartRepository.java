package com.skytakeout.repository;

import com.skytakeout.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    /**
     * 动态条件查询
     * */
    @Query("select 1 from ShoppingCart sc where :userId is not null and :userId = sc.userId" +
            " and (:setMealId is not null and :setMealId = sc.setmealId " +
            "and :dishId is not null and :dishId = sc.dishId " +
            "and :dishFlavor is not null and :dishFlavor = sc.dishFlavor" +
            ")")
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Query("update ShoppingCart set number = :number where id = :id")
    void updateNumberById(ShoppingCart shoppingCart);

}
