package org.wangyl.jiji.dto;


import lombok.Data;
import org.wangyl.jiji.entity.Setmeal;
import org.wangyl.jiji.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    //套餐所包含的菜（菜与套餐的关系）
    private List<SetmealDish> setmealDishes;

    //套餐所属分类名称
    private String categoryName;
}
