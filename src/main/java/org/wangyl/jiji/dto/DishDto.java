package org.wangyl.jiji.dto;


import lombok.Data;
import org.wangyl.jiji.entity.Dish;
import org.wangyl.jiji.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    //口味列表
    private List<DishFlavor> flavors = new ArrayList<>();

    //所属分类名称
    private String categoryName;

    private Integer copies;
}
