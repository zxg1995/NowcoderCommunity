package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Created by Paul Z on 2020/6/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    //SpringMVC提供管理Thymeleaf模板的类，可以直接注入
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("943488869@qq.com", "Test Mail", "Hello Spring Mail!");
    }

    @Test
    public void testHtmlMail(){
        //设置模板里的变量内容
        Context context = new Context();
        context.setVariable("username", "Paul");

        //调用模板引擎指定需要生成的html文件以及内容
        //返回的就是动态网页的源码（以字符串形式）
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("943488869@qq.com", "Test Mail HTML", content);
    }

}
