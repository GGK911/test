package pdfTest;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * pdf
 * https://kb.itextpdf.com/home/it5kb/ebooks/the-best-itext-5-questions-on-stack-overflow
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-19 10:06
 **/
public class test {

    @SneakyThrows
    public static void main(String[] args) {
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

        // 加粗， 斜体，删除线
        Font styleFont = new Font(baseFont, 12f, Font.BOLD | Font.ITALIC | Font.STRIKETHRU);
        // 正常字体
        Font normalFont = new Font(baseFont, 12f);

        System.out.println("//*************************************************创建一个PDF**********************************************************//");
        // 1.创建document实例
        // 自定义页大小
        // Rectangle pagesize = new Rectangle(216f, 720f);
        // Document document = new Document(pagesize, 36f, 72f, 108f, 180f);
        // rotate横向显示
        Document document = new Document(PageSize.A4.rotate());
        // Document document = new Document(PageSize.A4);
        FileOutputStream out = new FileOutputStream("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\create.pdf");
        // 2.创建PdfWriter 实例，并指定输出路径。
        PdfWriter writer = PdfWriter.getInstance(document, out);
        // 设置PDF版本
        writer.setPdfVersion(PdfWriter.VERSION_1_6);
        // open前 设置行距
        writer.setInitialLeading(100f);
        // 自定义事件
        writer.setPageEvent(new PdfCustomEvent(baseFont));
        // 3. 打开 document实例，开始document中添加内容
        document.open();
        // 4.添加内容
        document.add(new Paragraph("test......."));
        document.add(Chunk.NEWLINE);

        document.add(new Chunk("Chunk"));
        document.add(Chunk.NEWLINE);

        /* Chunk */
        Chunk chunk = new Chunk("UNDERLINE");
        Chunk chunk2 = new Chunk("E");

        // 下划线
        chunk.setUnderline(0.2f, -3f);
        document.add(chunk);
        document.add(Chunk.NEWLINE);

        // 上标
        Chunk id = new Chunk("2");
        // 正数>0
        id.setTextRise(6);
        document.add(chunk2);
        document.add(id);
        document.add(Chunk.NEWLINE);

        // 下标
        // 负数<0
        id.setTextRise(-6);
        document.add(chunk2);
        document.add(id);
        document.add(Chunk.NEWLINE);

        document.add(new Chunk("Phrase"));
        document.add(Chunk.NEWLINE);

        /* Phrase */
        Phrase p = new Phrase();
        p.add(new Chunk("正常", normalFont));
        p.add(Chunk.NEWLINE);
        p.add(Chunk.NEWLINE);
        p.add(new Chunk("样式", styleFont));
        p.add(Chunk.NEWLINE);
        document.add(p);

        document.add(new Chunk("Paragraph"));
        document.add(Chunk.NEWLINE);

        /* Paragraph */
        Paragraph paragraph = new Paragraph();
        // 直接添加Phrase不会分段换行，只会超过行限制后换行，默认行距
        paragraph.add(new Phrase("国国国国国国国国国国国国国国国国", normalFont));
        paragraph.add(new Phrase("国国国国国国国国国国国国国国国国", normalFont));
        paragraph.add(new Phrase("国国国国国国国国国国国国国国国国", normalFont));
        paragraph.setSpacingBefore(10f);
        document.add(paragraph);

        document.add(Chunk.NEWLINE);

        Paragraph paragraph2 = new Paragraph();
        // 会分段换行，会超过行限制后换行，但是是默认行距
        paragraph2.add(new Paragraph("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国", normalFont));
        paragraph2.add(new Paragraph("国国国国国国国国国国国国国国国国", normalFont));
        paragraph2.add(new Paragraph("国国国国国国国国国国国国国国国国", normalFont));
        paragraph2.setSpacingBefore(10f);
        document.add(paragraph2);

        document.add(Chunk.NEWLINE);

        Paragraph paragraph3 = new Paragraph();
        // 添加Phrase手动添加换行符会分段换行，会超过行限制后换行，不是默认行距，段落自定义行距生效
        paragraph3.add(new Phrase("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n", normalFont));
        paragraph3.add(new Phrase("国国国国国国国国国国国国国国国国\n", normalFont));
        paragraph3.add(new Phrase("国国国国国国国国国国国国国国国国\n", normalFont));
        paragraph3.setLeading(20f);
        paragraph3.setSpacingBefore(10f);
        document.add(paragraph3);

        document.add(Chunk.NEWLINE);

        Paragraph paragraph4 = new Paragraph();
        Paragraph paragraph4_1 = new Paragraph("GGK911");
        paragraph4_1.setAlignment(Paragraph.ALIGN_RIGHT);
        Paragraph paragraph4_2 = new Paragraph("GGK922");
        paragraph4_2.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph paragraph4_3 = new Paragraph("GGK933");
        paragraph4_3.setAlignment(Paragraph.ALIGN_LEFT);
        // 两边对齐好像没效果
        Paragraph paragraph4_4 = new Paragraph("GGK944");
        paragraph4_4.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        paragraph4.add(paragraph4_1);
        paragraph4.add(paragraph4_2);
        paragraph4.add(paragraph4_3);
        paragraph4.add(paragraph4_4);
        document.add(paragraph4);

        /* 表格 */

        // 流式添加元素，必须设置列数
        PdfPTable table = new PdfPTable(2);
        // 添加单元格
        table.addCell("cell0");
        PdfPCell cell = new PdfPCell(new Phrase("cell1"));
        // 跨两行
        cell.setRowspan(2);
        // 左右居中
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        // 上下居中
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        table.addCell("cell2");
        PdfPCell cell2 = new PdfPCell(new Phrase("cell3"));
        cell2.setColspan(2);
        cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell2);
        // 单独一列，没有补满一行，这行是直接去掉的，要搭配completeRow直接布满空列，才显示全
        table.addCell("cell4");
        table.completeRow();
        // 默认宽度80%，补慢
        table.setWidthPercentage(100);

