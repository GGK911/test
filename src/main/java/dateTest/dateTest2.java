package dateTest;

import cn.hutool.core.date.DateUtil;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.util.Date;

/**
 * 时间戳前后测试
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-30 16:26
 **/
public class dateTest2 {
    public static void main(String[] args) {
        long now = System.currentTimeMillis();
        long yesterday = DateUtil.yesterday().getTime();
        System.out.println("NOW:" + now);
        System.out.println("YES:" + yesterday);

        Date certStartTime = DateUtil.parse("2023-11-30 12:00:00");
        Date now2 = DateUtil.parse("2023-11-30 11:57:59");
        if (certStartTime.getTime() > DateUtil.offsetMinute(now2, 2).getTime()) {
            System.out.println("证书未生效");
        } else {
            System.out.println("证书已生效");
        }

        Date certEndTime = DateUtil.parse("2023-11-30 13:00:00");
        // Date now3 = DateUtil.parse("2023-11-30 12:57:59");
        Date now3 = DateUtil.parse("2023-11-30 12:58:00");
        if (DateUtil.offsetMinute(certEndTime, -2).getTime() < now3.getTime()) {
            System.out.println("证书过期");
        } else {
            System.out.println("证书未过期");
        }
    }
}
