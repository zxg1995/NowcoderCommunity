package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by Paul Z on 2020/6/5
 */
@Service
//@Scope("prototype")        //"prototype"的意思是每次访问容器中的Bean都会创建一个新的实例，默认参数为"singleton",表示单例
//大多数情况下，使用的是单例方式
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    @PostConstruct           //让容器在构造器之后自动的调用该方法
    public void init(){
        //自定义初始化方法，用于初始化某些数据
        System.out.println("初始化AlphaService");
    }

    @PreDestroy             //让容器在对象销毁之前调用它
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
        //service通过依赖注入调用alphaDao的方法
        return alphaDao.select();
    }
}
