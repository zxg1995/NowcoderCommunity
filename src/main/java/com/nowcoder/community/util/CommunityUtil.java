package com.nowcoder.community.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    //向浏览器返回JSON字符串的方法
    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

    //测试
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "ZXG");
        map.put("age", 25);
        System.out.println(getJSONString(0, "OK", map));
    }

}
