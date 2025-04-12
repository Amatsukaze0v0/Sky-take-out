package com.skytakeout.controller.admin;


import com.skytakeout.result.Result;
import com.skytakeout.service.ReportService;
import com.skytakeout.vo.OrderReportVO;
import com.skytakeout.vo.SalesTop10ReportVO;
import com.skytakeout.vo.TurnoverReportVO;
import com.skytakeout.vo.UserReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 数据统计相关接口
 * */
@RestController
@RequestMapping("/admin/report")
@Tag(name = "营业额统计接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @Operation(summary = "获取营业额统计数据")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("正在获取从 {} 到 {} 的营业额数据。", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }

    @GetMapping("/userStatistics")
    @Operation(summary = "获取用户量统计数据")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("正在获取从 {} 到 {} 的用户量数据。", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    @GetMapping("/ordersStatistics")
    @Operation(summary = "统计订单数据")
    public Result<OrderReportVO> orderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("正在获取从 {} 到 {} 的订单数据。", begin, end);
        return Result.success(reportService.getOrdersStatistics(begin, end));
    }

    @GetMapping("/top10")
    @Operation(summary = "统计销量排名")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("正在获取从 {} 到 {} 的销量排名数据。", begin, end);
        return null;
    }
}
