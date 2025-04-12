package com.skytakeout.controller.notify;

import com.skytakeout.context.BaseContext;
import com.skytakeout.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


/**
 * 模拟支付控制器
 */
@Controller
@RequestMapping("/mock-payment")
@Slf4j
public class MockPaymentController {

    @Autowired
    private OrderService orderService;

    /**
     * 显示模拟支付页面
     */
    @GetMapping
    public String showPaymentPage(
            @RequestParam("orderId") Long orderId,
            @RequestParam("orderNumber") String orderNumber,
            @RequestParam("outTradeNo") String outTradeNo,
            @RequestParam("amount") BigDecimal amount,
            Model model) {

        model.addAttribute("orderId", orderId);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("outTradeNo", outTradeNo);
        model.addAttribute("amount", amount);

        return "mock-payment";  // 返回模拟支付页面
    }

    /**
     * 处理支付请求
     */
    @PostMapping("/process")
    public String processPayment(
            @RequestParam("outTradeNo") String outTradeNo,
            @RequestParam("paymentMethod") String paymentMethod) {

        try {
            // 设置当前用户ID（在实际应用中，这应该从会话或认证信息中获取）
            Long userId = BaseContext.getCurrentID();
            // 这里为了演示，我们假设用户已登录
            // 在实际应用中，您需要确保用户已登录并获取正确的用户ID

            // 模拟支付处理延迟
            Thread.sleep(1500);

            // 调用支付成功回调
            orderService.paySuccess(outTradeNo);

            return "redirect:/mock-payment/payment-success";  // 重定向到支付成功页面
        } catch (Exception e) {
            log.error("支付处理失败", e);
            return "redirect:/mock-payment/payment-failed";  // 重定向到支付失败页面
        }
    }

    /**
     * 支付成功页面
     */
    @GetMapping("/payment-success")
    public String paymentSuccess() {
        return "payment-success";
    }

    /**
     * 支付失败页面
     */
    @GetMapping("/payment-failed")
    public String paymentFailed() {
        return "payment-failed";
    }
}