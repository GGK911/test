package byteTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author TangHaoKai
 * @version V1.0 2024/1/8 16:08
 **/
public class ByteCopyTest01 {
    @Test
    @SneakyThrows
    public void copyTest() {
        // 0, 5, 10, 5
        byte[] bytes = new byte[]{0, 1, 2, 3, 4, 0, 0, 0, 0, 0, 5, 6, 7, 8, 9};
        byte[] plus = new byte[10];

        System.arraycopy(bytes, 0, plus, 0, 5);
        System.arraycopy(bytes, 10, plus, 5, 5);
        System.out.println(Arrays.toString(plus));
    }
}
