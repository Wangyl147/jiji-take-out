package org.wangyl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wangyl.reggie.dto.SetmealDto;
import org.wangyl.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //保存套餐和菜品与套餐之间的关联关系
    public void saveWithRelations(SetmealDto setmealDto);

    //删除套餐的同时，删除和菜品与套餐之间的关联关系
    public void deleteWithRelations(List<Long> ids);
}
