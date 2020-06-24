package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Paul Z on 2020/6/10
 */
@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;    //注入域名

    @Value("${server.servlet.context-path}")
    private String contextPath;   //注入项目名

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    //进行注册的方法，返回值为一个集合，参数为一个user对象
    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        //进行空值处理
        if (user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        //验证账号存在否
        User u = userMapper.selectByName(user.getUsername());
        if (u != null){
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }

        //验证邮箱存在否
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null){
            map.put("emailMsg", "该邮箱已存在！");
            return map;
        }

        //注册用户
        //给用户生成一个salt
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //设置用户密码的时候是加密后的密码
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);  //刚注册都是普通用户
        user.setStatus(0);  //刚注册需要激活
        user.setActivationCode(CommunityUtil.generateUUID());  //随机生成用户的激活码
        //给用户生成随机的头像，头像使用牛客网库中的头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        //将注册用户信息添加到数据库
        userMapper.insertUser(user);

        //需要给用户发送一封激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //激活链接的格式如下
        // http://localhost:8080/community/activation/用户id/用户激活码
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "社区账号激活", content);

        return map;
    }

    //用户进行激活的方法,返回的就是激活的状态
    public int activation(int useId, String code){
        User user = userMapper.selectById(useId);
        //如果用户的status已经为1，表示已经激活过了，故返回ACTIVATION_REPEAT状态
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }
        else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(useId, 1);
            return ACTIVATION_SUCCESS;
        }
        else {
            return ACTIVATION_FAILURE;
        }
    }

    //实现一个用户登录的方法，参数expiredSeconds表示登录状态保存的时间（秒数）
    public Map<String, Object> login(String username, String password, long expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        //验证输入值的合法性
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        //验证该账号是否激活
        if (user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }
        //验证密码是否正确
        password = CommunityUtil.md5(password+user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg", "输入的密码不正确！");
            return map;
        }

        //到这一步了，表示登录成功了
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        //生成的登录凭证ticket要返回给浏览器，让浏览器下次访问时携带，才能记住登录状态
        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    //实现用户退出方法
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket, 1);
    }

    //实现查询凭证的方法
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    //实现发送验证码方法
    public Map<String, Object> sendVerificationCode(String email){
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if (StringUtils.isBlank(email)){
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        //查询邮箱对应的用户是否存在
        User user = userMapper.selectByEmail(email);
        if (user == null){
            map.put("emailMsg", "该邮箱对应账号不存在！");
            return map;
        }
        //验证该邮箱对应账号是否激活
        if (user.getStatus() == 0){
            map.put("emailMsg", "该邮箱对应账号未激活！");
            return map;
        }

        //生成验证码
        String verificationCode = CommunityUtil.generateUUID().substring(0,8);
        map.put("verificationCode", verificationCode);

        //给用户发验证码邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        context.setVariable("verificationCode", verificationCode);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(user.getEmail(), "账号密码找回", content);

        return map;
    }

    //实现忘记密码的方法
    public Map<String, Object> forgetPassword(String email, String password){
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(email)){
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        //验证合法性
        User user = userMapper.selectByEmail(email);
        if (user == null){
            map.put("emailMsg", "该邮箱对应账号不存在！");
            return map;
        }
        if (user.getStatus() == 0){
            map.put("emailMsg", "该邮箱对应账号未激活！");
            return map;
        }

        String newPassword = CommunityUtil.md5(password+user.getSalt());
        userMapper.updatePassword(user.getId(), newPassword);

        return map;
    }

    //更新用户头像所在路径
    public int updateHeader(int userId, String heardUrl){
        return userMapper.updateHeader(userId, heardUrl);
    }

    //更新用户的密码
    public int updatePassword(int userId, String newPassword){
        return userMapper.updatePassword(userId, newPassword);
    }
}
