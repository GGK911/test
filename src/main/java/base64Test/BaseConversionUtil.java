package base64Test;

import org.apache.commons.lang3.StringUtils;


/**
 * 进制转换
 *
 * @author : zouhui
 * @date : 2020-09-15
 **/
public class BaseConversionUtil {

    /**
     * 初始化 62 进制数据，索引位置代表字符的数值，比如 A代表10，z代表61等
     */
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SCALE = 62;

    /**
     * 10进制转为62进制
     *
     * @param num Long 型数字
     * @return 62进制字符串  如果不够6个长度，左侧填充0，例:000asf
     */
    public static String base62Encode(long num) {
        StringBuilder sb = new StringBuilder();
        int remainder;
        while (num > SCALE - 1) {
            remainder = Long.valueOf(num % SCALE).intValue();
            sb.append(CHARS.charAt(remainder));
            num = num / SCALE;
        }
        sb.append(CHARS.charAt(Long.valueOf(num).intValue()));
        String value = sb.reverse().toString();
        return StringUtils.leftPad(value, 6, '0');
    }

    /**
     * 62进制字符串转为10进制
     *
     * @param str 编码后的62进制字符串
     * @return 解码后的 10 进制字符串
     */
    public static long base62Decode(String str) {
        str = str.replace("^0*", "");
        long num = 0;
        int index;
        for (int i = 0; i < str.length(); i++) {
            index = CHARS.indexOf(str.charAt(i));
            num += (long) (index * (Math.pow(SCALE, str.length() - i - 1)));
        }
        return num;
    }
}
