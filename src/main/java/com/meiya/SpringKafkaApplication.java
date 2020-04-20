package com.meiya;

import com.meiya.mq.kafka.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot启动类
 * @author linwb
 * @since 20200415
 */
@SpringBootApplication
public class SpringKafkaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaApplication.class, args);
    }

    @Autowired
    private Producer sender;

    @Override
    public void run(String... strings) throws Exception {
//        for (int i = 1; i < 13; i++){
//            sender.send("message-" + i);
//        }
    }
}