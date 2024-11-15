package sealTest;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.FontSelector;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 制章工具
 * 如修改图章大小需注意：
 * 企业参数scaleSize
 *
 * @author TangHaoKai
 * @version V1.0 2023/12/26 11:59
 **/
public class CreateSealUtil {
    // 默认印章图片像素大小 推荐3的倍数 不要小于300
    private final static int IMAGE_SIZE = 300;

    // 画笔颜色
    // private final static Color COLOR = Color.BLACK;
    private final static Color COLOR = Color.RED;

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
        // byte[] seal = createSquareSeal("A");
        // byte[] seal = createSquareSeal("国");
        // byte[] seal = createSquareSeal("陈佳");
        // byte[] seal = createSquareSeal("得意");
        // byte[] seal = createSquareSeal("张淋然");
        // byte[] seal = createSquareSeal("添加测试");
        // byte[] seal = createSquareSeal("国国国国国");
        byte[] seal = createSquareSeal("国国国国国国国国");
        // byte[] seal = createSquareSeal("塔布斯·赛里克江·亚里士多德");
        // byte[] seal = createSquareSeal("大陆云盾");
        //
        // byte[] seal = createCircleSeal("\uD870\uDF86\uD84D\uDCC3\uD852\uDE4A\uE27E㵥\uE4A1四川华西妇幼细胞生物技术有限公司");
        // byte[] seal = createCircleSeal("图章字体测试");
        // byte[] seal = createCircleSeal("大陆云盾电子认证服务有限公司");
        FileUtil.writeBytes(seal, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\sealTest\\test2.png");
    }

