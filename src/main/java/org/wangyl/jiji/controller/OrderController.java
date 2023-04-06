package org.wangyl.jiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wangyl.jiji.common.BaseContext;
import org.wangyl.jiji.common.CustomException;
import org.wangyl.jiji.common.R;
import org.wangyl.jiji.dto.OrdersDto;
import org.wangyl.jiji.entity.OrderDetail;
import org.wangyl.jiji.entity.Orders;
import org.wangyl.jiji.service.OrderDetailService;
import org.wangyl.jiji.service.OrdersService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;



    //用户下单
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        //通过basecontext获取用户ID，通过用户ID查询购物车，所以不传这些数据
        ordersService.submit(orders);
        return R.success("");
    }

    //订单分页查询
    @GetMapping("/userPage")
    public R<Page<Orders>> page(int page,int pageSize){
        Page<Orders> originPage = new Page<>(page,pageSize);
        //查询订单基本信息
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // select * from orders where user_id=? order by ? DESC
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(originPage,queryWrapper);
        return  R.success(originPage);
    }



    @GetMapping({"/page"})
    public R<Page<Orders>> employeePage(int page, int pageSize){
        Page<Orders> originPage = new Page<>(page,pageSize);
        //查询订单基本信息
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // select * from orders order by ? DESC
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(originPage,queryWrapper);
        return R.success(originPage);



    }
}
