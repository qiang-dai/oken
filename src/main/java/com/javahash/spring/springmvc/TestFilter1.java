package com.javahash.spring.springmvc;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by daiqiang on 2017-05-19
 */
public class TestFilter1 extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getHeader("User-Agent").contains("IntelliJ IDEA")) {
            throw new ServletException();
        }
//        String path = request.getServletPath();
//        Long timeStamp = System.currentTimeMillis();
//        if (timeMap.containsKey(path)) {
//            //要大于5秒,否则拒绝
//            Long last = timeMap.get(path);
//            if (timeStamp - last < 5*1000) {
//                throw new ServletException();
//            }
//        }
//        timeMap.put(path, timeStamp);
//        if (path.contains("/uploadFileByHttp")) {
//            //pass
//        } else {
//            BufferedInputStream input = new BufferedInputStream(request.getInputStream());
//            byte[] spx = IOUtils.toByteArray(input);
//            request.setAttribute(ParamNameConstants.COMMON_SPX, spx);
//        }

            //在DispatcherServlet之前执行
            //System.out.println("############TestFilter1 doFilterInternal executed############");
            filterChain.doFilter(request, response);
        //在视图页面返回给客户端之前执行，但是执行顺序在Interceptor之后
        //System.out.println("############TestFilter1 doFilter after############");
//      try {
//          Thread.sleep(10000);
//      } catch (InterruptedException e) {
//          e.printStackTrace();
//      }
    }

}
