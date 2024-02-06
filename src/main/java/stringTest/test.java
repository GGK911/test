package stringTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;

/**
 * @author TangHaoKai
 * @version V1.0 2024/1/15 15:33
 **/
public class test {
    @Test
    public void formatTest() {
        String name = "John";
        int age = 25;

        // 使用固定长度的空格占位符
        String formattedString = String.format("Name: %-10s | Age: %03d", name, age);

        // 打印格式化后的字符串
        System.out.println(formattedString);
    }

    @Test
    @SneakyThrows
    public void formatTest02() {
        String name = "John";
        int age = 25;

        // 使用固定长度的空格占位符
        String reg = "Name: %-" + 10 + "s | Age: %03d";
        String formattedString = String.format(reg, name, age);

        // 打印格式化后的字符串
        System.out.println(formattedString);

        System.out.println(URLEncoder.encode("大陆云盾电子认证服务有限公司数字证书订户协议", "UTF-8"));
    }
}
