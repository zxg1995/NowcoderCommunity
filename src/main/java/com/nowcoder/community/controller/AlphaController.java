package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Paul Z on 2020/6/4
 * 该类用于演示一些示例与demo
 */
@Controller
//给这个类提供一个名为“alpha”的访问名
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //http的请求与响应已经被封装成request和response对象了

        //获取请求行数据
        System.out.println(request.getMethod());  //获取请求的方法
        System.out.println(request.getServletPath());   //获取请求路径
        //获取请求头的数据
        Enumeration<String> enumeration = request.getHeaderNames();  //获取的是header的key，
                                                                     // Enumeration是很早以前的迭代器，现在基本不用了
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+" "+value);
        }
        //获取请求体的数据
        System.out.println(request.getParameter("code"));    //获得请求体中的参数

        //response是给浏览器返回响应数据的对象
        //返回响应数据
        response.setContentType("text/html;charset=utf-8");    //设置返回内容的类型，这里是返回一个网页，字符集为utf-8
        //通过response获取输出流
        try(PrintWriter writer = response.getWriter()) {
            writer.write("<h1>牛客网</h1>");   //向浏览器打印一个网页的内容
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //GET请求：用来向服务器获取数据，为默认请求方式

    // /students?current=1&limit=20
    //假如现在请求students数据并进行分页显示，current表示当前第几页，limit表示每页多少条数据
    @RequestMapping(path = "/students", method = RequestMethod.GET)  //method参数可以指定只能使用哪种请求方法访问该页面
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit){
        //默认情况下函数可以取到请求路径中的同名参数
        //若当请求路径中没有参数时，可以参数前面加入注解@RequestParam
        //其中name表示对应请求路径中的参数名，required表示是否强制该参数存在，defaultValue表示默认值
        return "Students: current "+current+" limit "+limit;
    }

    // /student/123
    //假设此时获取id为123的student，此时参数为路径的一部分
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        //此时就需要用另外一个注解@PathVariable
        return "a student's id is " + id;
    }

    //POST请求：应用于浏览器向服务器提交数据
    //GET请求也可以提交数据，但是提交的数据会暴露在路径上，且由于请求路径的长度是有限的，过多数据请求肯定是无法处理的
    //示例：将student.html提交的数据传过来
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        //当参数名与表格input框中名字一致，就可以自动将数据传过来
        return "Success! student's name is "+name+" age is "+age;
    }

    //向浏览器响应动态的html数据
    //假设浏览器向服务器查询一个老师的信息，服务器将老师的信息以html形式响应给浏览器
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    //当不加注解@ResponseBody的时候默认就是返回一个html，此时函数返回值的类型为ModelAndView
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        //向对象里添加数据
        mav.addObject("name", "张三");
        mav.addObject("age", 30);

        //设置模板的名称和路径
        mav.setViewName("/demo/view");    //默认为html文件，故不用加后缀名

        return mav;
    }

    //响应html的另一种方式
    //假设返回一个学校的信息
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){

        //model对象由DispatcherServlet创建，可以在函数中向对象中添加数据
        model.addAttribute("name", "同济大学");
        model.addAttribute("age", 110);

        //直接返回html所在路径
        return "/demo/view";
    }

    //响应JSON数据（一般是在异步请求中）
    //异步请求就是指当前网页不刷新，但是数据已经提交到了服务端与数据库进行了一次查询，并返回了一个局部验证的结果
    //JSON实际上是具有特定格式的字符串，Java对象可以转变成JSON字符串，JSON字符串可以转变为JavaScript对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        //函数返回的是一个java对象，因为通过注解@ResponseBody指定了页面返回字符串
        //故最终返回到页面上的是JSON字符串
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 30);
        emp.put("salary", 10000.00);
        return emp;
    }

    //返回包含多个员工的JSON数据
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){

        List<Map<String, Object>> list = new LinkedList<>();

        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 30);
        emp.put("salary", 10000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", 34);
        emp.put("salary", 15000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", 40);
        emp.put("salary", 30000.00);
        list.add(emp);

        return list;
    }

    //HTTP Cookie示例

    //首先，服务端返回一个cookie给浏览器保存下来，cookie放于response头部
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){

        //创建cookie对象
        //每个cookie对象只能保存一组key，value数据，且只能为字符串数据
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());

        //设置cookie生效的范围
        //即访问哪些网页会使用到这些cookie
        cookie.setPath("/community/alpha");

        //设置cookie的生存时间
        //默认情况下是保存到内存中，关闭浏览器cookie就没有了
        //设置一定时间后，就保存到硬盘里, 比如保存10分钟
        cookie.setMaxAge(60 * 10);

        //发送cookie，即将其添加到response的头部里
        response.addCookie(cookie);

        //返回结果是字符串还是网页没有关系
        return "Set cookie!";
    }

    //再来一个请求，看看request里有没有cookie
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        //@CookieValue注解可以获得key为code的cookie的value字符串，并赋给参数
        System.out.println(code);
        return "get cookie!";
    }

    //Session示例
    //session由spring管理，因此会自动返回一个带cookie的response
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){

        //session保存在服务器端，故可以存不同类型的数据，也可以存许多数据
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");

        return "set session!";
    }

    //演示从session中取值
    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));

        return "get session!";
    }
}
