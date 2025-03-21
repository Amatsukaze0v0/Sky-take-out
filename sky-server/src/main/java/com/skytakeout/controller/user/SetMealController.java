package com.skytakeout.controller.user;

import com.skytakeout.entity.SetMeal;
import com.skytakeout.result.Result;
import com.skytakeout.service.SetmealService;
import com.skytakeout.vo.DishItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController("userSetMealController")
@RequestMapping("/user/setmeal")
@Tag(name = "用户端套餐接口")
public class SetMealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 条件查询
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId")
    public Result<List<SetMeal>> list(Long categoryId) {
        log.info("查询分类ID为 {} 的套餐", categoryId);
        List<SetMeal> list = setmealService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @Operation(summary = "根据套餐id查询包含的菜品列表")
    public Result<List<DishItemVO>> dishList(@PathVariable("id") Long id) {
        log.info("查询ID为 {} 的菜品列表", id);
        List<DishItemVO> list = setmealService.getDishBySetmealId(id);
        return Result.success(list);
    }
}