    /**
     * 制作方章(个人图章)
     *
     * @param name 名称
     * @return 字节
     */
    public static byte[] createSquareSeal(String name) {
        // 两字是否在左
        boolean twoLeft = false;
        // 是否保持字体宽度，不拉伸
        boolean isometricFont = false;
        int pointCount = name.codePointCount(0, name.length());
        BufferedImage bufferedImage = null;
        if (pointCount == 1) {
            bufferedImage = drawOneStringHeightImageSizeHalf(name);
        } else if (pointCount == 2) {
            bufferedImage = drawTwoHorizonString(StrUtil.subString(name, 0, 1), StrUtil.subString(name, 1, 2));
        } else if (pointCount == 3) {
            if (twoLeft) {
                bufferedImage = drawThreeHorizonTwoLeftString(StrUtil.subString(name, 1, 2), StrUtil.subString(name, 2, 3), StrUtil.subString(name, 0, 1));
            } else {
                bufferedImage = drawThreeHorizonTwoRightString(StrUtil.subString(name, 1, 2), StrUtil.subString(name, 2, 3), StrUtil.subString(name, 0, 1));
            }
        } else if (pointCount == 4) {
            bufferedImage = drawFourString(StrUtil.subString(name, 2, 3), StrUtil.subString(name, 3, 4), StrUtil.subString(name, 0, 1), StrUtil.subString(name, 1, 2));
        } else if (pointCount == 5) {
            if (twoLeft && isometricFont) {
                bufferedImage = drawFiveVerticalTwoLeftIsometricFontStringTest(StrUtil.subString(name, 2, 3), StrUtil.subString(name, 3, 4), StrUtil.subString(name, 4, 5), StrUtil.subString(name, 0, 1), StrUtil.subString(name, 1, 2));
            } else if (!twoLeft && !isometricFont) {
                bufferedImage = drawFiveVerticalTwoRightNotIsometricFontStringTest(StrUtil.subString(name, 2, 3), StrUtil.subString(name, 3, 4), StrUtil.subString(name, 4, 5), StrUtil.subString(name, 0, 1), StrUtil.subString(name, 1, 2));
            } else if (twoLeft && !isometricFont) {
                bufferedImage = drawFiveVerticalTwoLeftNotIsometricFontStringTest(StrUtil.subString(name, 2, 3), StrUtil.subString(name, 3, 4), StrUtil.subString(name, 4, 5), StrUtil.subString(name, 0, 1), StrUtil.subString(name, 1, 2));
            } else if (!twoLeft && isometricFont) {
                bufferedImage = drawFiveVerticalTwoRightIsometricFontStringTest(StrUtil.subString(name, 2, 3), StrUtil.subString(name, 3, 4), StrUtil.subString(name, 4, 5), StrUtil.subString(name, 0, 1), StrUtil.subString(name, 1, 2));
            }
        } else if (pointCount == 6) {
            if (twoLeft && isometricFont) {
                bufferedImage = drawSixVerticalTwoRightIsometricFontStringTest(StrUtil.subString(name, 0, 1), StrUtil.subString(name, 1, 2), StrUtil.subString(name, 2, 3), StrUtil.subString(name, 3, 4), StrUtil.subString(name, 4, 5), StrUtil.subString(name, 5, 6));
            } else if (twoLeft && !isometricFont) {
                bufferedImage = drawSixVerticalTwoRightNotIsometricFontStringTest(StrUtil.subString(name, 0, 1), StrUtil.subString(name, 1, 2), StrUtil.subString(name, 2, 3), StrUtil.subString(name, 3, 4), StrUtil.subString(name, 4, 5), StrUtil.subString(name, 5, 6));
            } else if (!twoLeft && !isometricFont) {
                // 因为对称，所以调换字序就行了
                bufferedImage = drawSixVerticalTwoRightNotIsometricFontStringTest(StrUtil.subString(name, 3, 4), StrUtil.subString(name, 4, 5), StrUtil.subString(name, 5, 6), StrUtil.subString(name, 0, 1), StrUtil.subString(name, 1, 2), StrUtil.subString(name, 2, 3));
            } else if (!twoLeft && isometricFont) {
                bufferedImage = drawSixVerticalTwoRightIsometricFontStringTest(StrUtil.subString(name, 3, 4), StrUtil.subString(name, 4, 5), StrUtil.subString(name, 5, 6), StrUtil.subString(name, 0, 1), StrUtil.subString(name, 1, 2), StrUtil.subString(name, 2, 3));
            }
        } else {
            // 横章
            for (int i = 0; i < pointCount; i += 2) {
                BufferedImage bufferedImagePre;
                if (i + 2 > pointCount) {
                    bufferedImagePre = drawOneStringHeight300(StrUtil.subString(name, i, i + 1));
                } else {
                    bufferedImagePre = drawTwoHorizonString(StrUtil.subString(name, i, i + 1), StrUtil.subString(name, i + 1, i + 2));
                }
                bufferedImage = transverseSplicingImage(bufferedImage, bufferedImagePre);
            }
        }
        // 边框
        if (pointCount <= 6) {
            bufferedImage = drawSquareOrder(bufferedImage);
        } else {
            bufferedImage = drawSquareOrderSelfAdaption(bufferedImage);
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream.toByteArray();
    }

    /**
     * 制作圆章(企业图章)
     *
     * @param name 名称
     * @return 字节
     */
    public static byte[] createCircleSeal(String name) {
        int pointCount = name.codePointCount(0, name.length());
        if (pointCount >= 48) {
            System.out.println("字数过长，推荐自定义图章");
            throw new RuntimeException("字数过长，推荐自定义图章");
        }
        // 字体长度匹配合适大小
        int fitFontSize = getFitFontSize(name.codePointCount(0, name.length()));
        // 画布 300X300
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D g2d = bufferedImage.createGraphics();
        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 透明度
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1));
        // 画笔颜色
        g2d.setPaint(COLOR);

        // 画圆
        int firstCircle = (int) (IMAGE_SIZE * 0.026);
        g2d.setStroke(new BasicStroke(firstCircle));
        // 因为圈占x像素，所以坐标位移x像素，整体宽高都减2 * x
        g2d.drawOval(firstCircle, firstCircle, IMAGE_SIZE - firstCircle * 2, IMAGE_SIZE - firstCircle * 2);

