package cipherTest;

import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
        System.out.println("secretKeyBase64>> " + Base64.toBase64String(secretKey.getEncoded()));
        System.out.println("secretKeyString>> " + new String(secretKey.getEncoded()));

        Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec("8<09444<?;19?4<5".substring(0, 16).getBytes(StandardCharsets.UTF_8), "SM4"));
        // cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), "SM4"));
        // cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal("Uvm0zK4yAESZyQf4FsAKSHnC+jlxLIqPLiTAvdWIAHT+tJwnGgmARsT7xbvHJI0c".getBytes());
        System.out.println("encrypt Base64>> " + Base64.toBase64String(encryptedBytes));

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec("8<09444<?;19?4<5".substring(0, 16).getBytes(StandardCharsets.UTF_8), "SM4"));
        // cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] cipherBytes = Base64.decode("DTZkdmri9ENQsytL/X+9Qz80+MNYXLkXOKSYlOtGbR6/l86puvbpjET3uMs2/bynIKx8Q3obxGmQZS9/PNrUOI9+UuJxvWs1AFsLuLHucmdTLA3skKO2YSv9vj3T2mR4LKJyJ+E6+hIjPh5JtSJ73NlqLQ62/ltzzIQ2ATXGbBkThk7xwW+i38iyef0hObHyHSXvBZqZ695VN0TGf73VYmCc6ZzQfK3Ab49KBiRqTr7mMNTFqGNn9gM/UR5Jf/cgyAtNw6s4Ovj4SzgbQ4liDEg1Gh0avxnoxklTTF3fGn6UYfoE6JvC9WkgXlHOCCUxaIDO8CS6W1XsmJUQczOF3M0TpXelcN8SSQ0+RsbN0ScUvCHRZUgXsvViSXC+fGk9EAwreJn+Eu6kqWCTMvqbXI44/OcCS8d8A/R2ICv0HAbX2qu10uwCFG5HmKrF8uFlypyZ7asGeu8wtherOW7CfkFtDZAu353rSnUAmV2KpRgvBgt/UTN1She1YgDDHu4YnjW7R1H2pIjGEkWMB4tRxw==");
        byte[] decryptedBytes = cipher.doFinal(cipherBytes);
        System.out.println("decrypt>> " + new String(decryptedBytes));

    }

}
