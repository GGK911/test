package sealTest;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 旋转图片
 *
 * @author TangHaoKai
 * @version V1.0 2024/4/28 12:23
 */
public class RotateImageTest {

    /**
     * 有问题，旋转后，长方形尺寸对不上，内容也对不上
     */
    @Test
    @SneakyThrows
    public void test() {
        int degrees = 90;
        BufferedImage originalImage = CreateSealUtil.drawOneStringHeightImageSizeHalf("十");
        // 获取原始图像的宽度和高度
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // 创建一个新的 BufferedImage，用于存储旋转后的图像
        // 这里宽度和高度根据角度选择最大值，以适应旋转后的图像大小
        BufferedImage rotatedImage = new BufferedImage(width, height, originalImage.getType());

        // 创建一个 Graphics2D 对象，用于绘制图像
        Graphics2D g2d = rotatedImage.createGraphics();

        // 创建一个 AffineTransform 对象，用于进行旋转操作
        AffineTransform transform = new AffineTransform();

        // 设置旋转中心为图像的中心
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        // 应用旋转变换，角度由参数指定
        transform.rotate(Math.toRadians(-degrees), centerX, centerY);

        // 将旋转操作应用于 Graphics2D 对象
        g2d.setTransform(transform);

        // 绘制原始图像到旋转后的图像上
        g2d.drawImage(originalImage, 0, 0, null);

        // 释放 Graphics2D 对象的资源
        g2d.dispose();

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(rotatedImage, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.writeBytes(outStream.toByteArray(), "C:\\Users\\ggk911\\Desktop\\1.png");
    }

    @Test
    @SneakyThrows
    public void test2() {
        int angle = 90;
        BufferedImage originalImage = CreateSealUtil.drawOneStringHeightImageSizeHalf("十");
        // 获取原始图像的宽度和高度
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // 创建一个 AffineTransform 对象，用于进行旋转操作
        AffineTransform transform = new AffineTransform();

        // 设置旋转中心为原始图像的中心
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        // 应用顺时针旋转变换，角度由参数指定
        transform.rotate(Math.toRadians(-angle), centerX, centerY);

        // 计算旋转后的图像大小
        double[] corners = new double[]{
                0, 0,
                width, 0,
                width, height,
                0, height
        };

        // 将角度转换应用于四个角点
        transform.transform(corners, 0, corners, 0, 4);

        // 计算新的图像边界
        double minX = Math.min(Math.min(corners[0], corners[2]), Math.min(corners[4], corners[6]));
        double maxX = Math.max(Math.max(corners[0], corners[2]), Math.max(corners[4], corners[6]));
        double minY = Math.min(Math.min(corners[1], corners[3]), Math.min(corners[5], corners[7]));
        double maxY = Math.max(Math.max(corners[1], corners[3]), Math.max(corners[5], corners[7]));

        // 新的图像宽度和高度
        int newWidth = (int) Math.ceil(maxX - minX);
        int newHeight = (int) Math.ceil(maxY - minY);

        // 创建一个新的 BufferedImage 用于存储旋转后的图像
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // 创建 Graphics2D 对象并设置渲染提示
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 计算平移量，使旋转后的图像内容正确定位
        double translateX = -minX;
        double translateY = -minY;

        // 在 Graphics2D 对象上应用变换和平移
        AffineTransform adjustedTransform = new AffineTransform();
        adjustedTransform.translate(translateX, translateY);
        adjustedTransform.concatenate(transform);
        g2d.setTransform(adjustedTransform);

        // 绘制原始图像到旋转后的图像上
        g2d.drawImage(originalImage, 0, 0, null);

        // 释放 Graphics2D 对象的资源
        g2d.dispose();

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(rotatedImage, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.writeBytes(outStream.toByteArray(), "C:\\Users\\ggk911\\Desktop\\1.png");
    }

}
