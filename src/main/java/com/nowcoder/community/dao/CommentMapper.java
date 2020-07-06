package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Paul Z on 2020/6/28
 */
@Mapper
@Repository
public interface CommentMapper {

    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    List<Comment> selectCommentsByUserAndEntity(int userId, int entityType, int offset, int limit);

    int selectCountByUserAndEntity(int userId, int entityType);

    int insertComment(Comment comment);
}
