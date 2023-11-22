package threadTest.synchronizedTest;

/**
 * 这里的是不同的线程实例
 * 对count这个临界资源的变更，使用synchronized线程保护
 * 并且是作用于整个Class上
 * 线程同步
 * （需要注意的是Class内如有非静态的对临界资源的变更，一样会造成争抢）
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-17 13:37
 **/
public class Synchronized4 implements Runnable {
    private static int count = 0;

    public static void main(String[] args) {
        // 这里的是不同的线程实例
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Synchronized4());
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

    /**
     * 静态方法加上synchronized会锁住整个Class
     */
    public static synchronized void increase() {
        count++;
    }

    /**
     * 非静态,访问时锁不一样不会发生互斥
     */
    public synchronized void increase4Obj(){
        count++;
    }
}
