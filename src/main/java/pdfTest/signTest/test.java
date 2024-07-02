package pdfTest.signTest;

import cn.hutool.core.io.FileUtil;
import com.ggk911.changeJarTest.TestUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pdfTest.signTest.strategy.file.PdfFile;
import pdfTest.signTest.strategy.file.SignFile;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/6/6 17:05
 */
public class test {
    static String pub = "MIID0TCCArmgAwIBAgIQN7iSKcgcdxx8CwhbpgrRCTANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yNDAzMTQwNjM4NDZaFw0yNTAzMTQwNjM4NDZaMIGNMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjUwMDIyODE5OTQwMTAxMzc1ODE/MD0GA1UEAww2VDExNzY3NTU4MTY2NDkxOTU1MjBA55S15a2Q5ZCI5ZCM5rWL6K+VMjAyMzAxMDZAMDJAMDQzMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6gKt2m2OSjA+WW0sNcPtM7m/1o9cZ3m9CrmbnE5804QZ4jkJdw+e8F1az+avtTlGk44qVRtWP1BB7eK201DeJ8icxdmFw6teTqFfEK9nhNarg+WoJZHRP5uB8qdIbTWZC70X2QZlrG7ZtbWFp2AL6ouqHPYs8OYQleUN5+4szz9paxL3AejqVUXTN2kr5ib6RwiGhDFI//WK5yvV0L/UTDVaAv5XgweMGTvvIKLZSZNJvq4AERbqLtFN+1JAfAa3geEq+lZjsydZFBTMlf/QzrR5aT8kSd/ZsOTNwVteA8LLA4QqL94cOsmw1dlAMmZxKp1zEKJDJh3uuEKbuTdjlwIDAQABo3wwejAfBgNVHSMEGDAWgBS5UekB8KkjH4pO6Q5rB9AMSi2moDAdBgNVHQ4EFgQU7qvzym2xaKbAIntsjnt3OLRlqLwwDAYDVR0TBAUwAwEBADALBgNVHQ8EBAMCBPAwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA0GCSqGSIb3DQEBCwUAA4IBAQCbMVvARHziIRiWhWXGeBjog99A40x7vU0Ls3zXcCdSQ02O3sGkTaQNBouwnYVTlZiMxjbiZs3Jhi69h9bqXblSMgSI1WaGp4Xi8R87Z2Adhtb1xZYR9TYhp47Iu93CoW3HXQQdNvvOOYzu94xHSRdrm7g64uyAK0iCqUY7mmjSbzg334LpcC0/BG9SWUnXrDpzUd9hjg9sMt5gvC7BNPgSaXE2H15cEGfhLTprtivzOmWs6LT6QqqRdOSnmoGpPB5DF9ZzUanex6Vd3b/xTSNVezMiF3VL/Q2MXBrf/1e8Exsg0narY9qDhOWlqlzY89M//nPRuVf3MJRcyf9TYBKH";
    static String pri = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDqAq3abY5KMD5ZbSw1w+0zub/Wj1xneb0KuZucTnzThBniOQl3D57wXVrP5q+1OUaTjipVG1Y/UEHt4rbTUN4nyJzF2YXDq15OoV8Qr2eE1quD5aglkdE/m4Hyp0htNZkLvRfZBmWsbtm1tYWnYAvqi6oc9izw5hCV5Q3n7izPP2lrEvcB6OpVRdM3aSvmJvpHCIaEMUj/9YrnK9XQv9RMNVoC/leDB4wZO+8gotlJk0m+rgARFuou0U37UkB8BreB4Sr6VmOzJ1kUFMyV/9DOtHlpPyRJ39mw5M3BW14DwssDhCov3hw6ybDV2UAyZnEqnXMQokMmHe64Qpu5N2OXAgMBAAECggEAFFh7SRxxXJ3pLdlCGMKaIv8pYLxCKnT5LK+KigKA89rubS7MVd+zz+4t8Rl7eWQrcLTRlWi1DPgrY0Vs496KEidtCf6plKOXpXp+S3MUqwYHD4auIeYVrVQV7kUBFQ2t6slcA3B88osIvrWzkyPUpWXTOoVkzuUR2BZ8KQgaOUJ9j/gNf0P0yCF9JDwGg1cxJWdnSP2rH9UEKm0X1QaSOfBRem4Ra+k4ALP9tBaBOjcoB2V0SWSqvmZW6MYaIHpsq5rA1FRpkr+hCJ1szqjgKZtWESPeHrzyoo7YD5sElslHXSatqRW5TwfHW9x9xA/DgfvFbBq256RlGpWvbXivsQKBgQD9QaP9vGc52bsmgP4jJGFxIm/xlVcWiBtenTOEEc3THZsOSKun7AuHKQ63E2jcv90dBGuU0h9ga2pXYYQLenRtJ9PlRKCRC0e0YZ6ZGsDzlm3VBON04P0cGYGe/X2fH8/wbA/Sl8BMgLz0mqX/P0XGogpvA16hG5uj/zyl55pJ8wKBgQDsi6nae0PdxCIdZ4/XdFQ0j8UHklj6maG+VIQ2saDl7sfKCCij0c+5X2szjikf0atKiPOhHx1zhVpTGkEXr94Tzs0BddtMFsq3d1DY0BV+T2Q4LxOQYdQ2SlB2/Txa+rAshWQ4scDK7jUQLEp0DZEBb4ltjGeHsns/5WBx26wkzQKBgHG+G2MITOxEh7NhV8J8wm7HYrODuRtbgb9apxp3zJM2xr8BkGbYssy/eeZrhzstyKcRpyetv33UgGxCVcW597RWoOplih/aixfOiCaHR4WfWDDGA0opCkSmh/raKqNe7Es0nV6d3TX+096f3lHnHvV5LWpyfvuopJxhM+HnRPpVAoGBALLXza4gcGlldx2gRULy8w9ie+eDoL6oFYAfym7H0gzGgkuf64oggdt350dtfm5OSiHqdbkmG1BSTL50JS7RiyiSvLET+KAIw2//SCMFAJslT3KjNTl+ncIGYdv1DwMCctzWZbXvvwIvI/N8aTZhY3cga1lmRIh2S3U96bNsETUpAoGALfRxJrEWZEXdAMOW26CAP5YMzF/NHbBlop3lQi32sKx4MOin2WX4ffsRdB9EIWYDm0GlhQTb4Mt7mLzIPYqAPoEAFDxqDn1O5o5GnXjbp0weUm1irR1buxcpKrADrEq3me8iHQOL8Ab4NfSk2Xu31pvzMc2uIqMwGH/X15v0vnA=";


