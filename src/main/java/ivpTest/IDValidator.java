package ivpTest;

import cn.hutool.core.io.FileUtil;
import org.bouncycastle.util.encoders.Base64;

import java.time.LocalDate;
import java.time.Period;

public class IDValidator {
    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    private static final String[] VALID_REGIONS = {"11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71", "81", "82", "91"};

    public static boolean isValidID(String id) {
        if (id == null || id.length() != 18) {
            return false;
        }

        // 检查前6位是否是有效地区
        // String region = id.substring(0, 6);
        // if (!isRegionValid(region)) {
        //     return false;
        // }

        // 检查年龄是否合理
        if (!isAgeValid(id)) {
            return false;
        }

        // 检查校验码
        return isCheckSumValid(id);
    }

    private static boolean isRegionValid(String region) {
        for (String validRegion : VALID_REGIONS) {
            if (region.startsWith(validRegion)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAgeValid(String id) {
        int year = Integer.parseInt(id.substring(6, 10));
        int month = Integer.parseInt(id.substring(10, 12));
        int day = Integer.parseInt(id.substring(12, 14));

        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate currentDate = LocalDate.now();
        Period age = Period.between(birthDate, currentDate);

        return age.getYears() >= 18 && age.getYears() <= 60; // 假设合理的年龄范围是18到60岁
    }

    private static boolean isCheckSumValid(String id) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            int digit = Character.getNumericValue(id.charAt(i));
            sum += digit * WEIGHTS[i];
        }
        int checkCodeIndex = sum % 11;
        char calculatedCheckCode = CHECK_CODES[checkCodeIndex];

        return calculatedCheckCode == id.charAt(17);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 11; i++) {
            String id = ("51010719751214221" + (i == 10 ? "X" : i)).toUpperCase(); // 示例身份证号码
            System.out.println(id + isValidID(id)); // 输出结果
        }
        String fileBase64 = "eyJjb2RlIjoiMTAwMTAyIiwibXNnIjoi6K+35rGC6Lev5b6EWy9kb3dubG9hZF3kuI3lrZjlnKgiLCJkZXRhaWwiOiJ7dGltZXN0YW1wPUZyaSBKdWwgMjYgMTU6NDU6MDEgQ1NUIDIwMjQsIHN0YXR1cz00MDQsIGVycm9yPU5vdCBGb3VuZCwgcGF0aD0vZG93bmxvYWR9IiwiaGVhZCI6eyJsZXZlbCI6IjEifSwiYm9keSI6bnVsbH0=";
        FileUtil.writeBytes(Base64.decode(fileBase64), "C:\\Users\\ggk911\\Desktop\\test.pdf");
    }
}