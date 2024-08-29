package redisTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springTest.SpringTestApplication;
import springTest.util.RedisUtil;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/28 17:21
 */
@SpringBootTest(classes = SpringTestApplication.class)
public class NumberTest {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    @SneakyThrows
    public void numberCountTest() {
        System.out.println("start");
        redisUtil.set("testKey", "唐好凯");
        String testKey = redisUtil.get("testKey");
        System.out.println(String.format("%-16s", "testKey>> ") + testKey);

        // 加0
        Long number = redisUtil.incrBy("number", 0);
        System.out.println(String.format("%-16s", "number>> ") + number);
        // 加1
        number = redisUtil.incrBy("number", 1);
        System.out.println(String.format("%-16s", "number>> ") + number);
        // 加10
        number = redisUtil.incrBy("number", 10);
        System.out.println(String.format("%-16s", "number>> ") + number);
        // 获取key
        testKey = redisUtil.get("number");
        // clean
        redisUtil.delete("number");
        System.out.println(String.format("%-16s", "testKey1>> ") + testKey);
        // 获取key
        testKey = redisUtil.get("number");
        System.out.println(String.format("%-16s", "testKey1>> ") + testKey);
        // 加0
        number = redisUtil.incrBy("number", 0);
        System.out.println(String.format("%-16s", "number>> ") + number);



    }

}
