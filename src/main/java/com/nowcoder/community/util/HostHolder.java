package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * Created by Paul Z on 2020/6/16
 * 该类用于持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {

    // ThreadLocal实现了线程隔离的user集合
    // session对象也是线程隔离的，保证每个浏览器的访问线程不会访问到别的用户信息
    // ThreadLocal底层原理是以线程为key，对象value存到map中
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    // 清理持有的用户信息
    public void clear(){
        // 根据当前线程进行清理
        users.remove();
    }

}
