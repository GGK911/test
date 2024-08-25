package httpTest;

import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/8 14:14
 */
public class springFrameWorkPost {
    @Test
    @SneakyThrows
    public void post() {
        RestTemplate restTemplate = new RestTemplate();


        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("test01", "test01");
        requestBody.put("test02", "test02");
        RequestEntity<Map<String, String>> request = RequestEntity.post("http://127.0.0.1:8077/test/jsonTest").accept(MediaType.APPLICATION_JSON).body(requestBody);
        Map<String, String> body = request.getBody();
        System.out.println(JSONUtil.toJsonStr(body));

        ResponseEntity<byte[]> entity = restTemplate.exchange("http://127.0.0.1:8077/test/jsonTest", HttpMethod.POST, request, byte[].class);
        if (entity.getStatusCode().is2xxSuccessful()) {
            byte[] entityBody = entity.getBody();
            System.out.println(new String(entityBody, StandardCharsets.UTF_8));
        }
    }
}