        // 嵌套表格
        PdfPTable table2 = new PdfPTable(3);
        // 设置单元格宽度比例
        table2.setWidths(new int[]{2, 1, 1});
        // 去掉这个单元格的边框
        PdfPCell in_cell1 = new PdfPCell(new Phrase("cell0"));
        in_cell1.setBorderWidthTop(0);
        in_cell1.setBorderWidthRight(0);
        in_cell1.setBorderWidthBottom(0);
        in_cell1.setBorderWidth(0);
        table2.addCell(in_cell1);
        // 去掉这个单元格的边框
        PdfPCell in_cell2 = new PdfPCell(new Phrase("cell1"));
        in_cell2.setBorderWidthTop(0);
        in_cell2.setBorderWidthRight(0);
        in_cell2.setBorderWidthBottom(0);
        in_cell2.setBorderWidth(0);
        table2.addCell(in_cell2);
        // 无法动态宽度，超过宽度，换行
        table2.addCell("cell2cell2cell2cell2cell2cell2cell2cell2cell2");
        table2.addCell("cell3");
        table2.addCell("cell4");
        table2.addCell("cell5");
        table2.addCell("cell6");
        table2.addCell("cell7");
        table2.addCell("cell8");
        // 嵌套子表格不适合设置上下间距
        table2.setSpacingBefore(50);
        table2.setSpacingAfter(20);

        PdfPCell cell3 = new PdfPCell(table2);
        cell3.setColspan(2);
        table.addCell(cell3);

        document.add(table);


        // 5. 关闭
        document.close();


        // System.out.println("//*************************************************读取PDF域参数**********************************************************//");
        //
        // String pdf = "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\合同测试模板.pdf";
        // byte[] pdfBytes = FileUtil.readBytes(pdf);
        // List<PdfParameterEntity> pdfParameterEntityList = new ArrayList<>();
        // PdfUtil.getPdfDomain(pdfBytes, pdfParameterEntityList, null);
        // System.out.println(JSONUtil.parse(pdfParameterEntityList).toStringPretty());
        //
        // System.out.println("//*************************************************PDF域填充**********************************************************//");
        //
        // Map<String, String> map = new HashMap<>();
        // map.put("Check Box3", "是");
        // map.put("Text1", "PDF域填充");
        // map.put("Text2", "PDF域填充PDF域填充PDF域填充PDF域填充PDF域填充PDF域填充PDF域填充PDF域填充PDF域填充PDF域填充");
        //
        // byte[] pdfFill = PdfUtil.pdfFill(pdfBytes, map);
        // FileUtil.writeBytes(pdfFill, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\testFill.pdf");


    }
}
