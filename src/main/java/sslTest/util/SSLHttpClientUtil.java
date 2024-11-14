package sslTest.util;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

public class SSLHttpClientUtil {

    /**
     * 发送 HTTP/HTTPS POST 请求
     *
     * @param targetUrl 请求的目标 URL
     * @param param     请求参数 JSON 字符串
     * @param jksData   客户端 JKS 文件的字节数组（用于 HTTPS 双向认证），若为 HTTP 请求可传入 null
     * @param password  JKS 密码，若 jksData 为 null 可忽略
     * @return 请求响应内容
     * @throws Exception 如果请求失败
     */
    public static String sendPostRequest(String targetUrl, String param, byte[] jksData, String password) throws Exception {
        try (CloseableHttpClient httpClient = targetUrl.startsWith("https")
                ? createHttpClientWithSSL(jksData, password)
                : HttpClients.createDefault()) {

            HttpPost httpPost = new HttpPost(targetUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                } else {
                    throw new RuntimeException("请求失败，HTTP 状态码：" + statusCode);
                }
            }
        }
    }

    /**
     * 创建带 SSL 的 HttpClient，用于双向 SSL 认证
     *
     * @param jksData  JKS 文件的字节数组
     * @param password JKS 密码
     * @return 配置了 SSL 的 HttpClient
     * @throws Exception 如果 SSL 配置失败
     */
    private static CloseableHttpClient createHttpClientWithSSL(byte[] jksData, String password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (ByteArrayInputStream bais = new ByteArrayInputStream(jksData)) {
            keyStore.load(bais, password.toCharArray());
        }

        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(keyStore, password.toCharArray())
                .loadTrustMaterial(keyStore, null)
                .build();

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .build();
    }
}

