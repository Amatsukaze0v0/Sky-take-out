package com.skytakeout.vo;

import com.skytakeout.entity.Dish;
import com.skytakeout.entity.DishFlavor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishVO implements Serializable {

    private Long id;
    //菜品名称
    private String name;
    //菜品分类id
    private Long categoryId;
    //菜品价格
    private BigDecimal price;
    //图片
    private String image;
    //描述信息
    private String description;
    //0 停售 1 起售
    private Integer status;
    //更新时间
    private LocalDateTime updateTime;
    //分类名称
    private String categoryName;
    //菜品关联的口味
    private List<DishFlavor> flavors = new ArrayList<>();

    //private Integer copies;
    /**
     * Constructor that takes a Dish object and category name
     * @param dish The dish object
     * @param categoryName The name of the category
     */
    public DishVO(Dish dish, String categoryName) {
        // Copy properties from Dish to DishVO
        BeanUtils.copyProperties(dish, this);
        // Set the category name
        this.categoryName = categoryName;
    }
}
