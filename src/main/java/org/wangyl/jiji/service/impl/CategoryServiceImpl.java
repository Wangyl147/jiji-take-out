package org.wangyl.jiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wangyl.jiji.common.CustomException;
import org.wangyl.jiji.dao.CategoryMapper;
import org.wangyl.jiji.entity.Category;
import org.wangyl.jiji.entity.Dish;
import org.wangyl.jiji.entity.Setmeal;
import org.wangyl.jiji.service.CategoryService;
import org.wangyl.jiji.service.DishService;
import org.wangyl.jiji.service.SetmealService;

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
