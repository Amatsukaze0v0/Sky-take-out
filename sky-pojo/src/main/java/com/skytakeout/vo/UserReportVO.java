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
public class UserReportVO implements Serializable {
    //日期，以逗号分隔，如：2025-04-06,2025-04-07,2025-04-08
    private String dateList;
    //用户总量，以逗号分割，如：200,210,300
    private String totalUserList;
    //新增用户，以逗号分隔，如：10,50,20
    private String newUserList;

}
