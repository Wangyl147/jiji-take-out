package org.wangyl.jiji.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.wangyl.jiji.entity.Employee;
import org.wangyl.jiji.entity.User;
import org.wangyl.jiji.service.EmployeeService;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
public class ShiroRealm extends AuthorizingRealm {
    @Autowired
    EmployeeService employeeService;

    //访问授权设置
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("授权开始");
        Object object= principalCollection.getPrimaryPrincipal();
        Set<String> roleList=new HashSet<>();
        Set<String> stringPermission = new HashSet<>();

        if(object instanceof Employee){
            roleList.add("employee");
            if()
        }
        if(object instanceof User){

        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(roleList);
        simpleAuthorizationInfo.setStringPermissions(stringPermission);

        return null;
    }

    //登录验证配置
    //接收用户的输入token，返回实际状态info
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1、根据token的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        String name= ((UsernamePasswordToken) authenticationToken).getUsername();
        queryWrapper.eq(Employee::getUsername, name);//查询条件：等值查询
        Employee emp = employeeService.getOne(queryWrapper);
        //如果用户不存在，抛异常
        if (emp==null)throw new UnknownAccountException();

        //2、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) throw new LockedAccountException();

        //3、返回数据库中查到的密码密文和salt值
        String password= emp.getPassword();


        //4、返回用户、真正的密码、salt和realm
        return new SimpleAuthenticationInfo(emp,password, ByteSource.Util.bytes(name),this.getName());

    }

    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher hashedCredentialsMatcher=new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        super.setCredentialsMatcher(hashedCredentialsMatcher) ;
    }
}
