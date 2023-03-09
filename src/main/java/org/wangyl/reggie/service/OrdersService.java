package org.wangyl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wangyl.reggie.entity.Employee;
import org.wangyl.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    //用户下单
    public void submit(Orders orders);
}
