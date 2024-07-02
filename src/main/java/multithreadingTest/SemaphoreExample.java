package multithreadingTest;

import java.util.concurrent.Semaphore;

/**
 * 信号量
 *
 * @author TangHaoKai
 * @version V1.0 2024/6/17 9:19
 */
public class SemaphoreExample {
    private static final int MAX_CONCURRENT_THREADS = 3; // 最大并发线程数
    private static Semaphore semaphore = new Semaphore(MAX_CONCURRENT_THREADS); // 创建信号量对象

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(new Task()).start(); // 创建并启动10个线程
        }
    }

    static class Task implements Runnable {
        @Override
        public void run() {
            try {
                semaphore.acquire(); // 获取许可
                System.out.println("线程 " + Thread.currentThread().getName() + " 开始执行任务");
                // 模拟耗时操作
                Thread.sleep(1000);
                System.out.println("线程 " + Thread.currentThread().getName() + " 完成任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release(); // 释放许可
            }
        }
    }
}
