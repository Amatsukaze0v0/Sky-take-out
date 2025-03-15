package com.skytakeout.service.impl;

import com.skytakeout.context.BaseContext;
import com.skytakeout.dto.DishDTO;
import com.skytakeout.dto.DishPageQueryDTO;
import com.skytakeout.entity.Category;
import com.skytakeout.entity.Dish;
import com.skytakeout.entity.DishFlavor;
import com.skytakeout.repository.CategoryRepository;
import com.skytakeout.repository.DishFlavorRepository;
import com.skytakeout.repository.DishRepository;
import com.skytakeout.result.PageResult;
import com.skytakeout.service.DishService;
import com.skytakeout.vo.DishVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private DishFlavorRepository dishFlavorRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 保存菜品及其口味
     * */
    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dish.setCreateUser(BaseContext.getCurrentID());
        dish.setUpdateUser(BaseContext.getCurrentID());
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        //向菜品表插入一条数据
        dishRepository.save(dish);

        Long dishId = dish.getId();
        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        for (DishFlavor df : flavors) {
            df.setDishId(dishId);
        }
        dishFlavorRepository.saveAll(flavors);
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        int page = dishPageQueryDTO.getPage();
        int pageSize = dishPageQueryDTO.getPageSize();
        String name = dishPageQueryDTO.getName();

        // 创建分页对象，页码从0开始，所以需要减1
        Pageable pageable = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Dish> found = dishRepository.findByNameLike(name, pageable);
        List<Dish> dishList = found.getContent();

        List<DishVO> dishVOList =dishList.stream().map(
                dish -> {
                    DishVO dishVO = new DishVO();
                    BeanUtils.copyProperties(dish, dishVO);
                    Category category = categoryRepository.findById(dish.getCategoryId()).orElse(null);
                    if (category != null) {
                        dishVO.setCategoryName(category.getName());
                    }
                    return dishVO;
                }
        ).collect(Collectors.toList());

        return new PageResult(found.getTotalElements(), dishVOList);
    }

    @Override
    public void deleteBatch(List<Long> ids) {

    }

    @Override
    public DishVO getById(Long id) {
        return dishRepository.findDishVOById(id);
    }

    @Override
    public void update(DishDTO dishDTO) {

    }

    @Override
    public List<Dish> getDishByCategoryId(Long categoryId) {
        return null;
    }

    @Override
    public List<DishVO> listWithFlavor(Long categoryId) {
        return null;
    }

    @Override
    public void startOrStop(Integer status, Long id) {

    }
}