        // 中间五角星 大小比例 0.4
        int size = (int) (IMAGE_SIZE * 0.4F);
        g2d.setFont(new Font("宋体", Font.PLAIN, size));
        // 高度Y误差 0.16
        g2d.drawString("★", IMAGE_SIZE / 2F - size / 2F, (float) (IMAGE_SIZE / 2F - size / 2F + size - size * 0.16F));

        // 画字
        // 距离圆圈边界值 8 近点 / 15 平衡
        int topFix = 8;
        // int topFix = 15;
        double circleRadius = IMAGE_SIZE / 2F;
        // 字体 宋体 大小 0.088
        Font font = new Font("宋体", Font.PLAIN, fitFontSize);
        FontRenderContext fontRenderContext = g2d.getFontRenderContext();
        Rectangle2D stringBounds = font.getStringBounds(name, fontRenderContext);
        double width = stringBounds.getWidth();
        double height = stringBounds.getHeight();
        double scaleSize = 2;
        topFix = topFix + Convert.toInt(height / scaleSize * 2.0);
        // 调节字体间距
        double interval;
        if (pointCount < 9) {
            interval = width / (pointCount - 1) * 1.45;
        } else if (pointCount < 12) {
            interval = width / (pointCount - 1) * 1.25;
        } else if (pointCount < 15) {
            interval = width / (pointCount - 1) * 0.95;
        } else if (pointCount < 18) {
            interval = width / (pointCount - 1) * 0.85;
        } else {
            interval = width / (pointCount - 1) * 0.75;
        }
        double radius = circleRadius + stringBounds.getY() - topFix;
        double radianInterval = 2 * Math.asin(interval / (2 * radius));
        double firstAngle;
        // 第一个字起始位置
        double fix = 0.18;
        if (pointCount % 2 == 1) {
            firstAngle = (pointCount - 1) * radianInterval / 2.0 + Math.PI / 2 + fix;
        } else {
            firstAngle = (pointCount / 2.0 - 1) * radianInterval + radianInterval / 2.0 + Math.PI / 2 + fix;
        }
        // 字体旋转倾斜度      数值越大顺时针方向旋转度越高
        double gradient = 0.2;
        // 字体间是否靠近度    数值越大越靠近
        double distance = 0.9;

