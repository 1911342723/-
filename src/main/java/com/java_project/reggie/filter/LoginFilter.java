package com.java_project.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.java_project.reggie.common.BaseContext;
import com.java_project.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
* 检查用户是否登录
* */
@WebFilter(filterName = "loginFilter" , urlPatterns = "/*")//第二个参数是声明要拦截哪些路径
@Slf4j
public class LoginFilter implements Filter {
    //路径匹配器，用于来看当前路径是否在数组里，在就不拦截，不在就拦截
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    //重写diFilter方法
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1:获取本次请求url
        String requestURI = request.getRequestURI();
            //把不需要处理的请求放行，如已经登陆了然后切换菜品之类的，或者请求登录就放行
            //这里只需要拦截前端控制器的请求，不是的不需要拦截
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",//用户端的页面，不需要处理拦截
                "common/**",
                "/user/**",//移动端发送短信
                "/user/login",//移动端登录
                "order/**"
        };

        //2:判断本次请求是否需要处理
        boolean check = Check(urls,requestURI);

        //3:如果不需要处理，就放行
        if(check){
            filterChain.doFilter(request,response);
            return;
        }
        //4：如果需要拦截，就判断是否登录
            //获取Seesion对象，查看是否登录,如果登陆了
            if(request.getSession().getAttribute("employee")!= null){
                //放行
                Long emId = (Long)request.getSession().getAttribute("employee");
                //将线程名称改为id，用于公共字段的id动态获取
                BaseContext.setThreadLocal(emId);
                filterChain.doFilter(request,response);
                return;
            }
            //判断移动端用户的登录，并记录到session中
            else if(request.getSession().getAttribute("user")!= null){
                //放行
                Long emId = (Long)request.getSession().getAttribute("user");
                //将线程名称改为id，用于公共字段的id动态获取
                BaseContext.setThreadLocal(emId);
                filterChain.doFilter(request,response);
                return;
            }
            else {
                //否则就跳转到登录页面,结合前端的js看其axios拦截器，失败返回接受什么.返回通用类R的错误形式，它自身状态码就是0，还能传递一个字符串
                response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
                return;
            }

    }

    //检查方法
    public boolean Check(String[] URL,String requestURL){
        for (String url:URL){
            boolean match =  PATH_MATCHER.match(url,requestURL);
            if(match){
                return true;
            }

        }
        return false;
    }
}
