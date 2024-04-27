package sealTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

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
}
