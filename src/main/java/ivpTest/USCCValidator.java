package ivpTest;


import cn.hutool.core.util.CreditCodeUtil;

import java.util.HashMap;
import java.util.Map;
public class USCCValidator {
    // 统一社会信用代码的长度
    private static final int USCC_LENGTH = 18;

    // 统一社会信用代码的字符集
    private static final String CHARSET = "0123456789ABCDEFGHJKLMNPQRTUWXY";

    // 字符对应的值
    private static final Map<Character, Integer> CHAR_MAP = new HashMap<>();

    static {
        for (int i = 0; i < CHARSET.length(); i++) {
            CHAR_MAP.put(CHARSET.charAt(i), i);
        }
    }

    // 权重
    private static final int[] WEIGHTS = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
    public static boolean validate(String uscc) {
        if (uscc == null || uscc.length() != USCC_LENGTH) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < USCC_LENGTH - 1; i++) {
            char c = uscc.charAt(i);
            if (!CHAR_MAP.containsKey(c)) {
                return false;
            }
            sum += CHAR_MAP.get(c) * WEIGHTS[i];
        }
        int remainder = sum % 31;
        int checkCodeValue = (31 - remainder) % 31;
        char checkCode = CHARSET.charAt(checkCodeValue);
        return checkCode == uscc.charAt(USCC_LENGTH - 1);
    }
    public static void main(String[] args) {
        // 测试示例
        String testUSCC = "91320303MAD88XU88W";
        boolean creditCode = CreditCodeUtil.isCreditCode(testUSCC);
        System.out.println(creditCode);
        if (validate(testUSCC)) {
            System.out.println(testUSCC + " 是有效的统一社会信用代码。");
        } else {
            System.out.println(testUSCC + " 是无效的统一社会信用代码。");
        }
    }
}