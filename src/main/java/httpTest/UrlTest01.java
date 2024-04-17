package httpTest;

import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/1 10:38
 */
public class UrlTest01 {
    @Test
    @SneakyThrows
    public void postTest() {
        Map<String, Object> body = new HashMap<>(16);
        body.put("appId", "mcsca-4qsnlfee12");
        String post = HttpUtil.post("http://183.66.184.22:803/unifiedaccount/admin/sysAppInfo/api/v1/findPublicKeyByAppId", body);
        System.out.println(post);
    }
}
