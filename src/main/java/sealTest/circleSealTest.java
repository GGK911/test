package sealTest;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 圆圈图章测试
 *
 * @author TangHaoKai
 * @version V1.0 2023-12-22 09:42
 **/
public class circleSealTest {
    public static void main(String[] args) {
        String str = "大陆云盾㵥\uE4A1大陆云盾㵥\uE4A1大陆云盾㵥\uE4A1大陆云盾㵥\uE4A1";

        if (str.length() >= 24) {
            System.out.println("图章过长，推荐自定义图章");
        }

        //1.画布
        BufferedImage bufferedImage = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
        //2.画笔
        Graphics2D g2d = bufferedImage.createGraphics();
        //2.1抗锯齿设置,文本不抗锯齿，圆中心的文字会被拉长
        // RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        // //其他图形抗锯齿
        // hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // g2d.setRenderingHints(hints);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //2.2设置背景透明度
        // g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0));
        //2.3填充矩形
        // g2d.fillRect(0, 0, 170, 170);
        //2.4重设透明度，开始画图
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1));
        //2.5设置画笔颜色
        g2d.setPaint(Color.red);

        //1.圆线条粗细默认是圆直径的1/35
        //2.画圆
        int firstCircle = 8;
        g2d.setStroke(new BasicStroke(firstCircle));
        // 因为圈占5像素，所以坐标位移5像素，整体宽高都减2*5
        g2d.drawOval(firstCircle, firstCircle, 300 - firstCircle * 2, 300 - firstCircle * 2);

        // 距离圆圈边界值
        int topFix = 15;
        double circleRadius = 300 / 2F;
        Font font = new Font("宋体", Font.PLAIN, 26);
        FontRenderContext fontRenderContext = g2d.getFontRenderContext();
        Rectangle2D stringBounds = font.getStringBounds(str, fontRenderContext);
        double width = stringBounds.getWidth();
        double height = stringBounds.getHeight();
        double scaleSize = 2;
        topFix = topFix + Convert.toInt(height / scaleSize * 2.0);
        int count = str.codePointCount(0, str.length());
        // 调节字体间距
        double interval;
        if (count < 9) {
            interval = width / (count - 1) * 1.45;
        } else if (count < 12) {
            interval = width / (count - 1) * 1.25;
        } else if (count < 15) {
            interval = width / (count - 1) * 0.95;
        } else if (count < 18) {
            interval = width / (count - 1) * 0.85;
        } else {
            interval = width / (count - 1) * 0.75;
        }
        double radius = circleRadius + stringBounds.getY() - topFix;
        double radianInterval = 2 * Math.asin(interval / (2 * radius));
        double firstAngle;
        // 第一个字起始位置
        double fix = 0.18;
        if (count % 2 == 1) {
            firstAngle = (count - 1) * radianInterval / 2.0 + Math.PI / 2 + fix;
        } else {
            firstAngle = (count / 2.0 - 1) * radianInterval + radianInterval / 2.0 + Math.PI / 2 + fix;
        }
        // 字体旋转倾斜度      数值越大顺时针方向旋转度越高
        double gradient = 0.2;
        // 字体间是否靠近度    数值越大越靠近
        double distance = 0.9;

        g2d.setFont(new Font("宋体", Font.PLAIN, 120));
        g2d.drawString("★", 300 / 2 - 120 / 2, 300 / 2 - 120 / 2 + 120 - 20);

        Map<String, Font> fitFont = SealUtil.getFitFont(str, 26);
        for (int i = 0; i < count; i++) {
            double aa = firstAngle - i * radianInterval;
            double ax = radius * Math.sin(Math.PI / 2 - aa);
            double ay = radius * Math.cos(aa - Math.PI / 2);
            AffineTransform transform = AffineTransform.getRotateInstance(Math.PI / 2 - aa + gradient);
            AffineTransform scaleform = AffineTransform.getScaleInstance(distance, scaleSize);
            // 防止文本和外圈间距
            transform.concatenate(scaleform);

            String text = SealUtil.subString(str, i, i + 1);
            Font textFont = fitFont.get(text);
            Font font2 = textFont.deriveFont(transform);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);//设置抗锯齿
            g2d.setFont(font2);
            g2d.setPaint(Color.red);
            // 文字
            g2d.drawString(str.substring(i, i + 1), (float) (circleRadius + ax), (float) (circleRadius - ay));
        }

        g2d.dispose();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.writeBytes(outStream.toByteArray(), "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\sealTest\\test.png");
    }
}
