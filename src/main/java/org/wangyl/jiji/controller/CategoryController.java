package org.wangyl.jiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wangyl.jiji.common.R;
import org.wangyl.jiji.entity.Category;
import org.wangyl.jiji.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        log.info("category:{}",category);
        return R.success("新增分类成功");
    }

    //分类信息分页查询
    //在开始时调用，也会在列表信息变动、搜索时调用
    @GetMapping("/page")
    public R<Page<Category>> page(int page,int pageSize){

        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);

        //执行查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    //根据id删除分类
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类，id为{}",ids);
        categoryService.remove(ids);
        return R.success("分类删除成功");
    }

    //根据id修改分类信息
    //会在修改时调用
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    //根据条件查询分类数据
    //不会在进入分类修改界面时调用以用于回显（因为分类修改界面不是一个单独的界面，而是一个对话框）
    //会在添加和修改菜品时调用，用来为菜品提供一个分类选项
    //还会在添加和修改套餐时调用，用来为套餐提供一个分类选项
    //此外，在添加和修改套餐时，这个方法还会被调用，返回各个菜品的分类，为选择套餐菜品做准备
    @GetMapping("/list")
    public R<List<Category>> list(Category category){

        //条件构造器，添加等值查询和排序条件
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        lambdaQueryWrapper.orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return R.success(list);
    }

}
