package com.meiya.mq.task;

import org.springframework.beans.factory.annotation.Value;

/**
 * 打印任务，定时打印
 * @author linwb
 * @since 2020/4/17
 */
public class PrintTask {
    /* 二维码备份目录 */
    @Value("${qrCodeBackupDir:/u01/qrCode/backup/}")
    private String qrCodeBackupDir;


}
