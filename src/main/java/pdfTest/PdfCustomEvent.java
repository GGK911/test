package pdfTest;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;

/**
 * PDF自定义事件（页眉、页码、水印）
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-16 16:33
 **/
public class PdfCustomEvent extends PdfPageEventHelper {
    /**
     * 打底字体（一般都是中文字体）
     */
    private final BaseFont baseFont;
    /**
     * 页码初始页
     */
    private int page = 0;
    /**
     * 页码开始页数
     */
    private static final int PAGE_INDEX_START = 0;

    public PdfCustomEvent(BaseFont baseFont) {
        this.baseFont = baseFont;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        super.onStartPage(writer, document);
    }

    @SneakyThrows
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        super.onEndPage(writer, document);
        PdfContentByte canvas = writer.getDirectContent();
        Rectangle rectangle = writer.getPageSize();
        Font headerFont = new Font(baseFont, 10f, Font.NORMAL);
        // 文字页眉
        // ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase("大陆云盾", headerFont), rectangle.getLeft() + 35, rectangle.getTop() - 17, 0);
        // 图片页眉
        Image image = Image.getInstance(FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\resources\\file\\image\\页眉log.png"));
        image.setAlignment(Image.MIDDLE);
        image.scaleToFit(60, 30);
        image.setAbsolutePosition(rectangle.getLeft() + 35, rectangle.getTop() - 37);
        writer.getDirectContent().addImage(image);

        // 水印字体
        Font watermarkFont = new Font(baseFont, 130, Font.NORMAL, new BaseColor(223, 223, 223, 120));
        // 水印
        ColumnText.showTextAligned(writer.getDirectContentUnder(), Element.ALIGN_CENTER, new Phrase("机密", watermarkFont), rectangle.getRight() / 2, rectangle.getTop() / 2, 33);
        if (document.getPageNumber() > PAGE_INDEX_START) {
            // 页脚
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("" + page, headerFont), rectangle.getRight() / 2, rectangle.getBottom() + 10, 0);
            page++;
        }
    }
}
