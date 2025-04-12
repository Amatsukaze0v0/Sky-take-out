package com.skytakeout.controller.user;

import com.skytakeout.dto.OrdersPaymentDTO;
import com.skytakeout.dto.OrdersSubmitDTO;
import com.skytakeout.result.Result;
import com.skytakeout.service.OrderService;
import com.skytakeout.vo.OrderPaymentVO;
import com.skytakeout.vo.OrderSubmitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 订单支付
     */
    @PostMapping("/payment")
    @Operation(summary = "订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 支付成功通知
     */
    @PostMapping("/pay-success")
    @Operation(summary = "支付成功通知")
    public Result<String> paySuccess(@RequestParam("outTradeNo") String outTradeNo) {
        log.info("支付成功通知：{}", outTradeNo);
        orderService.paySuccess(outTradeNo);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    @Operation(summary = "客户催单")
    public Result reminder(@PathVariable("id") Long id) {
        log.info("客户催单：{}", id);
        orderService.reminder(id);
        return Result.success();
    }
}
