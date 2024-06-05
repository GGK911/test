package pdfTest;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;
import sealTest.StrUtil;

/**
 * @author TangHaoKai
 * @version V1.0 2024/3/19 17:43
 */
public class PdfCustomEvent2 extends PdfPageEventHelper {
    /**
     * 打底字体（一般都是中文字体）
     */
    private final BaseFont baseFont;
    /**
     * 模板
     */
    public PdfTemplate pdfTemplate;

    public PdfCustomEvent2(BaseFont baseFont) {
        this.baseFont = baseFont;
    }

    @SneakyThrows
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        float len = baseFont.getWidthPoint("1" + writer.getPageNumber() + " 页", 10);
        Phrase footer = new Phrase("第 " + writer.getPageNumber() + " 页 共", new Font(baseFont, 10, Font.NORMAL, BaseColor.GRAY));
        PdfContentByte cb = writer.getDirectContent();
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase("ESV123321123321"), document.right(), document.top() + 10, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, (document.right() - document.rightMargin()) - len, document.bottom(), 0);
        // cb.addTemplate(pdfTemplate, document.right() - document.rightMargin(), document.bottom());
        cb.addTemplate(pdfTemplate, document.right() - document.rightMargin(), document.bottom() - 2);
        String str = "零一二三四五六七八九十零一二三四五六七八九十零一二三四五六七八九十零一二三四五六七八九十零一二三四五六七八九十零一二三四五六七八九十零一二三四五六七八九十零一二三四五六七八九十";
        // String str = "金诚金诚金诚金诚金诚金诚金诚金诚金诚金诚金诚金诚金诚金诚金诚金诚金诚金诚诚";
        int trueLength = str.codePointCount(0, str.length());
        if (trueLength <= 38) {
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("《" + str + ".pdf》", new Font(baseFont, 10, Font.NORMAL, BaseColor.GRAY)), document.right() / 2, document.bottom(), 0);
        } else if (trueLength <= 38 * 2) {
            String pre = StrUtil.subString(str, 0, trueLength / 2);
            String end = StrUtil.subString(str, trueLength / 2, trueLength);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("《" + pre + "", new Font(baseFont, 10, Font.NORMAL, BaseColor.GRAY)), document.right() / 2, document.bottom(), 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("" + end + ".pdf》", new Font(baseFont, 10, Font.NORMAL, BaseColor.GRAY)), document.right() / 2, document.bottom() - 10, 0);
        } else {
            String pre = StrUtil.subString(str, 0, trueLength / 3);
            String mid = StrUtil.subString(str, trueLength / 3, trueLength / 3 * 2);
            String end = StrUtil.subString(str, trueLength / 3 * 2, trueLength);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("《" + pre + "", new Font(baseFont, 10, Font.NORMAL, BaseColor.GRAY)), document.right() / 2, document.bottom(), 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("" + mid + "", new Font(baseFont, 10, Font.NORMAL, BaseColor.GRAY)), document.right() / 2, document.bottom() - 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("" + end + ".pdf》", new Font(baseFont, 10, Font.NORMAL, BaseColor.GRAY)), document.right() / 2, document.bottom() - 20, 0);
        }
    }

    /**
     * 文档打开时创建模板
     */
    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        pdfTemplate = writer.getDirectContent().createTemplate(30, 16);
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        String foot2 = " " + (writer.getPageNumber()) + " 页";
        // method:1 有问题，字体显示不全
        // //最后一步了，就是关闭文档的时候，将模板替换成实际的 Y 值,至此，page x of y 制作完毕，完美兼容各种文档size。
        // pdfTemplate.beginText();
        // //生成的模版的字体、颜色
        // pdfTemplate.setFontAndSize(baseFont, 10);
        // pdfTemplate.setColorFill(BaseColor.GRAY);
        // //模版显示的内容
        // pdfTemplate.showText(foot2);
        // pdfTemplate.endText();
        // pdfTemplate.closePath();

        // method:2
        ColumnText.showTextAligned(pdfTemplate, Element.ALIGN_LEFT, new Phrase(foot2, new Font(baseFont, 10, Font.NORMAL, BaseColor.GRAY)), 2, 2, 0);
    }
}
