package com.skytakeout.service.impl;

import com.skytakeout.context.BaseContext;
import com.skytakeout.dto.ShoppingCartDTO;
import com.skytakeout.entity.Dish;
import com.skytakeout.entity.SetMeal;
import com.skytakeout.entity.ShoppingCart;
import com.skytakeout.repository.*;
import com.skytakeout.service.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ShoppingCartServiceTest {

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private SetMealRepository setMealRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // 为 dishRepository 的方法设置模拟行为
        Dish mockDish = new Dish();
        mockDish.setId(1L);
        mockDish.setName("宫保鸡丁");
        mockDish.setPrice(new BigDecimal("38.00"));
        mockDish.setStatus(1); // 假设状态为1表示启用
        mockDish.setImage("dish.jpg");
        when(dishRepository.findById(1L)).thenReturn(Optional.of(mockDish));

        // 为 setMealRepository 的方法设置模拟行为
        SetMeal mockSetMeal = new SetMeal();
        mockSetMeal.setId(1L);
        mockSetMeal.setName("商务套餐");
        mockSetMeal.setPrice(new BigDecimal("128.00"));
        mockSetMeal.setStatus(1);
        mockSetMeal.setImage("setmeal.jpg");

        // 确保 findById 方法返回有效的 SetMeal 对象
        when(setMealRepository.findById(anyLong())).thenReturn(Optional.of(mockSetMeal));
    }

    @Test
    public void testAddToShoppingCart_NewDishItem() {
        // 准备测试数据
        ShoppingCartDTO dto = new ShoppingCartDTO();
        dto.setDishId(1L);
        dto.setDishFlavor("辣");

        // 模拟用户ID
        try (MockedStatic<BaseContext> baseContext = mockStatic(BaseContext.class)) {
            baseContext.when(BaseContext::getCurrentID).thenReturn(1L);

            // 模拟购物车为空 - 模拟 list 方法返回空列表
            when(shoppingCartRepository.list(any(ShoppingCart.class)))
                    .thenReturn(List.of());

            // 模拟购物车为空
            when(shoppingCartRepository.findByUserIdAndDishIdAndDishFlavor(anyLong(), anyLong(), anyString()))
                    .thenReturn(null);

            // 模拟保存购物车项
            when(shoppingCartRepository.save(any(ShoppingCart.class))).thenAnswer(i -> i.getArgument(0));

            // 执行测试
            shoppingCartService.add(dto);

            // 验证结果
            verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        }
    }

    @Test
    public void testAddToShoppingCart_NewSetmealItem() {
        // 准备测试数据
        ShoppingCartDTO dto = new ShoppingCartDTO();
        dto.setSetmealId(1L);

        // 模拟用户ID
        try (MockedStatic<BaseContext> baseContext = mockStatic(BaseContext.class)) {
            baseContext.when(BaseContext::getCurrentID).thenReturn(1L);

            // 模拟购物车为空
            when(shoppingCartRepository.findByUserIdAndSetmealId(anyLong(), anyLong()))
                    .thenReturn(null);

            // 模拟保存购物车项
            when(shoppingCartRepository.save(any(ShoppingCart.class))).thenAnswer(i -> i.getArgument(0));

            // 执行测试
            shoppingCartService.add(dto);

            // 验证结果
            verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
            verify(setMealRepository, times(1)).findById(anyLong());
        }
    }
    @Test
    public void testAddToShoppingCart_ExistingDishItem() {
        // 准备测试数据
        ShoppingCartDTO dto = new ShoppingCartDTO();
        dto.setDishId(1L);
        dto.setDishFlavor("辣");

        ShoppingCart existingItem = new ShoppingCart();
        existingItem.setId(1L);
        existingItem.setUserId(1L);
        existingItem.setDishId(1L);
        existingItem.setDishFlavor("辣");
        existingItem.setNumber(1);

        // 模拟用户ID
        try (MockedStatic<BaseContext> baseContext = mockStatic(BaseContext.class)) {
            baseContext.when(BaseContext::getCurrentID).thenReturn(1L);

            // 模拟购物车已有该商品 - 同时模拟 list 方法和 findByUserIdAndDishIdAndDishFlavor 方法
            when(shoppingCartRepository.list(any(ShoppingCart.class)))
                    .thenReturn(List.of(existingItem));
            when(shoppingCartRepository.findByUserIdAndDishIdAndDishFlavor(anyLong(), anyLong(), anyString()))
                    .thenReturn(existingItem);

            // 模拟保存购物车项
            when(shoppingCartRepository.save(any(ShoppingCart.class))).thenAnswer(i -> i.getArgument(0));

            // 执行测试
            shoppingCartService.add(dto);

            // 验证结果 - 应该调用 updateNumberById 而不是 save
            verify(shoppingCartRepository, times(1)).updateNumberById(any(ShoppingCart.class));
            verify(shoppingCartRepository, never()).save(any(ShoppingCart.class));
            assertEquals(2, existingItem.getNumber()); // 数量应该增加1
        }
    }

    @Test
    public void testGetShoppingCartList() {
        // 准备测试数据
        List<ShoppingCart> cartList = new ArrayList<>();
        ShoppingCart item1 = new ShoppingCart();
        item1.setId(1L);
        item1.setUserId(1L);
        item1.setDishId(1L);
        item1.setNumber(2);
        cartList.add(item1);

        // 模拟用户ID
        try (MockedStatic<BaseContext> baseContext = mockStatic(BaseContext.class)) {
            baseContext.when(BaseContext::getCurrentID).thenReturn(1L);

            // 模拟查询购物车
            when(shoppingCartRepository.findByUserId(1L)).thenReturn(cartList);

            // 执行测试
            List<ShoppingCart> result = shoppingCartService.getShoppingCartList();

            // 验证结果
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getDishId());
            assertEquals(2, result.get(0).getNumber());
        }
    }

    @Test
    public void testCleanShoppingCart() {
        // 模拟用户ID
        try (MockedStatic<BaseContext> baseContext = mockStatic(BaseContext.class)) {
            baseContext.when(BaseContext::getCurrentID).thenReturn(1L);

            // 执行测试
            shoppingCartService.clean();

            // 验证结果
            verify(shoppingCartRepository, times(1)).deleteByUserId(1L);
        }
    }
}