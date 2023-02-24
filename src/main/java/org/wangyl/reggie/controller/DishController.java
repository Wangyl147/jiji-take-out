package org.wangyl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.wangyl.reggie.common.R;
import org.wangyl.reggie.dto.DishDto;
import org.wangyl.reggie.entity.Category;
import org.wangyl.reggie.entity.Dish;
import org.wangyl.reggie.service.CategoryService;
import org.wangyl.reggie.service.DishFlavorService;
import org.wangyl.reggie.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

//菜品管理
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){//不能用Dish，因为有多余字段，用另外一个类接收
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    //菜品信息分页查询
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page,int pageSize,String name){
        /*
         * 为什么不扩展service方法，我认为是因为page()函数的返回方式不太一样
         * page()函数返回的是void，但其内部的Page参数却发生了改变，这可能是调用了一些底层的功能
         * 如果在service内实现新的page()方法，就也要再手动调用底层功能，很不方便
         * 所以就直接在controller类中实现功能
         */

        log.info(page+" "+pageSize);
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);

        //构造条件构造器并设置条件
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        //查询菜品基本信息
        dishService.page(pageInfo,lambdaQueryWrapper);

        //拷贝对象到dto
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //处理records
        List<Dish> pageInfoRecords = pageInfo.getRecords();
        List<DishDto> dishDtoList = pageInfoRecords.stream().map((item)->{
            //生成dishdto对象，拷贝dish属性
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据分类id填充分类名称
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);

        //直接返回pageinfo会导致分类名称无法显示
        return R.success(dishDtoPage);
    }

    //根据我们的id查询菜品信息和口味信息
    @GetMapping("/{id}")
    //返回值要和页面对应
    public R<DishDto> getById(@PathVariable Long id){

        DishDto dishdto = dishService.getWithFlavorById(id);
        return R.success(dishdto);
    }

    //根据我们的id修改菜品信息和口味信息
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("菜品修改成功");
    }


}
