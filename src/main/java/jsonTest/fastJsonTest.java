package jsonTest;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/5 15:31
 */
public class fastJsonTest {

    @Test
    @SneakyThrows
    public void test() {
        String param = "{\"aa\":\"f\",\"aac\":\"g\",\"ab\":\"b\",\"abb\":\"h\",\"abc\":\"i\",\"ac\":\"a\",\"bb\":\"e\",\"ca\":\"a\",\"cc\":\"d\",\"d\":\"c\"}";
        JSONObject jsonObject = JSONObject.parseObject(param);
        String jsonString = JSONObject.toJSONString(jsonObject, SerializerFeature.SortField, SerializerFeature.SortField);
        System.out.println(jsonString);
    }
}
