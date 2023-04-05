package org.wangyl.jiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wangyl.jiji.dto.SetmealDto;
import org.wangyl.jiji.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //保存套餐和菜品与套餐之间的关联关系
    public void saveWithRelations(SetmealDto setmealDto);

    //删除套餐的同时，删除和菜品与套餐之间的关联关系
    public void deleteWithRelations(List<Long> ids);

    //停售套餐
    public void stop(List<Long> ids);

    //启售套餐
    //需要保证旗下的所有菜品都处于启售状态
    public void start(List<Long> ids);

    //在查询套餐基础信息的同时，还要查询菜品，用于回显
    public SetmealDto getWithDishesById(Long id);

    // 修改套餐的同时修改关联菜品信息
    public void updateWithDishes(SetmealDto setmealDto);
}
