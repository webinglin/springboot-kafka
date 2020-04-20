package com.meiya.mq.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;

/**
 * 打印机服务    JAVA调用打印机触发打印
 * @author linwb
 * @since 2020/4/15
 */
public class PrintUtil {
    private static final Logger LOG = LoggerFactory.getLogger(PrintUtil.class);

    /**
     * 打印操作
     * @param printerName  打印机名称
     * @param printFile     打印的文件
     */
    public static boolean print(String printerName, File printFile){
        PrintService[] printServices = PrinterJob.lookupPrintServices();
        PrintService printService = null;
        for (PrintService _printService : printServices) {
            if(printerName.equals(_printService.getName())){
                printService = _printService;
            }
        }
        if(printService==null){
            LOG.error("- - - - - - - - - - - - - - - - ");
            LOG.error("找不到可用的打印机");
            LOG.error("- - - - - - - - - - - - - - - - ");
            return false;
        }


        // 打印格式
        DocFlavor flavor = DocFlavor.INPUT_STREAM.JPEG;
        // 打印请求属性集
        HashPrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        // 打印的质量 高
        pras.add(PrintQuality.HIGH);
//        pras.add(OrientationRequested.PORTRAIT);

        // 1份
        pras.add(new Copies(1));
        // 单面打印
        pras.add(Sides.ONE_SIDED);
        // 设置打印的纸张
        pras.add(MediaSizeName.ISO_A10);


        // 打印的文件输入流
        try(FileInputStream fin = new FileInputStream(printFile)) {
            DocAttributeSet das = new HashDocAttributeSet();
            das.add(new MediaPrintableArea(0, 0, 1, 1, MediaPrintableArea.INCH));

            Doc doc = new SimpleDoc(fin, flavor, das);

            DocPrintJob job = printService.createPrintJob();
            job.print(doc, pras);
        } catch (Exception e){
            LOG.error("- - - - - - - - - - - - - - - - ");
            LOG.error("打印出错", e);
            LOG.error("- - - - - - - - - - - - - - - - ");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String printerName = "Microsoft Print to PDF";
        print(printerName, new File("D:\\DEV_ENV\\img\\180字符.jpg"));
    }

}
