package sealTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/17 20:28
 */
public class BackgroundSeal {

    /**
     * 引导签名背景
     */
    @Test
    @SneakyThrows
    public void transPortFont() {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300, 300 / 2, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        graphics.setPaint(new Color(0f, 0f, 0f, 0.2f));
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        // graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 这里必须是注册字体，直接createFont不行，找不到字体
        GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        genv.registerFont(Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("font/SmileySans-Oblique.ttf")));
        graphics.setFont(new Font("楷体", Font.PLAIN, 120));
        // graphics.setFont(new Font("得意黑 斜体", Font.PLAIN, 120));

        graphics.drawString("国", 10, 120);
        graphics.drawString("国", 165, 120);
        graphics.setStroke(new BasicStroke(10));
        graphics.drawRect(0, 0, 300, 300 / 2);

        // 边框
        // BufferedImage bufferedImage1 = new BufferedImage(300, 300, bufferedImage.getType());
        // Graphics2D graphics1 = bufferedImage1.createGraphics();
        // // graphics1.setPaint(Color.red);
        // // graphics1.setPaint(Color.BLACK);
        // graphics1.drawImage(bufferedImage, 0, 0, 300, 300, null);
        // graphics1.setStroke(new BasicStroke(16));
        // graphics1.drawRect(0, 0, 300, 300);
        // graphics1.dispose();
        //
        // bufferedImage = bufferedImage1;

        graphics.dispose();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.writeBytes(outStream.toByteArray(), "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\sealTest\\test.png");
    }

    @Test
    @SneakyThrows
    public void lanZongTest() {
        String input = "龘"; // 输入一个中文
        int imageSize = 200; // 设置图片的大小
        Color textColor = new Color(0, 0, 0, 60); // 设置文字颜色的透明度（0-255）
        Color backgroundColor = Color.WHITE; // 设置背景色为白色
        // 创建一个透明背景的图片
        BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, imageSize, imageSize);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color lineColor = new Color(255, 0, 0, 60); // 设置文字颜色的透明度（0-255）
        // 设置红色的虚线
        graphics.setColor(lineColor);
        float dash[] = {10.0f};
        graphics.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        // 绘制对角线
        graphics.drawLine(0, 0, imageSize, imageSize);
        graphics.drawLine(imageSize, 0, 0, imageSize);

        graphics.drawLine(1, 0, imageSize - 1, 0); // 上边框
        graphics.drawLine(imageSize - 1, 0, imageSize - 1, imageSize - 1); // 右边框
        graphics.drawLine(imageSize - 1, imageSize - 1, 0, imageSize - 1); // 下边框
        graphics.drawLine(1, imageSize - 1, 0, 0); // 左边框

        // 绘制中间的红色虚线十字
        int centerX = imageSize / 2;
        int centerY = imageSize / 2;
        graphics.drawLine(centerX, 0, centerX, imageSize);
        graphics.drawLine(0, centerY, imageSize, centerY);

        // 设置字体和文字颜色
        Font font = new Font("宋体", Font.PLAIN, imageSize);
        graphics.setFont(font);
        graphics.setColor(textColor);
        // 计算文字在图片中的位置
        FontMetrics fm = graphics.getFontMetrics();
        int totalWidth = fm.stringWidth(input);
        int startX = (imageSize - totalWidth) / 2;
        int startY = (imageSize - fm.getHeight()) / 2 + fm.getAscent();
        // 在图片上绘制文字
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            String text = String.valueOf(c);
            int charWidth = fm.charWidth(c);
            graphics.drawString(text, startX, startY);
            startX += charWidth;
        }
        graphics.dispose();
        // 保存图片
        try {
            ImageIO.write(image, "PNG", new File("d:\\output.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
