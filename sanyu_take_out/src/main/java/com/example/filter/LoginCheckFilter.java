package com.example.filter;

import com.alibaba.fastjson.JSON;
import com.example.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查是否登录
 * 主程序加注释部件扫描
 */
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截到请求：{}",request.getRequestURI());
        String requestURI = request.getRequestURI();
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
//        判断路径是否需要处理
        boolean check = check(requestURI, urls);
        System.out.println(requestURI);
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
//        判断是否登录，已登录则放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }
//        未登录,通过输出流方式向页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String requestURI,String[] urls){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
