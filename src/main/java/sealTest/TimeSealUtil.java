package sealTest;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 时间章生成
 *
 * @author TangHaoKai
 * @version V1.0 2024/5/8 16:15
 */
public class TimeSealUtil {

    public static void main(String[] args) throws Exception {
        byte[] bytes = draw("2024-12-29 23:59:59");
        bytes = draw("2024-12-29");
        FileUtil.writeBytes(bytes, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\sealTest\\test.png");
    }

    public static byte[] draw(String timeStr) throws IOException {
        DateTime time = DateUtil.parse(timeStr);
        // 画布
        BufferedImage bufferedImage;
        if (timeStr.length() > 10) {
            bufferedImage = new BufferedImage(300 * 4 - 60, 100, BufferedImage.TYPE_4BYTE_ABGR);
        } else {
            bufferedImage = new BufferedImage(300 * 2 + 40, 100, BufferedImage.TYPE_4BYTE_ABGR);
        }
        // 画笔
        Graphics2D graphics = bufferedImage.createGraphics();
        // 画笔颜色
        // graphics.setPaint(Color.BLUE);
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体
        graphics.setFont(new Font("宋体", Font.BOLD, 80));
        // 填充背景
        // graphics.fillRect(0, 0, 300 * 4, 300 / 3);
        // 换颜色
        // graphics.setPaint(Color.RED);
        graphics.setPaint(Color.BLACK);

        graphics.drawString(DateUtil.format(time, "yyyy"), 10, 80);
        graphics.drawString("年", 190, 80);

        graphics.drawString(DateUtil.format(time, "MM"), 280, 80);
        graphics.drawString("月", 370, 80);

        graphics.drawString(DateUtil.format(time, "dd"), 460, 80);
        graphics.drawString("日", 540, 80);

        if (timeStr.length() > 10) {
            graphics.drawString(DateUtil.format(time, "HH"), 630, 80);
            graphics.drawString("时", 710, 80);

            graphics.drawString(DateUtil.format(time, "mm"), 800, 80);
            graphics.drawString("分", 880, 80);

            graphics.drawString(DateUtil.format(time, "ss"), 970, 80);
            graphics.drawString("秒", 1050, 80);
        }

        graphics.dispose();

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outStream);
        return outStream.toByteArray();
    }

}
