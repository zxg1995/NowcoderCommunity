package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Paul Z on 2020/6/30
 */
@ControllerAdvice(annotations = Controller.class)    //表示只扫描带有Controller注解的类，不带参数则扫描spring容器所有的Bean
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)  //表示要处理所有的异常
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());
        //详细记录异常栈中的数据
        for (StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

        //判断请求的类型是异步请求还是普通请求，根据请求的不同进行不同的处理
        String xRequestWith = request.getHeader("x-requested-with");
        if (xRequestWith.equals("XMLHttpRequest")){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常!"));
        }
        else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
