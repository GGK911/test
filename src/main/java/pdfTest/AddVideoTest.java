package pdfTest;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfFileSpecification;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/17 16:45
 */
public class AddVideoTest {

    /**
     * 通过注释添加附件到pdf文件里
     * 必须要有东西来显示
     */
    @Test
    @SneakyThrows
    public void addVideo() {
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

        Path pdfPath = Paths.get("src/main/java/pdfTest", "合同测试模板.pdf");
        Path path = Paths.get("src/main/resources/file", "video.mp4");
        Path fieldChange = Paths.get("target", "1.pdf");

        byte[] videoBytes = Files.readAllBytes(path);
        ByteOutputStream byteOutputStream = new ByteOutputStream();

        PdfReader pdfReader = new PdfReader(Files.readAllBytes(pdfPath));
        PdfStamper pdfStamper = new PdfStamper(pdfReader, byteOutputStream);
        PdfWriter writer = pdfStamper.getWriter();

        // 添加文件附件
        PdfAnnotation fileAttachment = PdfAnnotation.createFileAttachment(writer, new Rectangle(100, 100, 200, 200), "TestVideo001.mp4", videoBytes, "TestVideo001.mp4", "TestVideo001.mp4");

        pdfStamper.addAnnotation(fileAttachment, 1);
        pdfStamper.close();
        pdfReader.close();
        FileUtil.writeBytes(byteOutputStream.getBytes(), fieldChange.toAbsolutePath().toString());
        byteOutputStream.close();
        System.out.println(">> " + fieldChange.toAbsolutePath());
    }

    /**
     * 读取附件
     */
    @Test
    @SneakyThrows
    public void getStream() {
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

        Path pdfPath = Paths.get("src/main/java/pdfTest", "合同测试模板.pdf");
        Path path = Paths.get("src/main/resources/file", "video.mp4");
        Path fieldChange = Paths.get("target", "2.pdf");

        byte[] videoBytes = Files.readAllBytes(path);
        ByteOutputStream byteOutputStream = new ByteOutputStream();

        PdfReader pdfReader = new PdfReader(Files.readAllBytes(pdfPath));
        PdfStamper pdfStamper = new PdfStamper(pdfReader, byteOutputStream);
        // 第一页的附件 可以循环
        PdfArray asArray = pdfReader.getPageN(1).getAsArray(PdfName.ANNOTS);
        for (int i = 0; i < asArray.size(); i++) {
            PdfDictionary asDict = asArray.getAsDict(i);
            if (PdfName.FILEATTACHMENT.equals(asDict.getAsName(PdfName.SUBTYPE))) {
                PdfDictionary asDict1 = asDict.getAsDict(PdfName.FS);
                PdfDictionary asDict2 = asDict1.getAsDict(PdfName.EF);
                for (PdfName name : asDict2.getKeys()) {
                    // 提取
                    byte[] streamBytes = PdfReader.getStreamBytes((PRStream) asDict2.getAsStream(name));

                }
            }
        }
        pdfStamper.close();
        pdfReader.close();
        FileUtil.writeBytes(byteOutputStream.getBytes(), fieldChange.toAbsolutePath().toString());
        byteOutputStream.close();
        System.out.println(">> " + fieldChange.toAbsolutePath());
    }


    /**
     * 添加附件到pdf文件里
     * 侧边栏显示
     */
    @Test
    @SneakyThrows
    public void addVideo2() {
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

        Path pdfPath = Paths.get("src/main/java/pdfTest", "合同测试模板.pdf");
        Path path = Paths.get("src/main/resources/file", "video.mp4");
        Path path2 = Paths.get("src/main/resources/file/image", "页眉log.png");
        Path fieldChange = Paths.get("target", "2.pdf");

        byte[] videoBytes = Files.readAllBytes(path);
        byte[] pngBytes = Files.readAllBytes(path2);
        ByteOutputStream byteOutputStream = new ByteOutputStream();

        PdfReader pdfReader = new PdfReader(Files.readAllBytes(pdfPath));
        PdfStamper pdfStamper = new PdfStamper(pdfReader, byteOutputStream);

        // mp4
        pdfStamper.getWriter().addFileAttachment(PdfFileSpecification.fileEmbedded(pdfStamper.getWriter(), "video.mp4", "video.mp4", videoBytes));

        // png
        pdfStamper.getWriter().addFileAttachment(PdfFileSpecification.fileEmbedded(pdfStamper.getWriter(), "页眉log.png", "页眉log.png", pngBytes));


        pdfStamper.close();
        pdfReader.close();
        FileUtil.writeBytes(byteOutputStream.getBytes(), fieldChange.toAbsolutePath().toString());
        byteOutputStream.close();
        System.out.println(">> " + fieldChange.toAbsolutePath());
    }


}
