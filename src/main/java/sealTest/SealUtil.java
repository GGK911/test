package sealTest;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.GrayColor;

import javax.imageio.ImageIO;
import java.awt.*;
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
 * 印章工具类
 */
public class SealUtil {

    // 默认从10x10的位置开始画，防止左上部分画布装不下
    private final static int INIT_BEGIN = 10;

    // 默认印章图片大小
    private final static int IMAGE_SIZE = 300;

    // 画笔颜色
    private final static Color COLOR = Color.RED;

    // 个人印模字体大小
    private final static int PERSON_FONT_SIZE = 120;

    // 个人印模边线宽度
    private final static int LINE_SIZE = 16;

    //印模字体类型
    private final static String FONT_FAMILY = "宋体";

    //印模字体大小
    private final static int FONT_SIZE = 120;

    // 企业印模边线圆半径
    private final static int BORDER_CIRCLE_SIZE = IMAGE_SIZE / 2 - 73;

    // 企业印模中心文字
    private final static String CENTER_FONT = "★";

    // 企业印模主文字圆边距
    private final static int MAIN_FONT_MARGIN_SIZE = 2;

    // 企业印模主文字字间距
    private final static double MAIN_FONT_SPACE_SIZE = 20.0;

    // 基础宋体打底
    private final static BaseFont BASE_FONT;

    // 备用字体
    private final static Map<BaseFont, String> SPARE_FONT = new HashMap<>();

