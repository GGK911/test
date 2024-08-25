package httpTest;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/16 14:51
 */

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestUtility {

    public static void main(String[] args) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("params1", "123");
        params.put("params2", "321");
        String result = sendFormDataRequest("http://127.0.0.1:8077/test/formTest", params);
        System.out.println(String.format("%-16s", "result>> ") + result);
    }

    public static String sendFormDataRequest(String url, Map<String, String> params) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // Add parameters
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.APPLICATION_FORM_URLENCODED);
        }

        HttpEntity entity = builder.build();
        post.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(post);
        String result = EntityUtils.toString(response.getEntity());
        response.close();

        return result;
    }
}
