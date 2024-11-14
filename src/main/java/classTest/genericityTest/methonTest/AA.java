package classTest.genericityTest.methonTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/10/19 15:33
 */
public class AA<U> {

    public <K, U> Map<K, U> getMap(K k, U u) {
        HashMap<K, U> kuHashMap = new HashMap<>();
        kuHashMap.put(k, u);
        return kuHashMap;
    }

    public <T> U getU(T t, U u) {
        return u;
    }

    // 这是一个简单的泛型方法
    public static <T> T add(T x, T y) {
        return y;
    }

}
