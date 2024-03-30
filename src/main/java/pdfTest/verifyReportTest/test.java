package pdfTest.verifyReportTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 创建合同加强报告
 *
 * @author TangHaoKai
 * @version V1.0 2024/3/25 10:04
 */
public class test {

    @Test
    @SneakyThrows
    public void createTest() {
        Path path = Paths.get("src/main/java/pdfTest/verifyReportTest", "analysisPdf.pdf");
        path = Paths.get("src/main/java/pdfTest/verifyReportTest", "analysisPdf2.pdf");
        byte[] pdfBytes = Files.readAllBytes(path);
        ReportEntity reportEntity = AnalysisPdfUtil.verifyBase(pdfBytes, path.getFileName().toString());
        reportEntity.setEsvIndexId("test001");

        // json输出
        if (false) {
            for (ReportEntity.ReportSignEntity reportSignEntity : reportEntity.getSignInfoList()) {
                reportSignEntity.setSealData(null);
            }
            JSONConfig config = JSONConfig.create();
            config.setIgnoreNullValue(false);
            System.out.println(JSONUtil.parse(reportEntity, config).toStringPretty());
        }

        byte[] bytes = AdEPactReportUtil.genReport(reportEntity);
        Path out = Paths.get("target/test01.pdf");
        FileUtil.writeBytes(bytes, out.toAbsolutePath().toString());

    }
}
