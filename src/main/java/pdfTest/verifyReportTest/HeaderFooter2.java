package pdfTest.verifyReportTest;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @author TangHaoKai
 * @version V1.0 2024/3/19 16:52
 */
public class HeaderFooter2 extends PdfPageEventHelper {
    /**
     * 默认页眉
     */
    public String header = "大陆云盾电子认证服务有限公司";

    public String footer;

    /**
     * 文档字体大小，页脚页眉最好和文本大小一致
     */
    public int presentFontSize = 1;

    /**
     * 页脚大小
     */
    public int presentFootSize = 10;

    /**
     * 文档页面大小，最好前面传入，否则默认为A4纸张
     */
    public Rectangle pageSize = PageSize.A4;

    /**
     * 模板
     */
    public PdfTemplate pdfTemplate;

    /**
     * 基础字体对象
     */
    public BaseFont bf = null;

    /**
     * 利用基础字体生成的字体对象，一般用于生成中文文字
     */
    public Font fontDetail = null;

    /**
     * Creates a new instance of PdfReportM1HeaderFooter 无参构造方法.
     */
    public HeaderFooter2() {

    }

    /**
     * Creates a new instance of PdfReportM1HeaderFooter 构造方法.
     *
     * @param yeMei           页眉字符串
     * @param presentFontSize 数据体字体大小
     * @param pageSize        页面文档大小，A4，A5，A6横转翻转等Rectangle对象
     */
    public HeaderFooter2(String yeMei, String footer, int presentFontSize, Rectangle pageSize) {
        this.header = yeMei;
        this.presentFontSize = presentFontSize;
        this.pageSize = pageSize;
        this.footer = footer;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setPresentFontSize(int presentFontSize) {
        this.presentFontSize = presentFontSize;
    }

    /**
     * 文档打开时创建模板
     *
     * @param writer
     * @param document
     */
    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        pdfTemplate = writer.getDirectContent().createTemplate(50, 50);
    }

    /**
     * 关闭每页的时候，写入页眉，写入'第几页共'这几个字。
     *
     * @param writer
     * @param document
     */
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            this.addPage(writer, document);
//            this.addWatermark(writer);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加分页
     *
     * @param writer
     * @param document
     * @throws IOException
     * @throws DocumentException
     */
    public void addPage(PdfWriter writer, Document document) throws IOException, DocumentException {
        try {
            if (bf == null) {
                bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            }
            if (fontDetail == null) {
                fontDetail = new Font(bf, presentFontSize, Font.BOLD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Font font = new Font(bf, presentFootSize, Font.NORMAL, BaseColor.GRAY);
        Chunk c = new Chunk(header, font);
        // 字下方的一条细线
        // c.setUnderline(new BaseColor(0, 0, 0), 0.015F, 0.015F, -0.7F, -0.7F, 1);
        Phrase head = new Phrase(c);

        // 拿到当前的PdfContentByte
        PdfContentByte cb = writer.getDirectContent();
        // 写入页眉
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, head, document.right(), document.top() + 10, 0);
        // 计算前半部分的的长度
        float len = bf.getWidthPoint("1" + writer.getPageNumber() + " 页", presentFootSize);
        // 写入页脚1
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("第 " + writer.getPageNumber() + " 页 共", font), (document.right() - document.rightMargin()) - len, document.bottom() - 10, 0);
        // 写入页脚2的模板
        cb.addTemplate(pdfTemplate, document.right() - document.rightMargin(), document.bottom() - 10);
        // 文件名
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("《" + footer + "》", font), document.right() / 2, document.bottom() - 10, 0);
    }

    //加水印
    public void addWatermark(PdfWriter writer) {
        // 水印图片
        try {
            PdfContentByte waterMar = writer.getDirectContentUnder();
            waterMar.beginText();
            // 设置水印透明度
            PdfGState gs = new PdfGState();
            // 设置笔触字体不透明度为0.4f
            gs.setStrokeOpacity(0.4f);
            Image image = Image.getInstance(IOUtils.toByteArray(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("static/images/waterBG.png"))));
            // 设置坐标 绝对位置 X Y
            image.setAbsolutePosition(0, 0);
            //依照比例缩放
            image.scalePercent(41.3F);
            waterMar.addImage(image);
            // 设置透明度
            waterMar.setGState(gs);
            waterMar.endText();
            waterMar.stroke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        //最后一步了，就是关闭文档的时候，将模板替换成实际的 Y 值,至此，page x of y 制作完毕，完美兼容各种文档size。
        pdfTemplate.beginText();
        //生成的模版的字体、颜色
        pdfTemplate.setFontAndSize(bf, presentFootSize);
        pdfTemplate.setColorFill(BaseColor.GRAY);
        String foot2 = " " + (writer.getPageNumber()) + " 页";
        //模版显示的内容
        pdfTemplate.showText(foot2);
        pdfTemplate.endText();
        pdfTemplate.closePath();
    }
}
