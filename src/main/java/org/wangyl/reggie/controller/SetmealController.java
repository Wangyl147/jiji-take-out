package org.wangyl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wangyl.reggie.common.R;
import org.wangyl.reggie.dto.SetmealDto;
import org.wangyl.reggie.entity.Category;
import org.wangyl.reggie.entity.Setmeal;
import org.wangyl.reggie.entity.SetmealDish;
import org.wangyl.reggie.service.CategoryService;
import org.wangyl.reggie.service.SetmealDishService;
import org.wangyl.reggie.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

//套餐管理
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    //添加套餐
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithRelations(setmealDto);
        return R.success("套餐添加成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize,String name){
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //条件查询setmeal
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,queryWrapper);

        //构造dto
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> setmealPageRecords = setmealPage.getRecords();
        List<SetmealDto> setmealDtoPageRecords = setmealPageRecords.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();//分类id

            //根据分类id填充分类名称
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoPageRecords);

        return R.success(setmealDtoPage);
    }

    //改变套餐状态


    //删除套餐
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.deleteWithRelations(ids);
        return R.success("套餐删除成功");
    }



    //TODO：完成套餐的启停售和修改功能

    //回显套餐信息


    // 修改套餐
}
