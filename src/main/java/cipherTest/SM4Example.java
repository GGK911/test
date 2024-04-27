package cipherTest;

import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/12 14:31
 */
public class SM4Example {

    public static void main(String[] args) throws Exception {
        // 添加 Bouncy Castle 加密提供程序
        Security.addProvider(new BouncyCastleProvider());

        // 创建 KeyGenerator 对象，用于生成对称密钥
        KeyGenerator keyGenerator = KeyGenerator.getInstance("SM4", "BC");

        // 生成随机的 SM4 密钥
        SecretKey secretKey = keyGenerator.generateKey();

        // 生成随机的初始化向量 (IV)
        byte[] ivBytes = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);

        // 设置 CBC 模式参数
        Cipher cipher = Cipher.getInstance("SM4/CBC/PKCS5Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));
        byte[] encryptBytes = cipher.doFinal("abcd1234".getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        // 输出生成的密钥和 IV
        System.out.println("生成的密钥: " + secretKey);
        System.out.println("生成的密钥Base64: " + Base64.toBase64String(secretKey.getEncoded()));

        System.out.println("生成的 IV Base64: " + Base64.toBase64String(ivBytes));

        System.out.println("encrypt>> " + Base64.toBase64String(encryptBytes));
        System.out.println("decrypt>> " + new String(decryptBytes));
    }

    @Test
    @SneakyThrows
    public void ecbMODETest() {
        // 添加 Bouncy Castle 加密提供程序
        Security.addProvider(new BouncyCastleProvider());

        // 创建 KeyGenerator 对象，用于生成对称密钥
        KeyGenerator keyGenerator = KeyGenerator.getInstance("SM4", "BC");

        // 生成随机的 SM4 密钥
        SecretKey secretKey = keyGenerator.generateKey();

        Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal("abcd1234".getBytes());
        System.out.println("encrypt Base64>> " + Base64.toBase64String(encryptedBytes));

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        System.out.println("decrypt>> " + new String(decryptedBytes));

    }

}
