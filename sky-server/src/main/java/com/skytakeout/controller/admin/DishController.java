package com.skytakeout.controller.admin;

import com.skytakeout.dto.DishDTO;
import com.skytakeout.dto.DishPageQueryDTO;
import com.skytakeout.result.PageResult;
import com.skytakeout.result.Result;
import com.skytakeout.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Tag(name = "菜品相关接口")
public class DishController {

    private DishService dishService;

    @PostMapping
    @Operation(summary = "新增菜品")
    public Result save(DishDTO dishDTO) {
        log.info("新增菜品");
        dishService.save(dishDTO);
        return Result.success();
    }
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询;{}", dishPageQueryDTO);
        return Result.success(dishService.pageQuery(dishPageQueryDTO));
    }
}
