package org.wangyl.jiji.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.wangyl.jiji.common.BaseContext;
import org.wangyl.jiji.common.R;
import org.wangyl.jiji.common.ShiroUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录的过滤器
 * 可以使用过滤器，也可以使用拦截器来限制对某些url的访问
 * 过滤器是门口的保安，拦截器是公司里巡逻的保安
 */
@Slf4j
//@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        if(ShiroUtils.getSubject().hasRole("employee")){
            log.info("employee登录，请求地址为{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        if(ShiroUtils.getSubject().hasRole("user")){
            log.info("user登录，请求地址为{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //5、如果未登录则返回未登录结果
        //不需要进行页面跳转，前端会自动根据后端返回的数据完成跳转
        //以输出流方式向客户端页面响应数据
        log.info("未登录，请求地址为{}",requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
