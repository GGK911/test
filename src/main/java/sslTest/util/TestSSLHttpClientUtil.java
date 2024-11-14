package sslTest.util;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestSSLHttpClientUtil {

    private static final String TARGET_URL = "https://test.mcsca.com.cn:806/cms/bus/orderApply/save";
    // private static final String TARGET_URL = "https://test.mcsca.com.cn:806/cms/CAShare/certService/dualCertApplyAndDownload";
    public static void main(String[] args) {
        try {
            String targetUrl = TARGET_URL;
            String param = "{ \"transactionTime\": \"20240903182359000\"}";

            // 将 JKS 文件加载为 byte[] 数组
            byte[] jksData = Files.readAllBytes(Paths.get("src/main/java/sslTest/jks/BJZZ231128.jks"));
            String password = "11111111";

            // 调用工具类发送请求
            String response = SSLHttpClientUtil.sendPostRequest(targetUrl, param, jksData, password);
            System.out.println("响应内容：" + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
