package com.skytakeout.service;

import com.skytakeout.dto.ShoppingCartDTO;
import com.skytakeout.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查询购物车列表
     * @return
     */
    List<ShoppingCart> getShoppingCartList();

    /**
     * 清空购物车
     */
    void clean();

    /**
     * 删除购物车一个商品
     * @param shoppingCartDTO
     */
    void deleteOne(ShoppingCartDTO shoppingCartDTO);
}

