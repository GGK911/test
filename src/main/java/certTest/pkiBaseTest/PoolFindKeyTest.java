package certTest.pkiBaseTest;

import cn.com.mcsca.pdf.signature.PdfKeywordFinder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池iText查询
 *
 * @author TangHaoKai
 * @version V1.0 2024/10/8 15:01
 */
public class PoolFindKeyTest {

    public static void main(String[] args) {

        // 创建一个固定大小的线程池，控制最大并发数为 ?
        // ExecutorService threadPool = Executors.newSingleThreadExecutor();
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        long startTotal = System.currentTimeMillis();

        // 启动 i < ? 个任务
        for (int i = 0; i < 1000; i++) {
            int threadNumber = i + 1;

            // 提交任务到线程池
            threadPool.submit(() -> {

                // while (true) {
                try {
                    // 读取 PDF 文件路径
                    Path path = Paths.get("src/main/java/certTest/pkiBaseTest", "2500-查询关键字字数影响测试.pdf");

                    // 读取文件字节内容
                    byte[] pdfBytes = Files.readAllBytes(path);

                    long start = System.currentTimeMillis();

                    // 查找关键词位置 (假设 PdfKeywordFinder 是一个有效的工具类)
                    List<float[]> keywordPositions = PdfKeywordFinder.findKeywordPositions(pdfBytes, "萧媚皱着浅眉盯着石碑前的紫裙少女");

                    // 输出处理结果（可选，根据需要进行处理）
                    System.out.println("Thread " + threadNumber + " found " + keywordPositions.size() + " occurrences of the keyword. Total time: " + (System.currentTimeMillis() - start) + "ms. Leave Start time:" + (System.currentTimeMillis() - startTotal) + "ms");

                } catch (Exception e) {
                    System.err.println("Thread " + threadNumber + " encountered an error: " + e.getMessage());
                }
                // }
            });
        }

        System.out.println("All tasks have been submitted to the thread pool. ");
    }

}
