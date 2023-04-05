package org.wangyl.jiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wangyl.jiji.dto.DishDto;
import org.wangyl.jiji.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {

    //在新增菜品的同时，还要插入口味，操作2张表
    public void saveWithFlavor(DishDto dishDto);

    // 在查询菜品基础信息的同时，还要查询口味
    public DishDto getWithFlavorById(Long id);

    //在更新菜品的同时，还要更新口味，操作2张表
    public void updateWithFlavor(DishDto dishDto);

    //停售菜品（不用动绑定的口味）,如果绑定了启售的套餐则不能停售
    public void stop(List<Long> ids);

    //启售菜品
    public void start(List<Long> ids);

    //批量删除，在删除菜品的同时，还要删除对应的口味，而且如果绑定了套餐则不能删除
    public void deleteWithRelations(List<Long> ids);
}
