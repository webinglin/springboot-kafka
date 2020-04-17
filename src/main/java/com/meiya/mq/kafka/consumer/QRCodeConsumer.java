package com.meiya.mq.kafka.consumer;

import com.meiya.mq.util.QRCodeZxingUtil;
import com.meiya.springboot.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 二维码消费者，每接收一条消息，就消费一条记录，生成二维码图片，保存到特定的目录。然后定时打印任务再打印的时候，就将特定目录的二维码图片(只要图片格式）打印出来。
 * 如果有某一条消息还没生成好图片，那这个图片的打印就等到下一次打印任务执行的时候再打印出来
 * @author linwb
 * @since 2020/4/16
 */
@Service
public class QRCodeConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(QRCodeConsumer.class);

    /* 二维码消费目录 */
    @Value("${qrCodeDir:/u01/qrCode/consumer/}")
    private String qrCodeDir;

    /**
     * 消费者消息监听服务，来一条消息就消费一条
     * @param message  接收到的消息
     */
    @KafkaListener(id = "consumer-01", topics = "${spring.kafka.topic}")
    public void receive(/*@Payload */String message,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                        @Header(KafkaHeaders.OFFSET) Long offset){
        /*
         * 接收到消息之后，就生成二维码图片到固定的目录。
         * 然后打印机服务就定时从某个目录下获取二维码图片，然后打印出来。打印之后，将二维码图片备份到备份目录。
         * 打印服务只要扫描过滤出JPG的二维码图片，而且要按照时间过滤出来，然后备份走就好。如果有正在生成的图片就等下一批次再打印
         * 每个二维码图片都按照时间命名。
         */

        LOG.info("received message='{}' with partition-offset='{}'", message, partition + "-" + offset);
        if(StringUtils.isBlank(message)){
            return ;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = df.format(new Date());
        File qrCodeParentDir = new File(qrCodeDir);
        if(!qrCodeParentDir.exists()){
            qrCodeParentDir.mkdirs();
        }
        String qrCodeImg = qrCodeParentDir + "/" + StringUtil.getBase64Guid() + "_" + dateStr + ".jpg";

        QRCodeZxingUtil.generateQRcodePic(message,100,100,qrCodeImg);

    }

}
