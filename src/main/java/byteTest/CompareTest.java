package byteTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author TangHaoKai
 * @version V1.0 2024/10/23 13:57
 */
public class CompareTest {
    @Test
    @SneakyThrows
    public void Test() {
        Integer xint = Integer.parseInt("cc", 16);
        // xint = Integer.parseInt("4c", 16);
        xint = Integer.parseInt("80", 16);
        xint = Integer.parseInt("7f", 16);

        if (xint >= 0x80) {
            System.out.println(">= 0x80");
        } else {
            System.out.println("< 0x80");
        }
    }
}