    @Test
    @SneakyThrows
    public void pdfSign() {
        byte[] fileBytes = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\横+倒.pdf");
        byte[] sealBytes = FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\sealTest\\test3.png");
        byte[] timeBytes = FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\sealTest\\test.png");
        Map<String, Object> signPosition = new HashMap<>();
        String coordinate = "[{\"lby\":100,\"lbx\":100,\"rty\":200,\"rtx\":200,\"page\":1}]";
        String timeSealCoordinate = "[{\"lby\":100,\"lbx\":100,\"rty\":200,\"rtx\":200,\"page\":2}]";
        // 坐标
        // SignFile signFile = new PdfFile("1", pub, pri, sealBytes, fileBytes, coordinate, "", "", "", "", signPosition, 1, timeBytes, timeSealCoordinate);
        // 关键字
        // SignFile signFile = new PdfFile("2", pub, pri, sealBytes, fileBytes, "", "电子认证", "", "", "", signPosition, 1, timeBytes, timeSealCoordinate);
        // 签署域签署
        SignFile signFile = new PdfFile("3", pub, pri, sealBytes, fileBytes, "", "电子认证", "", "", "", signPosition, 1, timeBytes, timeSealCoordinate);

        byte[] sign = signFile.sign();
        FileUtil.writeBytes(sign, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\signTest\\sign.pdf");
    }

}
