package httpTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/10/30 16:39
 */
public class HttpClientTest {
    @Test
    @SneakyThrows
    public void postJsonTest() {
        String url = "http://192.168.7.6:8077/test/jsonTest";
        String jsonPayload = "{\"param1\":\"value\"}";

        try {
            String response = HttpUtil.postJson(url, jsonPayload);
            System.out.println("JSON Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @SneakyThrows
    public void postFormTest() {
        String url = "http://192.168.7.6:8077/test/formTest";
        Map<String, Object> formParams = new HashMap<>();
        formParams.put("param", "testUser1234");

        try {
            String response = HttpUtil.postForm(url, formParams);
            System.out.println("Form Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
