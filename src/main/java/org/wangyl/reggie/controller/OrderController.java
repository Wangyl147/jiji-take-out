package org.wangyl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wangyl.reggie.common.BaseContext;
import org.wangyl.reggie.common.CustomException;
import org.wangyl.reggie.common.R;
import org.wangyl.reggie.dto.OrdersDto;
import org.wangyl.reggie.entity.OrderDetail;
import org.wangyl.reggie.entity.Orders;
import org.wangyl.reggie.service.OrderDetailService;
import org.wangyl.reggie.service.OrdersService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    //用户下单
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        //通过basecontext获取用户ID，通过用户ID查询购物车，所以不传这些数据
        ordersService.submit(orders);
        return R.success("");
    }

    //订单分页查询
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(int page, int pageSize){
        Page<Orders> originPage = new Page<>(page,pageSize);
        Page<OrdersDto> dtoPage = new Page<>(page,pageSize);

        //查询订单基本信息
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // select * from orders where user_id=? order by ? DESC
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(originPage,queryWrapper);

        BeanUtils.copyProperties(originPage,dtoPage,"records");
        List<OrdersDto> ordersDtos = originPage.getRecords().stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            //select * from order_detail where order_id=?
            if (item.getId() == null) {
                throw new CustomException("订单不存在");
            }
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId, item.getId());
            ordersDto.setOrderDetails(orderDetailService.list(lambdaQueryWrapper));
            return ordersDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(ordersDtos);
        return R.success(dtoPage);
    }
}
