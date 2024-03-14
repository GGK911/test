package stringTest;

import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.util.EncryptUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Test
    @SneakyThrows
    public void subTest() {
        String dn = "cn=123,o=312";
        int i = dn.indexOf(",");
        String en = dn.substring(i + 1);
        String substring = dn.substring(0, i);
        System.out.println(en);
        System.out.println(substring);

    }

    @Test
    @SneakyThrows
    public void splitTest() {
        String dn = "CN=TperRSAx1@唐好凯@01@007,SERIALNUMBER=371724200206052210,OU=localRA,O=MCSCA,C=CN";
        String input = dn;
        String[] substrings = input.split(",");
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = substrings.length - 1; i >= 0; i--) {
            resultBuilder.insert(0, substrings[i]);
            System.out.println(resultBuilder);
            if (i > 0) {
                resultBuilder.insert(0, ",");
            }
        }

    }

    @Test
    @SneakyThrows
    public void test() {
        String pri = "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";
        String sign = "";
        String aes = "ViZt5EBpISHyNFhd18c1iIg1GQCYey5hgpjUuHqUGO+s7wB/9Yt8v9s77qcLIbnQf9lUEXLx0yP77+w4Fbg8dan7L57SC/3gNbuqoRFmVMSkBbwin4nQo6PnflhtPuuYq+eCVsLUkbI/vrQatib/waWgqy3QAoj/wRKaLyiIIhkI/siQC781DxXAqt4vmkriGPteTEkAVx1AqUI5Avn9vEbQvqPm7GkkPnJQd1gp/fC6mhEETNC/SfqMIgGH8RMWiF9wgQPbiRPveGIJGrVjDzFSHwfQjGiKGriUchF2uO3NJY0SSCy1bVlO6NPr2x0YBZlgS25+uOW5wOlG+ZNp8g==";
        try {
            String privateKey = EncryptUtil.decrypt("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCtOM6sFUxBoVYglTlHIHpVBG/385Lawtohwu88YeOn7uJcY6yTfKfWdsfYEMrMa/Pfvp3uC+Et7ECzXlkM4YqeWHtGyxtLRXVXXlADi4IsbFEMUNhegrEMyq/yvees5ECsnxAFhug6w4f+CBhepWwDCwu0MALa5xnfVYSF9iBz3fXWpixdRRnaEs5dzgZWy3kJ2fJ/mTWbicm7AjvRuGJujsE2qqJtXcOrDKU/9ohAx1ZAW+nB0Dxs7/xOUzLZPJwYLxO8uiJiYCbxvnbrLF1aYKlpBCD6BbQF2mJwjlll0S93BzKZcYvYYYIj1obtq97O8znGjcuRgMQs8Ey+VB+vAgMBAAECggEAPdtaWjMkzw74/ZusH40mgjOadFXDrGEGmiXNXqeqLy7sIIfreaN7H+e8x5h/gu5N4SllpjsRx19lX2girqnf4VnBc+9VqNR96ZwhQJLSAmEPtDEugtlythmvKSTNlXzQ55PJmd+qEEoAxyNG6I1z+8Y3ALpgWqFKKOmV8GyK/DTAqjJJi/j7PyqLqqpQuqGs87Rjs/qbN8ScGs8AujDC+eadXGgmVk6AtRr1wkf1MpQp+xpBm6QYQTnqlCxmIluTMlLPmj9GhgxFsrgDWAVp6+/9AiRx6gSfu3H1+6ASQ30B7cusd73ZtbFayLfMa8QPUQMMgByaTypTTgyN4JOYEQKBgQDqyaw8JEdEOeHXbEA+vnEsuuKCqZ+noOOQibeweeBxUEHt0nfIo9vutiI8QVMONOJveqawk8qfiIUYaVte3zhClLYk9rys54gNMJjIXYr0WbbnUehMLSVUhBZymz6T7aBaWyHyf/97vYlcmJKHoCew8FZrEpXwstghLMuf4cYZyQKBgQC83zK2oI3UfItJsWuGrJL3KCyHeHHpZrcdQBcVHWRwBdALWpmJJwkOiB9PvtBgCgUIjyoj3HoGPXJBDxUj/EYH6VWJITW1ZDxmpKelCUCPFmkOUjS3YYGty378MAmDS3WLlKnyzTAbrt31+0AXMqU8OHANI8WvZtFNSozg9iKptwKBgGgXFvvm3Y2a18xI2sa2abh59jgVeYm4o4sN81kS/3VdLo2AVMioFLZlGxJ5p5fRzF2+E66PJzLJNLCY7QBHmEq0YXhLx2QklcW7ONED37nrGFK/lmxHS5iHougWeYzducy1QHyhUKQMaJybq8LjNxWTx8xahg0bTQSQNopgbxI5AoGBAJsXzVUaUl0CSH6jKmDUpXo/ixFTXncC2aszTcEQ+cDjhQtNwnZVj6JXNR8O2Z2DnM6CgWAhVDJ7kq7J69o49mjYulx44NmrDc5bty5WgqT9CheweYl8kDheuk/sQmOGO2f7E/NFexPAbJPpVZ+2/uiMj7a6gUKfc4+8gCLa+2vRAoGAInx8ASLZEGZkG98SKT111Fx3/VJn+XV4qpqu1rOCA1rgZaggqiIYBcAR0+K6YpSWflb7uFtHEjVwA7y86CKyU81yxkMcogDyZ/PG24isjqaEo8KO8vQqQpTjHJxCZBLp1k7gNrM0aoQuXblCH4jrEfwhcjumjv0HYy9FoOSzt4o=", aes, pri);
            System.out.println(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
