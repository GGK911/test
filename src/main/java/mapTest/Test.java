package mapTest;

import lombok.SneakyThrows;

import java.util.HashMap;

/**
 * MAP数据结构测试
 *
 * @author TangHaoKai
 * @version V1.0 2024/4/26 12:36
 */
public class Test {

    /**
     * 获取空
     */
    @org.junit.jupiter.api.Test
    @SneakyThrows
    public void getNullTest() {
        HashMap<Object, Object> map = new HashMap<>();
        Object o = map.get("123");
    }

}
