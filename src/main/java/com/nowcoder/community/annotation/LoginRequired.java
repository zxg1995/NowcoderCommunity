package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Paul Z on 2020/6/24
 */
@Target(ElementType.METHOD)  //表明注解用在方法之上
@Retention(RetentionPolicy.RUNTIME)  //表明程序运行时注解有效
public @interface LoginRequired {
    //该注解里什么都不用写
    //该注解的作用只是给方法上打上标记，方便拦截器进行拦截
}
