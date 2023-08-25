package org.wangyl.jiji.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wangyl.jiji.common.ShiroRealm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ShiroConfig {
    //获取shiro过滤器


    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager){//通过get方法自动注入
        ShiroFilterFactoryBean shiroFilterFactoryBean=new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String,String> interceptMap=new HashMap<>();
        /*
            添加shiro的内置过滤器
            用于添加需要拦截的路径,并设置拦截级别
            拦截级别：，
              anon :无需认证就可以访问
              authc：必须认证了才能访问,
              user： 必须拥有 remeberMe（记住我）功能才能访问
              perms：拥有对某个资源的权限才能访问
              roles： 拥有某个角色才能访问
        */
        // 白名单
        String[] urls = new String[]{
                "/employee/login",//正常登录途径
                "/employee/logout",
                "/backend/**",//静态资源不作限制
                "/front/**",
                "/common/**",
                "/user/sendMsg",//获取验证码
                "/user/login",//登录url须放行
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        Arrays.stream(urls).map(s->interceptMap.put(s,"anon")).collect(Collectors.toList());
        interceptMap.put("/**","authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(interceptMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Autowired ShiroRealm shiroRealm){//通过get方法自动注入
        DefaultWebSecurityManager webSecurityManager=new DefaultWebSecurityManager();
        webSecurityManager.setRealm(shiroRealm);
        return webSecurityManager;
    }

    @Bean
    public ShiroRealm getShiroRealm(){

        return new ShiroRealm();
    }
}
