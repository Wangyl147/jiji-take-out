package org.wangyl.jiji.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.wangyl.jiji.entity.Employee;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
