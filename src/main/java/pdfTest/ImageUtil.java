package pdfTest;

import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author TangHaoKai
 * @version V1.0 2023-12-20 10:24
 **/
public class ImageUtil {
    /**
     * 流形式按尺寸大小缩放图片
     *
     * @param in     文件字节流
     * @param out    文件字节输出流
     * @param width  宽
     * @param height 高
     **/
    public static void scaleBySize(ByteArrayInputStream in, ByteArrayOutputStream out, Integer width, Integer height) {
        try {
            Thumbnails.of(in)
                    .size(width, height)
                    // 默认是按照比例缩放的
                    .keepAspectRatio(false)
                    .toOutputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("缩放图片后输出流异常");
        }
    }

    /**
     * 流形式按尺寸大小缩放图片(按照比例缩放)
     *
     * @param in     文件字节流
     * @param out    文件字节输出流
     * @param width  宽
     * @param height 高
     */
    public static void scaleBySizeKeepRatio(ByteArrayInputStream in, ByteArrayOutputStream out, Integer width, Integer height) {
        try {
            Thumbnails.of(in)
                    .size(width, height)
                    .toOutputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("缩放图片后输出流异常");
        }
    }

    /**
     * 流形式按尺角度旋转图片
     *
     * @param in    文件字节流
     * @param out   文件字节输出流
     * @param angle 旋转角度 正数：顺时针 负数：逆时针
     */
    public static void rotateByAngle(ByteArrayInputStream in, ByteArrayOutputStream out, Integer width, Integer height, Double angle) {
        try {
            Thumbnails.of(in)
                    .size(width, height)
                    .rotate(angle)
                    .toOutputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("缩放图片后输出流异常");
        }
    }
}
