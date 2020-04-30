package com.meiya.mq.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * kafka消息生产者
 * @author linwb
 * @since 20200415
 */
@Service
public class KafkaProducer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic:todoItemTopic}")
    private String topic;

    public void send(String data){
        LOG.info("sending message='{}' to topic='{}'", data, topic);
        kafkaTemplate.send(topic, data);
    }
}
