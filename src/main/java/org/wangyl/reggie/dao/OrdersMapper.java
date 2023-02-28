package org.wangyl.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.wangyl.reggie.entity.AddressBook;
import org.wangyl.reggie.entity.Orders;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
