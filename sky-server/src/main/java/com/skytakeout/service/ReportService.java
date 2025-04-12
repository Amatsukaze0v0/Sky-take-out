package com.skytakeout.service;

import com.skytakeout.vo.*;

import java.time.LocalDate;

public interface ReportService {

    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间内销量排名前十
     * */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
