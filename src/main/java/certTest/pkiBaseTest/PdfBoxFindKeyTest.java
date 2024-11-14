package certTest.pkiBaseTest;

import cn.hutool.json.JSONUtil;
import org.slf4j.LoggerFactory;
import pdfTest.parse.BoxKeyPosition;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author TangHaoKai
 * @version V1.0 2024/9/30 13:40
 */
public class PdfBoxFindKeyTest {

    static {
        Logger logger = (Logger) LoggerFactory.getLogger("org.apache.pdfbox");
        logger.setLevel(Level.ERROR);
    }

    public static void main(String[] args) {

        // 启动 i < ? 个线程
        for (int i = 0; i < 10; i++) {
            int threadNumber = i + 1;

            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        // 读取 PDF 文件路径
                        Path path = Paths.get("src/main/java/certTest/pkiBaseTest", "2500-查询关键字字数影响测试.pdf");

                        // 读取文件字节内容
                        byte[] pdfBytes = Files.readAllBytes(path);

                        long start = System.currentTimeMillis();

                        // 查找关键词位置
                        BoxKeyPosition boxKeyPosition = new BoxKeyPosition("萧媚皱着浅眉盯着石碑前的紫裙少女", pdfBytes);

                        // 输出处理结果（可选，根据需要进行处理）
                        System.out.println("Thread " + threadNumber + " found " + JSONUtil.toJsonStr(boxKeyPosition.getPosition()) + " occurrences of the keyword. total times :" + (System.currentTimeMillis() - start));

                    } catch (Exception e) {
                        System.err.println("Thread " + threadNumber + " encountered an error: " + e.getMessage());
                    }
                }
            });

            thread.start(); // 启动线程
        }

        System.out.println("All threads have been started.");
    }
}
