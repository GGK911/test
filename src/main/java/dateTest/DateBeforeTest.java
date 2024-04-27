package dateTest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * beforeTest
 *
 * @author TangHaoKai
 * @version V1.0 2024/4/23 10:53
 */
public class DateBeforeTest {

    public static void main(String[] args) throws Exception {
        String endTime = "2000-01-01 00:00:02";
        String date2 = "2000-01-01 00:00:01";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date endDate = sdf.parse(endTime);
        Date dateParse2 = sdf.parse(date2);

        System.out.println(endDate.before(new Date()));
    }

}
