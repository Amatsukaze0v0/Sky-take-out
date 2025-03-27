package com.skytakeout.controller.user;

import com.skytakeout.constant.StatusConstant;
import com.skytakeout.entity.Dish;
import com.skytakeout.result.Result;
import com.skytakeout.service.DishService;
import com.skytakeout.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/user/dish")
@RestController("userDishController")
@Slf4j
@Tag(name = "用户端菜品接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @Operation(summary = "根据分类ID查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        //Redis缓存查询
        String key = "dish_" + categoryId;
        //若有，直接读取
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (list != null && !list.isEmpty()) {
            return Result.success(list);
        }

        //如果不存在，查询数据库，将查询到的数据放入 redis 中
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        list = dishService.listWithFlavor(categoryId);
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }
}