        // 为每个字找到合适字体
        Map<String, Font> fitFont = getFitFont(name, fitFontSize, Font.PLAIN);
        for (int i = 0; i < pointCount; i++) {
            double aa = firstAngle - i * radianInterval;
            double ax = radius * Math.sin(Math.PI / 2 - aa);
            double ay = radius * Math.cos(aa - Math.PI / 2);
            AffineTransform transform = AffineTransform.getRotateInstance(Math.PI / 2 - aa + gradient);
            AffineTransform scaleform = AffineTransform.getScaleInstance(distance, scaleSize);
            // 防止文本和外圈间距
            transform.concatenate(scaleform);

            String text = SealUtil.subString(name, i, i + 1);
            Font textFont = fitFont.get(text);
            Font font2 = textFont.deriveFont(transform);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);//设置抗锯齿
            g2d.setFont(font2);
            g2d.setPaint(COLOR);
            // 文字
            g2d.drawString(StrUtil.subString(name, i, i + 1), (float) (circleRadius + ax), (float) (circleRadius - ay));
        }

        g2d.dispose();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream.toByteArray();
    }

    /**
     * 遍历字体获取text最合适字体(利用itext的FontSelector)
     *
     * @param text     文字
     * @param fontSize 字体大小
     * @return 字体
     */
    public static Map<String, Font> getFitFont(String text, int fontSize, int fontStyle) {
        FontSelector fs = new FontSelector();
        for (Map.Entry<com.itextpdf.text.Font, String> fontEntry : SPARE_FONT.entrySet()) {
            fs.addFont(fontEntry.getKey());
        }
        Phrase process = fs.process(text);
        List<Chunk> chunks = process.getChunks();
        Map<String, Font> stringFontMap = new HashMap<>();
        for (Chunk chunk : chunks) {
            String content = chunk.getContent();
            for (int i = 0; i < content.codePointCount(0, content.length()); i++) {
                String fontName = SPARE_FONT.get(chunk.getFont());
                stringFontMap.put(StrUtil.subString(content, i, i + 1), new Font(fontName, fontStyle, fontSize));
            }
        }
        return stringFontMap;
    }

    /**
     * 字体长度匹配字体大小
     *
     * @param textLength 字体实际长度
     * @return 适合大小
     */
    public static int getFitFontSize(int textLength) {
        int fontSize;
        if (textLength <= 23) {
            fontSize = 26;
        } else if (textLength <= 27) {
            fontSize = 25;
        } else if (textLength <= 28) {
            fontSize = 24;
        } else if (textLength <= 30) {
            fontSize = 23;
        } else if (textLength <= 32) {
            fontSize = 22;
        } else if (textLength <= 35) {
            fontSize = 21;
        } else if (textLength <= 38) {
            fontSize = 20;
        } else if (textLength <= 41) {
            fontSize = 19;
        } else if (textLength <= 44) {
            fontSize = 18;
        } else if (textLength <= 47) {
            fontSize = 17;
        } else {
            fontSize = 5;
        }
        return fontSize;
    }

    /**
     * 垂直二字
     * +-----+
     * |  A  |
     * +-----+
     * |  A  |
     * +-----+
     *
     * @param top    二字上，单字图像
     * @param bottom 二字下，单字图像
     * @return 图像
     */
    private static BufferedImage drawTwoVerticalString(BufferedImage top, BufferedImage bottom) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE / 2, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(top, 0, (int) (0 + IMAGE_SIZE * 0.04), IMAGE_SIZE / 2, IMAGE_SIZE / 2, null);
        graphics.drawImage(bottom, 0, IMAGE_SIZE / 2 - (int) (IMAGE_SIZE * 0.04), IMAGE_SIZE / 2, IMAGE_SIZE / 2, null);
        graphics.dispose();
        return bufferedImage;
    }


    /**
     * 画平行二字
     * +-----+-----+
     * |  A  |  A  |
     * +-----+-----+
     *
     * @param left  左字
     * @param right 右字
     * @return 图像
     */
    public static BufferedImage drawTwoHorizonString(String left, String right) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(drawOneStringHeightImageSizeHalf(left), (int) (0 + IMAGE_SIZE * 0.033), 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.drawImage(drawOneStringHeightImageSizeHalf(right), (int) (IMAGE_SIZE / 2 - IMAGE_SIZE * 0.033), 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 画垂直三字
     * +-----+
     * |  A  |
     * +-----+
     * |  A  |
     * +-----+
     * |  A  |
     * +-----+
     *
     * @param top    三字上，单字图像
     * @param middle 三字中，单字图像
     * @param bottom 三字下，单字图像
     * @return 图像
     */
    private static BufferedImage drawThreeVerticalString(BufferedImage top, BufferedImage middle, BufferedImage bottom) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE / 2, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(top, 0, IMAGE_SIZE / 3 * 0, IMAGE_SIZE / 2, IMAGE_SIZE / 3, null);
        graphics.drawImage(middle, 0, IMAGE_SIZE / 3 * 1, IMAGE_SIZE / 2, IMAGE_SIZE / 3, null);
        graphics.drawImage(bottom, 0, IMAGE_SIZE / 3 * 2, IMAGE_SIZE / 2, IMAGE_SIZE / 3, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 三字，二字在左
     * +-----+-----+
     * |  A  |     |
     * +-----|  A  |
     * |  A  |     |
     * +-----+-----+
     *
     * @param leftTop    左上字
     * @param leftBottom 左下字
     * @param right      右字
     * @return 图像
     */
    public static BufferedImage drawThreeHorizonTwoLeftString(String leftTop, String leftBottom, String right) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(drawTwoVerticalString(drawOneStringHeightImageSizeHalf(leftTop), drawOneStringHeightImageSizeHalf(leftBottom)), (int) (0 + IMAGE_SIZE * 0.02), 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.drawImage(drawOneStringHeight300(right), IMAGE_SIZE / 2 - (int) (IMAGE_SIZE * 0.02), (int) (0 - IMAGE_SIZE * 0.02), IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 三字，二字在右
     * +-----+-----+
     * |     |  A  |
     * +  A  |-----+
     * |     |  A  |
     * +-----+-----+
     *
     * @param rightTop    右上
     * @param rightBottom 右下
     * @param left        左
     * @return 图像
     */
    public static BufferedImage drawThreeHorizonTwoRightString(String rightTop, String rightBottom, String left) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(drawOneStringHeight300(left), (int) (0 + IMAGE_SIZE * 0.02), (int) (0 - IMAGE_SIZE * 0.02), IMAGE_SIZE / 2 + 20, IMAGE_SIZE + 10, null);
        graphics.drawImage(drawTwoVerticalString(drawOneStringHeightImageSizeHalf(rightTop), drawOneStringHeightImageSizeHalf(rightBottom)), IMAGE_SIZE / 2 - (int) (IMAGE_SIZE * 0.02), 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 画四字
     * +-----+-----+
     * |  A  |  A  |
     * +-----+-----+
     * |  A  |  A  |
     * +-----+-----+
     *
     * @param leftTop     左上
     * @param leftBottom  左下
     * @param rightTop    右上
     * @param rightBottom 右下
     * @return 图像
     */
    public static BufferedImage drawFourString(String leftTop, String leftBottom, String rightTop, String rightBottom) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(drawTwoVerticalString(drawOneStringHeightImageSizeHalf(leftTop), drawOneStringHeightImageSizeHalf(leftBottom)), (int) (0 + IMAGE_SIZE * 0.033), 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.drawImage(drawTwoVerticalString(drawOneStringHeightImageSizeHalf(rightTop), drawOneStringHeightImageSizeHalf(rightBottom)), IMAGE_SIZE / 2 - (int) (0 + IMAGE_SIZE * 0.033), 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 五字，二字在左，等比例字体
     * +-----+-----+
     * |     |  A  |
     * +  A  +-----+
     * |-----|  A  |
     * +  A  +-----+
     * |     |  A  |
     * +-----+-----+
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
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(drawThreeVerticalString(drawOneStringHeightImageSizeOneThirds(topThree), drawOneStringHeightImageSizeOneThirds(middleThree), drawOneStringHeightImageSizeOneThirds(bottomThree)), IMAGE_SIZE / 2, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.drawImage(drawTwoVerticalString(drawOneStringHeightImageSizeHalf(topTwo), drawOneStringHeightImageSizeHalf(bottomTwo)), 0, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 五字，二字在左，非等比例字体
     * +-----+-----+
     * |     |  A  |
     * +  A  +-----+
     * |-----|  A  |
     * +  A  +-----+
     * |     |  A  |
     * +-----+-----+
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
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(drawThreeVerticalString(imageStretch(drawOneStringHeightImageSizeHalf(topThree), IMAGE_SIZE / 2, IMAGE_SIZE / 3), imageStretch(drawOneStringHeightImageSizeHalf(middleThree), IMAGE_SIZE / 2, IMAGE_SIZE / 3), imageStretch(drawOneStringHeightImageSizeHalf(bottomThree), IMAGE_SIZE / 2, IMAGE_SIZE / 3)), IMAGE_SIZE / 2, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.drawImage(drawTwoVerticalString(drawOneStringHeightImageSizeHalf(topTwo), drawOneStringHeightImageSizeHalf(bottomTwo)), 0, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 五字，二字在右，等比例字体
     * +-----+-----+
     * |  A  |     |
     * +-----+  A  +
     * |  A  |-----|
     * +-----+  A  +
     * |  A  |     |
     * +-----+-----+
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
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(drawThreeVerticalString(drawOneStringHeightImageSizeOneThirds(topThree), drawOneStringHeightImageSizeOneThirds(middleThree), drawOneStringHeightImageSizeOneThirds(bottomThree)), 0, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.drawImage(drawTwoVerticalString(drawOneStringHeightImageSizeHalf(topTwo), drawOneStringHeightImageSizeHalf(bottomTwo)), IMAGE_SIZE / 2, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 五字，二字在右，非等比例字体
     * +-----+-----+
     * |  A  |     |
     * +-----+  A  +
     * |  A  |-----|
     * +-----+  A  +
     * |  A  |     |
     * +-----+-----+
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
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(drawThreeVerticalString(imageStretch(drawOneStringHeightImageSizeHalf(topThree), IMAGE_SIZE / 2, IMAGE_SIZE / 3), imageStretch(drawOneStringHeightImageSizeOneThirds(middleThree), IMAGE_SIZE / 2, IMAGE_SIZE / 3), imageStretch(drawOneStringHeightImageSizeOneThirds(bottomThree), IMAGE_SIZE / 2, IMAGE_SIZE / 3)), 0, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.drawImage(drawTwoVerticalString(drawOneStringHeightImageSizeHalf(topTwo), drawOneStringHeightImageSizeHalf(bottomTwo)), IMAGE_SIZE / 2, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 六字，非等比例字体
     * +-----+-----+
     * |  A  |  A  |
     * +-----+-----+
     * |  A  |  A  |
     * +-----+-----+
     * |  A  |  A  |
     * +-----+-----+
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
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(drawThreeVerticalString(imageStretch(drawOneStringHeightImageSizeHalf(topLeft), IMAGE_SIZE / 2, IMAGE_SIZE / 3), imageStretch(drawOneStringHeightImageSizeHalf(middleLeft), IMAGE_SIZE / 2, IMAGE_SIZE / 3), imageStretch(drawOneStringHeightImageSizeHalf(bottomLeft), IMAGE_SIZE / 2, IMAGE_SIZE / 3)), 0, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.drawImage(drawThreeVerticalString(imageStretch(drawOneStringHeightImageSizeHalf(topRight), IMAGE_SIZE / 2, IMAGE_SIZE / 3), imageStretch(drawOneStringHeightImageSizeHalf(middleRight), IMAGE_SIZE / 2, IMAGE_SIZE / 3), imageStretch(drawOneStringHeightImageSizeHalf(bottomRight), IMAGE_SIZE / 2, IMAGE_SIZE / 3)), IMAGE_SIZE / 2, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 六字，等比例字体
     * +-----+-----+
     * |  A  |  A  |
     * +-----+-----+
     * |  A  |  A  |
     * +-----+-----+
     * |  A  |  A  |
     * +-----+-----+
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
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 颜色
        graphics.setPaint(COLOR);
        graphics.drawImage(drawThreeVerticalString(drawOneStringHeightImageSizeOneThirds(topLeft), drawOneStringHeightImageSizeOneThirds(middleLeft), drawOneStringHeightImageSizeOneThirds(bottomLeft)), 0, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.drawImage(drawThreeVerticalString(drawOneStringHeightImageSizeOneThirds(topRight), drawOneStringHeightImageSizeOneThirds(middleRight), drawOneStringHeightImageSizeOneThirds(bottomRight)), IMAGE_SIZE / 2, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 画单字，高度为IMAGE_SIZE，宽度为IMAGE_SIZE / 2
     * +-----+
     * |  A  |
     * +-----+
     *
     * @param str 字
     * @return 图像
     */
    public static BufferedImage drawOneStringHeight300(String str) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE / 2, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 画字
        graphics.drawImage(drawOneStringHeightImageSizeHalf(str), 0, 0, IMAGE_SIZE / 2, IMAGE_SIZE, null);
        // 释放
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 画单字，高度为IMAGE_SIZE / 2，宽度为IMAGE_SIZE / 2
     * +-----+
     * |  A  |
     * +-----+
     *
     * @param str 字
     * @return 图像
     */
    public static BufferedImage drawOneStringHeightImageSizeHalf(String str) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE / 2, IMAGE_SIZE / 2, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Map<String, Font> fitFont = getFitFont(str, (int) (IMAGE_SIZE * 0.4), Font.BOLD);
        // 字体
        graphics.setFont(fitFont.get(str));
        // 颜色
        graphics.setPaint(COLOR);
        // 画字
        graphics.drawString(str, (int) (IMAGE_SIZE * 0.05), (int) (IMAGE_SIZE * 0.4));
        // 释放
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 画单字，高度为IMAGE_SIZE / 3，宽度为IMAGE_SIZE / 2
     * +-----+
     * |  A  |
     * +-----+
     *
     * @param str 字
     * @return 图像
     */
    public static BufferedImage drawOneStringHeightImageSizeOneThirds(String str) {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(IMAGE_SIZE / 2, IMAGE_SIZE / 3, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Map<String, Font> fitFont = getFitFont(str, (int) (IMAGE_SIZE * 0.266), Font.BOLD);
        // 字体
        graphics.setFont(fitFont.get(str));
        // 颜色
        graphics.setPaint(COLOR);
        // 画字
        graphics.drawString(str, (int) (IMAGE_SIZE * 0.116), (int) (IMAGE_SIZE * 0.266));
        // 释放
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

    /**
     * 画矩形外线（IMAGE_SIZE）
     *
     * @param bufferedImage 原始图像
     * @return 图像
     */
    public static BufferedImage drawSquareOrder(BufferedImage bufferedImage) {
        BufferedImage bufferedImage1 = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, bufferedImage.getType());
        Graphics2D graphics1 = bufferedImage1.createGraphics();
        graphics1.setPaint(COLOR);
        graphics1.drawImage(bufferedImage, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
        graphics1.setStroke(new BasicStroke((float) (IMAGE_SIZE * 0.053)));
        graphics1.drawRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        graphics1.dispose();
        return bufferedImage1;
    }

    /**
     * 画矩形外线（自适应）
     *
     * @param bufferedImage 原始图像
     * @return 图像
     */
    public static BufferedImage drawSquareOrderSelfAdaption(BufferedImage bufferedImage) {
        BufferedImage bufferedImage1 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        Graphics2D graphics1 = bufferedImage1.createGraphics();
        graphics1.setPaint(COLOR);
        graphics1.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics1.setStroke(new BasicStroke((float) (bufferedImage.getHeight() * 0.053)));
        graphics1.drawRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics1.dispose();
        return bufferedImage1;
    }

    /**
     * 横向拼接图片
     *
     * @param img1  左
     * @param img2 右
     * @return 拼接后
     */
    public static BufferedImage transverseSplicingImage(BufferedImage img1, BufferedImage img2) {
        if (img1 == null && img2 != null) {
            return img2;
        }
        // 确认两个图片的高度相同，如果不同则抛出异常或处理
        if (img1.getHeight() != img2.getHeight()) {
            throw new IllegalArgumentException("Images must have the same height to concatenate them horizontally.");
        }

        // 设置偏移量，右侧图片向左移动offsetX个像素
        int offsetX = 25;

        // 创建一个新的BufferedImage对象，宽度是两个图片宽度的总和减去偏移量，高度保持一致
        int width = img1.getWidth() + img2.getWidth() - offsetX;
        int height = img1.getHeight();
        BufferedImage concatenatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 获取图形上下文
        Graphics g = concatenatedImage.getGraphics();

        // 将第一个图片绘制到新图像的左侧
        g.drawImage(img1, 0, 0, null);

        // 将第二个图片绘制到新图像的偏移位置
        g.drawImage(img2, img1.getWidth() - offsetX, 0, null);

        // 释放图形上下文
        g.dispose();
        return concatenatedImage;
    }
}
