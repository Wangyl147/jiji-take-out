package org.wangyl.jiji.aop;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;
import org.wangyl.jiji.common.CustomException;
import org.wangyl.jiji.common.R;
import org.wangyl.jiji.dto.OrdersDto;
import org.wangyl.jiji.entity.OrderDetail;
import org.wangyl.jiji.entity.Orders;
import org.wangyl.jiji.service.OrderDetailService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Aspect
@Slf4j
public class OrderAdvice {

    @Autowired
    private OrderDetailService orderDetailService;

    @Pointcut(value = "execution(* org.wangyl.jiji.controller.OrderController.*age(..))")
    private void pointCut(){
    }

    @Around(value = "pointCut()")

    public Object advice(ProceedingJoinPoint pjp) throws Throwable {
        int page= (int) pjp.getArgs()[0];
        int pageSize= (int) pjp.getArgs()[1];
        Page<OrdersDto> dtoPage = new Page<>(page,pageSize);
        Page<Orders> originPage = (Page<Orders>) ((R)pjp.proceed()).getData();
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
