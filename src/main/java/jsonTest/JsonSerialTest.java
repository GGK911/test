package jsonTest;

import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/3 14:48
 */
public class JsonSerialTest {
    @Test
    @SneakyThrows
    public void fastTest() {
        Person person = new Person("f", "e", "d", "c", "b", "a", "g", "h", "i", "a");

        String jsonString = JSONObject.toJSONString(person, SerializerFeature.SortField, SerializerFeature.SortField);
        System.out.println(jsonString);
        System.out.println(JSONUtil.toJsonPrettyStr(jsonString));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static public class Person {
        private String aa;
        private String bb;
        private String cc;
        private String d;
        private String ab;
        private String ac;
        private String aac;
        private String abb;
        private String abc;
        private String ca;
    }

    @Test
    @SneakyThrows
    public void epactTest() {
        Person person = new Person("f", "e", "d", "c", "b", "a", "g", "h", "i", "a");
        Map<String, Object> signMap = new HashMap<>(2);
        // 进行签名
        signMap.put("reqHead", person);
        signMap.put("reqBody", "1234");
        String sortJsonStr = SignatureUtil.getSortJsonStr(signMap);
        System.out.println(sortJsonStr);
    }
}
