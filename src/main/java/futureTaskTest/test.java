package futureTaskTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/7 16:21
 */
public class test {
    @Test
    @SneakyThrows
    public void test01() {
        try {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return hydxCheck();
                } catch (McscaServiceException e2) {
                    System.out.println("自定义异常1");
                    throw e2;
                } catch (Exception e) {
                    System.out.println("Exception异常1");
                    throw new RuntimeException(e);
                }
            });

            try {
                String s = future.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof McscaServiceException) {
                    System.out.println("自定义异常3");
                }
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            } catch (McscaServiceException e2) {
                System.out.println("自定义异常2");
                throw e2;
            }
        } catch (McscaServiceException e2) {
            System.out.println("自定义异常3");
            throw e2;
        }

    }

    private static String hydxCheck() {
        throw new McscaServiceException("参数异常");
    }
}
