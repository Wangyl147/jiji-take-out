package org.wangyl.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wangyl.reggie.dao.DishMapper;
import org.wangyl.reggie.dto.DishDto;
import org.wangyl.reggie.entity.Dish;
import org.wangyl.reggie.entity.DishFlavor;
import org.wangyl.reggie.service.DishFlavorService;
import org.wangyl.reggie.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService ;

    //在新增菜品的同时，还要插入口味，操作2张表
    @Override
    @Transactional // 事务控制
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到dish表
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //stream流循环处理
        flavors = flavors.stream().map((item)->{
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());

        //保存多个菜品口味到dishflavor表
        dishFlavorService.saveBatch(flavors);
    }
}
