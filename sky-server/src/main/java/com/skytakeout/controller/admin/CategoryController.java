package com.skytakeout.controller.admin;

import com.skytakeout.dto.CategoryDTO;
import com.skytakeout.dto.CategoryPageQueryDTO;
import com.skytakeout.result.PageResult;
import com.skytakeout.result.Result;
import com.skytakeout.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Tag(name = "分类相关接口")
public class CategoryController {

    private CategoryService categoryService;
    @PostMapping
    @Operation(summary = "新建分类")
    public Result addNewCategory(CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }
    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public Result<PageResult> pageQuery(@RequestParam(required = false) String name,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(defaultValue = "1") Integer type) {
        CategoryPageQueryDTO categoryPageQueryDTO = new CategoryPageQueryDTO();
        categoryPageQueryDTO.setPage(page);
        categoryPageQueryDTO.setName(name);
        categoryPageQueryDTO.setType(type);
        categoryPageQueryDTO.setPageSize(pageSize);

        log.info("查询分页：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
    @PostMapping("/status/{status}")
    @Operation(summary = "分类状态修改")
    public Result changeStatus(@PathVariable Integer status, @RequestParam Long id) {
        log.info("启用禁用员工账号:{},{}", status, id);
        categoryService.setStatus(status, id);
        return Result.success();
    }
}
