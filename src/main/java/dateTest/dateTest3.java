package dateTest;

import cn.hutool.core.date.DateUtil;

import java.sql.Timestamp;

/**
 * 时间戳打印测试
 *
 * @author TangHaoKai
 * @version V1.0 2023-12-08 09:43
 **/
public class dateTest3 {
    public static void main(String[] args) {
        System.out.println(DateUtil.parse(new Timestamp(System.currentTimeMillis()).toString()));
        System.out.println(DateUtil.date());
    }
}
