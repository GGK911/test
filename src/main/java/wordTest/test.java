package wordTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * word测试
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-06 09:42
 **/
public class test {
    @SneakyThrows
    public static void main(String[] args) {
        // 版本更新
        // String doc = "C:\\Users\\ggk911\\Desktop\\多页模板-简单-老版本4doc.doc";
        // String doc = "C:\\Users\\ggk911\\Desktop\\换行测试4.docx";
        String doc = "C:\\Users\\ggk911\\Desktop\\奖金.docx";
        // String doc = "C:\\Users\\ggk911\\Desktop\\奖金英文变量.docx";
        String docx = "C:\\Users\\ggk911\\Desktop\\test.docx";
        String fillDocx = "C:\\Users\\ggk911\\Desktop\\testFill.docx";
        byte[] bytes = FileUtil.readBytes(doc);
        byte[] toDocx = WordTemplateUtil.docToDocx(bytes);
        assert toDocx != null;
        FileUtil.writeBytes(toDocx, docx);

        // 读取变量
        // 变量List
        List<String> param = new ArrayList<>();
        // 明细表
        List<Map<String, List<String>>> table = new ArrayList<>();
        WordTemplateUtil.getWordAllVars(FileUtil.readBytes(docx), param, table);
        System.out.println(JSONUtil.parse(param).toStringPretty());

        // 填充
        // 填充参数
        Map<String, Object> fill = new HashMap<>();
        for (String s : param) {
            fill.put(s, "测试填充");
            if ("项目奖金开始计算日期".equals(s)) {
                // fill.put(s, null);
            }
        }
        Map<String, List<Map<String, String>>> tableDetail = new HashMap<>();
        byte[] fillDocxBytes = WordTemplateUtil.setDocxParam(FileUtil.readBytes(docx), fill, tableDetail);
        FileUtil.writeBytes(fillDocxBytes, fillDocx);


    }
}
