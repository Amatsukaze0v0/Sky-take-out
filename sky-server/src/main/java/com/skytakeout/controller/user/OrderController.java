package com.skytakeout.controller.user;

import com.skytakeout.dto.OrdersSubmitDTO;
import com.skytakeout.result.Result;
import com.skytakeout.service.OrderService;
import com.skytakeout.vo.OrderSubmitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user/order")
@RestController("userOrderController")
@Slf4j
@Tag(name = "用户端订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @Operation(summary = "新建提交订单")
    public Result<OrderSubmitVO> submit(OrdersSubmitDTO ordersSubmitDTO) {
        log.info("提交订单：{}", ordersSubmitDTO);
        OrderSubmitVO submit = orderService.submit(ordersSubmitDTO);
        return Result.success(submit);
    }

}
