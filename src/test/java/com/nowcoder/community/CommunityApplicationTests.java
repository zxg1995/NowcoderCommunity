package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)      //设置CommunityApplication作为配置类
public class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;      //使用成员变量记录一下Spring容器

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		//ApplicationContext实际上是Spring容器的接口
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		//测试一下对Spring容器的引用，打印一下看看有没有对象
		System.out.println(applicationContext);

		//测试一下容器是否扫描到了Bean对象，并根据接口类型返回相关的对象
		//若容器中相同类型的对象有多个，返回优先级更高的
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());

		//通过Bean对象的名字来访问特定的Bean
		alphaDao = applicationContext.getBean("alphaHibernate", AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Test
	public void testBeanManager(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		//一般情况下，容器中的Bean对象只实例化一次，即在容器中是单例存在的
		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired     //采用依赖注入的方式将容器中的Bean对象注入到声明的属性中
	@Qualifier("alphaHibernate")  //将容器中特定名称的Bean注入到属性中
	private AlphaDao alphaDao;
	@Autowired
	private AlphaService alphaService;
	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
	}
}
