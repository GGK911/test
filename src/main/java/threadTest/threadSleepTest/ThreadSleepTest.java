package threadTest.threadSleepTest;

/**
 * 作者：Intopass
 * 链接：https://www.zhihu.com/question/41048032/answer/89431513
 * 来源：知乎
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 *
 * 首先，一个线程不应该由其他线程来强制中断或停止，而是应该由线程自己自行停止。所以，Thread.stop, Thread.suspend, Thread.resume
 * 都已经被废弃了。而 Thread.interrupt 的作用其实也不是中断线程，而是「通知线程应该中断了」，具体到底中断还是继续运行，应该由被通知
 * 的线程自己处理。具体来说，当对一个线程，调用 interrupt() 时，
 * ① 如果线程处于被阻塞状态（例如处于sleep, wait, join 等状态），那么线程将立即退出被阻塞状态，并抛出一个InterruptedException异常。仅此而已。
 * ② 如果线程处于正常活动状态，那么会将该线程的中断标志设置为 true，仅此而已。被设置中断标志的线程将继续正常运行，不受影响。
 * interrupt() 并不能真正的中断线程，需要被调用的线程自己进行配合才行。也就是说，一个线程如果有被中断的需求，那么就可以这样做。
 * ① 在正常运行任务时，经常检查本线程的中断标志位，如果被设置了中断标志就自行停止线程。
 * ② 在调用阻塞方法时正确处理InterruptedException异常。（例如，catch异常后就结束线程。）
 * Thread thread = new Thread(() -> {
 *     while (!Thread.interrupted()) {
 *         // do more work.
 *     }
 * });
 * thread.start();
 *
 * 一段时间以后
 * thread.interrupt();
 * 具体到你的问题，Thread.interrupted()清除标志位是为了下次继续检测标志位。如果一个线程被设置中断标志后，选择结束线程那么自然不
 * 存在下次的问题，而如果一个线程被设置中断标识后，进行了一些处理后选择继续进行任务，而且这个任务也是需要被中断的，那么当然需要清除
 * 标志位了。
 * @author TangHaoKai
 * @version V1.0 2023-11-17 14:21
 **/
public class ThreadSleepTest {

    public static void main(String[] args) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // Thread.sleep(1000);
                    if (Thread.interrupted()) {
                        System.out.println("interrupted: " + this.isInterrupted());
                        System.out.println("EXIT");
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("interrupted: " + this.isInterrupted() + "\t" + e.getMessage());
                }
                System.out.println("interrupted: " + this.isInterrupted());
                System.out.println("SLEEP END");
            }
        };
        System.out.println(thread.getState());
        thread.start();
        System.out.println(thread.getState());
        thread.interrupt();
        System.out.println(thread.getState());
    }

}
