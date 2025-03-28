package com.skytakeout.repository;

import com.skytakeout.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    /**
     * 动态条件查询
     * */
    @Query("select sc from ShoppingCart sc where " +
            "(:#{#cart.userId} is null or sc.userId = :#{#cart.userId}) and " +
            "(:#{#cart.setmealId} is null or sc.setmealId = :#{#cart.setmealId}) and " +
            "(:#{#cart.dishId} is null or sc.dishId = :#{#cart.dishId}) and " +
            "(:#{#cart.dishFlavor} is null or sc.dishFlavor = :#{#cart.dishFlavor})")
    List<ShoppingCart> list(@Param("cart") ShoppingCart shoppingCart);

    @Query("update ShoppingCart set number = :#{#cart.number} where id = :#{#cart.id}")
    void updateNumberById(@Param("cart") ShoppingCart shoppingCart);

    @Query("delete from ShoppingCart where userId = :currentID ")
    void deleteByUserId(Long currentID);

    Object findByUserIdAndDishIdAndDishFlavor(long l, long l1, String s);

    Object findByUserId(long l);

    Object findByUserIdAndSetmealId(long l, long l1);
}
