package com.skytakeout.controller.admin;

import com.skytakeout.dto.DishDTO;
import com.skytakeout.dto.DishPageQueryDTO;
import com.skytakeout.result.PageResult;
import com.skytakeout.result.Result;
import com.skytakeout.service.DishService;
import com.skytakeout.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Tag(name = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @Operation(summary = "新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.save(dishDTO);

        //清理Redis对应分类缓存数据
        String key = "dish_"+dishDTO.getCategoryId();
        redisTemplate.delete(key);

        return Result.success();
    }
    @PostMapping("/page")
    @Operation(summary = "分页查询菜品")
    public Result<PageResult> pageQuery(@RequestBody DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询;{}", dishPageQueryDTO);
        return Result.success(dishService.pageQuery(dishPageQueryDTO));
    }
    @DeleteMapping
    @Operation(summary = "删除菜品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);

        //约定：删除所有缓存
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return Result.success();
    }
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据ID查询菜品：{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }
    @PutMapping
    @Operation(summary = "更新菜品")
    public Result editDish(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.update(dishDTO);

        //约定：删除所有缓存
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return Result.success();
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "修改菜品状态")
    public Result setStatus(@PathVariable Integer status, @RequestBody Long id) {
        log.info("修改菜品ID：{}", id);
        dishService.startOrStop(status, id);

        //约定：删除所有缓存
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return Result.success();
    }
}
