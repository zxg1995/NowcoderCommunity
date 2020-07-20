package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Paul Z on 2020/7/1
 */
@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    //定义切点
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")  //表示service包下所有类下所有的方法都是连接点
    public void pointcut(){
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        //用户[1.2.3.4]在[xxx(时间)]访问了[com.nowcoder.community.service.xxx()]
        //得到用户的IP地址
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //如果非Controller中的方法访问了service中的方法，就会产生得不到request的异常，因此要判断一下
        if (attributes == null){
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //得到连接点方法所在的类名以及方法名
            String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
            logger.info(String.format("某方法在[%s]访问了[%s]", now, target));
        }
        else {
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getRemoteHost();
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //得到连接点方法所在的类名以及方法名
            String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
            logger.info(String.format("用户[%s]在[%s]访问了[%s]", ip, now, target));
        }
    }
}
