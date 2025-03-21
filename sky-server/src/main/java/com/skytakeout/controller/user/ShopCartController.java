package com.skytakeout.controller.user;

import com.skytakeout.dto.ShoppingCartDTO;
import com.skytakeout.result.Result;
import com.skytakeout.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Tag(name = "购物车相关接口")
public class ShopCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @Operation(summary = "添加购物车")
    public Result addToCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加到购物车：{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }
}
