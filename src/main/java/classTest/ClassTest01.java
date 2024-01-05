package classTest;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author TangHaoKai
 * @version V1.0 2023-12-21 15:42
 **/
public class ClassTest01 {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("312", "123");
        map.put("123", "456");
        System.out.println(JSONUtil.parse(map));
        //  Map<String, String> TO Map<String, Object>
        Map<String, Object> toObj = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println(JSONUtil.parse(toObj));
    }
}
