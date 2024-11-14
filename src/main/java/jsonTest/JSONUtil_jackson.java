package jsonTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public class JSONUtil_jackson {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 配置 ObjectMapper 忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // 对象转 JSON 字符串
    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null; // 或者抛出一个自定义异常
        }
    }

    // JSON 字符串转对象
    public static <T> T fromJsonString(String jsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null; // 或者抛出一个自定义异常
        }
    }

    // JSON 字符串转带泛型的 List
    public static <T> List<T> fromJsonStringToList(String jsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null; // 或者抛出一个自定义异常
        }
    }

    // 从 JSON 字符串中获取指定 key 的值
    public static String getValue(String jsonString, String key) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            return Optional.ofNullable(rootNode.get(key))
                    .map(JsonNode::asText)
                    .orElse(null); // 或者返回默认值
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null; // 或者抛出一个自定义异常
        }
    }

    // 从嵌套 JSON 字符串中获取值
    public static String getNestedValue(String jsonString, String parentKey, String childKey) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode parentNode = rootNode.path(parentKey);
            return removeQuotes(parentNode.path(childKey).toString()); // 或者返回默认值
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null; // 或者抛出一个自定义异常
        }
    }

    // 去掉字符串前后的引号
    public static String removeQuotes(String str) {
        if (str == null) {
            return null;
        }
        // 使用正则表达式去掉前后的引号
        return str.replaceAll("^\"|\"$", "");
    }
}

