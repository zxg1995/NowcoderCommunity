package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Paul Z on 2020/6/11
 * 了解日志级别的设置，并进行一些测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void testLogger(){
        System.out.println(logger.getName());   //看一下这个logger对象的名字,实际上就是getLogger时指定类的名字

        logger.debug("debug log");   //记录debug级别的日志
        logger.info("info log");     //记录普通级别的日志
        logger.warn("warn log");
        logger.error("error log");
    }

}
