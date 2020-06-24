package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * Created by Paul Z on 2020/6/14
 * 在这个接口中采用注解的方式来实现方法对数据库的SQL操作
 */
@Mapper
@Repository
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    //增删改一般都返回int型，表示修改的行数
    int insertLoginTicket(LoginTicket loginTicket);   //在login_ticket表中插入一行数据

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);        //根据凭证字符串进行查询

    //注解中也可以使用动态SQL语句，缺点就是不方便书写与阅读
    @Update({
            "<script> ",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if> ",
            "</script>"
    })
    int updateStatus(String ticket, int status);      //修改相应凭证的状态，可表示是否退出

}
