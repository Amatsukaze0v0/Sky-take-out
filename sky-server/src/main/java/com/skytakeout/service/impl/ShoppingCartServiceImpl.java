package com.skytakeout.service.impl;

import com.skytakeout.context.BaseContext;
import com.skytakeout.dto.ShoppingCartDTO;
import com.skytakeout.entity.Dish;
import com.skytakeout.entity.SetMeal;
import com.skytakeout.entity.ShoppingCart;
import com.skytakeout.repository.DishRepository;
import com.skytakeout.repository.SetMealRepository;
import com.skytakeout.repository.ShoppingCartRepository;
import com.skytakeout.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private SetMealRepository setMealRepository;
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //获取用户ID
        shoppingCart.setUserId(BaseContext.getCurrentID());

        List<ShoppingCart> list = shoppingCartRepository.list(shoppingCart);
        //如果购物车有相同商品，数量+1
        if (list != null && !list.isEmpty()) {
            //约定：只可能查到唯一一条数据，因条件互斥：只可能是菜品或套餐
            ShoppingCart thing = list.get(0);
            thing.setNumber(thing.getNumber() + 1);
            shoppingCartRepository.updateNumberById(thing);
        } else {
            //如果不在，新增此商品
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();

            //判断是菜品还是套餐
            if (dishId != null) {
                // 添加的是菜品
                Dish dish = dishRepository.getReferenceById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            } else {
                //添加的是套餐
                SetMeal setMeal = setMealRepository.getReferenceById(setmealId);

                shoppingCart.setName(setMeal.getName());
                shoppingCart.setImage(setMeal.getImage());
                shoppingCart.setAmount(setMeal.getPrice());

            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartRepository.save(shoppingCart);
        }

    }

    @Override
    public List<ShoppingCart> getShoppingCartList() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentID());
        return shoppingCartRepository.list(shoppingCart);
    }

    @Override
    public void clean() {
        shoppingCartRepository.deleteByUserId(BaseContext.getCurrentID());
    }

    @Override
    public void deleteOne(ShoppingCartDTO shoppingCartDTO) {

    }
}
