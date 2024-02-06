package dateTest;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    @Test
    public void formatTest() {
        // 创建一个 Date 对象，表示当前时间
        Date currentDate = new Date();

        // 创建 SimpleDateFormat 对象，指定日期格式和时区
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss z");

        // 设置时区为 GMT
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        // 格式化 Date 对象
        String formattedDate = sdf.format(currentDate);

        // 输出格式化后的日期字符串
        System.out.println(formattedDate);
    }
}
