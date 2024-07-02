package cipherTest;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
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
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec("7?6>?<13;0?94=96".substring(0, 16).getBytes(StandardCharsets.UTF_8), "SM4"));
        // cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), "SM4"));
        // cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal("Uvm0zK4yAESZyQf4FsAKSHnC+jlxLIqPLiTAvdWIAHT+tJwnGgmARsT7xbvHJI0c".getBytes());
        System.out.println("encrypt Base64>> " + Base64.toBase64String(encryptedBytes));

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec("7?6>?<13;0?94=96".substring(0, 16).getBytes(StandardCharsets.UTF_8), "SM4"));
        // cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] cipherBytes = Base64.decode("dC0wQR2WxAiLas9I74TaegdO9Rjx2V/b7m/O6wrk2mIEv8LB8Jo/ZFolqls3J74CcZHANReV1ZI2fFUMnYATlyrG3JRbFMaDB8dv0iTXyzzBg6dNX5mzyGr8IL7Hrwh3znaFaj7QT3Z4vKIXno9cwJCLZ36zRts943PEQYXXwU32rZOInIrrD9q3ojOnINngdcJ7eyMeyZh4y4DOFoDyGkV4evM7JEOpq9Rpe0BY51kaN1py9VXINn8b8nyM2tCW71/pG095+xr+qGMfuof/dNzfXfUI7EdgspbNByuGJe1dL9Mc/3rBnESMdV2IAx84RN9cQ0gwwEdBYvM3/gSj0gGdLcDVMxJwEjCuQdhHOzsHoFZ5RZxLuI/AiYJDPYeR");
        byte[] decryptedBytes = cipher.doFinal(cipherBytes);
        System.out.println("decrypt>> " + new String(decryptedBytes));

    }

    @Test
    @SneakyThrows
    public void sm4KeyTest() {
        String sm4KeyStr = "728BD2683A5BE169";
        String sm4Cipher = "3274E399E51094A4E63E5419B05201213463D7FC250BE31EAA77BAC6B4D1BD8C0138EED4DAAB3CEC822EB2F7BE3E6F20575C445DD207741880DB60F8F8D64D8CD0269FDA0026AC131DA79CF5292E07A66D04FCADB13516730278D8E7CD4455A071AD74174939DC4765D21E66231FEAFAEC661A9F9C7951AEC52C578F3404993C";
        String decryptSm4 = decryptSm4(sm4Cipher, sm4KeyStr);
        System.out.println("sm4KeyStr>> " + decryptSm4);

        SecretKey sm4SecretKey = new SecretKeySpec(sm4KeyStr.getBytes(StandardCharsets.UTF_8), "SM4");
        String decryptSm41 = decryptSm4(sm4Cipher, sm4SecretKey);
        System.out.println("sm4SecretKey>> " + decryptSm41);

        String sm4KeyHex = Hex.toHexString(sm4KeyStr.getBytes(StandardCharsets.UTF_8));
        System.out.println("sm4KeyHex>> " + sm4KeyHex);
        SecretKey sm4SecretKey2 = new SecretKeySpec(Hex.decode(sm4KeyHex), "SM4");
        String decryptSm42 = decryptSm4(sm4Cipher, sm4SecretKey2);
        System.out.println("sm4SecretKey2>> " + decryptSm42);

        // 创建 KeyGenerator 对象，用于生成对称密钥
        KeyGenerator keyGenerator = KeyGenerator.getInstance("SM4", "BC");
        // 生成随机的 SM4 密钥
        SecretKey generateKey = keyGenerator.generateKey();
        System.out.println("generateKeyStr>> " + new String(generateKey.getEncoded()));
        System.out.println("generateKeyHex>> " + Hex.toHexString(generateKey.getEncoded()));

        String randomNumbers = RandomUtil.randomNumbers(16);
        SecretKey sm4SecretKey3 = new SecretKeySpec(randomNumbers.getBytes(StandardCharsets.UTF_8), "SM4");
        System.out.println("randomNumbersStr>> " + new String(randomNumbers.getBytes(StandardCharsets.UTF_8)));
        System.out.println("randomNumbersHex>> " + Hex.toHexString(randomNumbers.getBytes(StandardCharsets.UTF_8)));

        String encryptSm4 = encryptSm4("{\"context\":{\"transNo\":\"123456\",\"authType\":[\"00\",\"01\"],\"transType\":[\"00\",\"01\"],\"appID\":\"002\",\"isBase64\":null,\"devices\":null}}", sm4SecretKey3);
        System.out.println("sm4SecretKey3Enc>> " + encryptSm4);

        String decryptedSm4 = decryptSm4(encryptSm4, sm4SecretKey3, Padding.PKCS5Padding);
        System.out.println("sm4SecretKey3Dec>> " + decryptedSm4);

    }

    @Test
    @SneakyThrows
    public void hutoolTest() {
        String plainText = "13983053455";
        SecretKey secretKey = new SecretKeySpec(Hex.decode("0ac4c41501f3fb5397455f2b2bb6b5c5"), "SM4");
        String key = new String(secretKey.getEncoded(), StandardCharsets.UTF_8);
        SecretKey secretKey2 = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "SM4");
        System.out.println(ArrayUtil.equals(secretKey.getEncoded(), secretKey2.getEncoded()));


        System.out.println("ECB模式加密");
        String cipherText = encryptSm4(plainText, secretKey);
        System.out.println("密文: " + cipherText);
        String cipherText2 = encryptSm4(plainText, secretKey);
        System.out.println("密文2: " + cipherText2);

        // 最开始Base64的
        // cipherText = "5mn2LVVvIomqjAhEgtEqkaWriimyXa9qXI+24YLu4jx2Ec9OZlJsVjdxkYOR6zUcGrx/2/mQET5VIhyuW/6WJRxjd7X0oxsKUN8FZ+cJev/tQUeDhu+TSJLxKIsgQq3lbNdGPP9fHkS7agGiSbur13ZUAsGQw3eTUjVsUaMsK+Q=";
        // key = "BC837B74B600EE41";
        //
        cipherText = "3274E399E51094A4E63E5419B05201213463D7FC250BE31EAA77BAC6B4D1BD8C0138EED4DAAB3CEC822EB2F7BE3E6F20575C445DD207741880DB60F8F8D64D8CD0269FDA0026AC131DA79CF5292E07A66D04FCADB13516730278D8E7CD4455A071AD74174939DC4765D21E66231FEAFAEC661A9F9C7951AEC52C578F3404993C";
        key = "728BD2683A5BE169";

        String plainText1 = decryptSm4(cipherText, key);
        System.out.println("明文: " + plainText1);
        System.out.println();
    }

    //加密为16进制，也可以加密成base64/字节数组
    public static String encryptSm4(String plaintext, String key) {
        SM4 sm4 = new SM4(Mode.ECB, Padding.PKCS5Padding, key.getBytes());
        return sm4.encryptBase64(plaintext);
    }

    //加密为16进制，也可以加密成base64/字节数组
    public static String encryptSm4(String plaintext, SecretKey key) {
        SM4 sm4 = new SM4(Mode.ECB, Padding.PKCS5Padding, key);
        return sm4.encryptBase64(plaintext);
    }

    //解密
    public static String decryptSm4(String ciphertext, String key) {
        for (Padding value : Padding.values()) {
            try {
                System.out.println("ECB模式解密");
                System.out.println("填充方式：" + value.name());
                SM4 sm4 = new SM4(Mode.ECB, value, key.getBytes());
                return sm4.decryptStr(ciphertext, CharsetUtil.CHARSET_UTF_8);
            } catch (Exception e) {
                System.out.println(value.name() + " Error!");
            }
        }
        System.out.println("填充方式全部错误");
        return null;
    }

    //解密
    public static String decryptSm4(String ciphertext, SecretKey key) {
        for (Padding value : Padding.values()) {
            try {
                System.out.println("ECB模式解密");
                System.out.println("填充方式：" + value.name());
                SM4 sm4 = new SM4(Mode.ECB, value, key);
                return sm4.decryptStr(ciphertext, CharsetUtil.CHARSET_UTF_8);
            } catch (Exception e) {
                System.out.println(value.name() + " Error!");
            }
        }
        System.out.println("填充方式全部错误");
        return null;
    }

    public static String decryptSm4(String ciphertext, SecretKey key, Padding padding) {
        SM4 sm4 = new SM4(Mode.ECB, padding, key);
        return sm4.decryptStr(ciphertext, CharsetUtil.CHARSET_UTF_8);
    }

}
