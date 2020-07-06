package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Created by Paul Z on 2020/7/1
 */
//@Component
//@Aspect
public class AlphaAspect {

    //定义切点
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")  //表示service包下所有类下所有的方法都是连接点
    public void pointcut(){

    }

    //总共有五类Advice
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }

    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("AfterThrowing");
    }

    //在连接点方法前后同时织入代码
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("aroundBefore");
        Object obj = joinPoint.proceed();   //调用目标组件的方法
        System.out.println("aroundAfter");
        return obj;
    }

}
