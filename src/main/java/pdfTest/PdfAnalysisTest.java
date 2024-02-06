package pdfTest;

import cn.com.mcsca.itextpdf.text.pdf.PdfArray;
import cn.com.mcsca.itextpdf.text.pdf.PdfDictionary;
import cn.com.mcsca.itextpdf.text.pdf.PdfName;
import cn.com.mcsca.itextpdf.text.pdf.PdfObject;
import cn.com.mcsca.itextpdf.text.pdf.PdfReader;
import cn.com.mcsca.itextpdf.text.pdf.PdfStamper;
import cn.com.mcsca.itextpdf.text.pdf.PdfString;
import cn.com.mcsca.itextpdf.text.pdf.security.PdfPKCS7;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.security.Provider;

/**
 * @author TangHaoKai
 * @version V1.0 2024/1/17 15:50
 **/
public class PdfAnalysisTest {

    private static Provider BC = new BouncyCastleProvider();

    /**
     * 注释对象
     */
    @Test
    @SneakyThrows
    public void analysis01() {
        // 读取和解析 PDF 文档
        String pdf = "C:\\Users\\ggk911\\Desktop\\个人信息授权书(2).pdf";
        byte[] readBytes = FileUtil.readBytes(pdf);
        PdfReader reader = new PdfReader(readBytes);
        // 对于每个 PDF 页面
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            // 获取页面，PDF 页面
            PdfDictionary page = reader.getPageN(i);
            // 获取第 i 页的所有注释
            PdfArray annotsArray = page.getAsArray(PdfName.ANNOTS);
            // 如果页面没有批注
            if (page.getAsArray(PdfName.ANNOTS) == null) {
                continue;
            }
            // 对于每个批注
            for (int j = 0; j < annotsArray.size(); ++j) {
                //对于当前批注
                PdfDictionary curAnnot = annotsArray.getAsDict(j);
                System.out.println(curAnnot.toString());

            }
        }
    }

    /**
     *
     */
    @Test
    @SneakyThrows
    public void analysis02() {
        // 读取和解析 PDF 文档
        String pdf = "C:\\Users\\ggk911\\Desktop\\cfca.pdf";
        byte[] readBytes = FileUtil.readBytes(pdf);
        PdfReader reader = new PdfReader(readBytes);
        // 对于每个 PDF 页面
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            // 获取页面，PDF 页面
            PdfDictionary page = reader.getPageN(i);
            // 获取第 i 页的所有注释
            PdfArray annotsArray = page.getAsArray(PdfName.ANNOTS);
            // 如果页面没有批注
            if (page.getAsArray(PdfName.ANNOTS) == null) {
                continue;
            }
            // 对于每个批注
            for (int j = 0; j < annotsArray.size(); ++j) {
                //对于当前批注
                PdfDictionary curAnnot = annotsArray.getAsDict(j);
                PdfDictionary pdfObject = (PdfDictionary) PdfReader.getPdfObject(curAnnot.get(PdfName.V));
                if (pdfObject != null) {
                    PdfString asString = pdfObject.getAsString(PdfName.CONTENTS);

                }
            }
        }
    }

    @Test
    @SneakyThrows
    public void analysisFullComTest() {
        // 读取和解析 PDF 文档
        // String pdf = "C:\\Users\\ggk911\\Desktop\\cfca.pdf";
        // String pdf = "C:\\Users\\ggk911\\Desktop\\劳动合同( 含变更+隐私声明 )-无固定期限5.pdf";
        String pdf = "C:\\Users\\ggk911\\Desktop\\域模板.pdf";
        // String pdf = "C:\\Users\\ggk911\\Desktop\\多页测试1.pdf";
        byte[] readBytes = FileUtil.readBytes(pdf);
        PdfReader reader = new PdfReader(readBytes);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final PdfStamper pdfStamper = new PdfStamper(reader, bos, '\0', true);
        // final PdfStamper pdfStamper = new PdfStamper(reader, bos);
        System.out.println("是否1.5压缩：" + pdfStamper.getWriter().isFullCompression());
    }


}
