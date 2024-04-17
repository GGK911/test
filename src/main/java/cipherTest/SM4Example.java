package cipherTest;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
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

        // 输出生成的密钥和 IV
        System.out.println("生成的密钥: " + secretKey);
        System.out.println("生成的密钥Base64: " + Base64.toBase64String(secretKey.getEncoded()));

        System.out.println("生成的 IV Base64: " + Base64.toBase64String(ivBytes));
    }

}
