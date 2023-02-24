package org.wangyl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wangyl.reggie.common.R;
import org.wangyl.reggie.dto.DishDto;
import org.wangyl.reggie.entity.Dish;


public interface DishService extends IService<Dish> {

    //在新增菜品的同时，还要插入口味，操作2张表
    public void saveWithFlavor(DishDto dishDto);

    // 在查询菜品基础信息的同时，还要查询口味
    public DishDto getWithFlavorById(Long id);

    //在更新菜品的同时，还要更新口味，操作2张表
    public void updateWithFlavor(DishDto dishDto);

}
