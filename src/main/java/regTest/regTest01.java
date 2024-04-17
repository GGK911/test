package regTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TangHaoKai
 * @version V1.0 2024/3/18 15:10
 */
public class regTest01 {
    /**
     * 正则表达式能做到匹配字符串，不为空匹配'RSA'和'SM2'，为空则通过吗？
     */
    @Test
    @SneakyThrows
    public void regTest() {
        String input1 = ""; // 测试空字符串情况
        String input2 = "RSA";
        String input3 = "02";
        String input4 = "01";

        String regex = "^(?:RSA|SM2|rsa|sm2|01\\b|\\s*)$";
        Pattern pattern = Pattern.compile(regex);

        System.out.println("Input: " + input1);
        System.out.println("Matched: " + matchPattern(pattern, input1));

        System.out.println("Input: " + input2);
        System.out.println("Matched: " + matchPattern(pattern, input2));

        System.out.println("Input: " + input3);
        System.out.println("Matched: " + matchPattern(pattern, input3));

        System.out.println("Input: " + input4);
        System.out.println("Matched: " + matchPattern(pattern, input4));
    }

    private static boolean matchPattern(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    @Test
    @SneakyThrows
    public void regTest2() {
        String input4 = "";

        String reg = "[a-zA-Z0-9]+";
        Pattern pattern = Pattern.compile(reg);
        System.out.println("Input: " + input4);
        System.out.println("Matched: " + matchPattern(pattern, input4));

    }
}
