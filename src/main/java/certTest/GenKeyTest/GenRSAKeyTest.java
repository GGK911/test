package certTest.GenKeyTest;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 生成KEY测试
 * 1.静态方法每次生成工厂
 * 2.静态方法初始化工厂后生成
 * 2.1纯KeyPair对象10轮100对2048位RSAKEY
 * 耗时/ms37312
 * 耗时/ms39163
 * 耗时/ms38241
 * 耗时/ms44543
 * 耗时/ms32837
 * 耗时/ms42267
 * 耗时/ms39393
 * 耗时/ms39738
 * 耗时/ms41430
 * 耗时/ms39675
 * 2.2纯KeyPair对象10轮100对512位RSAKEY
 * 耗时/ms1525
 * 耗时/ms1074
 * 耗时/ms1073
 * 耗时/ms986
 * 耗时/ms905
 * 耗时/ms1015
 * 耗时/ms968
 * 耗时/ms1023
 * 耗时/ms971
 * 耗时/ms948
 *
 * @author TangHaoKai
 * @version V1.0 2024/6/29 11:30
 */
public class GenRSAKeyTest {
    private static final Provider BC = new BouncyCastleProvider();
    private static KeyPairGenerator keyPairGenerator;

    static {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA", BC);
            keyPairGenerator.initialize(2048);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 单线程
    public static void main1(String[] args) throws NoSuchAlgorithmException {
        long startTotal = System.currentTimeMillis();
        for (int j = 0; j < 10; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
            }
            System.out.println("耗时/ms" + (System.currentTimeMillis() - start));
        }
        System.out.println("总耗时/ms" + (System.currentTimeMillis() - startTotal));
    }

    // 多线程
    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        long startTotal = System.currentTimeMillis();
        for (int j = 0; j < 20; j++) {
            service.execute(
                    () -> {
                        long start = System.currentTimeMillis();
                        for (int i = 0; i < 5; i++) {
                            KeyPair keyPair = keyPairGenerator.generateKeyPair();
                        }
                        System.out.println("耗时/ms" + (System.currentTimeMillis() - start));
                    }
            );
        }
        service.shutdown();
        // 等待所有任务完成，最长等待时间设置为1小时
        if (service.awaitTermination(1, TimeUnit.HOURS)) {
            System.out.println("总耗时/ms" + (System.currentTimeMillis() - startTotal));
        } else {
            System.out.println("Timeout occurred before all tasks finished.");
        }
    }
}
