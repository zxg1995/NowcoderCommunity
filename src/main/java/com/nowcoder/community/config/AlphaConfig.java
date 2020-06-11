package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * Created by Paul Z on 2020/6/5
 *
 * 自定义一个配置类，可以方便管理第三方Bean对象
 */
@Configuration       //一般配置类使用这个即可
public class AlphaConfig {

    @Bean
    //这个函数作用就是将返回对象装配到Spring容器中，在容器中Bean对象的名字就是该方法名
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

}
