package cipherTest;

import lombok.SneakyThrows;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/12 15:11
 */
public class TripleDESExample {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // 要加密的原始数据
        String plainText = "Hello, Triple DES encryption!";

        // 使用 Triple DES 密钥进行加密和解密
        KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
        // 密钥必须是长度为 24 的字节数组
        SecretKey key = keyGen.generateKey();

        // 生成随机的初始化向量 (IV) 8位
        byte[] ivBytes = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);

        System.out.println("生成的密钥Base64: " + org.bouncycastle.util.encoders.Base64.toBase64String(key.getEncoded()));
        System.out.println("生成的 IV Base64: " + org.bouncycastle.util.encoders.Base64.toBase64String(ivBytes));

        // 加密数据
        String encryptedText = encrypt(plainText, key, ivBytes);
        System.out.println("Encrypted text: " + encryptedText);

        // 解密数据
        String decryptedText = decrypt(encryptedText, key, ivBytes);
        System.out.println("Decrypted text: " + decryptedText);
    }

    // 使用 Triple DES 密钥进行加密
    @SneakyThrows
    private static String encrypt(String plainText, SecretKey secretKey, byte[] ivBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "DESede");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 使用 Triple DES 密钥进行解密
    @SneakyThrows
    private static String decrypt(String encryptedText, SecretKey secretKey, byte[] ivBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "DESede");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}

