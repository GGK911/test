package txtTest;


import cn.hutool.core.util.StrUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author TangHaoKai
 * @version V1.0 2024/9/30 11:34
 */
public class LineFilter {
    private static final String ROOT = "src/main/java/txtTest";

    public static void main(String[] args) {
        // 指定输入文件和保存输出的文件
        String inputFilePath = Paths.get(ROOT, "favorites_2024_9_30.html").toAbsolutePath().toString(); // 输入文件路径
        String outputFilePath = Paths.get(ROOT, "save.txt").toAbsolutePath().toString(); // 输出文件路径

        filterLinesWithKeyword(inputFilePath, outputFilePath, "cnblogs");
    }

    /**
     * 过滤包含指定关键字的行，并将其写入输出文件
     *
     * @param inputFile  输入文件路径
     * @param outputFile 输出文件路径
     * @param keyword    要查找的关键字
     */
    public static void filterLinesWithKeyword(String inputFile, String outputFile, String keyword) {
        Path inputPath = Paths.get(inputFile);
        Path outputPath = Paths.get(outputFile);

        try (BufferedReader reader = Files.newBufferedReader(inputPath);
             BufferedWriter writer = Files.newBufferedWriter(outputPath,
                     java.nio.file.StandardOpenOption.CREATE,
                     java.nio.file.StandardOpenOption.APPEND)) {

            String line;
            while ((line = reader.readLine()) != null) {
                // 检查当前行是否包含关键字
                if (line.contains(keyword)) {
                    // int urlStart = line.indexOf("https");
                    // int urlEnd = line.indexOf("\"", line.indexOf("\"") + 1);
                    // line = line.substring(urlStart, urlEnd);

                    int urlStart = line.indexOf("\">") + 2;
                    int urlEnd = line.indexOf("</A>");
                    line = line.substring(urlStart, urlEnd);

                    line = StrUtil.removeAll(line, " ,!?.\\./".toCharArray());
                    line = line.replace("（", "(");
                    line = line.replace("）", ")");
                    line = line.replace("【", "[");
                    line = line.replace("】", "]");

                    // 将该行写入到输出文件中
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("保存成功，所有包含关键字 '" + keyword + "' 的行已写入 " + outputFile);

        } catch (IOException e) {
            System.err.println("处理文件时出错: " + e.getMessage());
        }
    }
}
