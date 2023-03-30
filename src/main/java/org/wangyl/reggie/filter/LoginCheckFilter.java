package org.wangyl.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.util.AntPathMatcher;
import org.wangyl.reggie.common.BaseContext;
import org.wangyl.reggie.common.R;

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
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        //log.info("已拦截到请求：{}",requestURI);
        // 白名单
        String[] urls = new String[]{
                "/employee/login",//正常登录途径
                "/employee/logout",
                "/backend/**",//静态资源不作限制
                "/front/**",
                "/common/**",
                "/user/sendMsg",//获取验证码
                "/user/login"//登录url须放行
        };
        //2、判断本次请求是否需要处理
        boolean check = check(urls,requestURI);


        //3、如果不需要处理，直接放行
        if (check){
            //log.info("本次请求无需处理：{}",request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }

        //4-1、判断employee登录状态，如果已登录则放行
        if(request.getSession().getAttribute("employee")!=null){
            Long empId =(Long) request.getSession().getAttribute("employee");

            log.info("员工已登录，ID为：{}",empId);

            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //4-2、判断user登录状态，如果已登录则放行
        if(request.getSession().getAttribute("user")!=null){
            //从session取出userid存入threadLocal供同线程其他方法使用
            Long usrId =(Long) request.getSession().getAttribute("user");

            log.info("用户已登录，ID为：{}",usrId);

            BaseContext.setCurrentId(usrId);

            filterChain.doFilter(request,response);
            return;
        }

        //5、如果未登录则返回未登录结果
        //不需要进行页面跳转，前端会自动根据后端返回的数据完成跳转
        //以输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

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
