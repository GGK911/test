package fontTest;

import cn.com.mcsca.itextpdf.text.BaseColor;
import cn.com.mcsca.itextpdf.text.FontFactory;
import cn.com.mcsca.itextpdf.text.pdf.BaseFont;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/2 16:34
 */
public class test {
    @Test
    @SneakyThrows
    public void regFontTest() {
        Font font = Font.createFont(Font.TRUETYPE_FONT, ResourceUtil.getStream("font/JinbiaoSong.TTF"));
        // Register the font
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] allFonts = ge.getAllFonts();
        for (Font f : allFonts) {
            if (f.getName().equals("金标宋体") || f.getFamily().equals("金标宋体")) {
                System.out.println("Font registered successfully.");
                break;
            }
        }
        ge.registerFont(font);
        String name = font.getName();
        String family = font.getFamily();
        BaseFont baseFont1 = FontFactory.getFont("src/main/resources/font/JinbiaoSong.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 12f, cn.com.mcsca.itextpdf.text.Font.NORMAL, BaseColor.BLACK).getBaseFont();

        Font font1 = new Font(name, Font.PLAIN, 12);
        Font font2 = new Font(family, Font.PLAIN, 12);
        Font font3 = new Font(font.getPSName(), Font.PLAIN, 12);

        // Use the font
        Font[] allFonts2 = ge.getAllFonts();
        for (Font f : allFonts2) {
            if (f.getName().equals("金标宋体") || f.getFamily().equals("金标宋体")) {
                System.out.println("Font registered successfully.");
                break;
            }
        }
    }
}
