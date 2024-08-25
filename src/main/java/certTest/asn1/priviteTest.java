package certTest.asn1;

import lombok.SneakyThrows;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.junit.jupiter.api.Test;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/30 9:45
 */
public class priviteTest {

    @Test
    @SneakyThrows
    public void struct() {
        // 获取SM2曲线参数
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
    }

}
