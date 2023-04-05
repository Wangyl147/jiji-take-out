package org.wangyl.jiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wangyl.jiji.dao.OrderDetailMapper;
import org.wangyl.jiji.entity.OrderDetail;
import org.wangyl.jiji.service.OrderDetailService;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
