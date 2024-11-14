// package springTest.config;
//
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.DisposableBean;
// import org.springframework.stereotype.Component;
//
// import javax.annotation.PostConstruct;
//
// /**
//  * 线程测试
//  *
//  * @author TangHaoKai
//  * @version V1.0 2024/10/31 16:54
//  */
// @Slf4j
// @Component
// public class ThreadTest implements Runnable, DisposableBean {
//     private Thread thread;
//     private volatile boolean isRunning = true;
//
//     public ThreadTest() {
//         System.out.println("执行ThreadTest的构造方法");
//         this.thread = new Thread(this);
//         System.out.println("线程启动");
//         thread.start();
//     }
//
//     /**
//      * Runnable的启动
//      */
//     @Override
//     public void run() {
//         while (isRunning) {
//             System.out.println("isRunning为true，正在执行1");
//             try {
//                 Thread.sleep(500);
//             } catch (InterruptedException e) {
//                 // 退出循环
//                 break;
//             }
//             System.out.println("isRunning为true，正在执行2");
//             try {
//                 Thread.sleep(500);
//             } catch (InterruptedException e) {
//                 // 退出循环
//                 break;
//             }
//             System.out.println("isRunning为true，正在执行3");
//         }
//         System.out.println("isRunning设置为false，已跳出while循环");
//     }
//
//     /**
//      * bean被销魂
//      */
//     @Override
//     public void destroy() {
//         System.out.println("正在销毁bean，调用destroy方法，isRunning设置为false");
//         isRunning = false;
//     }
//
// }
