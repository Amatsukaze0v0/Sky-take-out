package com.skytakeout.task;


import com.skytakeout.constant.StatusConstant;
import com.skytakeout.entity.Orders;
import com.skytakeout.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
* 处理订单相关定时任务类
* */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 每分钟检测未支付情况，处理超时订单为取消状态
     * */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        //处理15分钟前【未支付】订单，超过15分钟则认为超时
        List<Orders> ordersList = orderRepository.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders o : ordersList) {
                o.setStatus(Orders.CANCELLED);
                o.setCancelReason("订单超时，自动取消");
                o.setCancelTime(LocalDateTime.now());
            }
            orderRepository.saveAll(ordersList);
        }
    }

    /**
     * 检查是否存在派送中订单，修改为已完成，每天凌晨一点触发一次
     * */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("处理中派送订单，时间戳：{}", LocalDateTime.now());
        //处理前一天仍处于【派送中】订单，当前时间减一小时
        List<Orders> ordersList = orderRepository.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusHours(-1));

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders o : ordersList) {
                o.setStatus(Orders.COMPLETED);
            }
            orderRepository.saveAll(ordersList);
        }

    }
}
