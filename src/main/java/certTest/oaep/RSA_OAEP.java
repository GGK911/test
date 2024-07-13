package certTest.oaep;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;

/**
 * RSA组件之最优非对称加密填充(OAEP)的实现
 * https://stackoverflow.com/questions/50298687/bouncy-castle-vs-java-default-rsa-with-oaep
 *
 * @author TangHaoKai
 * @version V1.0 2024/4/22 10:17
 */
public class RSA_OAEP {

    private static final Provider BC = new BouncyCastleProvider();

    private static String M = "abcdefg1234567";

    public static void main(String[] args) throws Exception {

        // 2048 位RSA公私钥
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", BC);
        keyPairGenerator.initialize(2048);
        KeyPair rsaKeyPair = keyPairGenerator.generateKeyPair();
        PrivateKey rsaPrivateKey = rsaKeyPair.getPrivate();
        PublicKey rsaPublicKey = rsaKeyPair.getPublic();

        // “RSA/ECB/PKCS1Padding”实际上并没有实现ECB模式加密。它应该被称为“RSA/None/PKCS1Padding”，因为它只能用于加密单个明文块（或者实际上是一个秘密密钥）。这只是Sun/Oracle的一个命名错误。
        // "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING"
        // "RSA/ECB/OAEPWithSHA-1AndMGF1PADDING"
        Cipher encryptionCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        // Cipher encryptionCipher = Cipher.getInstance("RSA/NONE/OAEPPadding", BC);
        OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey, oaepParameterSpec);
        byte[] encryptBytes = encryptionCipher.doFinal(M.getBytes(StandardCharsets.UTF_8));
        System.out.println("encrypt HEX>> " + Hex.toHexString(encryptBytes));


        Cipher decryptionCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        // Cipher decryptionCipher = Cipher.getInstance("RSA/ECB/OAEPPadding", BC);
        oaepParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        decryptionCipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey, oaepParameterSpec);
        byte[] decryptBytes = decryptionCipher.doFinal(encryptBytes);
        System.out.println("decrypt String>> " + new String(decryptBytes));

    }

}
