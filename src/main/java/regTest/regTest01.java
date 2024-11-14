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

    @Test
    @SneakyThrows
    public void urlTest() {
        String urlPattern = "^(https?:\\/\\/)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d{1,5})?(\\/[^\\s]*)?$";
        // 可以为空
        urlPattern = "^$|^(https?:\\/\\/([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d{1,5})?(\\/[^\\s]*)?)$";
        // ip type
        urlPattern = "^$|^(https?:\\/\\/(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|https?:\\/\\/(?:\\d{1,3}\\.){3}\\d{1,3})(:\\d{1,5})?(\\/[^\\s]*)?$";
        // ip ex
        urlPattern = "^$|^(https?:\\/\\/(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|https?:\\/\\/(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\\.(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3})(:\\d{1,5})?(\\/[^\\s]*)?$";
        // /suffix
        urlPattern = "^$|^(https?:\\/\\/(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|https?:\\/\\/(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\\.(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3})(:\\d{1,5})?(\\/[^\\s]*\\/?)?$";


        String testUrl1 = "https://www.example.com";
        String testUrl2 = "http://example.com:8080/path/to/resource";
        String testUrl3 = "ftp://example.com";  // 不符合
        String testUrl4 = "C:";
        String testUrl5 = "C:/";
        String testUrl6 = "C:/a";
        String testUrl7 = "https://www.example.com/awldjajdwjaljd";
        String testUrl8 = "http://www.baidu.com";
        String testUrl9 = "";
        String testUrl10 = "http://123.11.111.1";
        String testUrl11 = "http://123.11.111.1:808";
        String testUrl12 = "http://999.11.111.1:808";
        String testUrl13 = "http://123.11.111.1:808/";
        String testUrl14 = "https://www.example.com/123123";
        String testUrl15 = "https://www.example.com/awdlj?a=123";


        System.out.println(testUrl1.matches(urlPattern));
        System.out.println(testUrl2.matches(urlPattern));
        System.out.println(testUrl3.matches(urlPattern));
        System.out.println(testUrl4.matches(urlPattern));
        System.out.println(testUrl5.matches(urlPattern));
        System.out.println(testUrl6.matches(urlPattern));
        System.out.println(testUrl7.matches(urlPattern));
        System.out.println(testUrl8.matches(urlPattern));
        System.out.println(testUrl9.matches(urlPattern));
        System.out.println(testUrl10.matches(urlPattern));
        System.out.println(testUrl11.matches(urlPattern));
        System.out.println(testUrl12.matches(urlPattern));
        System.out.println(testUrl13.matches(urlPattern));
        System.out.println(testUrl14.matches(urlPattern));
        System.out.println(testUrl15.matches(urlPattern));

    }
}
