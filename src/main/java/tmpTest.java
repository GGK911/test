import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/2 9:54
 */
public class tmpTest {
    @Test
    @SneakyThrows
    public void strToFile() {
        Path path = Paths.get("target/DAECCKey.dat");

        String str = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000107002a0b30e0d8a25284cfbaed1aac287b13ad9d80271d9c03ef040230dac34e6efc08dc1a03c87dd13c0583ec531ba14964dafa8a3623b4b4f3ced2bb77ee73b8645547422b780b1375c44b2c87bcea3c02b436113e286222651287fad2a230f358";
        FileUtil.writeBytes(Hex.decode(str), path.toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void cutFile() {
        byte[] bytes = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\test02.pdf");
        byte[] content = new byte[268328 - 247846];
        System.arraycopy(bytes, 247846, content, 0, content.length);
        System.out.println(new String(content, StandardCharsets.UTF_8));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class Extension {
        String extName;
        String extOID;
        String extValue;
    }

    @Test
    @SneakyThrows
    public void mapJsonTest() {
        Extension extension1 = new Extension("客户自定义的扩展", "1.2.3.4.5.6.7.8.9.0", "test1");
        Extension extension2 = new Extension("客户自定义的扩展", "1.2.3.4.5.6.7.8.9.0", "test2");
        Extension extension3 = new Extension("客户自定义的扩展", "1.2.3.4.5.6.7.8.9.0", "test3");
        List<Extension> list = Arrays.asList(extension1, extension2, extension3);
        String listJsonStr = JSONUtil.toJsonStr(list);
        System.out.println(String.format("%-16s", "listJsonStr>> ") + listJsonStr);
    }

    @Test
    @SneakyThrows
    public void mapJsonTest2() {
        TreeMap<String, Object> certExts = new TreeMap<>();
        certExts.put("1.2.3", "test1");
        String listJsonStr = JSONUtil.toJsonStr(certExts);
        System.out.println(String.format("%-16s", "listJsonStr>> ") + listJsonStr);
    }

}
