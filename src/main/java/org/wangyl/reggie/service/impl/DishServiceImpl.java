package org.wangyl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wangyl.reggie.dao.DishMapper;
import org.wangyl.reggie.dto.DishDto;
import org.wangyl.reggie.entity.Category;
import org.wangyl.reggie.entity.Dish;
import org.wangyl.reggie.entity.DishFlavor;
import org.wangyl.reggie.service.DishFlavorService;
import org.wangyl.reggie.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

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

}
