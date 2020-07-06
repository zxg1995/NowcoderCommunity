package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * Created by Paul Z on 2020/6/5
 */
@Service
//@Scope("prototype")        //"prototype"的意思是每次访问容器中的Bean都会创建一个新的实例，默认参数为"singleton",表示单例
//大多数情况下，使用的是单例方式
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

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

    //假设将新增一个用户并发一个报道贴这两个业务整合成一个事务
    /***
     * 实验Spring的声明式事务
     *
     * isolation参数表示可以指定事务的隔离级别，propagation参数指定事务的传播机制。
     * 事务传播机制：解决了当业务方法A调用了业务方法B，这两个方法都是事务的情况下，B的事物创建问题。
     * REQUIRED：对于B，支持当前事务（即A），若当前事务不存在，则创建新事务。
     * REQUIRES_NEW：无论当前事务存不存在，B都会创建一个新事务，并且暂停当前事务（外部事务）。
     * NESTED：如果当前事务存在(A),则B嵌套在该事务中执行(但是相当于一个独立的事务，有独立的提交和回滚),否则就会REQUIRED一样。
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增的用户发一个帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道!");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("asd");   //添加一个错误，实验事务会不会回滚

        return "OK!";
    }

    // 实验编程式事务
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);  //指定隔离级别
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED); //指定传播机制

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                //新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/91t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增的用户发一个帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("Hello");
                post.setContent("新人报道!");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("asd");   //添加一个错误，实验事务会不会回滚

                return "OK!";
            }
        });  //这是一个匿名的回调方法，在这个方法中我们实现相应的事务逻辑
    }
}
