package com.skytakeout.service.impl;

import com.skytakeout.dto.GoodsSalesDTO;
import com.skytakeout.entity.Orders;
import com.skytakeout.repository.OrderRepository;
import com.skytakeout.repository.UserRepository;
import com.skytakeout.service.ReportService;
import com.skytakeout.vo.OrderReportVO;
import com.skytakeout.vo.SalesTop10ReportVO;
import com.skytakeout.vo.TurnoverReportVO;
import com.skytakeout.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * 查询订单表——当日已完成的订单总值
     * */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();

        //计算dateList, 存放从begin到end（均含）范围内的每日日期
        List<LocalDate> dateList = new ArrayList<>();


        dateList.add(begin);
        //日期计算
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        //查询每日营业额：状态为【已完成】的订单金额合计
        for (LocalDate date : dateList) {
            //定义为一天开始到一天结束
            LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

            //select sum(o.amount) from orders o where o.order_time > ? and order_time < ? and status = 5
            Double turnover = orderRepository.sumByTimeRangeAndStatus(startOfDay, endOfDay, Orders.COMPLETED);
            turnoverList.add(turnover == null ? 0.0 : turnover);
        }

        //拼接日期、营业额字符串
        turnoverReportVO.setDateList(StringUtils.join(dateList, ","));
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ","));
        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        UserReportVO userReportVO = new UserReportVO();

        //计算dateList, 存放从begin到end（均含）范围内的每日日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        //日期计算
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        //总用户数量
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            //定义为一天开始到一天结束
            LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

            //select count(id) from user where create_time > ? and create_time < ?
            //查询新用户数
            Integer newUsers = userRepository.countNewUsersByTimeRange(startOfDay, endOfDay);
            newUserList.add(newUsers == null ? 0 : newUsers);

            //查询截至当天的总用户数
            Integer totalUsers = userRepository.countTotalUsersUntil(endOfDay);
            totalUserList.add(totalUsers == null ? 0 : totalUsers);
        }

        //拼接日期、用户数据字符串
        userReportVO.setDateList(StringUtils.join(dateList, ","));
        userReportVO.setNewUserList(StringUtils.join(newUserList, ","));
        userReportVO.setTotalUserList(StringUtils.join(totalUserList, ","));

        return userReportVO;
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        OrderReportVO orderReportVO = new OrderReportVO();

        //计算dateList, 存放从begin到end（均含）范围内的每日日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        //日期计算
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> dailyAllOrdersList = new ArrayList<>();
        List<Integer> dailyValidOrdersList = new ArrayList<>();

        for (LocalDate date : dateList) {
            //定义为一天开始到一天结束
            LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

            //select count(id) from Order where createTime > ? and createTime < ? and status = ?
            //查询有效订单和所有订单数量，处理null值
            Integer validOrders = orderRepository.countByTimeRangeAndStatus(startOfDay, endOfDay, Orders.COMPLETED);
            Integer allOrders = orderRepository.countByTimeRange(startOfDay, endOfDay);

            dailyValidOrdersList.add(validOrders == null ? 0 : validOrders);
            dailyAllOrdersList.add(allOrders == null ? 0 : allOrders);
        }

        //统计总和数据，处理null值，使用安全的方式
        Integer totalOrdersCount = dailyAllOrdersList.stream().reduce(0, Integer::sum);
        Integer totalValidOrdersCount = dailyValidOrdersList.stream().reduce(0, Integer::sum);
        //计算完成率
        Double completionRate = 0.0;
        if(totalOrdersCount != 0) {
            completionRate = totalValidOrdersCount.doubleValue() / totalOrdersCount * 100;
        }

        //拼接日期、用户数据字符串
        orderReportVO.setDateList(StringUtils.join(dateList, ","));
        orderReportVO.setOrderCountList(StringUtils.join(dailyAllOrdersList, ","));
        orderReportVO.setValidOrderCountList(StringUtils.join(dailyValidOrdersList, ","));
        orderReportVO.setOrderCompletionRate(completionRate);
        orderReportVO.setValidOrderCount(totalValidOrdersCount);
        orderReportVO.setTotalOrderCount(totalOrdersCount);

        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();

        LocalDateTime startTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderRepository.getSalesTop10(startTime, endTime);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        salesTop10ReportVO.setNameList(nameList);
        salesTop10ReportVO.setNumberList(numberList);
        return salesTop10ReportVO;
    }


}
