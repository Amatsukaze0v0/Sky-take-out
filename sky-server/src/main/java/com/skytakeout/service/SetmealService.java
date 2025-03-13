package com.skytakeout.service;

import com.skytakeout.dto.SetmealDTO;
import com.skytakeout.dto.SetmealPageQueryDTO;
import com.skytakeout.entity.SetMeal;
import com.skytakeout.result.PageResult;
import com.skytakeout.vo.DishItemVO;
import com.skytakeout.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 新增套餐，以及套餐和菜品的关联关系
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐信息，以及对应的菜品信息
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 修改套餐状态
     * @param status
     * @param id
     */
    void setStatus(Integer status, Long id);

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    List<SetMeal> list(Long categoryId);

    /**
     * 根据套餐id查询菜品
     * @param id
     * @return
     */
    List<DishItemVO> getDishBySetmealId(Long id);
}
