package org.wangyl.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wangyl.reggie.common.CustomException;
import org.wangyl.reggie.dao.CategoryMapper;
import org.wangyl.reggie.entity.Category;
import org.wangyl.reggie.entity.Dish;
import org.wangyl.reggie.entity.Setmeal;
import org.wangyl.reggie.service.CategoryService;
import org.wangyl.reggie.service.DishService;
import org.wangyl.reggie.service.SetmealService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    //根据id删除分类，删除之前进行判断
    @Override
    public void remove(Long id) {

        //添加查询条件，按分类id进行查询
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联菜品，如果关联抛出业务异常
        if (count1>0){
            //关联菜品，抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //添加查询条件，按分类id进行查询
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        //查询当前分类是否关联套餐，如果关联抛出业务异常
        if (count2>0){
            //关联套餐，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        //正常删除
        super.removeById(id);

    }
}
