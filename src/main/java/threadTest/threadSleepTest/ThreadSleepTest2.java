package threadTest.threadSleepTest;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-20 11:33
 **/
public class ThreadSleepTest2 {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Thread thread = Thread.currentThread();
        Thread thread1 = new Thread(() -> {
            try {
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    Thread.sleep(1000);
                    System.out.println("THREAD1 TIME: " + (System.currentTimeMillis() - startTime));
                }
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    System.out.println("THREAD1 INTERRUPTED END");
                } else {
                    System.out.println("业务异常退出");
                }
            }
        });
        thread1.start();
        try {
            // 子线程执行第五秒时给予中断
            Thread.sleep(5000);
            System.out.println("MAIN INTERRUPTED THREAD1 TIME: " + (System.currentTimeMillis() - startTime));
            thread1.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
