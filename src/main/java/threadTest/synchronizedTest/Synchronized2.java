package threadTest.synchronizedTest;

/**
 * 一个线程实例
 * 对count这个临界资源的变更，使用synchronized线程保护
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-17 10:58
 **/
public class Synchronized2 implements Runnable {
    private static int count = 0;

    public static void main(String[] args) {
        // 这里的始终是一个线程实例
        Synchronized2 synchronized2 = new Synchronized2();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(synchronized2);
            thread.start();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("result: " + count);
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000000; i++) {
            // 对count这个临界资源的变更，使用synchronized线程保护
            increase();
        }
    }

    public synchronized void increase() {
        count++;
    }
}
