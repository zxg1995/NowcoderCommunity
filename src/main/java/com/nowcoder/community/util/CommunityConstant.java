package com.nowcoder.community.util;

/**
 * Created by Paul Z on 2020/6/12
 * 实现用于存放项目相关常量的接口
 * 使用接口的意义在于方便不同的类实现与使用
 */
public interface CommunityConstant {

    //激活成功
    int ACTIVATION_SUCCESS = 0;

    //重复激活
    int ACTIVATION_REPEAT = 1;

    //激活失败
    int ACTIVATION_FAILURE = 2;

    //默认状态的登录超时时间(不勾“记住我”的时间)
    int DEFAULT_EXPIRED = 3600 * 12;

    //记住状态下的登录超时时间，记录100天
    int REMEMBER_EXPIRED = 3600 * 24 * 100;

    //实体类型：帖子
    int ENTITY_TYPE_POST = 1;

    //实体类型：评论
    int ENTITY_TYPE_COMMENT = 2;

    //实体类型：用户
    int ENTITY_TYPE_USER = 3;
}
