package org.wangyl.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wangyl.reggie.dao.ShoppingCartMapper;
import org.wangyl.reggie.entity.ShoppingCart;
import org.wangyl.reggie.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
