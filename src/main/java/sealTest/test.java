package sealTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 解决生僻字测试
 *
 * @author TangHaoKai
 * @version V1.0 2023-12-21 16:31
 **/
public class test {
    public static void main(String[] args) {
        // byte[] bytes = SealUtil.buildSeal("\uD870\uDF86\uD84D\uDCC3\uD852\uDE4A\uE27E㵥\uE4A1");
        // byte[] bytes = SealUtil.buildPersonSeal("陈佳", 18, 6, 0, 0, 0);;
        // FileUtil.writeBytes(bytes, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\sealTest\\test2.png");
        drawStringTest();

    }

    @SneakyThrows
    public static void drawStringTest() {
        // 画布
        BufferedImage bufferedImage = new BufferedImage(300 * 4 - 130, 300 / 3, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        // graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        // graphics.setFont(new Font("宋体", Font.BOLD, 120));
        // 这里必须是注册字体，直接createFont不行，找不到字体
        GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        genv.registerFont(Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("font/SmileySans-Oblique.ttf")));
        graphics.setFont(new Font("得意黑 斜体", Font.PLAIN, 80));
        // genv.registerFont(Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("font/JinbiaoSong.TTF")));
        // graphics.setFont(new Font("金标宋体", Font.PLAIN, 120));
        // 填充背景
        graphics.fillRect(0, 0, 300 * 4, 300 / 3);
        // 换颜色
        // graphics.setPaint(Color.RED);
        graphics.setPaint(Color.BLACK);

        int xOffset = -30;

        graphics.drawString("2024", xOffset + 50, 80);
        graphics.drawString("年", xOffset + 200, 80);

        graphics.drawString("12", xOffset + 290, 80);
        graphics.drawString("月", xOffset + 360, 80);

        graphics.drawString("29", xOffset + 450, 80);
        graphics.drawString("日", xOffset + 520, 80);

        graphics.drawString("24", xOffset + 520 + 160 - 70, 80);
        graphics.drawString("时", xOffset + 520 + 160, 80);

        graphics.drawString("59", xOffset + 520 + 160 + 160 - 70, 80);
        graphics.drawString("分", xOffset + 520 + 160 + 160, 80);

        graphics.drawString("59", xOffset + 520 + 160 + 160 + 160 - 70, 80);
        graphics.drawString("秒", xOffset + 520 + 160 + 160 + 160, 80);

        BufferedImage bufferedImage1 = new BufferedImage(300, 300, bufferedImage.getType());
        Graphics2D graphics1 = bufferedImage1.createGraphics();
        // graphics1.setPaint(Color.red);
        // graphics1.setPaint(Color.BLACK);
        // 边框
        graphics1.drawImage(bufferedImage, 0, 0, 300, 300, null);
        graphics1.setStroke(new BasicStroke(16));
        graphics1.drawRect(0, 0, 300, 300);
        graphics1.dispose();

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


}
