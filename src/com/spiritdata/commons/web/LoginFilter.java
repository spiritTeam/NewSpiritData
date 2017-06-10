package com.spiritdata.commons.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 系统过滤器，注意此过滤器的级别大于Spring的DispatchServlet。主要是判断是否有权限访问网站内容，可能类似SpringSecurity。
 * 此过滤器要涉及得即支持Web方式，也支持App方式
 * 1-不过滤：a).do；b)RESTFul风格的请求，这两类有框架的其他部分完成，这只是一个定义，具体的体现，要在filter配置中实现，以遵循j2ee标准
 * 2-对于前端处理来说，如果用frame，就要注意frame的风格，但框架，通过格式化标签定义，也会给出一个处理结构
 * </pre>
 * @author wanghui
 */
public class LoginFilter implements Filter {
    private Logger logger=LoggerFactory.getLogger(LoginFilter.class);

    private String ingores;//不过滤的资源
    private String noLogin;
    private String hasNewLogin;
    private String errorPage;//异常错误页面

    @Override
    public void destroy() {        
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}