package excelTest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import extense.Son;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.Serializable;

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

    @Test
    @SneakyThrows
    public void read() {
        String filePath = "C:\\Users\\ggk911\\Desktop\\葫芦娃临时抽检.xlsx" ;
        String sheetName = "SQL Results";
        List<RowData> rowDataList = new ArrayList<>();

        // 读取Excel文件
        ExcelReader reader = ExcelUtil.getReader(filePath, sheetName);

        // 读取所有行
        List<List<Object>> rows = reader.read();
        for (List<Object> row : rows) {
            RowData rowData = new RowData(
                    row.get(0).toString(),
                    row.get(1).toString(),
                    row.get(2).toString(),
                    row.get(3).toString()
            );
            rowDataList.add(rowData);
        }

        // 关闭读取器
        reader.close();

        // 将List序列化为文件
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:\\Users\\ggk911\\Desktop\\list"))) {
            oos.writeObject(rowDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 从文件读取并反序列化List
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\Users\\ggk911\\Desktop\\list"))) {
            List<RowData> deserializedList = (List<RowData>) ois.readObject();
            for (RowData data : deserializedList) {
                System.out.println(data);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    @SneakyThrows
    public void writeExcel() {
        // 从文件读取并反序列化List
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\Users\\ggk911\\Desktop\\list2"))) {
            List<RowData2> deserializedList = (List<RowData2>) ois.readObject();
            if (deserializedList != null) {
                // 使用Hutool写入数据到Excel
                ExcelWriter writer = ExcelUtil.getWriter("C:\\Users\\ggk911\\Desktop\\葫芦娃临时抽检-验证.xlsx", "结果");

                // 写入标题行
                writer.writeHeadRow(new ArrayList<>(Arrays.asList("Column A", "Column B", "Column C", "Column D", "Column E", "Column F")));

                // 写入数据行
                for (RowData2 rowData : deserializedList) {
                    writer.writeRow(new ArrayList<>(Arrays.asList(rowData.getColA(), rowData.getColB(), rowData.getColC(), rowData.getColD(), rowData.getColE(), rowData.getColF())));
                }

                // 关闭写入器，保存修改
                writer.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
