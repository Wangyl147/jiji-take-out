package org.wangyl.jiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wangyl.jiji.dao.EmployeeMapper;
import org.wangyl.jiji.entity.Employee;
import org.wangyl.jiji.service.EmployeeService;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
