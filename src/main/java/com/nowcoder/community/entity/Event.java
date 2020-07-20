package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul Z on 2020/7/7
 */
public class Event {

    private String topic; //kafka中消息的主题，也表示事件的类型

    private int userId;  //触发事件的用户

    private int entityType; //事件作用对象的类型

    private int entityId;  //事件作用对象的Id

    private int entityUserId; //事件作用对象的创作者

    private Map<String, Object> data =new HashMap<>();  //对未来事件属性做可扩展性

    public String getTopic() {
        return topic;
    }

    //这样修改可以进行.set().set().set()操作
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
