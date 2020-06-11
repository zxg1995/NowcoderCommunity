package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by Paul Z on 2020/6/9
 */

@Mapper       //Mybatis特有的注解用来使容器装配Bean
@Repository
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    //返回的是插入用户的行数
    int insertUser(User user);

    //返回的是修改了几行数据
    int updateStatus(int id, int status);

    //更新头像的路径
    int updateHeader(int id, String headerUrl);

    //更新密码
    int updatePassword(int id, String password);

}
