package html2pdfTest;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import shellTest.win;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author TangHaoKai
 * @version V1.0 2024/9/30 11:56
 */
public class cnblogTest {
    private static final String ROOT = "src/main/java/html2pdfTest";

    public static void main(String[] args) {
        Path path = Paths.get(ROOT, "归档文档.xlsx");
        ExcelReader reader = ExcelUtil.getReader(path.toAbsolutePath().toString());
        List<List<Object>> read = reader.read(0);
        for (List<Object> list : read) {
            String html = String.valueOf(list.get(0));
            String name = String.valueOf(list.get(1));

            win.callShellByExec("wkhtmltopdf " + html + " D:\\cnblogs\\" + name + ".pdf");
        }
    }
}
