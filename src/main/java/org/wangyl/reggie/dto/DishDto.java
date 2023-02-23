package org.wangyl.reggie.dto;


import lombok.Data;
import org.wangyl.reggie.entity.Dish;
import org.wangyl.reggie.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
