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
public class SalesTop10ReportVO implements Serializable {

    //商品名称列表，以逗号分隔，如：菜,饭,水
    private String nameList;

    //销量列表，以逗号分隔，如：100,21,10
    private String numberList;
}
