package com.skytakeout.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dish_flavor")
public class DishFlavor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dishId;

    //口味名称
    private String name;

    //口味数据list
    private String value;
}
