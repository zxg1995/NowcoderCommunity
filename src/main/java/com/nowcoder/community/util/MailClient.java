package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by Paul Z on 2020/6/11
 * 用于发送邮件的工具类
 */

//Component注解表示将这个Bean交给Spring容器进行管理，
//同时表示这个是在哪个层次都能用的通用Bean
@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;   //Spring中管理邮件发送的核心接口，同样可以由Spring容器直接注入

    @Value("${spring.mail.username}")
    private String from;   //将发件人的邮箱注入进来

    public void sendMail(String to, String subject, String content){
        try {
            //MimeMessage类用来封装邮件主体
            MimeMessage message = mailSender.createMimeMessage();
            //MimeMessageHelper用来帮助构建message
            MimeMessageHelper helper = new MimeMessageHelper(message);
            //设置邮件的发件人
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            //如果不设置第二个参数true，就表示发送的为普通文本
            //设置第二个参数true，就表示邮件发送的内容支持为html文本
            helper.setText(content, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败：" + e.getMessage());
        }
    }

}
