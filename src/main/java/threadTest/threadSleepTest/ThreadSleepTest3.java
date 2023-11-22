package threadTest.threadSleepTest;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-20 14:55
 **/
public class ThreadSleepTest3 {
    public static void main(String[] args) {
        Thread thread = Thread.currentThread();

        Thread subThread = new Thread(() -> {
            System.out.println("子线程执行...");
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println("执行子线程，主线程状态：" + thread.getState());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("子线程执行结束...");
        });
        // 线程终止以后会调用自身的 notifyAll方法，这样就会唤醒在该线程对象上等待的线程
        System.out.println("子线程未开启，主线程状态：" + thread.getState());
        subThread.start();
        try {
            // join(long millis)方法使用synchronized修饰
            // 主线程 等待 子线程执行结束，如果参数为 0 时表示永远等待
            subThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("子线程结束，主线程状态：" + thread.getState());

    }
}
