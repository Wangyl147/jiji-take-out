package org.wangyl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.wangyl.reggie.common.R;
import org.wangyl.reggie.dto.DishDto;
import org.wangyl.reggie.entity.Category;
import org.wangyl.reggie.entity.Dish;
import org.wangyl.reggie.entity.DishFlavor;
import org.wangyl.reggie.service.CategoryService;
import org.wangyl.reggie.service.DishFlavorService;
import org.wangyl.reggie.service.DishService;

import java.util.List;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private DishFlavorService dishFlavorService;

    //每次查询菜品都必须大量查询数据库，在高并发状态下会影响性能。
    //所以需要用redis缓存需要频繁查询的菜品
    //不但要改造list方法，也要改造update和save方法，删除原来的缓存，防止脏数据的出现
    //说到底就是保证一致性
    @Autowired
    private RedisTemplate<Object ,Object> redisTemplate;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){//不能用Dish，因为有多余字段，用另外一个类接收
        log.info(dishDto.toString());
        //清空redis中key对应的内容
        String key = "dish_" + dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    //菜品信息分页查询
    //在开始时调用，也会在列表信息变动、搜索时调用
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
    //会在进入修改界面时调用，用来回显菜品信息
    @GetMapping("/{id}")
    //返回值要和页面对应
    public R<DishDto> getById(@PathVariable Long id){

        DishDto dishdto = dishService.getWithFlavorById(id);
        return R.success(dishdto);
    }

    //根据我们的id修改菜品信息和口味信息
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        //清空redis中key对应的内容
        String key = "dish_" + dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        dishService.updateWithFlavor(dishDto);
        return R.success("菜品修改成功");
    }

    //根据条件查询菜品数据
    //在新建、添加套餐中会被调用，根据分类id查菜品
    //也会在用户端被调用，所以还需要传一个口味信息
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){//使用dish类型，泛用性更强

        //动态构造key，同时显示分类id和状态
        String key ="dish_" + dish.getCategoryId()+"_"+dish.getStatus();
        List<DishDto> dishes = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果存在，无需查询数据库
        if(dishes!=null){
            return R.success(dishes);
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //过滤，只保留启售菜品
        //select * from dish where category_id=?/name=?
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.like(null!=dish.getName(),Dish::getName, dish.getName());
        queryWrapper.orderByAsc(Dish::getSort);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        //补全口味信息
        List<DishDto> dtoList = list.stream().map((item) -> dishService.getWithFlavorById(item.getId())).collect(Collectors.toList());

        //如果不存在，则存入redis，有效期60分钟
        redisTemplate.opsForValue().set(key,dtoList,60, TimeUnit.MINUTES);

        return R.success(dtoList);
    }

    //TODO:完成菜品的启停售和删除功能

    //根据id改变启停售状态
    @PostMapping("/status/{newStatus}")
    public R<String> changeStatus(@PathVariable int newStatus,@RequestParam List<Long> ids){
        //update dish set status=0/1 where id in ids and status=1/0
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dish::getStatus,newStatus==0?1:0);
        updateWrapper.in(Dish::getId,ids);
        updateWrapper.set(Dish::getStatus,newStatus);
        dishService.update(updateWrapper);
        return R.success("");
    }


    //既能单删，又能批量删除
    //删除之前，关联的套餐也要删除
    @DeleteMapping
    public R<String> batchDelete(@RequestParam List<Long> ids){
        dishService.deleteWithRelations(ids);
        return R.success("菜品已删除");
    }

}
