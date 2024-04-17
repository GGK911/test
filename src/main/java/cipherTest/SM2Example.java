package cipherTest;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;

import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/12 15:23
 */
public class SM2Example {
    public static void main(String[] args) throws Exception {
        // 添加 Bouncy Castle 提供程序
        Security.addProvider(new BouncyCastleProvider());

        // 创建密钥对生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");

        // 初始化密钥对生成器
        keyPairGenerator.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"), new SecureRandom());

        // 生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 获取公钥和私钥
        ECPublicKeyParameters publicKey = (ECPublicKeyParameters) PublicKeyFactory.createKey(keyPair.getPublic().getEncoded());
        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());

        // 待加密的数据
        String plainText = "Hello, SM2 encryption!";

        // 使用公钥加密数据
        byte[] encryptedBytes = encrypt(plainText.getBytes(StandardCharsets.UTF_8), publicKey);
        String encryptedText = bytesToHex(encryptedBytes);
        System.out.println("Encrypted text: " + encryptedText);

        // 使用私钥解密数据
        byte[] decryptedBytes = decrypt(encryptedBytes, privateKey);
        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
        System.out.println("Decrypted text: " + decryptedText);
    }

    // 使用公钥加密数据
    private static byte[] encrypt(byte[] plainText, ECPublicKeyParameters publicKey) throws InvalidCipherTextException {
        SM2Engine engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
        ParametersWithRandom params = new ParametersWithRandom(publicKey, new SecureRandom());
        engine.init(true, params);
        return engine.processBlock(plainText, 0, plainText.length);
    }

    // 使用私钥解密数据
    private static byte[] decrypt(byte[] encryptedBytes, ECPrivateKeyParameters privateKey) throws InvalidCipherTextException {
        SM2Engine engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
        engine.init(false, privateKey);
        return engine.processBlock(encryptedBytes, 0, encryptedBytes.length);
    }

    // 将字节数组转换为十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}

