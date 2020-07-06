package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.*;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul Z on 2020/6/17
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired   //添加自定义注解
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    //实现上传用户头像的方法
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){

        //MultipartFile是SpringMVC提供的一个用于上传文件的接口
        //因此上传文件逻辑最好在表现层进行完成
        if (headerImage == null){
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }

        //得到用户上传图片的原始文件名
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }

        //生成随机文件名，保证每个用户上传的文件名不重复
        filename = CommunityUtil.generateUUID() + suffix;
        // 确定文件的存放路径
        File dest = new File(uploadPath+"/"+filename);
        try {
            //将headerImage对象存储到对应路径下的文件中
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }

        // 更新当前用户头像的路径（web访问路径）
        // 更新成http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @LoginRequired
    @RequestMapping(path = "/reset", method = RequestMethod.POST)
    public String resetPassword(String password, String newPassword, Model model){
        if (StringUtils.isBlank(password)){
            model.addAttribute("passwordMsg", "原密码输入不能为空！");
            return "/site/setting";
        }
        if (StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg", "新密码输入不能为空！");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        password = CommunityUtil.md5(password + user.getSalt());
        if (password != null && password.equals(user.getPassword())){
            newPassword = CommunityUtil.md5(newPassword + user.getSalt());
            userService.updatePassword(user.getId(), newPassword);
            return "redirect:/logout";
        }
        else {
            model.addAttribute("passwordMsg", "原密码输入不正确！");
            return "/site/setting";
        }
    }

    //实现访问用户头像的方法
    //注意：这个方法返回的是二进制数据（图像数据），因此需要调用HttpResponse，用流的方式传输给浏览器
    //因此不用返回字符串和网页
    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
        // 服务存放文件的路径
        filename = uploadPath + "/" + filename;
        // 文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try(
                FileInputStream fis = new FileInputStream(filename)
                ) {
            OutputStream os = response.getOutputStream();  //因为os对象是response返回的，故由Spring管理其关闭
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer, 0, b);  //输出流将buffer内容进行输出
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }

    //访问个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }

        model.addAttribute("user", user);

        //用户被点赞的数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //用户关注的数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        //用户的粉丝的数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        //当前登录用户对主页用户是否已关注
        //只有登录后才能显示已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

    //访问用户发布过的帖子
    @RequestMapping(path = "/post/{userId}", method = RequestMethod.GET)
    public String getUserPostPage(@PathVariable("userId") int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        int postCount = discussPostService.findDiscussPostRows(userId);
        model.addAttribute("postCount", postCount);

        page.setLimit(10);
        page.setPath("/user/post/" + userId);
        page.setRows(postCount);

        List<DiscussPost> list = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null){
            for (DiscussPost post : list){
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);

        return "/site/my-post";
    }

    //访问用户发布的对于帖子的所有评论
    @RequestMapping(path = "/comment/{userId}", method = RequestMethod.GET)
    public String getUserCommentPage(@PathVariable("userId") int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        int commentCount = commentService.findCommentCountByUserAndEntity(userId, ENTITY_TYPE_POST);
        model.addAttribute("commentCount", commentCount);

        page.setLimit(10);
        page.setPath("/user/comment/" + userId);
        page.setRows(commentCount);

        List<Comment> list = commentService.findCommentsByUserAndEntity
                (userId, ENTITY_TYPE_POST, page.getOffset(), page.getLimit());
        List<Map<String, Object>> comments = new ArrayList<>();
        if (list != null){
            for (Comment comment : list){
                Map<String, Object> map = new HashMap<>();
                map.put("comment", comment);
                DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
                map.put("post", post);
                comments.add(map);
            }
        }
        model.addAttribute("comments", comments);

        return "/site/my-reply";
    }

}
