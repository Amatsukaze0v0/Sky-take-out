package com.skytakeout.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportVO implements Serializable {

    //日期，以逗号分隔，如：2025-04-06,2025-04-07,2025-04-08
    private String dateList;

    //每日订单数，以逗号分隔，如：260,210,150
    private String orderCountList;

    //每日有效订单数，定义为状态【已完成】的订单
    private String validOrderCountList;

    //订单总数
    private Integer totalOrderCount;

    //有效订单数
    private Integer validOrderCount;

    //订单完成率
    private Double orderCompletionRate;
}
