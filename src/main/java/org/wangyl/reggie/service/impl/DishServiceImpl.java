package org.wangyl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wangyl.reggie.common.CustomException;
import org.wangyl.reggie.dao.DishMapper;
import org.wangyl.reggie.dto.DishDto;
import org.wangyl.reggie.entity.*;
import org.wangyl.reggie.service.DishFlavorService;
import org.wangyl.reggie.service.DishService;
import org.wangyl.reggie.service.SetmealDishService;
import org.wangyl.reggie.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

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

    //在查询菜品的同时，还要查询口味
    @Override
    public DishDto getWithFlavorById(Long id) {

        //获取菜品基本信息
        Dish dish = this.getById(id);
        //拷贝部分数据到DTO
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //设置等值查询条件，根据菜品id从dishflavor表中进行查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    //在更新菜品的同时，还要更新口味，操作2张表
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品基本信息
        this.updateById(dishDto);
        //新的菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        /*
         * 不能直接更新多个菜品口味的name和value，因为dishDto里没有dishflavor的id，而且可能产生新的flavor或删除旧的flavor
         * 最主要地，从性质上讲，对口味的修改本质上已经是对口味的删除和重建了
         * 同样的两条口味记录，由于前端没有发生请求，你无法判断这是同一条记录未被改变，还是删除了之后又重建了一模一样的出来
         * 所以先删除再添加
         */

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //stream流循环处理
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存多个菜品口味到dishflavor表
        dishFlavorService.saveBatch(flavors);


    }

    //停售菜品（不用动绑定的口味）
    //如果绑定了启售的套餐则不能停售
    @Override
    @Transactional
    public void stop(List<Long> ids) {
        //查询菜品的状态，如果绑定了套餐则不能停售
        //select count(*) from setmeal_dish where dish_id in ids
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        int count = setmealDishService.count(lambdaQueryWrapper);
        if(count>0){
            throw new CustomException("有菜品绑定了套餐，无法停售");
        }

        //update dish set status=0 where id in ids and status=1
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId,ids);
        updateWrapper.eq(Dish::getStatus,1);
        updateWrapper.set(Dish::getStatus,0);
        this.update(updateWrapper);

    }

    //启售菜品
    @Override
    public void start(List<Long> ids) {
        //update dish set status=1 where id in ids and status=0
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId,ids);
        updateWrapper.eq(Dish::getStatus,0);
        updateWrapper.set(Dish::getStatus,1);
        this.update(updateWrapper);
    }

    //批量删除，在删除菜品的同时，还要删除对应的口味
    //如果绑定了套餐则不能删除
    @Override
    @Transactional
    public void deleteWithRelations(List<Long> ids) {
        //如果没停售不能删除
        //select count(*) from dish where id in ids and status=1
        LambdaQueryWrapper<Dish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(Dish::getId,ids);
        queryWrapper1.eq(Dish::getStatus,1);
        int count = this.count(queryWrapper1);
        if(count>0){
            throw new CustomException("有菜品未停售，不能删除");
        }
        //如果绑定了套餐不能删除
        //select count(*) from setmeal_dish where dish_id in ids
        LambdaQueryWrapper<SetmealDish> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(SetmealDish::getDishId,ids);
        count = setmealDishService.count(queryWrapper2);
        if(count>0){
            throw new CustomException("有菜品绑定了套餐，不能删除");
        }

        //delete from dish where id in ids
        this.removeByIds(ids);

        //删除口味表的数据
        //delete from dish_flavor where dish_id in ids
        LambdaQueryWrapper<DishFlavor> deleteQueryWrapper = new LambdaQueryWrapper<>();
        deleteQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(deleteQueryWrapper);
    }


}
