package sealTest;

import cn.hutool.core.io.FileUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 生成图纸
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-20 11:13
 **/
public class test {
    public static void main(String[] args) {
        // byte[] bytes = SealUtil.buildPersonSeal("测试", 18, 6, 0, 0, 0);
        // FileUtil.writeBytes(bytes, "C:\\Users\\ggk911\\Desktop\\test.png");

        // BufferedImage buffer = drawThreeVerticalStringTest("测", "试", "章");
        // BufferedImage buffer = drawFiveVerticalTwoLeftStringTest("测", "试", "章", "五", "字");
        // BufferedImage buffer = drawRectangleStringTest(drawFiveVerticalTwoLeftIsometricFontStringTest("测", "试", "章", "五", "字"));
        // BufferedImage buffer = drawRectangleStringTest(drawFiveVerticalTwoRightIsometricFontStringTest("测", "试", "章", "五", "字"));
        // BufferedImage buffer = drawRectangleStringTest(drawFiveVerticalTwoLeftNotIsometricFontStringTest("测", "试", "章", "五", "字"));
        // BufferedImage buffer = drawOneStringStretchTest(drawOneStringHeight100Test("测"), 180, 100, 120);
        // BufferedImage buffer = drawRectangleStringTest(drawSixVerticalTwoRightIsometricFontStringTest("六", "字", "测", "试", "图", "章"));
        BufferedImage buffer = drawRectangleStringTest(drawSixVerticalTwoRightIsometricFontStringTest("六", "字", "测", "试", "图", "章"));

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(buffer, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.writeBytes(outStream.toByteArray(), "C:\\Users\\ggk911\\Desktop\\IsometricSix.png");
    }

    public static void drawStringTest() {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300, 300 / 2, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 填充背景
        // graphics.fillRect(0, 0, 300, 150);
        // 换颜色
        graphics.setPaint(Color.RED);

        graphics.drawString("测", 10, 120);
        graphics.drawString("试", 165, 120);

        BufferedImage bufferedImage1 = new BufferedImage(300, 300, bufferedImage.getType());
        Graphics2D graphics1 = bufferedImage1.createGraphics();
        graphics1.setPaint(Color.red);
        graphics1.drawImage(bufferedImage, 0, 0, 300, 300, null);
        graphics1.setStroke(new BasicStroke(16));
        graphics1.drawRect(0, 0, 300, 300);
        graphics1.dispose();

        bufferedImage = bufferedImage1;

        graphics.dispose();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.writeBytes(outStream.toByteArray(), "C:\\Users\\ggk911\\Desktop\\test.png");
    }

    /**
     * 画垂直二字
     *
     * @param top    二字上，单字图像
     * @param bottom 二字下，单字图像
     * @return 图像
     */
    public static BufferedImage drawTwoVerticalStringTest(BufferedImage top, BufferedImage bottom) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300 / 2, 300, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 填充背景
        // graphics.fillRect(0, 0, 300 / 2, 300);
        // 换颜色
        graphics.setPaint(Color.RED);

        graphics.drawImage(top, 0, 0, 150, 150, null);
        graphics.drawImage(bottom, 0, 150, 150, 150, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 画垂直三字
     *
     * @param top    三字上，单字图像
     * @param middle 三字中，单字图像
     * @param bottom 三字下，单字图像
     * @return 图像
     */
    public static BufferedImage drawThreeVerticalStringTest(BufferedImage top, BufferedImage middle, BufferedImage bottom) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300 / 2, 300, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 填充背景
        // graphics.fillRect(0, 0, 300 / 2, 300);
        // 换颜色
        graphics.setPaint(Color.RED);

        graphics.drawImage(top, 0, 0, 150, 100, null);
        graphics.drawImage(middle, 0, 100, 150, 100, null);
        graphics.drawImage(bottom, 0, 200, 150, 100, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 五字，二字在左，等比例字体
     *
     * @param topThree    三字上
     * @param middleThree 三字中
     * @param bottomThree 三字下
     * @param topTwo      二字上
     * @param bottomTwo   二字下
     * @return 图像
     */
    public static BufferedImage drawFiveVerticalTwoLeftIsometricFontStringTest(String topThree, String middleThree, String bottomThree, String topTwo, String bottomTwo) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 填充背景
        // graphics.fillRect(0, 0, 300 / 2, 300);
        // 换颜色
        graphics.setPaint(Color.RED);

        graphics.drawImage(drawThreeVerticalStringTest(drawOneStringHeight100Test(topThree), drawOneStringHeight100Test(middleThree), drawOneStringHeight100Test(bottomThree)), 150, 0, 150, 300, null);
        graphics.drawImage(drawTwoVerticalStringTest(drawOneStringHeight150Test(topTwo), drawOneStringHeight150Test(bottomTwo)), 0, 0, 150, 300, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 五字，二字在左，非等比例字体
     *
     * @param topThree    三字上
     * @param middleThree 三字中
     * @param bottomThree 三字下
     * @param topTwo      二字上
     * @param bottomTwo   二字下
     * @return 图像
     */
    public static BufferedImage drawFiveVerticalTwoLeftNotIsometricFontStringTest(String topThree, String middleThree, String bottomThree, String topTwo, String bottomTwo) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 填充背景
        // graphics.fillRect(0, 0, 300 / 2, 300);
        // 换颜色
        graphics.setPaint(Color.RED);

        graphics.drawImage(drawThreeVerticalStringTest(drawOneStringStretchTest(drawOneStringHeight150Test(topThree), 150, 100, 120), drawOneStringStretchTest(drawOneStringHeight150Test(middleThree), 150, 100, 120), drawOneStringStretchTest(drawOneStringHeight150Test(bottomThree), 150, 100, 120)), 150, 0, 150, 300, null);
        graphics.drawImage(drawTwoVerticalStringTest(drawOneStringHeight150Test(topTwo), drawOneStringHeight150Test(bottomTwo)), 0, 0, 150, 300, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 五字，二字在右，等比例字体
     *
     * @param topThree    三字上
     * @param middleThree 三字中
     * @param bottomThree 三字下
     * @param topTwo      二字上
     * @param bottomTwo   二字下
     * @return 图像
     */
    public static BufferedImage drawFiveVerticalTwoRightIsometricFontStringTest(String topThree, String middleThree, String bottomThree, String topTwo, String bottomTwo) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 填充背景
        // graphics.fillRect(0, 0, 300 / 2, 300);
        // 换颜色
        graphics.setPaint(Color.RED);

        graphics.drawImage(drawThreeVerticalStringTest(drawOneStringHeight100Test(topThree), drawOneStringHeight100Test(middleThree), drawOneStringHeight100Test(bottomThree)), 0, 0, 150, 300, null);
        graphics.drawImage(drawTwoVerticalStringTest(drawOneStringHeight150Test(topTwo), drawOneStringHeight150Test(bottomTwo)), 150, 0, 150, 300, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 五字，二字在右，非等比例字体
     *
     * @param topThree    三字上
     * @param middleThree 三字中
     * @param bottomThree 三字下
     * @param topTwo      二字上
     * @param bottomTwo   二字下
     * @return 图像
     */
    public static BufferedImage drawFiveVerticalTwoRightNotIsometricFontStringTest(String topThree, String middleThree, String bottomThree, String topTwo, String bottomTwo) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 填充背景
        // graphics.fillRect(0, 0, 300 / 2, 300);
        // 换颜色
        graphics.setPaint(Color.RED);

        graphics.drawImage(drawThreeVerticalStringTest(drawOneStringStretchTest(drawOneStringHeight150Test(topThree), 150, 100, 120), drawOneStringStretchTest(drawOneStringHeight100Test(middleThree), 150, 100, 120), drawOneStringStretchTest(drawOneStringHeight100Test(bottomThree), 150, 100, 120)), 0, 0, 150, 300, null);
        graphics.drawImage(drawTwoVerticalStringTest(drawOneStringHeight150Test(topTwo), drawOneStringHeight150Test(bottomTwo)), 150, 0, 150, 300, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 六字，非等比例字体
     *
     * @param topLeft     三字左上
     * @param middleLeft  三字左中
     * @param bottomLeft  三字左下
     * @param topRight    三字右上
     * @param middleRight 三字右中
     * @param bottomRight 三字右下
     * @return 图像
     */
    public static BufferedImage drawSixVerticalTwoRightNotIsometricFontStringTest(String topLeft, String middleLeft, String bottomLeft, String topRight, String middleRight, String bottomRight) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 填充背景
        // graphics.fillRect(0, 0, 300 / 2, 300);
        // 换颜色
        graphics.setPaint(Color.RED);
        graphics.drawImage(drawThreeVerticalStringTest(drawOneStringStretchTest(drawOneStringHeight150Test(topLeft), 150, 100, 120), drawOneStringStretchTest(drawOneStringHeight150Test(middleLeft), 150, 100, 120), drawOneStringStretchTest(drawOneStringHeight150Test(bottomLeft), 150, 100, 120)), 0, 0, 150, 300, null);
        graphics.drawImage(drawThreeVerticalStringTest(drawOneStringStretchTest(drawOneStringHeight150Test(topRight), 150, 100, 120), drawOneStringStretchTest(drawOneStringHeight150Test(middleRight), 150, 100, 120), drawOneStringStretchTest(drawOneStringHeight150Test(bottomRight), 150, 100, 120)), 150, 0, 150, 300, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 六字，等比例字体
     *
     * @param topLeft     三字左上
     * @param middleLeft  三字左中
     * @param bottomLeft  三字左下
     * @param topRight    三字右上
     * @param middleRight 三字右中
     * @param bottomRight 三字右下
     * @return 图像
     */
    public static BufferedImage drawSixVerticalTwoRightIsometricFontStringTest(String topLeft, String middleLeft, String bottomLeft, String topRight, String middleRight, String bottomRight) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 填充背景
        // graphics.fillRect(0, 0, 300 / 2, 300);
        // 换颜色
        graphics.setPaint(Color.RED);
        graphics.drawImage(drawThreeVerticalStringTest(drawOneStringHeight100Test(topLeft), drawOneStringHeight100Test(middleLeft), drawOneStringHeight100Test(bottomLeft)), 0, 0, 150, 300, null);
        graphics.drawImage(drawThreeVerticalStringTest(drawOneStringHeight100Test(topRight), drawOneStringHeight100Test(middleRight), drawOneStringHeight100Test(bottomRight)), 150, 0, 150, 300, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 画单字，高度为150
     *
     * @param str 字
     * @return 图像
     */
    public static BufferedImage drawOneStringHeight150Test(String str) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(150, 150, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 填充背景
        // graphics.fillRect(0, 0, 150 / 2, 300);
        // 换颜色
        graphics.setPaint(Color.RED);
        // 画字
        graphics.drawString(str, 10, 120);
        // 释放
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 画单字，高度为100
     *
     * @param str 字
     * @return 图像
     */
    public static BufferedImage drawOneStringHeight100Test(String str) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(150, 100, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 80));
        // 填充背景
        // graphics.fillRect(0, 0, 300 / 2, 300);
        // 换颜色
        graphics.setPaint(Color.RED);
        // 画字
        graphics.drawString(str, 35, 80);
        // 释放
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 拉伸单字
     *
     * @param bufferedImageOriginal 原始图像
     * @param width                 拉伸后宽
     * @param height                拉伸后高
     * @param fontSize              字体大小
     * @return 图像
     */
    public static BufferedImage drawOneStringStretchTest(BufferedImage bufferedImageOriginal, int width, int height, int fontSize) {
        // 画布
        BufferedImage bufferedImageAfter = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImageAfter.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, fontSize));
        // 画字
        graphics.drawImage(bufferedImageOriginal, 0, 0, width, height, null);
        // 释放
        graphics.dispose();
        return bufferedImageAfter;
    }

    /**
     * 画矩形外线（300X300）
     *
     * @param bufferedImage 原始图像
     * @return 图像
     */
    public static BufferedImage drawRectangleStringTest(BufferedImage bufferedImage) {
        BufferedImage bufferedImage1 = new BufferedImage(300, 300, bufferedImage.getType());
        Graphics2D graphics1 = bufferedImage1.createGraphics();
        graphics1.setPaint(Color.red);
        graphics1.drawImage(bufferedImage, 0, 0, 300, 300, null);
        graphics1.setStroke(new BasicStroke(16));
        graphics1.drawRect(0, 0, 300, 300);
        graphics1.dispose();
        return bufferedImage1;
    }

}
