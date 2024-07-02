package certTest.GenKeyTest;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.ECGenParameterSpec;

/**
 * 生成KEY测试
 * 1.静态方法每次生成工厂
 * 1.1纯KeyPair对象10轮10000对SM2KEY
 * 耗时/ms1671
 * 耗时/ms1052
 * 耗时/ms1060
 * 耗时/ms1018
 * 耗时/ms994
 * 耗时/ms896
 * 耗时/ms1008
 * 耗时/ms1087
 * 耗时/ms991
 * 耗时/ms1013
 * <p>
 * 2.静态方法初始化工厂后生成
 * 2.1纯KeyPair对象10轮10000对SM2KEY
 * 耗时/ms1555
 * 耗时/ms1032
 * 耗时/ms1051
 * 耗时/ms889
 * 耗时/ms918
 * 耗时/ms894
 * 耗时/ms901
 * 耗时/ms901
 * 耗时/ms916
 * 耗时/ms929
 * 2.2转HEX字符串10轮10000对SM2KEY
 * 耗时/ms1979
 * 耗时/ms1048
 * 耗时/ms1059
 * 耗时/ms932
 * 耗时/ms931
 * 耗时/ms942
 * 耗时/ms943
 * 耗时/ms1003
 * 耗时/ms993
 * 耗时/ms927
 * 2.3转BASE64字符串10轮10000对SM2KEY
 * 耗时/ms1714
 * 耗时/ms1065
 * 耗时/ms1056
 * 耗时/ms901
 * 耗时/ms957
 * 耗时/ms921
 * 耗时/ms939
 * 耗时/ms963
 * 耗时/ms1012
 * 耗时/ms953
 * <p>
 *
 * @author TangHaoKai
 * @version V1.0 2024/6/29 11:09
 */
public class GenSM2KeyTest {
    private static final Provider BC = new BouncyCastleProvider();
    private static final ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");
    private static KeyPairGenerator keyPairGenerator;

    static {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("EC", BC);
            keyPairGenerator.initialize(sm2Spec);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        for (int j = 0; j < 10; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", BC);
                keyPairGenerator.initialize(sm2Spec);

                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                // Hex.toHexString(keyPair.getPrivate().getEncoded());
                // Hex.toHexString(keyPair.getPublic().getEncoded());
                // Base64.toBase64String(keyPair.getPrivate().getEncoded());
                // Base64.toBase64String(keyPair.getPublic().getEncoded());
            }
            System.out.println("耗时/ms" + (System.currentTimeMillis() - start));
        }
    }
}
