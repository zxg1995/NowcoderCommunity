package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * Created by Paul Z on 2020/6/12
 * 封装一些项目常用的工具方法
 */
public class CommunityUtil {

    //生成随机字符的方法
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //采用基于MD5的方法对密码进行加密
    //具体方法是使用原始密码与一个随机字符串salt拼接起来，再使用MD5进行加密
    //防止密码过于简单而被解密
    public static String md5(String key){
        //用commons-lang3包提供的方法进行判空
        if (StringUtils.isBlank(key)){
            return null;
        }
        //返回16进制字符串
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

}
