package threadTest.threadPoolTest;

import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-20 16:33
 **/
public class ThreadPool1 {
    private static int count = 0;
    private static int times = 100000;

    @SneakyThrows
    public static void main(String[] args) {
        Object o = new Object();
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < times; i++) {
            service.execute(() -> {
                synchronized (o) {
                    count++;
                }
            });
        }
        // 关闭线程池
        service.shutdown();
        System.out.println(count);
    }
}
