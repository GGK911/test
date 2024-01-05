package threadTest.collectionTest;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 集合测试
 * 线程安全测试
 *
 * @author TangHaoKai
 * @version V1.0 2023/12/26 10:46
 **/
public class CollectionTest01 implements Runnable {
    // 线程不安全，因为其中对数组的操作分两步，有越界异常
    // static List<String> list = new ArrayList<>();
    // CopyOnWriteArrayList线程安全
    // static List<String> list = new CopyOnWriteArrayList<>();
    static List<String> list = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        CollectionTest01 collectionTest = new CollectionTest01();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(collectionTest);
            thread.start();
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("result: " + list.size());
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            // 对count这个临界资源的变更，没有线程保护
            list.add(i + "");
        }
    }
}
