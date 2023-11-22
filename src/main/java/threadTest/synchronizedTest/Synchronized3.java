package threadTest.synchronizedTest;

/**
 * 这里的是不同的线程实例
 * 对count这个临界资源的变更，使用synchronized线程保护
 * 会造成争夺临界资源
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-17 11:59
 **/
public class Synchronized3 implements Runnable {
    private static int count = 0;

    public static void main(String[] args) {
        // 这里的是不同的线程实例
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Synchronized3());
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
