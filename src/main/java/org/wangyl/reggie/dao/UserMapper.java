package org.wangyl.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.wangyl.reggie.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
