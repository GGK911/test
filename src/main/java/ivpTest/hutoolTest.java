package ivpTest;

import cn.hutool.core.util.CreditCodeUtil;
import cn.hutool.core.util.IdcardUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/18 14:59
 */
public class hutoolTest {
    @Test
    @SneakyThrows
    public void person() {
        for (int i = 0; i < 11; i++) {
            String id = ("37172420100605221" + (i == 10 ? "X" : i)).toUpperCase(); // 示例身份证号码
            // 输出结果
            if (IdcardUtil.isValidCard(id)) {
                System.out.println(id + "YES");
            } else {
                System.out.println(id + "NO");
            }
        }

    }

    @Test
    @SneakyThrows
    public void ent() {
        if (CreditCodeUtil.isCreditCode("91330101589882738D")) {
            System.out.println("YES");
        } else {
            System.out.println("NO");
        }
    }

    @Test
    @SneakyThrows
    public void getAge() {
        int ageByIdCard = IdcardUtil.getAgeByIdCard("371724201006052210");
        System.out.println(ageByIdCard);
    }

    @Test
    @SneakyThrows
    public void test() {
        String applyCardNo = "371724201099052219";
        // 校验位
        try {
            boolean validCard18 = IdcardUtil.isValidCard18(applyCardNo);
            if (validCard18) {
                // 18岁
                int ageByIdCard;
                try {
                    ageByIdCard = IdcardUtil.getAgeByIdCard(applyCardNo);
                } catch (Exception e) {
                    System.out.println("申请人身份证规则不符");
                    return;
                }
                if (ageByIdCard < 18) {
                    System.out.println("申请人年龄不合法");
                    return;
                }
            } else {
                System.out.println("申请人身份证非法");
                return;
            }
        } catch (Exception e) {
            System.out.println("申请人身份证规则不符");
            return;
        }
    }


}
