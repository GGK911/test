package charSetTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author TangHaoKai
 * @version V1.0 2024/10/17 17:47
 */
public class changeTest {
    @Test
    @SneakyThrows
    public void changeTest() {
        byte[] textBytes = "12345abcdefgABCDEFG唐好凯{}[],./!@#$%^&*()-_=+\\".getBytes(StandardCharsets.UTF_8);

        System.out.println(new String(textBytes, StandardCharsets.ISO_8859_1));
        System.out.println(new String(textBytes, StandardCharsets.UTF_8));
        System.out.println(new String(textBytes, StandardCharsets.US_ASCII));
        System.out.println(new String(textBytes, StandardCharsets.UTF_16));
        System.out.println(new String(textBytes, CharsetUtil.CHARSET_GBK));
    }

    @Test
    @SneakyThrows
    public void httpTest() {
        HttpResponse response = HttpRequest.post("http://192.168.7.6:8077/test/cert/applyAndDown").execute();
        byte[] bodyBytes = response.bodyBytes();
        String charset = response.charset();

        System.out.println(new String(bodyBytes, StandardCharsets.ISO_8859_1));
        System.out.println(new String(bodyBytes, StandardCharsets.UTF_8));
        System.out.println(new String(bodyBytes, StandardCharsets.US_ASCII));
        System.out.println(new String(bodyBytes, StandardCharsets.UTF_16));
        System.out.println(new String(bodyBytes, CharsetUtil.CHARSET_GBK));
    }

    @Test
    @SneakyThrows
    public void fileTest() {
        byte[] bytes = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\新文件 1.txt");
        System.out.println(new String(bytes, StandardCharsets.ISO_8859_1));
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
        System.out.println(new String(bytes, StandardCharsets.US_ASCII));
        System.out.println(new String(bytes, StandardCharsets.UTF_16));
        System.out.println(new String(bytes, CharsetUtil.CHARSET_GBK));
    }

}
