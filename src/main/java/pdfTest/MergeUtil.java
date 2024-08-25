package pdfTest;

import com.itextpdf.io.IOException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 引入依赖
 * <!--itextpdf-->
 * <dependency>
 * <groupId>com.itextpdf</groupId>
 * <artifactId>itextpdf</artifactId>
 * <version>5.5.13</version>
 * </dependency>
 * <dependency>
 * <groupId>com.itextpdf</groupId>
 * <artifactId>itext-asian</artifactId>
 * <version>5.2.0</version>
 * </dependency>
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/8 16:27
 */
public class MergeUtil {
    private static final Path file1 = Paths.get("src/main/java/pdfTest/横+倒.pdf");
    private static final Path file2 = Paths.get("src/main/java/pdfTest/fill.pdf");
    private static final Path mergeFile = Paths.get("src/main/java/pdfTest/MERGE.pdf");

    public static void main(String[] args) throws Exception {
        byte[] file1Bytes = Files.readAllBytes(file1);
        byte[] file2Bytes = Files.readAllBytes(file2);
        // 合并顺序与这里的数组顺序相关
        List<byte[]> files = new ArrayList<>(Arrays.asList(file1Bytes, file2Bytes));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 合并方法
        mergePdf(byteArrayOutputStream, files);

        byte[] mergeBytes = byteArrayOutputStream.toByteArray();
        Files.write(mergeFile, mergeBytes);
    }

    /**
     * 合并多个pdf
     *
     * @param os    合并的pdf输出目标流
     * @param files 需要被合并的多个pdf列表
     */
    public static void mergePdf(OutputStream os, List<byte[]> files) throws Exception {

        Document document = null;
        try {

            // 获取纸张大小并实例化一个新的空文档, 例如 A5 纸
            document = new Document(new PdfReader(files.get(0)).getPageSize(1));
            // 实例化复制工具
            final PdfCopy copy = new PdfCopy(document, os);
            // 打开文档准备写入内容
            document.open();
            // 循环所有pdf文件
            for (byte[] file : files) {

                // 读取pdf
                final PdfReader reader = new PdfReader(file);
                // 获取页数
                final int numberOfPages = reader.getNumberOfPages();
                // pdf的所有页, 从第1页开始遍历, 这里要注意不是0
                for (int i = 1; i <= numberOfPages; i++) {

                    document.newPage();
                    // 把第 i 页读取出来
                    final PdfImportedPage page = copy.getImportedPage(reader, i);
                    // 把读取出来的页追加进输出文件里
                    copy.addPage(page);
                }
            }
        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

}