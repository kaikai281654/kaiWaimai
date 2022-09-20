package com.kai.waimai.filter;


import com.alibaba.fastjson.JSON;
import com.kai.waimai.common.BaseContext;
import com.kai.waimai.entity.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//检查用户是否已经完成登陆
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        //获取请求的url
        String requestURI = request.getRequestURI();

        String[] urls=new String[]{
              "/employee/login",
              "/employee/logout",
              "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"

        };
        //判断登陆状态
        boolean check=check(urls,requestURI);
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //已登录放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("employee"));

            Long empid= (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empid);

            long id=Thread.currentThread().getId();
            log.info("线程id是{}",id);

            filterChain.doFilter(request,response);
            return;
        }

        //移动端
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("user"));

            Long userId= (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            long id=Thread.currentThread().getId();
            log.info("线程id是{}",id);

            filterChain.doFilter(request,response);
            return;
        }





        //未登录通过数据流的方式向客户端返回数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    //路径匹配
    public boolean check(String[] urls,String requestURI){
        for (String url:urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
