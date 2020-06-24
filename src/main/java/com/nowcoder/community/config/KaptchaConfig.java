package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by Paul Z on 2020/6/14
 */
@Configuration
public class KaptchaConfig {

    @Bean
    //返回的Producer是一个接口类型，是Kaptcha的核心接口
    public Producer kaptchaProducer(){

        //设置Properties对象，实际上就是用来封装Properties文件中的数据的
        //在这里，我们可以直接通过给Properties对象塞值的方式进行设置
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");  //单位是像素
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");  //设置字体大小
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");  //设置字体的颜色（黑色）
        //给定验证码中字符来源的范围
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYAZ");
        properties.setProperty("kaptcha.textproducer.char.length", "4");  //生成验证码的长度
        //设置验证码图片的干扰类型，先设置成无干扰类型
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");


        //DefaultKaptcha是Producer的实现类
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
