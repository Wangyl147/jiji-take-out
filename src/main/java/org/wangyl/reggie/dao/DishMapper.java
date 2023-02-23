package org.wangyl.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.wangyl.reggie.entity.Dish;


@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
