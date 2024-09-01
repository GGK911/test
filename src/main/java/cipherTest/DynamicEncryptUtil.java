package cipherTest;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Arrays;

/**
 * 对称加密工具类
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/30 9:38
 */
public class DynamicEncryptUtil {

    private static final Provider BC = new BouncyCastleProvider();

    /**
     * 通过算法生成密钥
     *
     * @param alg 算法
     * @return 密钥
     * @throws NoSuchAlgorithmException 算法异常
     */
    public static SecretKey getKeyByAlg(String alg) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator;
        if (alg.equalsIgnoreCase("SM4")) {
            keyGenerator = KeyGenerator.getInstance("SM4", BC);
        } else if (alg.equalsIgnoreCase("desede")) {
            keyGenerator = KeyGenerator.getInstance("DESede");
        } else if (alg.equalsIgnoreCase("aes")) {
            keyGenerator = KeyGenerator.getInstance("aes");
        } else {
            keyGenerator = KeyGenerator.getInstance("SM4", BC);
        }
        return keyGenerator.generateKey();
    }

    /**
     * 对称加密
     *
     * @param indata    明文
     * @param key       密钥
     * @param iv        向量
     * @param alg       指定算法
     * @param isPadding 填充方式
     * @return 密文
     */
    public static byte[] encrypt(byte[] indata, byte[] key, byte[] iv, String alg, boolean isPadding) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        // 自动选择算法
        if (alg == null || alg.isEmpty()) {
            alg = selectAlgorithmByKeyLength(key.length);
        } else {
            // 检查算法和密钥长度的匹配
            checkKeyLengthForAlgorithm(key.length, alg);
        }

        // 填充模式
        String padding = isPadding ? "PKCS5Padding" : "NoPadding";
        String mode = (iv == null || iv.length == 0) ? "ECB" : "CBC";
        String transformation = alg + "/" + mode + "/" + padding;

        Cipher cipher = Cipher.getInstance(transformation, BC);

        // 生成密钥
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, alg);

        // 初始化加密器
        if (mode.equals("ECB")) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        } else {
            IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOf(iv, iv.length));
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        }

        // 执行加密
        return cipher.doFinal(indata);
    }

    // 根据密钥长度自动选择算法
    private static String selectAlgorithmByKeyLength(int keyLength) {
        switch (keyLength) {
            case 16:
                return "SM4"; // SM4 (128-bit)
            case 24:
                return "AES"; // AES (192-bit)
            case 32:
                return "AES"; // AES (256-bit)
            case 14:
                return "DESede"; // 3DES (112-bit)
            case 21:
                return "DESede"; // 3DES (168-bit)
            default:
                throw new IllegalArgumentException("Unsupported key length: " + keyLength);
        }
    }

    // 检查算法和密钥长度的匹配
    private static void checkKeyLengthForAlgorithm(int keyLength, String alg) {
        switch (alg) {
            case "SM4":
                if (keyLength != 16) {
                    throw new IllegalArgumentException("SM4 requires a 16-byte key.");
                }
                break;
            case "AES":
                if (keyLength != 16 && keyLength != 24 && keyLength != 32) {
                    throw new IllegalArgumentException("AES requires a 16, 24, or 32-byte key.");
                }
                break;
            case "DESede":
                if (keyLength != 14 && keyLength != 24) {
                    throw new IllegalArgumentException("DESede requires a 14-byte (112-bit) or 24-byte (168-bit) key.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + alg);
        }
    }

    public static void main(String[] args) throws Exception {
        byte[] indata = "This is a test message.".getBytes(); // 加密原文
        byte[] key = new byte[16]; // 16 字节的密钥（自动使用 SM4 算法）
        byte[] iv = new byte[16]; // 向量 (IV)，如需要

        boolean isPadding = true; // 是否使用填充
        String alg = ""; // 指定算法 (例如: "AES", "SM4", "DESede")，留空则自动选择

        byte[] encryptedData = encrypt(indata, key, iv, alg, isPadding);
        System.out.println("Encrypted data: " + Hex.toHexString(encryptedData));
    }

}

