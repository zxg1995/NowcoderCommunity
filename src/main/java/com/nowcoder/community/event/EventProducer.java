package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by Paul Z on 2020/7/7
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 触发事件（实际上就是生产一个消息）
    public void fireEvent(Event event){

        //将事件发布到指定的主题
        //内容为该事件对象的JSON格式字符串，方便转换
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));

    }

}
