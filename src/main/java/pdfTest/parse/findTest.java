package pdfTest.parse;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.CMapAwareDocumentFont;
import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.parser.ContentByteUtils;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/30 14:32
 */
public class findTest {
    private final static String ROOT = "src/main/java/pdfTest";

    @Test
    @SneakyThrows
    public void content() {
        Path pdfPath = Paths.get(ROOT, "parse/aip填充.pdf");
        byte[] bytes = FileUtil.readBytes(pdfPath);

        PdfReader reader = new PdfReader(bytes);
        byte[] streamBytes = reader.getPageContent(1);
        PRTokeniser tokenizer = new PRTokeniser(new RandomAccessFileOrArray(streamBytes));
        while (tokenizer.nextToken()) {
            // if (tokenizer.getTokenType() == PRTokeniser.TokenType.STRING) {
            System.out.println(tokenizer.getTokenType());
            System.out.println(tokenizer.getStringValue());
            // }
        }
    }

    @Test
    @SneakyThrows
    public void stream() {
        // Path pdfPath = Paths.get(ROOT, "横+倒.pdf");
        Path pdfPath = Paths.get(ROOT, "parse/aip填充.pdf");
        byte[] bytes = FileUtil.readBytes(pdfPath);

        PdfReader reader = new PdfReader(bytes);
        SimpleTextExtractionStrategy listener = new SimpleTextExtractionStrategy();
        PdfContentStreamProcessor processor = new PdfContentStreamProcessor(listener);
        PdfDictionary pageDic = reader.getPageN(1);
        PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
        processor.processContent(ContentByteUtils.getContentBytesForPage(reader, 3), resourcesDic);

        String resultantText = listener.getResultantText();
        System.out.println(resultantText);
    }

    @Test
    @SneakyThrows
    public void fontTet() {
        byte[] bytes0 = IOUtils.toByteArray(ResourceUtil.getStream("font/SIMSUN.TTF"));
        BaseFont font = BaseFont.createFont("SIMSUN.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes0, null);


    }

    @Test
    @SneakyThrows
    public void pdfBoxTest() {
        // pdfbox
        Path pdfPath = Paths.get(ROOT, "横+倒.pdf");
        // Path pdfPath = Paths.get(ROOT, "parse/aip填充.pdf");
        byte[] bytes = FileUtil.readBytes(pdfPath);

        BoxKeyPosition boxKeyPosition = new BoxKeyPosition("主要负责人", bytes);
        System.out.println(JSONUtil.toJsonStr(boxKeyPosition.getPosition()));


    }

}
