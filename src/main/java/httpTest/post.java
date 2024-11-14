package httpTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 电子签章请求签署文件超时测试
 *
 * @author TangHaoKai
 * @version V1.0 2024/7/2 10:39
 */
public class post {

    @Test
    @SneakyThrows
    public void postTest() {
        Map<String, Object> form = new HashMap<>();
        form.put("reqHead", "{\n" +
                "    \"version\": \"2.2.0\",\n" +
                "    \"appId\": \"1807957784533213184\",\n" +
                "    \"reqTime\": \"20240108160955000\",\n" +
                "    \"customerId\": \"1016\",\n" +
                "    \"productAppId\": \"\",\n" +
                "    \"serialNo\": \"122121133114\"\n" +
                "}");
        form.put("reqBody", "{\n" +
                "    \"userId\":\"1807963497414287360\",\n" +
                "    \"smsCode\":\"\",\n" +
                "    \"coordinate\":[\n" +
                "        {\"lby\":100,\"lbx\":100,\"rty\":200,\"rtx\":200,\"page\":1},\n" +
                "        {\"lby\":100,\"lbx\":100,\"rty\":200,\"rtx\":200,\"page\":2}\n" +
                "    ]\n" +
                "}");
        form.put("sign", "MEQCIFLqmhTde1Xo7lJonGBnAjPVzXoopnj3obfFy7fi8RZLAiA4JPw7nWzCTuk3Dx8jTwI3AHNJkSpSVKEpA54XvK/G4A==");
        form.put("sealFile", FileUtil.file("C:\\Users\\ggk911\\Desktop\\测试图片\\THK.png"));
        // form.put("contractFile", FileUtil.file("C:\\Users\\ggk911\\Desktop\\测试文件\\102页PDF.pdf"));
        form.put("contractFile", FileUtil.file("C:\\Users\\ggk911\\Desktop\\测试文件\\域模板.pdf"));

        HttpRequest request = HttpRequest.post("https://ess.mcsca.com.cn/ess/busi/sign/userId/coordinate").form(form);
        // 10s 连接超时
        request.setConnectionTimeout(1000 * 10);
        // 10s 读取超时
        request.setReadTimeout(1000 * 10);

        final long start = System.currentTimeMillis();
        HttpResponse response = request.execute();
        System.out.println("耗时：" + (System.currentTimeMillis() - start));

    }

    @Test
    @SneakyThrows
    public void downloadTest() {
        HttpResponse response = HttpRequest.post("http://localhost:7777/epact/api/download/downLoadSignFile")
                .header("Content-Type", "multipart/form-data")
                .form("reqParam", "{\n" +
                        "    \"reqBody\":{\n" +
                        "        \"pactFileId\":\"1821003250757849088\",\n" +
                        "        \"orderNo\":\"\"\n" +
                        "    },\n" +
                        "    \"reqHead\":{\n" +
                        "        \"appId\":\"1205323297932181504\",\n" +
                        "        \"customerId\":\"1013\",\n" +
                        "        \"reqTime\":\"20230530143515000\",\n" +
                        "        \"serialNo\":\"1234567\",\n" +
                        "        \"version\":\"1.0\"\n" +
                        "    },\n" +
                        "    \"sign\":\"\"\n" +
                        "}")
                // .form("file", new File("C:\\Users\\ggk911\\Desktop\\03移动数字证书(CA)互认技术标准_CA数字证书系统接入指南-0822.pdf"))
                .execute();
        String body = response.body();
        System.out.println(JSONUtil.parse(body).toStringPretty());
    }

    @Test
    public void downLoadTest2() {
        String param = "{\n" +
                "    \"reqBody\":{\n" +
                "        \"pactFileId\":\"1821003250757849088\",\n" +
                "        \"orderNo\":\"\"\n" +
                "    },\n" +
                "    \"reqHead\":{\n" +
                "        \"appId\":\"1205323297932181504\",\n" +
                "        \"customerId\":\"1013\",\n" +
                "        \"reqTime\":\"20230530143515000\",\n" +
                "        \"serialNo\":\"1234567\",\n" +
                "        \"version\":\"1.0\"\n" +
                "    },\n" +
                "    \"sign\":\"\"\n" +
                "}";

        // 创建HttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 设置目标URL
            String url = "http://localhost:7777/epact/api/download/downLoadSignFile";
            HttpPost httpPost = new HttpPost(url);

            // 构建表单请求
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            // 添加表单字段，使用默认的content type (multipart/form-data)
            builder.addTextBody("reqParam", param, ContentType.create("application/octet-stream"));
            // builder.addTextBody("reqParam", param, ContentType.TEXT_PLAIN.withCharset("UTF-8"));

            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            // 执行请求
            org.apache.http.HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                System.out.println(EntityUtils.toString(responseEntity, java.nio.charset.StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
