package org.wangyl.jiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wangyl.jiji.dao.ShoppingCartMapper;
import org.wangyl.jiji.entity.ShoppingCart;
import org.wangyl.jiji.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
