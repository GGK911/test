package pdfTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/28 9:55
 */
public class PdfParamTest {

    @Test
    @SneakyThrows
    public void getParam() {
        Path path = Paths.get("src/main/java/pdfTest", "横+倒.pdf");
        byte[] bytes = Files.readAllBytes(path);
        List<PdfUtil.PdfParameterEntity> pdfDomain = PdfUtil.getPdfDomain(bytes);
        System.out.println(JSONUtil.toJsonPrettyStr(pdfDomain));
    }

    @Test
    @SneakyThrows
    public void removeParam() {
        Path path = Paths.get("src/main/java/pdfTest", "横+倒.pdf");
        byte[] bytes = Files.readAllBytes(path);
        byte[] pdfBytes = PdfUtil.removeAllField(bytes);
        FileUtil.writeBytes(pdfBytes, "C:\\Users\\ggk911\\Desktop\\1.pdf");
    }

}
