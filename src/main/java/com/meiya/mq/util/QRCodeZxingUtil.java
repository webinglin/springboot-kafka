package com.meiya.mq.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 * @author linwb
 * @since 2020/4/15
 */
public class QRCodeZxingUtil {
    private static final Logger LOG = LoggerFactory.getLogger(QRCodeZxingUtil.class);

    /* 生成的二维码图片扩展名 */
    private static final String QRCODE_EXTENSION = "jpg" ;

    /**
     * 生成二维码 直接将二维码图片写到指定文件目录
     *
     * @param content 二维码内容
     * @param width   二维码宽度
     * @param height  二维码高度，通常建议二维码宽度和高度相同
     * @param picPath  二维码图片存放位置
     */
    public static void generateQRcodePic(String content, int width, int height, String picPath) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            // 构造二维字节矩阵
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 构造文件目录，若目录不存在，则创建目录
            File file = new File(picPath);
            File parentFile = file.getParentFile();
            if(!parentFile.exists()){
                parentFile.mkdirs();
            }


            // 将二位字节矩阵按照指定图片格式，写入指定文件目录，生成二维码图片
            MatrixToImageWriter.writeToPath(bitMatrix, QRCODE_EXTENSION, file.toPath());
        } catch (WriterException | IOException e) {
            LOG.error("生成二维码图片出错", e);
        }
    }

    /**
     * 生成二维码 生成二维码图片字节流
     *
     * @param content 二维码内容
     * @param width   二维码宽度和高度
     */
    public static byte[] generateQRcodeByte(String content, int width) {
        byte[] codeBytes = null;
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);


            // 构造二维字节矩阵，将二位字节矩阵渲染为二维缓存图片
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, width, hints);
            BufferedImage image = toBufferedImage(bitMatrix);

            // 定义输出流，将二维缓存图片写到指定输出流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, QRCODE_EXTENSION, out);

            // 将输出流转换为字节数组
            codeBytes = out.toByteArray();

        } catch (WriterException | IOException e) {
            LOG.error("生成二维码图片字节数组出错", e);
        }
        return codeBytes;
    }

    /**
     * 将二维字节矩阵渲染为二维缓存图片
     *
     * @param matrix 二维字节矩阵
     * @return 二维缓存图片
     */
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int onColor = 0xFF000000;
        int offColor = 0xFFFFFFFF;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? onColor : offColor);
            }
        }
        return image;
    }

    /**
     * 解析二维码内容
     *
     * @param filepath 二维码路径
     */
    public static void readQRcode(String filepath) {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        File file = new File(filepath);

        // 图片缓冲
        BufferedImage image = null;
        // 二进制比特图
        BinaryBitmap binaryBitmap = null;

        try {
            image = ImageIO.read(file);
            binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Result result = multiFormatReader.decode(binaryBitmap);

            System.out.println("读取二维码： " + result.toString());
            System.out.println("二维码格式： " + result.getBarcodeFormat());
            System.out.println("二维码内容： " + result.getText());

        } catch (IOException | NotFoundException e) {
            LOG.error("二维码解析出错", e);
        }
    }

    /**
     * main 测试方法
     */
    public static void main(String[] args) {
        int width = 100;

        // 消息可能存在中文，
        String cont = "{'msg':'您在内网有一条待办',idcards:'359999199999999999,359999199999999999,359999199999999999,359999199999999999,359999199999999999,359999199999999999,359999199999999999,359999199999999999,359999199999999999,359999199999999999,359999199999999999'}";

        String picPath = "D:\\DEV_ENV/img/qrcode.jpg";

        // 生成二维码，直接写到本地
        generateQRcodePic(cont, width, width, picPath);

        // 测试二维码信息解析
        readQRcode(picPath);

        // 生成二维码，返回字节数组
        String picPath2 = "D:\\DEV_ENV/img/qrcodeByte.png";
        File pathFile = new File(picPath2);
        byte[] fileIo = generateQRcodeByte(cont, width);
        try(OutputStream os = new FileOutputStream(pathFile)){
            os.write(fileIo);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
