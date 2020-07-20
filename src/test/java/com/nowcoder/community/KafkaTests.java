package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Paul Z on 2020/7/7
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        //一般生产者方法是我们主动调用的，要指定topic和message，
        //而消费者方法是被动的，一旦监听到消息队列的内容就会执行处理方法
        kafkaProducer.sendMessage("test", "你好");
        kafkaProducer.sendMessage("test", "在吗");

        //延迟主线程10秒，以等待消费者方法的执行
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

@Component
class KafkaProducer{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content){
        kafkaTemplate.send(topic, content);
    }
}

@Component
class KafkaConsumer{

    //使用这个注解后，会有一个消费者线程始终监听这个主题，处于阻塞状态，一旦监听到这个主题有消息数据，就立刻读
    //一旦读到消息数据后，就会交给该注解修饰的方法进行处理
    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record){

        //调这个方法的时候，Spring会自动的将消息封装成ConsumerRecord类型的对象
        //通过对ConsumerRecord对象的处理，可以读到原始的消息
        System.out.println(record.value());
    }

}