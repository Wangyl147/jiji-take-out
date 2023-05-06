package org.wangyl.jiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wangyl.jiji.common.BaseContext;
import org.wangyl.jiji.common.R;
import org.wangyl.jiji.common.ShiroUtils;
import org.wangyl.jiji.entity.ShoppingCart;
import org.wangyl.jiji.service.ShoppingCartService;

import java.time.LocalDateTime;

import java.util.List;

//购物车管理
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    ShoppingCartService shoppingCartService;

    //新增
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCart.setUserId(ShiroUtils.getEmployeeOrUserId());
        //判断购物车里有没有对应id的商品
        //select * from shopping_cart where user_id=? and dish_id=?/setmeal_id=?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(ShoppingCart::getUserId,ShiroUtils.getEmployeeOrUserId());
        queryWrapper.eq(null!=shoppingCart.getSetmealId(),ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        queryWrapper.eq(null!=shoppingCart.getDishId(),ShoppingCart::getDishId,shoppingCart.getDishId());
        ShoppingCart shoppingCartInDb = shoppingCartService.getOne(queryWrapper);
        //如果存在加一
        if(shoppingCartInDb!=null){
            shoppingCart.setNumber(shoppingCartInDb.getNumber()+1);
            shoppingCart.setId(shoppingCartInDb.getId());
            shoppingCartService.updateById(shoppingCart);
        }else{
            //如果不存在，直接插入
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        return R.success(shoppingCart);
    }

    //显示
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        //Long userId = BaseContext.getCurrentId();
        Long userId =ShiroUtils.getEmployeeOrUserId();
        //select * from ahopping_cart where user_id=?
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        return R.success(shoppingCartService.list(lambdaQueryWrapper));
    }

    //修改
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCart.setUserId(ShiroUtils.getEmployeeOrUserId());

        //判断购物车里有没有对应id的商品
        //select * from shopping_cart where user_id=? and dish_id=?/setmeal_id=?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(ShoppingCart::getUserId,ShiroUtils.getEmployeeOrUserId());
        queryWrapper.eq(null!=shoppingCart.getSetmealId(),ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        queryWrapper.eq(null!=shoppingCart.getDishId(),ShoppingCart::getDishId,shoppingCart.getDishId());
        ShoppingCart shoppingCartInDb = shoppingCartService.getOne(queryWrapper);
        //如果存在且number不为0，则减一
        if(shoppingCartInDb!=null && shoppingCartInDb.getNumber()>0){
            shoppingCart.setId(shoppingCartInDb.getId());
            shoppingCart.setNumber(shoppingCartInDb.getNumber()-1);
            if(shoppingCart.getNumber()==0){
                //减到0了，则删除
                shoppingCartService.removeById(shoppingCart);
            }else{
                shoppingCartService.updateById(shoppingCart);
            }
        }else{
            return R.error("商品未购买");
        }
        return R.success(shoppingCart);
    }

    //删除
    @DeleteMapping("/clean")
    public R<String> clean(){
        //Long userId = BaseContext.getCurrentId();
        Long userId = ShiroUtils.getEmployeeOrUserId();
        //delete from shopping_cart where user_id=?
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("");
    }
}
