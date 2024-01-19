package stringTest;

import org.junit.jupiter.api.Test;

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
}
