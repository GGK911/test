package certTest.miniCATest;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/13 15:31
 */
public class MiniCATest {
    public static final Provider BC = new BouncyCastleProvider();
    public static final KeyPairGenerator rsaKeyPairGenerator;
    public static final KeyPairGenerator sm2KeyPairGenerator;
    public static final KeyFactory rsaKeyFactory;
    public static final KeyFactory sm2KeyFactory;
    public static final String ROOT = "src/main/java/certTest/miniCATest";

    static {
        try {
            rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA", BC);
            sm2KeyPairGenerator = KeyPairGenerator.getInstance("EC", BC);
            rsaKeyFactory = KeyFactory.getInstance("RSA", BC);
            sm2KeyFactory = KeyFactory.getInstance("EC", BC);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
