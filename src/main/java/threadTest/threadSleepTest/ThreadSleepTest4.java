package threadTest.threadSleepTest;

/**
 * 两个任务交叉执行
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-20 15:19
 **/
public class ThreadSleepTest4 {
    private static int count = 0;

    public static void main(String[] args) {
        Object o = new Object();
        Thread subThread = new Thread(() -> {
            System.out.println("子线程执行...");
            try {
                synchronized (o) {
                    for (int i = 0; i < 100; i++) {
                        if (count == 50) {
                            o.wait();
                        }
                        count++;
                        System.out.println("子线程");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("子线程执行结束...");
        });

        Thread subThread1 = new Thread(() -> {
            System.out.println("子线程2执行...");
            try {
                synchronized (o) {
                    for (int i = 0; i < 100; i++) {
                        count++;
                        System.out.println("子线程2");
                    }
                    o.notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("子线程2执行结束...");
        });
        subThread.start();
        subThread1.start();

        try {
            subThread1.join();
            subThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(count);
    }
}
