package org.wangyl.jiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wangyl.jiji.dao.UserMapper;
import org.wangyl.jiji.entity.User;
import org.wangyl.jiji.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
