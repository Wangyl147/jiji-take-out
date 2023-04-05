package org.wangyl.jiji.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.wangyl.jiji.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
