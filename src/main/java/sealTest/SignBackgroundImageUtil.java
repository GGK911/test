package sealTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/28 9:13
 */
public class SignBackgroundImageUtil {

    // 默认印章图片像素大小 推荐3的倍数 不要小于300
    private final static int IMAGE_SIZE = 300;

    // 画笔颜色
    private final static Color COLOR = new Color(0, 0, 0, 60);

    // 字体
    private final static Map<com.itextpdf.text.Font, String> SPARE_FONT = new HashMap<>();

    static {
        GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();

        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("font/SIMSUN.TTF"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        genv.registerFont(font);
        try {
            byte[] simsunBytes = IOUtils.toByteArray(ResourceUtil.getStream("font/SIMSUN.TTF"));
            BaseFont simsunBaseFont = BaseFont.createFont("SIMSUN.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, simsunBytes, null);
            com.itextpdf.text.Font simsunFont = new com.itextpdf.text.Font(simsunBaseFont, 12f, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK);
            if (font != null) {
                SPARE_FONT.put(simsunFont, font.getName());
            }
        } catch (IOException | com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
        }

        Font spareFont1 = null;
        try {
            spareFont1 = Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("font/JinbiaoSong.TTF"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        genv.registerFont(spareFont1);
        try {
            byte[] jbBytes = IOUtils.toByteArray(ResourceUtil.getStream("font/JinbiaoSong.TTF"));
            BaseFont jbBaseFont = BaseFont.createFont("JinbiaoSong.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, jbBytes, null);
            com.itextpdf.text.Font jbFont = new com.itextpdf.text.Font(jbBaseFont, 12f, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK);
            if (spareFont1 != null) {
                SPARE_FONT.put(jbFont, spareFont1.getName());
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

        Font spareFont2 = null;
        try {
            spareFont2 = Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("font/simsunb.ttf"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        genv.registerFont(spareFont2);
        try {
            byte[] sbBytes = IOUtils.toByteArray(ResourceUtil.getStream("font/simsunb.ttf"));
            BaseFont sbBaseFont = BaseFont.createFont("simsunb.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, sbBytes, null);
            com.itextpdf.text.Font sbFont = new com.itextpdf.text.Font(sbBaseFont, 12f, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK);
            if (spareFont2 != null) {
                SPARE_FONT.put(sbFont, spareFont2.getName());
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

        // 得意黑
        // Font spareFont3 = null;
        // try {
        //     spareFont3 = Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("font/SmileySans-Oblique.ttf"));
        // } catch (FontFormatException | IOException e) {
        //     e.printStackTrace();
        // }
        // try {
        //     genv.registerFont(spareFont3);
        //     byte[] smileBytes = IOUtils.toByteArray(ResourceUtil.getStream("font/SmileySans-Oblique.ttf"));
        //     BaseFont baseFont = BaseFont.createFont("SmileySans-Oblique.ttf", BaseFont.IDENTITY_H, cn.com.mcsca.itextpdf.text.pdf.BaseFont.EMBEDDED, true, smileBytes, null);
        //     com.itextpdf.text.Font font = new com.itextpdf.text.Font(baseFont, 12f, com.itextpdf.text.Font.ITALIC, BaseColor.BLACK);
        //     if (spareFont3 != null) {
        //         SPARE_FONT.put(font, spareFont3.getName());
        //     }
        // } catch (IOException | DocumentException e) {
        //     e.printStackTrace();
        // }
    }

    public static void main(String[] args) {
        Path out = Paths.get("src/main/java/sealTest", "bg.png");
        byte[] seal = draw("唐好凯唐好凯唐好凯");
        FileUtil.writeBytes(seal, out.toAbsolutePath().toString());
    }

    /**
     * 绘制签署背景文字引导图片
     *
     * @param name 文字
     * @return 图像
     */
    public static byte[] draw(String name) {
        int codePointCount = name.codePointCount(0, name.length());
        if (codePointCount == 0) {
            throw new RuntimeException("签名背景文字为空");
        }
        BufferedImage image = null;
        for (int i = 0; i < codePointCount; i++) {
            BufferedImage one = drawOneString(StrUtil.subString(name, i, i + 1));
            if (image == null) {
                image = one;
            } else {
                image = splicingRight(image, one);
            }
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream.toByteArray();
    }

    /**
     * 绘制单个图像
     *
     * @param string 单个图像
     * @return 图像
     */
    public static BufferedImage drawOneString(String string) {
        Color backgroundColor = Color.WHITE; // 设置背景色为白色
        // 创建一个透明背景的图片
        BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color lineColor = new Color(255, 0, 0, 60); // 设置文字颜色的透明度（0-255）
        // 设置红色的虚线
        graphics.setColor(lineColor);
        float dash[] = {10.0f};
        graphics.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        // 绘制对角线
        graphics.drawLine(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        graphics.drawLine(IMAGE_SIZE, 0, 0, IMAGE_SIZE);

        graphics.drawLine(1, 0, IMAGE_SIZE - 1, 0); // 上边框
        graphics.drawLine(IMAGE_SIZE - 1, 0, IMAGE_SIZE - 1, IMAGE_SIZE - 1); // 右边框
        graphics.drawLine(IMAGE_SIZE - 1, IMAGE_SIZE - 1, 0, IMAGE_SIZE - 1); // 下边框
        graphics.drawLine(1, IMAGE_SIZE - 1, 0, 0); // 左边框

        // 绘制中间的红色虚线十字
        int centerX = IMAGE_SIZE / 2;
        int centerY = IMAGE_SIZE / 2;
        graphics.drawLine(centerX, 0, centerX, IMAGE_SIZE);
        graphics.drawLine(0, centerY, IMAGE_SIZE, centerY);

        // 设置字体和文字颜色
        Font font = new Font("宋体", Font.PLAIN, IMAGE_SIZE);
        graphics.setFont(font);
        graphics.setColor(COLOR);
        // 计算文字在图片中的位置
        FontMetrics fm = graphics.getFontMetrics();
        int totalWidth = fm.stringWidth(string);
        int startX = (IMAGE_SIZE - totalWidth) / 2;
        int startY = (IMAGE_SIZE - fm.getHeight()) / 2 + fm.getAscent();
        // 在图片上绘制文字
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            String text = String.valueOf(c);
            int charWidth = fm.charWidth(c);
            graphics.drawString(text, startX, startY);
            startX += charWidth;
        }
        graphics.dispose();
        return image;
    }

    /**
     * 拼接左右两个图像(以左边图像高度为准)
     *
     * @param left  左侧图像
     * @param right 右侧图像
     * @return 拼接后的图像
     */
    public static BufferedImage splicingRight(BufferedImage left, BufferedImage right) {
        int leftHeight = left.getHeight();
        int leftWidth = left.getWidth();
        int rightHeight = right.getHeight();
        int rightWidth = right.getWidth();
        // 高度不一样
        if (leftHeight != rightHeight) {
            // 拉伸右侧高度
            right = imageStretch(right, rightWidth * rightHeight / leftHeight, leftHeight);
        }
        rightWidth = right.getWidth();
        // 画布
        BufferedImage bufferedImage = new BufferedImage(leftWidth + rightWidth, leftHeight, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(left, 0, 0, leftWidth, leftHeight, null);
        graphics.drawImage(right, leftWidth, 0, rightWidth, leftHeight, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 拉伸图像
     * +---------+
     * | +-----+ |
     * | |  A  | |
     * | +-----+ |
     * +---------+
     *
     * @param bufferedImageOriginal 原始图像
     * @param width                 拉伸后宽
     * @param height                拉伸后高
     * @return 图像
     */
    public static BufferedImage imageStretch(BufferedImage bufferedImageOriginal, int width, int height) {
        // 画布
        BufferedImage bufferedImageAfter = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImageAfter.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 画字
        graphics.drawImage(bufferedImageOriginal, 0, 0, width, height, null);
        // 释放
        graphics.dispose();
        return bufferedImageAfter;
    }

}
