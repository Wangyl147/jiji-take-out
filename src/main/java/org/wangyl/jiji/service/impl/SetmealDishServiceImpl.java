package org.wangyl.jiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wangyl.jiji.dao.SetmealDishMapper;

import org.wangyl.jiji.entity.SetmealDish;
import org.wangyl.jiji.service.SetmealDishService;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
