package sslTest;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyStore;

public class SSLConnectionDemo2 {

    private static final String KEYSTORE_PATH = "src/main/java/sslTest/jks/BJZZ231128.jks";
    private static final String KEYSTORE_PASSWORD = "11111111";
    private static final String TARGET_URL = "https://test.mcsca.com.cn:806/cms/bus/orderApply/save";
    // private static final String TARGET_URL = "https://test.mcsca.com.cn:806/cms/CAShare/certService/dualCertApplyAndDownload";
    private static final String PARAM = "{\n" +
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

    public static void main(String[] args) {
        try {
            CloseableHttpClient httpClient = TARGET_URL.startsWith("https")
                    ? createHttpClientWithSSL()
                    : HttpClients.createDefault();

            sendRequest(httpClient, TARGET_URL, PARAM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendRequest(CloseableHttpClient httpClient, String url, String jsonParam) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(jsonParam, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                System.out.println("Response: " + responseBody);
            } else {
                System.err.println("Request failed with HTTP status: " + statusCode);
            }
        }
    }

    private static CloseableHttpClient createHttpClientWithSSL() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream keyStoreStream = Files.newInputStream(new File(KEYSTORE_PATH).toPath())) {
            keyStore.load(keyStoreStream, KEYSTORE_PASSWORD.toCharArray());
        }

        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(keyStore, KEYSTORE_PASSWORD.toCharArray())
                .loadTrustMaterial(keyStore, null)
                .build();

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .build();
    }
}

