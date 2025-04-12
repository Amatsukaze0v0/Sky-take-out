package com.skytakeout.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsSalesDTO implements Serializable {

    //商品名
    private String name;

    //销量
    private Integer number;
}
