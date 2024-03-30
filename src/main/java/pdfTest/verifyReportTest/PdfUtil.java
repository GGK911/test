package pdfTest.verifyReportTest;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * pdf操作工具类
 *
 * @author 文仔
 */
public class PdfUtil {
    /**
     * 多个pdf文件合并
     *
     * @param fileBytes 需要合并的pdf文件，按照合并的顺序进行组装为list
     * @return 返回合并后的二级制pdf文件
     * @throws IOException
     * @throws DocumentException
     */
    public static byte[] mergePdfFiles(List<byte[]> fileBytes) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(new PdfReader(fileBytes.get(0)).getPageSize(1));
        PdfCopy copy = new PdfCopy(document, out);
        document.open();
        // 添加pdf的基本信息
        addBaseInfo(document);
        for (byte[] fileByte : fileBytes) {
            PdfReader reader = new PdfReader(fileByte);
            int n = reader.getNumberOfPages();
            for (int j = 1; j <= n; j++) {
                document.newPage();
                PdfImportedPage page = copy.getImportedPage(reader, j);
                copy.addPage(page);
            }
        }
        document.close();
        return out.toByteArray();
    }

    /**
     * pdf 文件转byte
     *
     * @param pdfFile
     * @return
     * @throws Exception
     */
    public static List<byte[]> pdfToImage(byte[] pdfFile) throws Exception {
        //定义集合保存返回图片数据
        List<byte[]> fileList = new ArrayList<byte[]>();
        //抑制警告
        @SuppressWarnings("resource")
        PDDocument pdDocument = PDDocument.load(pdfFile);
        PDFRenderer renderer = new PDFRenderer(pdDocument);
        /* dpi越大转换后越清晰，相对转换速度越慢 */
        PdfReader reader = new PdfReader(pdfFile);
        int pages = reader.getNumberOfPages();
        for (int i = 0; i < pages; i++) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedImage image = renderer.renderImageWithDPI(i, 100);
            ImageIO.write(image, "png", byteArrayOutputStream);
            fileList.add(byteArrayOutputStream.toByteArray());
        }
        System.out.println("PDF文档转PNG图片成功！");
        return fileList;
    }

    /**
     * 添加pdf的基本信息
     *
     * @param document pdf文档
     */
    public static void addBaseInfo(Document document) {
        // 标题
        document.addTitle("电子签章验证报告");
        // 作者
        document.addAuthor("大陆云盾电子认证服务有限公司");
        // 主题
        document.addSubject("电子签章验证报告");
        // 关键字
        document.addKeywords("MCSCA、电子签名验签");
        // 创建者
        document.addCreator("大陆云盾电子认证服务有限公司");
    }
}
