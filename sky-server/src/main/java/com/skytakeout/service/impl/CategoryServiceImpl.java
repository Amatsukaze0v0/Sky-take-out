package com.skytakeout.service.impl;

import com.skytakeout.constant.StatusConstant;
import com.skytakeout.context.BaseContext;
import com.skytakeout.dto.CategoryDTO;
import com.skytakeout.dto.CategoryPageQueryDTO;
import com.skytakeout.entity.Category;
import com.skytakeout.repository.CategoryRepository;
import com.skytakeout.result.PageResult;
import com.skytakeout.service.CategoryService;
import com.skytakeout.util.AttributeFillerUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    /**
     * 新增分类
     * */
    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        //其他Attribute，status默认为Disable
        category.setStatus(StatusConstant.DISABLE);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        //创建+修改人
        category.setCreateUser(BaseContext.getCurrentID());
        category.setUpdateUser(BaseContext.getCurrentID());

        categoryRepository.save(category);
    }
    /**
     * 分页查询
     * */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        int page = categoryPageQueryDTO.getPage();
        int pageSize = categoryPageQueryDTO.getPageSize();
        String name = categoryPageQueryDTO.getName();
        Integer type = categoryPageQueryDTO.getType();

        // 创建分页对象，页码从0开始，所以需要减1
        Pageable pageable = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Category> categoryPage = categoryRepository.findByNameLike(name, pageable);
        List<Category> categoryList = categoryPage.getContent();

        long total = categoryPage.getTotalElements();
        return new PageResult(total, categoryList);
    }
    /**
     * 根据ID删除分类
     * */
    @Override
    public void delete(Long id) {
        //TODO： 菜品添加后，需要保证catagory内没有Dish才允许删除。
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("未找到分类"));
        categoryRepository.delete(category);
    }
    /**
     * 更新分类, 检测不到修改人则以1代替
     * */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category, AttributeFillerUtil.getNullPropertyNames(categoryDTO));
        //设置修改人和时间
        category.setUpdateTime(LocalDateTime.now());
        Long currentId = BaseContext.getCurrentID();
        if (currentId != null) {
            category.setUpdateUser(currentId);
        } else {
            category.setUpdateUser(1L);
        }

        categoryRepository.save(category);
    }
    /**
     * 修改分类启用/禁用
     * */
    @Override
    public void setStatus(Integer status, Long id) {
        categoryRepository.setStatus(status, id);
    }
    /**
     * 按分类Type返回查询的分类结果
     * */
    @Override
    public List<Category> list(Integer type) {
        if (type != null) {
            return categoryRepository.findByType(type);
        } else {
            return categoryRepository.findAll();
        }
    }
}
