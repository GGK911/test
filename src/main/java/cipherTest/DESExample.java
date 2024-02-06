package cipherTest;

import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class DESExample {

    public static void main(String[] args)  throws Exception{
        String key = "MIID2jCCAsICAQAwUjELMAkGA1UEBhMCQ04xDTALBgNVBAoMBENGQ0ExEjAQBgNVBAsMCUN1c3RvbWVyczEgMB4GA1UEAwwXQ0ZDQUBNb2JpbGVAQW5kcm9pZEAxLjAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC7iaMEgo9wRecWQc1dJ8+k/BFkSSYhEYp7FeI/4y5D710mOtJ73PbrjAMAH6ej9+8IgAro9y64xZL8FRttgt6BFK3zhtgK0uhd0FbkMj4PEOSbrfo9vUYOAlH7rIqwFBV9958YrJ99Ae2fkLa+65cm1nktjp132TwqBcKO06uiTBysecWZ+tAjykYVaaJaOh4ph1IgVUo2SVhSMMkGe2n0ZcY94+aOChEqI3Ur9Z4h39Ef/vDL+FtrhxRloN2KXSIBUI9ldxaLXXpT4YOt8W3DkQkpMpbErofmAAyn604EUnNvUFveSYyWWQl1bTzxRbsKMqw/WxXf23uIlq77vB3vAgMBAAGgggFBMBMGCSqGSIb3DQEJBxMGMTExMTExMIIBKAYJKoZIhvcNAQk/BIIBGTCCARUCAQEEggEOMIIBCgKCAQEA4f8FTzewBTR9LqdPmhlLeYySgKZnRUkJCx2tRG6juF3wibttyUzw4dKL0d0eB56FH8iqyinLJHRZaRreFHY+JEU3CwPXXaPOtWTnWG6g3Ty74cnzU4XCUhPSndAAtOiTYxzSCsHAju4Fs04UyMZ5pQlNPZtVh01q0x5sMQg5PblJHsVu0zByqPmagtlp1FRZ4X/psPgEfb40JEgcsj1DxRPwZUFd1x90atc9ppTzv5qxitGRMWCsr1R1NYSMOayjhvXkEYefuE798rP9kKl7ohgoeTqPznuzNdiwp57ux80aB9N4bz3YE4+6N+C4JLqOcPlCDvj2+bjziRfE+KZYKQIDAQABMA0GCSqGSIb3DQEBBQUAA4IBAQClAXbOe3gLoh4H704+LVO/gFGqzptQYcSfFqhSjU++Ygb";
        final byte[] decode = Base64.getDecoder().decode(key);
        System.out.println(new String(decode));
        System.out.println(Hex.toHexString(decode));
        // // 生成密钥
        // Key secretKey = generateKey();
        //
        // // 创建加密器和解密器
        // Cipher cipher = Cipher.getInstance("DES");
        //
        // // 加密
        // String originalText = "Hello, DES!";
        // byte[] encryptedText = encrypt(originalText, secretKey);
        // System.out.println("Encrypted Text: " + new String(encryptedText));
        //
        // // 解密
        // String decryptedText = decrypt(encryptedText, secretKey);
        // System.out.println("Decrypted Text: " + decryptedText);
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
