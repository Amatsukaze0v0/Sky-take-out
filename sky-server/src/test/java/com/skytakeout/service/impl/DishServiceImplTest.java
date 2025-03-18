package com.skytakeout.service.impl;

import com.skytakeout.constant.MessageConstant;
import com.skytakeout.constant.StatusConstant;
import com.skytakeout.entity.Dish;
import com.skytakeout.entity.SetMealDish;
import com.skytakeout.exception.DeletionNotAllowedException;
import com.skytakeout.repository.DishFlavorRepository;
import com.skytakeout.repository.DishRepository;
import com.skytakeout.repository.SetMealDishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DishServiceImplTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DishFlavorRepository dishFlavorRepository;

    @Mock
    private SetMealDishRepository setMealDishRepository;

    @InjectMocks
    private DishServiceImpl dishService;

    private Dish dish1;
    private Dish dish2;
    private Dish dish3;
    private List<Long> dishIds;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        dish1 = new Dish();
        dish1.setId(1L);
        dish1.setName("测试菜品1");
        dish1.setStatus(StatusConstant.DISABLE); // 停售状态

        dish2 = new Dish();
        dish2.setId(2L);
        dish2.setName("测试菜品2");
        dish2.setStatus(StatusConstant.ENABLE); // 起售状态

        dish3 = new Dish();
        dish3.setId(3L);
        dish3.setName("测试菜品3");
        dish3.setStatus(StatusConstant.DISABLE); // 停售状态

        dishIds = Arrays.asList(1L, 3L);
    }

    @Test
    void testDeleteBatch_Success() {
        // 模拟查询菜品
        when(dishRepository.getReferenceById(1L)).thenReturn(dish1);
        when(dishRepository.getReferenceById(3L)).thenReturn(dish3);

        // 模拟查询套餐菜品关联
        when(setMealDishRepository.findByDishId(anyLong())).thenReturn(Collections.emptyList());

        // 执行删除
        dishService.deleteBatch(dishIds);

        // 验证方法调用
        verify(dishFlavorRepository, times(2)).deleteByDishId(anyLong());
        verify(dishRepository, times(2)).deleteById(anyLong());
    }

    @Test
    void testDeleteBatch_DishOnSale() {
        // 测试删除起售中的菜品
        List<Long> ids = Arrays.asList(2L);

        // 模拟查询菜品
        when(dishRepository.getReferenceById(2L)).thenReturn(dish2);

        // 执行删除，应该抛出异常
        DeletionNotAllowedException exception = assertThrows(
                DeletionNotAllowedException.class,
                () -> dishService.deleteBatch(ids)
        );

        // 验证异常消息
        assert(exception.getMessage().equals(MessageConstant.DISH_ON_SALE));

        // 验证没有调用删除方法
        verify(dishFlavorRepository, never()).deleteByDishId(anyLong());
        verify(dishRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteBatch_DishRelatedToSetMeal() {
        // 测试删除关联套餐的菜品
        List<Long> ids = Arrays.asList(1L);

        // 模拟查询菜品
        when(dishRepository.getReferenceById(1L)).thenReturn(dish1);

        // 模拟查询套餐菜品关联，返回一个非空列表表示有关联
        List<SetMealDish> setMealDishes = new ArrayList<>();
        setMealDishes.add(new SetMealDish());
        when(setMealDishRepository.findByDishId(1L)).thenReturn(setMealDishes);

        // 执行删除，应该抛出异常
        DeletionNotAllowedException exception = assertThrows(
                DeletionNotAllowedException.class,
                () -> dishService.deleteBatch(ids)
        );

        // 验证异常消息
        assert(exception.getMessage().equals(MessageConstant.DISH_BE_RELATED_BY_SETMEAL));

        // 验证没有调用删除方法
        verify(dishFlavorRepository, never()).deleteByDishId(anyLong());
        verify(dishRepository, never()).deleteById(anyLong());
    }
}