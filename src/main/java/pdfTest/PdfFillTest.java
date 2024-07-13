package pdfTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/9 15:07
 */
public class PdfFillTest {
    private final static String ROOT = "src/main/java/pdfTest";

    @Test
    @SneakyThrows
    public void fillTest() {
        Path pdfPath = Paths.get(ROOT, "合同测试模板.pdf");
        byte[] bytes = FileUtil.readBytes(pdfPath);
        byte[] pdfFill = PdfUtil.pdfFill(bytes, "{\"Text1\":\"唐\u00B7好凯\"}");
        FileUtil.writeBytes(pdfFill, Paths.get(ROOT, "fill.pdf").toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void keyWordTest() {
        // Path pdfPath = Paths.get("C:\\Users\\ggk911\\Desktop\\123.pdf");
        Path pdfPath = Paths.get(ROOT, "fill.pdf");
        // Path pdfPath = Paths.get(ROOT, "create.pdf");
        byte[] bytes = FileUtil.readBytes(pdfPath);
        List<float[]> keywordPositions = PdfKeyWordFinderUtil.findKeywordPositions(bytes, "\u00B7");
        for (float[] keywordPosition : keywordPositions) {
            System.out.println(JSONUtil.toJsonStr(keywordPosition));
        }
    }

}
