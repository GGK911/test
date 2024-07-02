package httpTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

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
}
