package org.wangyl.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wangyl.reggie.dao.OrdersMapper;
import org.wangyl.reggie.entity.Orders;
import org.wangyl.reggie.service.OrdersService;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

}
