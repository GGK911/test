package excelTest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TangHaoKai
 * @version V1.0 2024/6/3 18:40
 */
public class HeadStyleTest {

    @Test
    @SneakyThrows
    public void test01() {
        @Cleanup
        ExcelWriter writer = ExcelUtil.getWriter();

        StyleSet styleSet = writer.getStyleSet();
        CellStyle headCellStyle = styleSet.getHeadCellStyle();
        Font font = writer.createFont();
        font.setBold(true);
        font.setFontHeight((short) 18);
        headCellStyle.setFont(font);

        writer.merge(4, "统计信息");

        font.setBold(false);
        font.setFontHeight((short) 180);
        headCellStyle.setFont(font);

        // writer.addHeaderAlias("deptName", "机构");
        // writer.addHeaderAlias("appName", "应用");
        // writer.addHeaderAlias("orderSum", "订单数量");
        // writer.addHeaderAlias("pactSum", "合同数量");
        writer.writeHeadRow(CollUtil.newArrayList("1", "2", "3", "4", "5"));
        List<List<String>> rows = new ArrayList<>();
        rows.add(CollUtil.newArrayList("1", "2", "3", "4", "5"));
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\excelTest\\test.xls");
        writer.write(rows, true);
        writer.flush(fileOutputStream);
        writer.close();
    }
}
