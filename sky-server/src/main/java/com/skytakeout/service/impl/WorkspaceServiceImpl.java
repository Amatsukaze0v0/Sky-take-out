package com.skytakeout.service.impl;

import com.skytakeout.constant.StatusConstant;
import com.skytakeout.entity.Orders;
import com.skytakeout.repository.DishRepository;
import com.skytakeout.repository.OrderRepository;
import com.skytakeout.repository.SetMealRepository;
import com.skytakeout.repository.UserRepository;
import com.skytakeout.service.WorkspaceService;
import com.skytakeout.vo.BusinessDataVO;
import com.skytakeout.vo.DishOverViewVO;
import com.skytakeout.vo.OrderOverViewVO;
import com.skytakeout.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private SetMealRepository setMealRepository;

    /**
     * 根据时间段统计营业数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        //查询总订单数
        Integer todaysOrderNum = orderRepository.countByTimeRange(begin, end);

        //营业额
        Double turnover = orderRepository.sumByTimeRangeAndStatus(begin, end, Orders.COMPLETED);
        turnover = turnover == null ? 0.0 : turnover;

        //有效订单数
        Integer validOrderNum = orderRepository.countByTimeRangeAndStatus(begin, end, Orders.COMPLETED);

        //客单价 = 营业额 / 有效订单数
        Double unitPrice = 0.0;
        //订单完成率 = 有效订单数 / 总订单数
        Double orderCompletionRate = 0.0;

        if (todaysOrderNum != 0 && validOrderNum != 0) {
            orderCompletionRate = validOrderNum.doubleValue() / todaysOrderNum;
            unitPrice = turnover / validOrderNum;
        }

        Integer newUserNum = userRepository.countNewUsersByTimeRange(begin, end);

        return BusinessDataVO.builder()
                .newUsers(newUserNum)
                .unitPrice(unitPrice)
                .turnover(turnover)
                .orderCompletionRate(orderCompletionRate)
                .validOrderCount(validOrderNum)
                .build();
    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    @Override
    public OrderOverViewVO getOrderOverView() {
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

        //待接单
        Integer awaitingOrders = orderRepository.countByTimeRangeAndStatus(begin, end, Orders.TO_BE_CONFIRMED);
        //待派送
        Integer deliveringOrders = orderRepository.countByTimeRangeAndStatus(begin, end, Orders.CONFIRMED);
        //已完成
        Integer completedOrders = orderRepository.countByTimeRangeAndStatus(begin, end, Orders.COMPLETED);
        //已取消
        Integer cancelledOrders = orderRepository.countByTimeRangeAndStatus(begin, end, Orders.CANCELLED);
        //全部订单
        Integer totalOrders = orderRepository.countByTimeRange(begin, end);

        return OrderOverViewVO.builder()
                .allOrders(totalOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveringOrders)
                .waitingOrders(awaitingOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    @Override
    public DishOverViewVO getDishOverView() {
        Integer sold = dishRepository.countByStatus(StatusConstant.ENABLE);

        Integer discontinued = dishRepository.countByStatus(StatusConstant.DISABLE);

        return DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    @Override
    public SetmealOverViewVO getSetmealOverView() {

        Integer sold = setMealRepository.countByStatus(StatusConstant.ENABLE);
        Integer discontinued = setMealRepository.countByStatus(StatusConstant.DISABLE);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
