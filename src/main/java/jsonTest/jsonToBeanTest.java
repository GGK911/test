package jsonTest;

import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/29 11:04
 */
public class jsonToBeanTest {
    @Test
    @SneakyThrows
    public void toBeanTest() {
        Map templateParam = JSONUtil.toBean("{\"B\":\"李四\",\"C\":\"张三\",\"C\":\"王二\"}", Map.class);
        String mapStr = JSONUtil.toJsonPrettyStr(templateParam);
        System.out.println(String.format("%-16s", "mapStr>> ") + mapStr);
    }
}
