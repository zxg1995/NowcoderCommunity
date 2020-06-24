package com.nowcoder.community.entity;

import java.util.Date;

/**
 * Created by Paul Z on 2020/6/14
 * 对应数据库中login_ticket表
 * 实现登录凭证实体
 */
public class LoginTicket {

    private int id;
    private int userId;
    private String ticket;   //随机的唯一的标识字符串
    private int status;      //登录状态，0-有效，1-无效
    private Date expired;    //凭证保留的期限

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }
}
