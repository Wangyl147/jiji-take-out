package org.wangyl.jiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wangyl.jiji.common.CustomException;
import org.wangyl.jiji.dao.SetmealMapper;
import org.wangyl.jiji.dto.SetmealDto;
import org.wangyl.jiji.entity.Setmeal;
import org.wangyl.jiji.entity.SetmealDish;
import org.wangyl.jiji.service.DishService;
import org.wangyl.jiji.service.SetmealDishService;
import org.wangyl.jiji.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;

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

    //停售套餐
    @Override
    public void stop(List<Long> ids) {
        //update setmeal set status=0 where id in ids and status=1
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,ids);
        updateWrapper.eq(Setmeal::getStatus,1);
        updateWrapper.set(Setmeal::getStatus,0);
        this.update(updateWrapper);
    }

    //启售套餐
    @Override
    @Transactional
    public void start(List<Long> ids) {
        //update setmeal set status=1 where id in ids and status=0
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,ids);
        updateWrapper.eq(Setmeal::getStatus,0);
        updateWrapper.set(Setmeal::getStatus,1);
        this.update(updateWrapper);
    }

    //在查询套餐基础信息的同时，还要查询菜品，用于回显
    @Override
    public SetmealDto getWithDishesById(Long id) {
        //获取setmeal基础信息
        //select * from setmeal where setmeal.id=id
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        //获取setmealDishes额外信息
        //select * from setmeal_dish where setmeal_id=id
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    //修改套餐的同时修改关联菜品信息
    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto) {
        //修改套餐基本信息
        this.updateById(setmealDto);
        //关联菜品列表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //先删除再添加，因为setmeal表没有属于自己的id
        //先删除setmeal_dish关系表中属于这个套餐的记录
        //delete from setmeal_dish where setmeal_id=(setmealDto.getSetmealId())
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //再添加新的菜品
        //首先标记上套餐的id
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //insert (...) into setmeal_dish
        setmealDishService.saveBatch(setmealDishes);

    }


}