    static {
        BASE_FONT = FontFactory.getFont("src/main/resources/font/SIMSUN.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 12f, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK).getBaseFont();

        GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();

        Font spareFont1 = null;
        try {
            spareFont1 = Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("font/JinbiaoSong.TTF"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        try {
            genv.registerFont(spareFont1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        BaseFont baseFont1 = FontFactory.getFont("src/main/resources/font/JinbiaoSong.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 12f, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK).getBaseFont();
        if (spareFont1 != null) {
            SPARE_FONT.put(baseFont1, spareFont1.getName());
        }

        Font spareFont2 = null;
        try {
            spareFont2 = Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("font/simsunb.ttf"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        try {
            genv.registerFont(spareFont2);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        BaseFont baseFont2 = FontFactory.getFont("src/main/resources/font/simsunb.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 12f, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK).getBaseFont();
        if (spareFont2 != null) {
            SPARE_FONT.put(baseFont2, spareFont2.getName());
        }

    }

    /**
     * 生成企业印章图片
     *
     * @param sealName 印章图片名称
     */
    public static byte[] buildSeal(String sealName) {
        SealConfiguration conf = getDefaultConfig(sealName);
        //1.画布
        BufferedImage bi = new BufferedImage(170, 170, BufferedImage.TYPE_4BYTE_ABGR);
        //2.画笔
        Graphics2D g2d = bi.createGraphics();
        //2.1抗锯齿设置,文本不抗锯齿，圆中心的文字会被拉长
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        //其他图形抗锯齿
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(hints);
        //2.2设置背景透明度
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0));
        //2.3填充矩形
        g2d.fillRect(0, 0, 170, 170);
        //2.4重设透明度，开始画图
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1));
        //2.5设置画笔颜色
        g2d.setPaint(COLOR);
        //3.画边线圆
        drawCicle(g2d, conf.getBorderCircle(), INIT_BEGIN, INIT_BEGIN);

        int borderCircleWidth = conf.getBorderCircle().getWidth();
        int borderCircleHeight = conf.getBorderCircle().getHeight();

        //4.画内边线圆
        if (conf.getBorderInnerCircle() != null) {
            int x = INIT_BEGIN + borderCircleWidth - conf.getBorderInnerCircle().getWidth();
            int y = INIT_BEGIN + borderCircleHeight - conf.getBorderInnerCircle().getHeight();
            drawCicle(g2d, conf.getBorderInnerCircle(), x, y);
        }

        //6.画弧形主文字
//        drawArcFont4Circle(g2d, conf.getMainFont(), true);
        drawSealName(g2d, conf.getMainFont());

        //8.画中心字
        drawFont(g2d, (borderCircleWidth + INIT_BEGIN) * 2, (borderCircleHeight + INIT_BEGIN) * 2, conf.getCenterFont());

        g2d.dispose();
        return buildBytes(bi);
    }

    /**
     * 生成私人印章图片
     *
     * @param name 印章图片名称
     */
    public static byte[] buildPersonSeal(String name, int twoFixH, int threeFixH, int threeFixW, int threeMarginH, int offsetW) {
        //获取配置信息
        SealFont font = getPersonDefault(name);

        //1.画布
        BufferedImage bi = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE / 2, BufferedImage.TYPE_4BYTE_ABGR);

        //2.画笔
        Graphics2D g2d = bi.createGraphics();

        //2.1设置画笔颜色
        g2d.setPaint(COLOR);

        //2.2抗锯齿设置
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //3.写签名
        int marginW = INIT_BEGIN;
        float marginH;
        FontRenderContext context = g2d.getFontRenderContext();
        Rectangle2D rectangle;
        Font f = null;
        // 找到所有text合适字体
        if (font.getFontText().codePointCount(0, font.getFontText().length()) == 2) {
            f = new Font(font.getFontFamily(), Font.BOLD, PERSON_FONT_SIZE);
            g2d.setFont(f);
            rectangle = f.getStringBounds(subString(font.getFontText(), 0, 1), context);
            marginH = (float) (Math.abs(rectangle.getCenterY()) * 2 + marginW) + twoFixH;
            Map<String, Font> fitFont = getFitFont(name, PERSON_FONT_SIZE);
            // 第一个字
            String firstText = subString(font.getFontText(), 0, 1);
            Font firstFont = fitFont.get(firstText);
            g2d.setFont(firstFont);
            g2d.drawString(firstText, marginW + offsetW, marginH);

            marginW += Math.abs(rectangle.getCenterX()) * 2 + (font.getFontSpace() == null ? INIT_BEGIN : font.getFontSpace());

            // 第二个字
            String secondText = subString(font.getFontText(), 1, 2);
            Font secondFont = fitFont.get(secondText);
            g2d.setFont(secondFont);
            g2d.drawString(secondText, marginW + offsetW, marginH);
            //拉伸 画正方形
            bi = getBI(bi);
        } else if (font.getFontText().codePointCount(0, font.getFontText().length()) == 3) {
            bi = drawThreeFont(bi, g2d, font.setFontText(font.getFontText()), twoFixH, threeFixH, threeFixW, threeMarginH);
        } else if (font.getFontText().codePointCount(0, font.getFontText().length()) == 4) {
            bi = drawFourFont(bi, font, twoFixH);
        }
        g2d.setFont(f);
        if (f != null) {
            rectangle = f.getStringBounds(subString(font.getFontText(), 0, 1), context);
            marginH = (float) (Math.abs(rectangle.getCenterY()) * 2 + twoFixH) + twoFixH - 28 + name.length() * 2;
            marginW += 8;
            g2d.drawString(subString(font.getFontText(), 0, 1), marginW, marginH);
        }

        //拉伸 画正方形
        bi = getBI(bi);

        return buildBytes(bi);
    }

    /**
     * 生成时间印章图片
     *
     * @param sealName 印章图片名称
     */
    public static byte[] buildDateSeal(String sealName) {
        //获取印章配置信息
        SealFont font = getPersonDefault(sealName);
        int fixH = 10;

        //1.画布
        BufferedImage bi = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE / 2, BufferedImage.TYPE_4BYTE_ABGR);
        //2.画笔
        Graphics2D g2d = bi.createGraphics();
        //2.1设置画笔颜色
        g2d.setPaint(Color.BLACK);
        //2.2抗锯齿设置
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        float marginW = INIT_BEGIN + 5;
        float marginH;
        FontRenderContext context = g2d.getFontRenderContext();
        Rectangle2D rectangle;
        Font f = new Font(font.getFontFamily(), Font.BOLD, 36);
        g2d.setFont(f);
        rectangle = f.getStringBounds(subString(font.getFontText(), 0, 1), context);
        marginH = (float) (Math.abs(rectangle.getCenterY()) * 2 + marginW) + fixH - 2;
        g2d.drawString(subString(font.getFontText(), 0, 1), marginW, marginH);

        for (int i = 1; i < sealName.codePointCount(0, sealName.length()); i++) {
            marginW += 20;
            if (i == 5 || i == 8) {
                marginW += 15;
                g2d.drawString(subString(font.getFontText(), i, i + 1), marginW, marginH);
            } else {
                g2d.drawString(subString(font.getFontText(), i, i + 1), marginW, marginH);
            }
        }

        //拉伸
        BufferedImage nbi = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE / 2, bi.getType());
        Graphics2D ng2d = nbi.createGraphics();
        ng2d.setPaint(Color.WHITE);
        ng2d.drawImage(bi, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);

        bi = nbi;
        return buildBytes(bi);
    }

    /**
     * 画三字
     *
     * @param bi   图片
     * @param g2d  原画笔
     * @param font 字体对象
     * @param fixH 修复膏
     */
    private static BufferedImage drawThreeFont(BufferedImage bi, Graphics2D g2d, SealFont font, int fixH, int threeFixH, int threeFixW, int threeMarginH) {
        fixH -= threeFixH;
        int marginW = threeFixW + LINE_SIZE;

        //设置字体
        Font f = new Font(font.getFontFamily(), Font.BOLD, font.getFontSize());
        g2d.setFont(f);

        FontRenderContext context = g2d.getFontRenderContext();
        Rectangle2D rectangle = f.getStringBounds(subString(font.getFontText(), 0, 1), context);
        float marginH = (float) (Math.abs(rectangle.getCenterY()) * 2 + marginW) + fixH;
        int oldW = marginW;

        marginW += rectangle.getCenterX() * 2 + (font.getFontSpace() == null ? INIT_BEGIN : font.getFontSpace());
        Map<String, Font> fitFont = getFitFont(font.getFontText(), PERSON_FONT_SIZE);
        // 第一个字
        String firstText = subString(font.getFontText(), 0, 1);
        Font firstFont = fitFont.get(firstText);
        g2d.setFont(firstFont);
        g2d.drawString(subString(font.getFontText(), 0, 1), marginW, marginH);

        //拉伸 画正方形
        bi = getBI(bi);

        g2d = bi.createGraphics();
        g2d.setPaint(COLOR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 第二个字
        String secondText = subString(font.getFontText(), 1, 2);
        Font secondFont = fitFont.get(secondText);
        g2d.setFont(secondFont);
        g2d.drawString(subString(font.getFontText(), 1, 2), oldW, marginH + fixH - threeMarginH);

        rectangle = f.getStringBounds(font.getFontText(), context);
        marginH += Math.abs(rectangle.getHeight());
        // 第三个字
        String thirdText = subString(font.getFontText(), 2, 3);
        Font thirdFont = fitFont.get(thirdText);
        g2d.setFont(thirdFont);
        g2d.drawString(subString(font.getFontText(), 2, 3), oldW, marginH);

        return bi;
    }

    /**
     * 画四字
     *
     * @param bi   图片
     * @param font 字体对象
     * @param fixH 修复膏
     */
    private static BufferedImage drawFourFont(BufferedImage bi, SealFont font, int fixH) {
        int offsetW = fixH == 40 ? 3 : 0;
        int offsetH = fixH == 40 ? 4 : 0;
        int marginW = LINE_SIZE + (fixH == 40 ? 0 : 2);
        //拉伸 画正方形
        bi = getBI(bi);

        Graphics2D g2d = bi.createGraphics();
        g2d.setPaint(COLOR);
        //抗齿距
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontRenderContext context = g2d.getFontRenderContext();//获取g2d的上下文
        //设置字体
        Font f = new Font(font.getFontFamily(), Font.BOLD, PERSON_FONT_SIZE);
        g2d.setFont(f);
        Rectangle2D rectangle = f.getStringBounds(subString(font.getFontText(), 0, 1), context);//上坡度
        float marginH = (float) (Math.abs(rectangle.getCenterY()) * 2 + marginW) + fixH;
        Map<String, Font> fitFont = getFitFont(font.getFontText(), PERSON_FONT_SIZE);
        // 第三个字
        String thirdText = subString(font.getFontText(), 2, 3);
        Font thirdFont = fitFont.get(thirdText);
        g2d.setFont(thirdFont);
        g2d.drawString(subString(font.getFontText(), 2, 3), marginW + offsetW, marginH);
        int oldW = marginW;
        marginW += Math.abs(rectangle.getCenterX()) * 2 + (font.getFontSpace() == null ? INIT_BEGIN : font.getFontSpace());
        // 第一个字
        String firstText = subString(font.getFontText(), 0, 1);
        Font firstFont = fitFont.get(firstText);
        g2d.setFont(firstFont);
        g2d.drawString(subString(font.getFontText(), 0, 1), marginW + offsetW, marginH);
        marginH += Math.abs(rectangle.getHeight());
        // 第四个字
        String fourText = subString(font.getFontText(), 3, 4);
        Font fourFont = fitFont.get(fourText);
        g2d.setFont(fourFont);
        g2d.drawString(subString(font.getFontText(), 3, 4), oldW + offsetW, marginH - offsetH);
        // 第二个字
        String secondText = subString(font.getFontText(), 1, 2);
        Font secondFont = fitFont.get(secondText);
        g2d.setFont(secondFont);
        g2d.drawString(subString(font.getFontText(), 1, 2), marginW + offsetW, marginH - offsetH);
        return bi;
    }

    /**
     * 圆形印章文字拉伸
     *
     * @param g2d      g2d对象
     * @param mainFont 主文字
     */
    private static void drawSealName(Graphics2D g2d, SealFont mainFont) {
        Font f;
        FontRenderContext context;
        Rectangle2D bounds;
        String sealName = mainFont.getFontText();
        int fontSize = mainFont.getFontSize();
        double scaleSize = 2;

        // 距离圆圈边界值  8
        int topFix = 12;
        double circleRadius = IMAGE_SIZE / 3.4;
        int fontStyle = mainFont.isBold() == 1 ? Font.BOLD : Font.PLAIN;

        // f = new Font(baseFont.getFamilyFontName()[0][3], fontStyle, fontSize);
        f = new Font("宋体", fontStyle, fontSize);

        context = g2d.getFontRenderContext();
        bounds = f.getStringBounds(sealName, context);
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        topFix = topFix + Convert.toInt(height / scaleSize * 2.0);
        int count = sealName.codePointCount(0, sealName.length());


        double interval;   // 调节字体间距
        if (sealName.length() < 9) {
            interval = width / (count - 1) * 1.45;
        } else if (sealName.length() < 12) {
            interval = width / (count - 1) * 1.25;
        } else if (sealName.length() < 15) {
            interval = width / (count - 1) * 0.95;
        } else if (sealName.length() < 18) {
            interval = width / (count - 1) * 0.85;
        } else {
            interval = width / (count - 1) * 0.75;
        }


        double radis = circleRadius + bounds.getY() - topFix;
        double radianInterval = 2 * Math.asin(interval / (2 * radis));
        double firstAngle;
//        if (count % 2 == 1) {
//            firstAngle = (count - 1) * radianInterval / 2.0 + Math.PI / 2 + 0.25;
//        } else {
//            firstAngle = (count / 2.0 - 1) * radianInterval + radianInterval / 2.0 + Math.PI / 2 + 0.25;
//        }

        // 第一个字起始位置
        double fix = 0.18;
        if (count % 2 == 1) {
            firstAngle = (count - 1) * radianInterval / 2.0 + Math.PI / 2 + fix;
        } else {
            firstAngle = (count / 2.0 - 1) * radianInterval + radianInterval / 2.0 + Math.PI / 2 + fix;
        }
//        if (fontTextLen % 2 == 1) {
//            firstAngle = (fontTextLen - 1) * radianPerInterval / 2.0 + Math.PI / 2 + fix;
//        } else {
//            firstAngle = (fontTextLen / 2.0 - 0.5) * radianPerInterval + Math.PI / 2 + fix;
//        }

        // 字体旋转倾斜度      数值越大顺时针方向旋转度越高
        double gradient = 0.2;
        // 字体间是否靠近度    数值越大越靠近
        double distance = 0.9;
        Map<String, Font> fitFont = getFitFont(sealName, fontSize);
        for (int i = 0; i < count; i++) {
            double aa = firstAngle - i * radianInterval;
            double ax = radis * Math.sin(Math.PI / 2 - aa);
            double ay = radis * Math.cos(aa - Math.PI / 2);
//            AffineTransform transform = AffineTransform.getRotateInstance(Math.PI / 2 - aa + 0.3);
//            AffineTransform transform = AffineTransform.getRotateInstance(Math.PI / 2 - aa + Math.toRadians(8));
            AffineTransform transform = AffineTransform.getRotateInstance(Math.PI / 2 - aa + gradient);
//            AffineTransform scaleform = AffineTransform.getScaleInstance(1, scaleSize);
            AffineTransform scaleform = AffineTransform.getScaleInstance(distance, scaleSize);

            // 防止文本和外圈间距
            transform.concatenate(scaleform);

            // 字体更换
            String text = subString(sealName, i, i + 1);
            f = fitFont.get(text);

            Font f2 = f.deriveFont(transform);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);//设置抗锯齿
            g2d.setFont(f2);
//            g2d.setPaint(new Color(0, 0, 0, 64));
//            g2d.drawString(sealName.substring(i, i + 1), (float) (circleRadius + ax), (float) (circleRadius - ay));
            g2d.setPaint(COLOR);
            g2d.drawString(subString(sealName, i, i + 1), (float) (circleRadius + ax), (float) (circleRadius - ay));
        }
    }

    /**
     * 绘制圆弧形文字
     *
     * @param g2d   画笔
     * @param font  字体对象
     * @param isTop 是否字体在上部，否则在下部
     */
    private static void drawArcFont4Circle(Graphics2D g2d, SealFont font, boolean isTop) {
        //字体长度
        // int fontTextLen = font.getFontText().length();
        int fontTextLen = font.getFontText().codePointCount(0, font.getFontText().length());

        //构造字体
        Font f = getFont(font);

        FontRenderContext context = g2d.getFontRenderContext();
        Rectangle2D rectangle = f.getStringBounds(font.getFontText(), context);

        double fontSpace = 0.0;
        if (isTop) {
            //5.文字之间间距，默认动态调整
            if (font.getFontSpace() != null) {
                if (fontTextLen == 1) {
                    fontSpace = 0;
                } else {
                    fontSpace = rectangle.getWidth() / (fontTextLen - 1) * 0.9;
                }
            }
        } else {
            fontSpace = font.getFontSpace();
        }

        //6.距离外圈距离
        int marginSize = font.getMarginSize() == null ? INIT_BEGIN : font.getMarginSize();

        //7.写字
        double newRadius = BORDER_CIRCLE_SIZE + rectangle.getY() - marginSize;
        //asin() 方法用于返回指定double类型参数的反正弦值
        double radianPerInterval = 2 * Math.asin(fontSpace / (2 * newRadius));

        double fix = 0.04;
        if (isTop) {
            fix = 0.18;
        }
        double firstAngle;
        if (!isTop) {
            if (fontTextLen % 2 == 1) {
                firstAngle = Math.PI + Math.PI / 2 - (fontTextLen - 1) * radianPerInterval / 2.0 - fix;
            } else {
                firstAngle = Math.PI + Math.PI / 2 - ((fontTextLen / 2.0 - 0.5) * radianPerInterval) - fix;
            }
        } else {
            if (fontTextLen % 2 == 1) {
                firstAngle = (fontTextLen - 1) * radianPerInterval / 2.0 + Math.PI / 2 + fix;
            } else {
                firstAngle = (fontTextLen / 2.0 - 0.5) * radianPerInterval + Math.PI / 2 + fix;
            }
        }

        for (int i = 0; i < fontTextLen; i++) {
            double theta;
            double thetaX;
            double thetaY;

            if (!isTop) {
                theta = firstAngle + i * radianPerInterval;
                thetaX = newRadius * Math.sin(Math.PI / 2 - theta);
                thetaY = newRadius * Math.cos(theta - Math.PI / 2);
            } else {
                theta = firstAngle - i * radianPerInterval;
                thetaX = newRadius * Math.sin(Math.PI / 2 - theta);
                thetaY = newRadius * Math.cos(theta - Math.PI / 2);
            }

            //表示正向旋转的度数
            AffineTransform transform;
            if (!isTop) {
                transform = AffineTransform.getRotateInstance(Math.PI + Math.PI / 2 - theta);
            } else {
                transform = AffineTransform.getRotateInstance(Math.PI / 2 - theta + Math.toRadians(8));
            }
            //复制当前 Font 对象并应用新的变换，创建一个新 Font 对象
            Font f2 = f.deriveFont(transform);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);//设置抗锯齿
//            Font f2 = f.deriveFont(1, transform);
            g2d.setFont(f2);
            g2d.drawString(subString(font.getFontText(), i, i + 1), (float) (BORDER_CIRCLE_SIZE + thetaX + INIT_BEGIN),
                    (float) (BORDER_CIRCLE_SIZE - thetaY + INIT_BEGIN));
        }
    }

    /**
     * 画文字
     *
     * @param g2d          画笔
     * @param circleWidth  边线圆宽度
     * @param circleHeight 边线圆高度
     * @param font         字体对象
     */
    private static void drawFont(Graphics2D g2d, int circleWidth, int circleHeight, SealFont font) {
        //构造字体
        Font f = getFont(font);
        g2d.setFont(f);

        FontRenderContext context = g2d.getFontRenderContext();
        String[] fontTexts = font.getFontText().split("\n");
        if (fontTexts.length > 1) {
            int y = 0;
            for (String fontText : fontTexts) {
                y += Math.abs(f.getStringBounds(fontText, context).getHeight());
            }
            //5.设置上边距
            float marginSize = INIT_BEGIN + (float) (circleHeight / 2 - y / 2);
            for (String fontText : fontTexts) {
                Rectangle2D rectangle2D = f.getStringBounds(fontText, context);
                g2d.drawString(fontText, (float) (circleWidth / 2 - rectangle2D.getCenterX() + 1), marginSize);
                marginSize += Math.abs(rectangle2D.getHeight());
            }
        } else {
            Rectangle2D rectangle2D = f.getStringBounds(font.getFontText(), context);
            //5.设置上边距，默认在中心
            float marginSize = font.getMarginSize() == null ?
                    (float) (circleHeight / 2 - rectangle2D.getCenterY()) :
                    (float) (circleHeight / 2 - rectangle2D.getCenterY()) + (float) font.getMarginSize();
            g2d.drawString(font.getFontText(), (float) (circleWidth / 2 - rectangle2D.getCenterX() + 1), marginSize);
        }
    }

    /**
     * 画圆
     *
     * @param g2d    画笔
     * @param circle 圆配置对象
     */
    private static void drawCicle(Graphics2D g2d, SealCircle circle, int x, int y) {
        if (circle == null) {
            return;
        }
        //1.圆线条粗细默认是圆直径的1/35
        int lineSize = circle.getLineSize() == null ? circle.getHeight() * 2 / 35 : circle.getLineSize();
        //2.画圆
        g2d.setStroke(new BasicStroke(lineSize));
        g2d.drawOval(x, y, circle.getWidth() * 2, circle.getHeight() * 2);
    }

    /**
     * 构造字体
     */
    private static Font getFont(SealFont font) {
        //1.字体长度
        int fontTextLen = font.getFontText().length();
        //2.字体大小，默认根据字体长度动态设定
        int fontSize = font.getFontSize() == null ? 29 + (10 - fontTextLen) / 2 : font.getFontSize();
        //3.字体样式
        int fontStyle = font.isBold() == 1 ? Font.BOLD : Font.PLAIN;
        //4.构造字体
        return new Font(font.getFontFamily(), fontStyle, fontSize);
//        return new Font("黑体", fontStyle, fontSize);
    }

    /**
     * 拉伸 画正方形
     */
    private static BufferedImage getBI(BufferedImage bi) {
        //拉伸
        BufferedImage nbi = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, bi.getType());
        Graphics2D ng2d = nbi.createGraphics();
        ng2d.setPaint(COLOR);
        //按指定大小绘制
        ng2d.drawImage(bi, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
        //画正方形
        ng2d.setStroke(new BasicStroke(LINE_SIZE));
        ng2d.drawRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        //释放资源
        ng2d.dispose();
        bi = nbi;
        return bi;
    }

    /**
     * 企业印模默认配置
     */
    private static SealConfiguration getDefaultConfig(String mainFontText) {
        SealConfiguration configuration = new SealConfiguration();
        SealFont mainFont = new SealFont();
        mainFont.setBold(Font.BOLD);
        mainFont.setFontFamily(FONT_FAMILY);
        mainFont.setMarginSize(MAIN_FONT_MARGIN_SIZE);
        mainFont.setFontText(mainFontText);
//        if (mainFontText.length() < 8) {
//            mainFont.setFontSize(49);
//        } else if (mainFontText.length() < 16) {
//            mainFont.setFontSize(38 - (mainFontText.length() - 7) * 2);
//        }

        int fontSize = 15;
        int textLength = mainFontText.length();
        // drawName
        if (textLength <= 23) {
            fontSize = 15;
        } else if (23 < textLength && textLength <= 27) {
            fontSize = 14;
        } else if (27 < textLength && textLength <= 30) {
            fontSize = 13;
        } else if (30 < textLength && textLength <= 34) {
            fontSize = 12;
        } else if (34 < textLength && textLength <= 38) {
            fontSize = 11;
        } else if (38 < textLength && textLength <= 41) {
            fontSize = 10;
        } else if (41 < textLength && textLength <= 47) {
            fontSize = 9;
        } else if (47 < textLength && textLength <= 62) {
            fontSize = 8;
        } else if (62 < textLength && textLength <= 76) {
            fontSize = 7;
        } else if (76 < textLength && textLength <= 90) {
            fontSize = 6;
        } else {
            fontSize = 5;
        }

        // drawArcFont4Circle 方法
//        if(textLength <= 15) {
//            fontSize = 21;
//        } else if(15 < textLength && textLength <= 17) {
//            fontSize = 19;
//        } else if(17 < textLength && textLength <= 20) {
//            fontSize = 17;
//        } else if(20 < textLength && textLength <= 24) {
//            fontSize = 15;
//        } else if(24 < textLength && textLength <= 28) {
//            fontSize = 13;
//        } else if(28 < textLength && textLength <= 35) {
//            fontSize = 11;
//        } else if(35 < textLength && textLength <= 40) {
//            fontSize = 10;
//        } else if(40 < textLength && textLength <= 45) {
//            fontSize = 9;
//        } else if(45 < textLength && textLength <= 50) {
//            fontSize = 8;
//        } else if(50 < textLength && textLength <= 100) {
//            fontSize = 7;
//            // 边距
//            mainFont.setMarginSize(4);
//        } else {
//            fontSize = 6;
//            mainFont.setMarginSize(4);
//        }

//        System.out.println("字体长度:" + textLength);
//        System.out.println("字体大小:" + fontSize);
        mainFont.setFontSize(fontSize);


        mainFont.setFontSpace(MAIN_FONT_SPACE_SIZE);

        //圆形章中心文字
        SealFont centerFont = new SealFont();
        centerFont.setBold(Font.BOLD);
        centerFont.setFontFamily(FONT_FAMILY);
        centerFont.setFontText(CENTER_FONT);
        centerFont.setFontSize(60);

        configuration.setMainFont(mainFont);
        configuration.setCenterFont(centerFont);
        //圆形印章边线圆
        configuration.setBorderCircle(new SealCircle(null, BORDER_CIRCLE_SIZE, BORDER_CIRCLE_SIZE));

        return configuration;
    }

    /**
     * 个人印模默认配置
     */
    private static SealFont getPersonDefault(String fontText) {
        //获取个人印章字体配置
        SealFont sealFont = new SealFont();
        sealFont.setFontFamily(FONT_FAMILY);
        sealFont.setFontSize(FONT_SIZE);
        sealFont.setBold(Font.BOLD);
        sealFont.setFontText(fontText);
        return sealFont;
    }

    /**
     * 生成印章图片的byte数组
     */
    private static byte[] buildBytes(BufferedImage image) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream.toByteArray();
    }

    /**
     * UTF-16生僻字截取
     * 因其字符下标串定义不同，此方法要遍历字符串
     *
     * @param str        要截取的字符串
     * @param startIndex 我们常识认为的str中第几个字符的下标（下标从0开始，左闭右开）
     * @param endIndex   是我们常识认为的str中第几个字符的下标（下标从0开始，左闭右开）
     * @return 我们常识认为应该截取的从startIndex到endIndex的str中的子串
     */
    public static String subString(String str, int startIndex, int endIndex) {
        // 定义：str是我们常识认为的中文字符
        // startIndex是我们常识认为的str中第几个字符的下标（下标从0开始）
        // endIndex是我们常识认为的str中第几个字符的下标（下标从0开始）
        if (StrUtil.isEmpty(str)) {
            return "";
        }
        // 字符串的总代码点数
        int codePointCount = str.codePointCount(0, str.length());
        // 创建一个纸带，记录每个 代码点数  所占 代码单位 大小，优化了纸带长度
        int[] tape = new int[Math.min(codePointCount, endIndex)];
        // 当前代码点数进度
        int strLength = 0;
        for (int index = 0; index <= tape.length - 1; index++) {
            // 接着判断一下 截取下来的加上后面的部分 的 代码单元和代码点数 是否一致
            String subStringNext = str.substring(strLength, (strLength + 2 > str.length() - 1 ? str.length() : strLength + 2));
            if (StrUtil.isBlank(subStringNext)) {
                break;
            }
            if (subStringNext.length() == subStringNext.codePointCount(0, subStringNext.length())) {
                // // 如果一致，纸带进度+1 代码点数+1，并记录所占 代码点数 位数
                tape[index] = 1;
                strLength++;
            } else {
                // 如果不一致，则证明截取下来的部分不完整要加上后面一位才完整
                tape[index] = 2;
                strLength += 2;
            }
        }
        // startIndex真实下标
        int startSum = 0;
        for (int i = 0; i < startIndex; i++) {
            startSum += tape[i];
        }
        // endIndex真实下标
        int endSum = 0;
        for (int i = startIndex; i < endIndex; i++) {
            endSum += tape[i];
        }
        return str.substring(startSum, startSum + endSum);
    }

    /**
     * 遍历字体获取text最合适字体(利用itext的FontSelector)
     *
     * @param text 文字
     * @param fontSize 字体大小
     * @return 字体
     */
    public static Map<String, Font> getFitFont(String text, int fontSize) {
        FontSelector fs = new FontSelector();
        for (Map.Entry<BaseFont, String> fontEntry : SPARE_FONT.entrySet()) {
            fs.addFont(new com.itextpdf.text.Font(fontEntry.getKey(), 12, 0, GrayColor.GRAYBLACK));
        }
        Phrase process = fs.process(text);
        List<Chunk> chunks = process.getChunks();
        Map<String, Font> stringFontMap = new HashMap<>();
        for (Chunk chunk : chunks) {
            String content = chunk.getContent();
            for (int i = 0; i < content.codePointCount(0, content.length()); i++) {
                String fontName = SPARE_FONT.get(chunk.getFont().getBaseFont());
                stringFontMap.put(subString(content, i, i + 1), new Font(fontName, Font.PLAIN, fontSize));
            }
        }
        return stringFontMap;
    }

}
