package pdfTest;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * PDF压缩
 *
 * @author TangHaoKai
 * @version V1.0 2024/10/8 16:56
 */
public class PdfCompressTest {

    @Test
    @SneakyThrows
    public void compressTest() {
        // 读取 PDF 文件路径
        Path path = Paths.get("src/main/java/certTest/pkiBaseTest", "40000-查询关键字字数影响测试-带图片.pdf");
        Path outPath = Paths.get("src/main/java/certTest/pkiBaseTest", "40000-查询关键字字数影响测试-带图片-压缩.pdf");

        // 读取文件字节内容
        byte[] pdfBytes = Files.readAllBytes(path);

        PdfReader reader = new PdfReader(pdfBytes);

        ByteArrayOutputStream compressOutPutStream = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, compressOutPutStream, PdfWriter.VERSION_1_7);

        // Document.compress = false;
        stamper.getWriter().setCompressionLevel(9);

        int total = reader.getNumberOfPages() + 1;
        for (int i = 1; i < total; i++) {
            reader.setPageContent(i, reader.getPageContent(i));
        }
        stamper.setFullCompression();
        stamper.close();

        reader.close();

        byte[] compressBytes = compressOutPutStream.toByteArray();

        FileUtil.writeBytes(compressBytes, outPath.toAbsolutePath().toString());

    }

}
