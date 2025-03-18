package com.skytakeout.service.impl;

import com.skytakeout.constant.MessageConstant;
import com.skytakeout.constant.StatusConstant;
import com.skytakeout.context.BaseContext;
import com.skytakeout.dto.DishDTO;
import com.skytakeout.dto.DishPageQueryDTO;
import com.skytakeout.entity.Category;
import com.skytakeout.entity.Dish;
import com.skytakeout.entity.DishFlavor;
import com.skytakeout.entity.SetMealDish;
import com.skytakeout.exception.DeletionNotAllowedException;
import com.skytakeout.repository.CategoryRepository;
import com.skytakeout.repository.DishFlavorRepository;
import com.skytakeout.repository.DishRepository;
import com.skytakeout.repository.SetMealDishRepository;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private DishFlavorRepository dishFlavorRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SetMealDishRepository setMealDishRepository;

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
    /**
     * 菜品批量删除
     * */
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断是否能够删除----是否存在起售中菜品?
        for (Long id : ids) {
            Dish dish = dishRepository.getReferenceById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            } else {
                //TODO: 判断----如果菜品关联套餐，不可删除
                List<SetMealDish> setMealDishes = setMealDishRepository.findByDishId(id);
                if (setMealDishes != null && !setMealDishes.isEmpty()) {
                    throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
                }
                //删除菜品关联的口味
                dishFlavorRepository.deleteByDishId(id);
                //删除菜品表中的菜品
                dishRepository.deleteById(id);
            }
        }
    }

    @Override
    public DishVO getById(Long id) {
        return dishRepository.findDishVOById(id);
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setUpdateTime(LocalDateTime.now());
        dish.setUpdateUser(BaseContext.getCurrentID());

        Long currentId = BaseContext.getCurrentID();
        if (currentId != null) {
            dish.setUpdateUser(currentId);
        } else {
            // 如果获取不到当前用户ID，设置默认值
            dish.setUpdateUser(1L);
        }

        dishRepository.save(dish);
    }

    @Override
    public List<Dish> getDishByCategoryId(Long categoryId) {
        if (categoryId != null) {
            return dishRepository.findByCategroyId(categoryId);
        } else {
            return dishRepository.findAll();
        }
    }

    @Override
    public List<DishVO> listWithFlavor(Long categoryId) {
        List<Dish> dishList = dishRepository.findByCategroyId(categoryId);

        // 使用Stream API转换为DishVO并添加口味信息
        return dishList.stream().map(dish -> {
            // 创建DishVO对象
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);

            // 查询分类名称
            Category category = categoryRepository.findById(dish.getCategoryId()).orElse(null);
            if (category != null) {
                dishVO.setCategoryName(category.getName());
            }

            // 查询并设置口味信息
            List<DishFlavor> flavors = dishFlavorRepository.findByDishId(dish.getId());
            dishVO.setFlavors(flavors);

            return dishVO;
        }).collect(Collectors.toList());
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = dishRepository.findById(id).orElse(null);

        if(dish == null) {
            throw new NoSuchElementException(MessageConstant.NOT_FOUND);
        }
        dish.setStatus(status);
        dish.setUpdateUser(BaseContext.getCurrentID());
        dish.setUpdateTime(LocalDateTime.now());

        dishRepository.save(dish);
    }
}
