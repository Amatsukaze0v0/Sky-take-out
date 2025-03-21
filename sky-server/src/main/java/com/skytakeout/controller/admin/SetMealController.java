package com.skytakeout.controller.admin;

import com.skytakeout.dto.SetmealDTO;
import com.skytakeout.dto.SetmealPageQueryDTO;
import com.skytakeout.result.PageResult;
import com.skytakeout.result.Result;
import com.skytakeout.service.SetmealService;
import com.skytakeout.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Tag(name = "管理端套餐相关接口")
public class SetMealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @Operation(summary = "新建套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "套餐分页查询")
    public Result<PageResult> page(@RequestBody SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询");
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @Operation(summary = "批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐");
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据id：{} 查询套餐", id);
        setmealService.getById(id);
        return null;
    }

    @PutMapping
    @Operation(summary = "修改套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "套餐起售停售")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result startOrstop(@PathVariable Integer status, Long id) {
        setmealService.setStatus(status, id);
        return Result.success();
    }
}
