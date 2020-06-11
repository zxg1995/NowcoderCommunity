package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Paul Z on 2020/6/10
 */
@Mapper
@Repository
public interface DiscussPostMapper {

    //传入参数userId的作用是方便后面使用该方法查询某一用户的发布过的帖子
    //offset表示开始显示的行号，limit限制最多查询的个数
    //可以使用动态SQL，不使用参数userId以进行首页帖子的显示
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Param注解的作用是给参数起个别名，方便当参数名过长时，后续SQL调用可使用该别名
    //如果后续使用动态SQL语句来拼一个条件（在<if>标签里使用），
    //并且该方法有且只有一个参数，这个时候必须要加@Param注解，不然会报错
    int selectDiscussPostRows(@Param("userId") int userId);

}
