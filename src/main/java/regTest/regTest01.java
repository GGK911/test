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
        String regex = "^(?:RSA|SM2|rsa|sm2|01|\\s*)$";
        Pattern pattern = Pattern.compile(regex);

        // 测试字符串
        String[] testStrings = {
                "RSA",
                "SM2",
                "rsa",
                "sm2",
                "01",
                " 01 ",
                "01 ",
                " SM2",
                "sm2 ",
                "RSA ",
                " ",
                "sm3",
                "02",
                "SM2&RSA"
        };

        // 匹配字符串
        for (String testString : testStrings) {
            Matcher matcher = pattern.matcher(testString);
            if (matcher.matches()) {
                System.out.println("Matched: " + testString);
            } else {
                System.out.println("Not Matched: " + testString);
            }
        }
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
