package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by Paul Z on 2020/6/12
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //访问注册页面的方法
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    //访问登录页面的方法
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //访问忘记密码页面的方法
    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String getForgetPage(){
        return "/site/forget";
    }

    //实现注册功能的方法
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()){
            model.addAttribute("msg", "注册成功！我们已经向您的邮箱发送了一封激活邮件,请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }
        else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    //访问激活链接页面
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS){
            model.addAttribute("msg", "激活成功！您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        }
        else if (result == ACTIVATION_REPEAT){
            model.addAttribute("msg", "无效操作！该账号已经激活了！");
            model.addAttribute("target", "/index");
        }
        else{
            model.addAttribute("msg", "激活失败！您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    //来获取登录界面中的验证码图片
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    //返回void类型是因为可以通过response对象向浏览器输出
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha", text);

        //将图片输出给浏览器
        response.setContentType("image/png");   //设置数据类型
        try {
            //该流可以不用考虑关闭，因为整个response由Spring进行管理，Spring会帮助进行关闭的
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    //实现登录逻辑
    //path可以相同，但是method必须有区别，Spring才能将两方法区别开
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code,
                        boolean rememberMe, Model model, HttpSession session, HttpServletResponse response){
        //首先要判断验证码是否正确，这个只需要在表现层处理
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/site/login";
        }

        //检查账号与密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED : DEFAULT_EXPIRED;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")){
            //这个ticket要通过cookie返回给浏览器，让浏览器保存
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";   //重定向到首页
        }
        else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    //实现退出逻辑
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";    //默认重定向到方法为GET的/login页面
    }

    //实现发送验证码的方法
    @RequestMapping(path = "/verificationCode", method = RequestMethod.GET)
    @ResponseBody
    public String sendVerificationCode(String email, HttpSession session){
        Map<String, Object> map = userService.sendVerificationCode(email);
        if (map.containsKey("verificationCode")){
            session.setAttribute("verificationCode", map.get("verificationCode"));
            return CommunityUtil.getJSONString(0);
        }
        else {
            return CommunityUtil.getJSONString(1, map.get("emailMsg").toString());
        }
    }

    //实现忘记密码中更改密码的方法
    @RequestMapping(path = "/forget", method = RequestMethod.POST)
    public String forgetPassword(String email, String verificationCode, String newPassword,
                                 Model model, HttpSession session){
        Map<String, Object> map = userService.forgetPassword(email, newPassword);

        //验证码的验证
        String code = (String) session.getAttribute("verificationCode");
        if (StringUtils.isBlank(verificationCode) || StringUtils.isBlank(code) || !code.equals(verificationCode)){
            model.addAttribute("codeMsg", "验证码错误！");
            return "/site/forget";
        }

        if (map.isEmpty()){
            return "redirect:/login";
        }
        else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }
    }
}
