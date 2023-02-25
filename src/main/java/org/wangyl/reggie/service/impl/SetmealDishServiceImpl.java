package org.wangyl.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wangyl.reggie.dao.SetmealDishMapper;

import org.wangyl.reggie.entity.SetmealDish;
import org.wangyl.reggie.service.SetmealDishService;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
