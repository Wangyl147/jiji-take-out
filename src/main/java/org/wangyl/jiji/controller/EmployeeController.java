package org.wangyl.jiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.wangyl.jiji.common.R;
import org.wangyl.jiji.common.SaltUtils;
import org.wangyl.jiji.common.ShiroUtils;
import org.wangyl.jiji.entity.Employee;
import org.wangyl.jiji.service.EmployeeService;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")//root url

// controller响应客户端请求，再返回给页面
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //员工登录
    @PostMapping("/login")// 向/employee/login发起post请求
    // 加入request是因为需要把员工id存入request的session中，如果不存session，后续跳转其他页面就不知道它有没有登录
    // @RequestBody将固定格式的数据(如json)封装为JavaBean对象
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        Subject subject = ShiroUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(employee.getUsername(), employee.getPassword());

        try {
            subject.login(token);
        }catch (UnknownAccountException e){
            return R.error("用户不存在");
        }catch (IncorrectCredentialsException e){
            return R.error("密码错误");
        }catch (LockedAccountException e){
            return R.error("账户被锁定");
        }
        subject.hasRole("employee");

        return R.success(ShiroUtils.getEmployee());


    }

    //员工退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理session中保存的当前登录的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息{}", employee.toString());

        //设置初始密码123456，但是要md5加密
        String salt= SaltUtils.generateSalt();
        employee.setPassword(DigestUtils.md5DigestAsHex((DigestUtils.md5DigestAsHex("123456".getBytes())+salt).getBytes()));

        //这部分属于公共字段，很多表中都有，可以把它们放在某个地方统一处理
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获得当前用户的ID
        Long empId = (Long) request.getSession().getAttribute("employee");

        //这部分属于公共字段，很多表中都有，可以把它们放在某个地方统一处理
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        //数据库insert操作
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    //员工信息分页查询
    //在开始时调用，也会在列表信息变动、搜索员工时调用
    @GetMapping("/page")//向/employee/page发起get请求
    public R<Page<Employee>> page(int page, int pageSize, String name){
        log.info("page={},pagesize={},name={}",page,pageSize,name);

        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件(如果第一个参数为false，则不会执行)
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    //根据我们的id修改员工信息
    //会在修改员工信息时调用，也会在禁用、启用员工时调用
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());

        //这部分属于公共字段，已统一处理
        // Long employeeId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(employeeId);

        //因为JS的精度问题，这里的id和数据库中的不一致
        //可以使用JacksonObjectMapper，完成Java Object到json的转换
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    //根据我们的id查询员工信息
    //会在进入修改界面时调用，用来回显员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
