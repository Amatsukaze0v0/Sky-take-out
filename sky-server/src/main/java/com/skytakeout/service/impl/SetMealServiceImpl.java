package com.skytakeout.service.impl;

import com.skytakeout.constant.MessageConstant;
import com.skytakeout.constant.StatusConstant;
import com.skytakeout.context.BaseContext;
import com.skytakeout.dto.SetmealDTO;
import com.skytakeout.dto.SetmealPageQueryDTO;
import com.skytakeout.entity.Category;
import com.skytakeout.entity.Dish;
import com.skytakeout.entity.SetMeal;
import com.skytakeout.entity.SetMealDish;
import com.skytakeout.exception.DeletionNotAllowedException;
import com.skytakeout.repository.CategoryRepository;
import com.skytakeout.repository.DishRepository;
import com.skytakeout.repository.SetMealDishRepository;
import com.skytakeout.repository.SetMealRepository;
import com.skytakeout.result.PageResult;
import com.skytakeout.service.SetmealService;
import com.skytakeout.util.AttributeFillerUtil;
import com.skytakeout.vo.DishItemVO;
import com.skytakeout.vo.SetmealVO;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImpl implements SetmealService {

    @Autowired
    private SetMealRepository setMealRepository;
    @Autowired
    private SetMealDishRepository setMealDishRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private DishRepository dishRepository;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    @Override
    public void save(SetmealDTO setmealDTO) {
        SetMeal setMeal = new SetMeal();
        BeanUtils.copyProperties(setmealDTO, setMeal);
        //保存套餐
        setMealRepository.save(setMeal);

        //关联套餐和菜品
        //获取生成的ID
        Long mealId = setMeal.getId();

        //注入到SetMealDish对象
        List<SetMealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(mealId);
        });

        //通过SetMealDish保存套餐和菜品的关联关系
        setMealDishRepository.saveAll(setmealDishes);
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int page = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();
        String name = setmealPageQueryDTO.getName();

        // 创建分页对象，页码从0开始，所以需要减1
        Pageable pageable = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createTime"));

        Page<SetMeal> found = setMealRepository.findByNameLike(name, pageable);
        List<SetmealVO> setmealVOList = found.stream().map(
                setMeal -> {
                    SetmealVO setmealVO = new SetmealVO();
                    BeanUtils.copyProperties(setMeal, setmealVO);
                    Category category = categoryRepository.findById(setMeal.getCategoryId()).orElse(null);
                    if (category != null) {
                        setmealVO.setCategoryName(category.getName());
                    }
                    return setmealVO;
                }
        ).collect(Collectors.toList());

        return new PageResult(found.getTotalElements(), setmealVOList);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            SetMeal setMeal = setMealRepository.getReferenceById(id);
            //起售中的套餐不能删除
            if (setMeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            } else {
                //删除套餐中的数据
                setMealRepository.deleteById(id);
                //删除setMealDish表的关联数据
                setMealDishRepository.deleteBySetmealId(id);
            }
        }
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        // 1. 根据id查询套餐基本信息
        SetMeal setMeal = setMealRepository.findById(id).orElse(null);
        if (setMeal == null) {
            return null;
        }

        // 2. 将套餐基本信息复制到VO对象
        BeanUtils.copyProperties(setMeal, setmealVO);

        // 3. 根据套餐id查询套餐菜品关系表中的菜品
        List<SetMealDish> setMealDishes = setMealDishRepository.findBySetmealId(id);

        // 4. 将套餐菜品设置到VO对象
        setmealVO.setSetmealDishes(setMealDishes);

        // 5. 查询分类信息并设置分类名称
        Category category = categoryRepository.findById(setMeal.getCategoryId()).orElse(null);
        if (category != null) {
            setmealVO.setCategoryName(category.getName());
        }

        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    public void update(SetmealDTO setmealDTO) {
        SetMeal setmeal = setMealRepository.findById(setmealDTO.getId()).orElseThrow();
        BeanUtils.copyProperties(setmealDTO, setmeal, AttributeFillerUtil.getNullPropertyNames(setmealDTO));

        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setUpdateUser(BaseContext.getCurrentID());

        //1、修改套餐表，执行update
        setMealRepository.save(setmeal);

        //获取套餐id
        Long setmealId = setmealDTO.getId();

        //2、删除套餐和菜品的关联关系，操作 setmeal_dish 表，执行 delete
        setMealDishRepository.deleteBySetmealId(setmealId);
        List<SetMealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //3、重新插入套餐和菜品的关联关系，操作 setmeal_dish 表，执行 insert
        setMealDishRepository.saveAll(setmealDishes);

    }

    @Override
    public void setStatus(Integer status, Long id) {
        // 查询套餐是否存在
        SetMeal setMeal = setMealRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("找不到ID为 " + id + " 的套餐"));

        // 起售套餐时，判断套餐内是否有停售菜品
        if (status == StatusConstant.ENABLE) {
            // 获取套餐中的所有菜品
            List<SetMealDish> setMealDishes = setMealDishRepository.findBySetmealId(id);

            if (setMealDishes != null && !setMealDishes.isEmpty()) {
                // 查询每个菜品的状态
                for (SetMealDish setMealDish : setMealDishes) {
                    // 这里需要添加一个方法来查询菜品状态
                    // 可以通过DishRepository查询
                    Integer dishStatus = getDishStatus(setMealDish.getDishId());

                    if (StatusConstant.DISABLE == dishStatus) {
                        // 如果有菜品未启用，则抛出异常
                        throw new RuntimeException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
        }

        // 更新套餐状态
        setMeal.setStatus(status);
        setMeal.setUpdateUser(BaseContext.getCurrentID());
        setMeal.setUpdateTime(LocalDateTime.now());

        setMealRepository.save(setMeal);
    }

    /**
     * 根据分类id查询套餐
     * @param categoryId 分类id
     * @return 套餐列表
     */
    @Override
    public List<SetMeal> list(Long categoryId) {
        // 创建查询条件
        SetMeal setMeal = new SetMeal();
        setMeal.setCategoryId(categoryId);
        setMeal.setStatus(StatusConstant.ENABLE); // 只查询起售中的套餐

        // 查询满足条件的套餐列表
        List<SetMeal> list = setMealRepository.findByCategoryIdAndStatus(categoryId, StatusConstant.ENABLE);

        return list;
    }

    @Override
    public List<DishItemVO> getDishBySetmealId(Long id) {
        // 查询套餐菜品关系表
        List<SetMealDish> setMealDishes = setMealDishRepository.findBySetmealId(id);

        // 将SetMealDish转换为DishItemVO
        List<DishItemVO> dishItemVOList = setMealDishes.stream().map(setMealDish -> {
            DishItemVO dishItemVO = new DishItemVO();

            // 复制基本属性
            dishItemVO.setName(setMealDish.getName());
            dishItemVO.setCopies(setMealDish.getCopies());
            dishItemVO.setImage(setMealRepository.findById(setMealDish.getSetmealId()).orElseThrow().getImage());
            dishItemVO.setDescription(setMealRepository.findById(setMealDish.getSetmealId()).orElseThrow().getDescription());

            // 可以根据需要设置其他属性

            return dishItemVO;
        }).collect(Collectors.toList());

        return dishItemVOList;
    }

    /**
     * 获取菜品状态
     * @param dishId 菜品id
     * @return 菜品状态
     */
    private Integer getDishStatus(Long dishId) {
        // 这里需要添加DishRepository依赖
        // 假设您有一个dishRepository
        return dishRepository.findById(dishId).map(Dish::getStatus).orElse(StatusConstant.DISABLE);

    }
}
