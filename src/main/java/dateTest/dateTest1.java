package dateTest;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 最新日期测试
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-23 09:36
 **/
public class dateTest1 {
    public static void main(String[] args) {
        DateTime date = DateUtil.date();
        DateTime yesterday = DateUtil.yesterday();
        DateTime tomorrow = DateUtil.tomorrow();
        List<Date> dateList = new ArrayList<>(3);
        dateList.add(date);
        dateList.add(yesterday);
        dateList.add(tomorrow);
        // Date::compareTo默认取最新的日期
        dateList.stream().max(Date::compareTo).ifPresent(System.out::println);

    }
}
