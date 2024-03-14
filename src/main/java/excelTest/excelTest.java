package excelTest;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import extense.Son;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * excel测试,依旧会显示为null的字段
 *
 * @author TangHaoKai
 * @version V1.0 2024/2/26 18:06
 */
public class excelTest {

    /**
     *
     */
    @Test
    public void create01() {
        List<Son> sonList = new ArrayList<>();
        final Son son1 = new Son(null, "18");
        final Son son2 = new Son("1", "18");
        sonList.add(son1);
        sonList.add(son2);
        @Cleanup
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(1, "意愿记录");
        writer.addHeaderAlias("name", "名称");
        writer.addHeaderAlias("age", "年龄");
        writer.write(sonList, true);

        Path file = Paths.get("src/main/java/excelTest", "test.xls");
        writer.flush(new File(file.toAbsolutePath().toUri()));
        writer.close();
    }
}
