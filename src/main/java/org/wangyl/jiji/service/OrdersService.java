package org.wangyl.jiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wangyl.jiji.entity.Orders;

public interface OrdersService extends IService<Orders> {

    //用户下单
    public void submit(Orders orders);
}
