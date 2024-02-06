package cipherTest;

import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CBCEncryptionExample {

    public static void main(String[] args) throws Exception {
        String originalText = "Hello, AES CBC Encryption!";
        String key = "0123456789abcdef"; // 128-bit key
        String iv = "1234567890abcdef"; // 128-bit IV

        System.out.println("Original Text: " + originalText);

        // Encryption
        String encryptedText = encrypt(originalText, key, iv);
        System.out.println("Encrypted Text: " + encryptedText);
        System.out.println(Hex.toHexString(encryptedText.getBytes(StandardCharsets.UTF_8)));

        // Decryption
        String decryptedText = decrypt(encryptedText, key, iv);
        System.out.println("Decrypted Text: " + decryptedText);
    }

    public static String encrypt(String plaintext, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParams = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParams = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));

        return new String(decryptedBytes);
    }
}
