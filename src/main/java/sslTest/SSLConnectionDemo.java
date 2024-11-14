package sslTest;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

/**
 * 双向SSL认证
 *
 * @author TangHaoKai
 * @version V1.0 2024/9/10 9:05
 */
public class SSLConnectionDemo {

    public static void main(String[] args) {
        // 客户端JKS文件路径
        Path path = Paths.get("src/main/java/sslTest/jks", "BJZZ231128.jks");
        // JKS文件的密码
        String keystorePassword = "11111111";
        // 请求url
        String targetUrl;
        // 请求参数
        String param;
        // 申请
        targetUrl = "https://test.mcsca.com.cn:806/cms/CAShare/certService/dualCertApplyAndDownload";
        param = "{\n" +
                "    \"transactionTime\": \"20240903182359000\",\n" +
                "    \"transactionChannelCode\": \"1\",\n" +
                "    \"transactionPlatformCode\": \"1\",\n" +
                "    \"certTypeCode\": \"1026\",\n" +
                "    \"publicKeyAlgorithm\": \"sm2\",\n" +
                "    \"keyLength\": \"256\",\n" +
                "    \"certValidity\": \"100\",\n" +
                "    \"personalOrOrgName\": \"唐好凯\",\n" +
                "    \"idCardOrLicenceType\": \"1\",\n" +
                "    \"idCardOrLicenceCode\": \"371724200206052210\",\n" +
                "    \"phoneNumber\": \"13983053455\",\n" +
                "    \"email\": \"13983053455@163.com\",\n" +
                "    \"csr\": \"MIIBQzCB6QIBADCBhjEPMA0GA1UECAwG6YeN5bqGMQ8wDQYDVQQHDAbph43luoYxIjAgBgkqhkiG9w0BCQEWEzEzOTgzMDUzNDU1QDE2My5jb20xCzAJBgNVBAYTAkNOMQ8wDQYDVQQKEwZHR0s5MTExDzANBgNVBAsTBkdHSzkxMTEPMA0GA1UEAxMGR0dLOTExMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEW3VeY0oztsoS7NoA81DnenrOn4sELm9bIEmAwHgPtdlojeH1RPtMRc4loRqR+OaX8qnl91p0hIlKIHxPJlKl96AAMAoGCCqBHM9VAYN1A0kAMEYCIQDrn9DcrlhPRlF8Pc+7Hsihv22wo8gV/nT1J/b/5lsgXAIhANO6lmRYTBsszPnE5ZPL687r779JjdUX3EhysRyLD7/t\"\n" +
                "}";

        // 心跳
        targetUrl = "https://test.mcsca.com.cn:806/cms/CAShare/certService/dualCertServiceHealthCheck";
        targetUrl = "http://183.66.184.22:803/cms-ora-interface/CAShare/certService/dualCertServiceHealthCheck";
        param = "{\n" +
                "    \"transactionTime\": \"20240903182359000\"\n" +
                "}";

        // 区分双向ssl，还是普通
        if (targetUrl.contains("https")) {
            try {
                PrintWriter out = null;
                // 初始化KeyStore对象
                KeyStore keyStore = KeyStore.getInstance("JKS");
                try {
                    InputStream fis = new ByteArrayInputStream(Files.readAllBytes(path));
                    keyStore.load(fis, keystorePassword.toCharArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 初始化KeyManagerFactory对象
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(keyStore, keystorePassword.toCharArray());

                // 初始化TrustManagerFactory对象
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);

                // 初始化SSLContext对象
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                // 创建连接
                URL url = new URL(targetUrl);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(sslContext.getSocketFactory());

                // 设置请求方法
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                //总是要通过getInputStream() 需从服务端获得响应所以setDoInput()默认是true；
                conn.setDoOutput(true);
                conn.setDoInput(true);
                // Post 请求不能使用缓存
                conn.setUseCaches(false);
                //设置连接主机超时 10s
                conn.setConnectTimeout(10000);
                //设置连接读取信息超时 10s
                conn.setReadTimeout(10000);
                //打开连接
                conn.connect();
                //获取HttpURLConnection对象对应的输出流（设置请求编码为UTF-8）
                //获取服务端发送的信息：conn.getOutputStream()
                out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8));
                // 发送请求参数
                out.print(param);
                // flush输出流的缓冲
                out.flush();

                // 获取响应码
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) { // 成功状态码
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    System.out.println(content);
                } else {
                    System.err.println("请求失败: HTTP 错误代码 " + responseCode);
                }

                // 关闭连接
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
