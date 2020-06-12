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

}
