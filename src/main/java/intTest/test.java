package intTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/9 13:46
 */
public class test {
    @Test
    @SneakyThrows
    public void negativeInt() {
        Integer a = 1;
        int size = a.intValue();
        size = (size - 6) / 12;
        System.out.println("读取配置文件，装载服务器 " + (size) + " 个服务器");
    }
}
