package com.skytakeout.service;


import com.skytakeout.dto.CategoryDTO;
import com.skytakeout.dto.CategoryPageQueryDTO;
import com.skytakeout.entity.Category;
import com.skytakeout.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 新增分类
     * @param categoryDTO
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     * @param id
     */
    void delete(Long id);

    /**
     * 修改分类信息
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 修改分类状态
     * @param status
     * @param id
     */
    void setStatus(Integer status, Long id);

    /**
     * 根据类型查询
     * @param type
     * @return
     */
    List<Category> list(Integer type);
}
