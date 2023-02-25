package org.wangyl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wangyl.reggie.common.CustomException;
import org.wangyl.reggie.dao.SetmealMapper;
import org.wangyl.reggie.dto.SetmealDto;
import org.wangyl.reggie.entity.Setmeal;
import org.wangyl.reggie.entity.SetmealDish;
import org.wangyl.reggie.service.SetmealDishService;
import org.wangyl.reggie.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    //保存套餐的同时，保存和菜品与套餐之间的关联关系
    @Override
    @Transactional
    public void saveWithRelations(SetmealDto setmealDto) {

        //保存基本信息
        this.save(setmealDto);

        //保存关联信息
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }

    //删除套餐的同时，删除和菜品与套餐之间的关联关系
    @Override
    @Transactional
    public void deleteWithRelations(List<Long> ids) {
        //查询套餐状态，确定是否删除
        //select count(*) from setmeal where id in ids and status=1;
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("有套餐未停售，无法删除");
        }

        //如果可以删除，先删除套餐表的数据
        this.removeByIds(ids);

        //然后删除关系表setmeal_dish的数据
        //delete from setmeal where setmeal_id in ids
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);

    }
}
