package cipherTest;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;

public class DESExample {

    public static void main(String[] args) throws Exception {
        // 生成密钥
        Key secretKey = generateKey();
        System.out.println("密钥" + secretKey.getAlgorithm() + Base64.toBase64String(secretKey.getEncoded()));

        // 创建加密器和解密器
        Cipher cipher = Cipher.getInstance("DES");

        // 加密
        String originalText = "Hello, DES!";
        byte[] encryptedText = encrypt(originalText, secretKey);
        System.out.println("Encrypted Text: " + new String(encryptedText));

        // 解密
        String decryptedText = decrypt(encryptedText, secretKey);
        System.out.println("Decrypted Text: " + decryptedText);
    }

    private static Key generateKey() throws Exception {
        // 使用 KeyGenerator 生成密钥
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(56); // 56位密钥
        return keyGenerator.generateKey();
    }

    private static byte[] encrypt(String text, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(text.getBytes());
    }

    private static String decrypt(byte[] encryptedText, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(encryptedText);
        return new String(decryptedBytes);
    }
}
