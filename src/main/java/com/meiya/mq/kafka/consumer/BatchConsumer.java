package com.meiya.mq.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * kafka消息消费者
 * @author linwb
 * @since 20200415
 */
@Service
public class BatchConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(BatchConsumer.class);

    @Value("${spring.kafka.consumer.poll-max-wait}")
    private int pollMaxWait;

    @KafkaListener(id = "batch-listener", topics = "${spring.kafka.topic}")
    public void receive(@Payload List<String> messages,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        LOG.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        LOG.info("beginning to consume batch messages");

        for (int i = 0; i < messages.size(); i++) {

            LOG.info("received message='{}' with partition-offset='{}'",
                    messages.get(i), partitions.get(i) + "-" + offsets.get(i));

        }
        LOG.info("all batch messages consumed");

        try {
            Thread.sleep(pollMaxWait);
        } catch (InterruptedException e) {
            LOG.error("sleep interrupted ",e);
        }
    }

}
