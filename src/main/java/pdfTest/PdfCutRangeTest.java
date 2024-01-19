package pdfTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ByteUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/1/17 13:52
 **/
public class PdfCutRangeTest {
    @Test
    @SneakyThrows
    public void cfcaRangeTest() {
        Map<String, long[]> rangMap = new HashMap<>();
        rangMap.put("range1", new long[]{0, 123104, 139490, 6817});
        rangMap.put("range2", new long[]{0, 170754, 187140, 6616});
        rangMap.put("range3", new long[]{0, 202903, 219289, 6602});
        String file = "C:\\Users\\ggk911\\Desktop\\cfca.pdf";
        byte[] pdf = FileUtil.readBytes(file);
        for (Map.Entry<String, long[]> entry : rangMap.entrySet()) {
            cutByRange("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\cutRangeTest\\" + entry.getKey(), pdf, entry.getValue());
        }

    }

    public byte[][] cutByRange(String path, byte[] pdf, long[] range) {
        // 前中后长度
        int BMATotalLength = (int) (range[2] + range[3]);
        // 前
        byte[] rangeBefore = new byte[(int) range[1]];
        // 中
        byte[] rangeMiddle = new byte[BMATotalLength - (int) (range[1] + range[3])];
        // 后
        byte[] rangeAfter = new byte[(int) range[3]];
        // 前+后
        byte[] rangeBeforeAndAfter = new byte[(int) (range[1] + rangeMiddle.length + range[3])];

        System.arraycopy(pdf, (int) range[0], rangeBefore, 0, (int) range[1]);
        System.arraycopy(pdf, (int) range[1] + (int) range[0], rangeMiddle, 0, BMATotalLength - (int) (range[1] + range[3]));
        System.arraycopy(pdf, (int) range[2], rangeAfter, 0, (int) range[3]);
        System.arraycopy(rangeBefore, 0, rangeBeforeAndAfter, 0, rangeBefore.length);
        byte[] middle = new byte[rangeMiddle.length];
        Arrays.fill(middle, (byte) 48);
        System.arraycopy(middle, 0, rangeBeforeAndAfter, rangeBefore.length, middle.length);
        System.arraycopy(rangeAfter, 0, rangeBeforeAndAfter, rangeBefore.length + middle.length, rangeAfter.length);
        FileUtil.writeBytes(rangeBefore, path + "Before");
        FileUtil.writeBytes(rangeMiddle, path + "Middle");
        FileUtil.writeBytes(rangeAfter, path + "After");
        FileUtil.writeBytes(rangeBeforeAndAfter, path + "BeforeAndAfter.pdf");
        return new byte[][]{rangeBefore, rangeMiddle, rangeAfter};
    }
}
