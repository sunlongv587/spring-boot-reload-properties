package com.sunlong.listenner;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/*
 * kafkaDemo
 *
 * @Author 孙龙
 * @Date 2018/1/19
 */
@Component
public class Listenner {

    @KafkaListener(topics = "topic1")
    public void listenT1(ConsumerRecord<?, ?> cr) throws Exception {
        System.out.println("listenT1收到消息！！   topic:>>>  " + cr.topic() + "    key:>>  " + cr.key() + "    value:>>  " + cr.value());
    }

    @KafkaListener(topics = "topic2")
    public void listenT2(ConsumerRecord<?, ?> cr) throws Exception {
        System.out.println("listenT2收到消息！！   topic:>>>  " + cr.topic() + "    key:>>  " + cr.key() + "    value:>>  " + cr.value());
    }

    @KafkaListener(topics = "topic3")
    public void listenT3(ConsumerRecord<?, ?> cr) throws Exception {
        System.out.println("listenT3收到消息！！   topic:>>>  " + cr.topic() + "    key:>>  " + cr.key() + "    value:>>  " + cr.value());
    }

    @KafkaListener(topics = "topic4")
    public void listenT4(ConsumerRecord<?, ?> cr) throws Exception {
        System.out.println("listenT4收到消息！！   topic:>>>  " + cr.topic() + "    key:>>  " + cr.key() + "    value:>>  " + cr.value());
    }

    @KafkaListener(topics = "topic5")
    public void listenT5(ConsumerRecord<?, ?> cr) throws Exception {
        System.out.println("listenT5收到消息！！   topic:>>>  " + cr.topic() + "    key:>>  " + cr.key() + "    value:>>  " + cr.value());
    }

}
