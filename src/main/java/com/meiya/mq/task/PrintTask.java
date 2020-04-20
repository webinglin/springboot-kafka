package com.meiya.mq.task;

import com.meiya.mq.util.PrintUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 打印任务，定时打印
 * @author linwb
 * @since 2020/4/17
 */
@Component
@EnableScheduling
@PropertySource("classpath:cron.properties")
public class PrintTask {
    private static final Logger LOG = LoggerFactory.getLogger(PrintTask.class);

    /* 二维码备份目录 */
    @Value("${spring.qrCodeBackupDir:/u01/qrCode/backup/}")
    private String qrCodeBackupDir;
    /* 二维码消费目录 */
    @Value("${spring.qrCodeDir:/u01/qrCode/consumer/}")
    private String qrCodeDir;
    /* 打印机名称 */
    @Value("${spring.printerName:win7-qrCode}")
    private String printerName;

    private ReentrantLock qrCodeReentrantLock = new ReentrantLock();

    @Scheduled(cron="${printQRCode.cron}")
    public void printQRCode(){
        System.out.println(111);

        qrCodeReentrantLock.lock();
        System.out.println(2222);
        try {
            File[] files = new File(qrCodeDir).listFiles(file -> file.getName().endsWith(".jpg"));
            if(files==null || files.length==0){
                LOG.info("暂无二维码图片需要打印");
                return ;
            }
            for (File file : files) {
                LOG.info("开始打印图片:{}", file.getName());
               if(!PrintUtil.print(printerName, file)){
                   continue ;
               }
                // 打印成功了 备份文件
                File backupFile = new File(qrCodeBackupDir + file.getName());
                FileUtils.moveFile(file, backupFile);
            }
        } catch (Exception e) {
            LOG.error("打印二维码出错",e);
        } finally {
            qrCodeReentrantLock.unlock();
        }
    }






}
