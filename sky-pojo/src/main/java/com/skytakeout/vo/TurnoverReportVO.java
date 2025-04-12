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
public class TurnoverReportVO implements Serializable {
    //日期，以逗号分隔，如：2025-04-06,2025-04-07,2025-04-08
    private String dateList;

    //营业额，以逗号分隔，如：406.0,152.0,75.0
    private String turnoverList;
}
