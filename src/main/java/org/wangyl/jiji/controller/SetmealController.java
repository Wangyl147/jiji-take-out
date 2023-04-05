package org.wangyl.jiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.wangyl.jiji.common.R;
import org.wangyl.jiji.dto.DishDto;
import org.wangyl.jiji.dto.SetmealDto;
import org.wangyl.jiji.entity.Category;
import org.wangyl.jiji.entity.Dish;
import org.wangyl.jiji.entity.Setmeal;
import org.wangyl.jiji.entity.SetmealDish;
import org.wangyl.jiji.service.CategoryService;
import org.wangyl.jiji.service.DishService;
import org.wangyl.jiji.service.SetmealDishService;
import org.wangyl.jiji.service.SetmealService;

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

    @Autowired
    private DishService dishService;

    //添加套餐
    @PostMapping
    @CacheEvict(value = "setmeal_cache",allEntries = true)//删除所有缓存的套餐
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

    //删除套餐
    @DeleteMapping
    @CacheEvict(value = "setmeal_cache",allEntries = true)//删除所有缓存的套餐
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.deleteWithRelations(ids);
        return R.success("套餐删除成功");
    }

    //根据条件查询套餐数据
    //会在用户端被调用
    @GetMapping("/list")
    @Cacheable(value = "setmeal_cache",key="#setmeal.categoryId+'_'+#setmeal.status")
    //为了让返回值能够存入缓存，需要将R视为serializable的
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    //根据id改变启停售状态
    @PostMapping("/status/{newStatus}")
    public R<String> changeStatus(@PathVariable int newStatus,@RequestParam List<Long> ids) {
        //update setmeal set status=0/1 where id in ids and status=1/0
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Setmeal::getStatus, newStatus == 0 ? 1 : 0);
        updateWrapper.in(Setmeal::getId, ids);
        updateWrapper.set(Setmeal::getStatus, newStatus);
        setmealService.update(updateWrapper);
        return R.success("");
    }


    //根据我们的id查询套餐信息
    //会在进入修改界面时调用，用来回显菜品信息
    @GetMapping("/{id}")
    //返回值要和页面对应
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getWithDishesById(id);
        return R.success(setmealDto);
    }

    // 修改套餐
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDishes(setmealDto);
        return R.success("");
    }

    //显示菜品详情
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> getDishes(@PathVariable Long id){
        //select * from setmeal_dish where setmeal_id=?
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

        List<DishDto> dishDtoList = setmealDishes.stream().map((item) -> {
            //select * from dish where id = ?
            DishDto dishDto=new DishDto();
            Dish dish = dishService.getById(item.getDishId());
            BeanUtils.copyProperties(dish,dishDto);
            dishDto.setCopies(item.getCopies());
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }

}
