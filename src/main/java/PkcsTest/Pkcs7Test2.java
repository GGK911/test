package PkcsTest;

import cn.com.mcsca.pki.core.bouncycastle.jce.provider.BouncyCastleProvider;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Base64;
import cn.com.mcsca.pki.core.util.EncryptUtil;
import cn.com.mcsca.pki.core.util.P12Util;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.com.mcsca.pki.core.x509.X509Certificate;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @author TangHaoKai
 * @version V1.0 2024/6/20 9:17
 */
public class Pkcs7Test2 {
    private static final BouncyCastleProvider BC = new BouncyCastleProvider();
    private static final String M = "大陆云盾";

    public static void main(String[] args) throws Exception {
        String sm2CertBase64 = "MIICmDCCAj+gAwIBAgIBAzAKBggqgRzPVQGDdTBYMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFQ2hpbmExFTATBgNVBAsMDEludGVybWVkaWF0ZTEiMCAGCSqGSIb3DQEJARYTMTM5ODMwNTM0NTVAMTYzLmNvbTAeFw0yNDAxMDIwOTA5NTFaFw0yNTAxMDEwOTA5NTFaMGYxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVDaGluYTESMBAGA1UEBwwJQ2hvbmdxaW5nMQ8wDQYDVQQDDAZHR0s5MTExIjAgBgkqhkiG9w0BCQEWEzEzOTgzMDUzNDU1QDE2My5jb20wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASNnEXTr1xvyeiCFyjvJMCitVR0UJ63UzfU4ftoGA0ejs3Pn/CLatCIzLfwjzimiqGf7jG8UxfOEdDY8QElLkqeo4HrMIHoMB0GA1UdDgQWBBSTRP7T62AAKxdBjQ5MDpIvQ+eaDDAfBgNVHSMEGDAWgBSKnV+2ua3/iWvHd8pNx80wRQmJYjAJBgNVHR8EAjAAMF8GCCsGAQUFBwEBAQH/BFAwTjAoBggrBgEFBQcwAYIcaHR0cDovLzEyNy4wLjAuMS9jYWlzc3VlLmh0bTAiBggrBgEFBQcwAoIWaHR0cDovLzEyNy4wLjAuMToyMDQ0MzAOBgNVHQ8BAf8EBAMCBsAwEgYDVR0TAQH/BAgwBgEB/wIBAzAWBgNVHSUBAf8EDDAKBggrBgEFBQcDCDAKBggqgRzPVQGDdQNHADBEAiBiMmP80Y2HbleOlOgGYSSpjANtC8rb8VxQ6/Fju2tiJwIgAjdUN740mQgwH6bTYoDw9oygZG8RVcpnrXYUWIVNt64=";
        String sm2PriBase64 = "MIGHAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBG0wawIBAQQg2TSyBsSej2+rzLbzosJISpHpvxnHkytt/ZFya/v3bk6hRANCAASNnEXTr1xvyeiCFyjvJMCitVR0UJ63UzfU4ftoGA0ejs3Pn/CLatCIzLfwjzimiqGf7jG8UxfOEdDY8QElLkqe";

        byte[] generatePfx = P12Util.generatePfx(sm2PriBase64, sm2CertBase64, "123456");
        System.out.println("PFX>> " + Base64.toBase64String(generatePfx));

        // 证书
        // X509Certificate x509Certificate = CertUtil.getX509CertificateBySM2(generatePfx);
        X509Certificate x509Certificate1 = new X509Certificate(Base64.decode(sm2CertBase64));
        // 私钥
        KeyFactory keyFactory = KeyFactory.getInstance("EC", BC);
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(sm2PriBase64)));


        // P1
        byte[] p1MessageSign = SignatureUtil.P1MessageSign("SM3withSM2", M.getBytes(), privateKey);
        boolean p1MessageVerify = SignatureUtil.P1MessageVerify("SM3withSM2", M.getBytes(), p1MessageSign, x509Certificate1.getPublickey());
        System.out.println("P1>> " + p1MessageVerify);

        // P7
        // 分离
        byte[] p7MessageSignDetach = SignatureUtil.P7MessageSignDetach("SM3withSM2", M.getBytes(), privateKey, x509Certificate1);
        boolean p7MessageVerifyDetach = SignatureUtil.P7MessageVerifyDetach(M.getBytes(), p7MessageSignDetach);
        System.out.println("P7分离>> " + p7MessageVerifyDetach);

        // 嵌入
        byte[] p7MessageSignAttach = SignatureUtil.P7MessageSignAttach("SM3withSM2", M.getBytes(), privateKey, x509Certificate1);
        boolean p7MessageVerifyAttach = SignatureUtil.P7MessageVerifyAttach(p7MessageSignAttach);
        System.out.println("P7嵌入>> " + p7MessageVerifyAttach);

        // 对称 DESede
        // 使用 Triple DES 密钥进行加密和解密
        KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
        // 密钥必须是长度为 24 的字节数组
        SecretKey key = keyGen.generateKey();
        // 生成随机的初始化向量 (IV) 8位
        byte[] ivBytes = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);

        byte[] bytes = EncryptUtil.encryptMessage("DESEDECBC", true, key.getEncoded(), ivBytes, M.getBytes(StandardCharsets.UTF_8));
        System.out.println(Base64.toBase64String(bytes));

        byte[] bytes1 = EncryptUtil.encryptMessage("DESEDECBC", false, key.getEncoded(), ivBytes, Base64.decode(bytes));
        System.out.println(new String(Base64.decode(bytes1)));

        // 使用已有key iv
        SecretKey originalKey = new SecretKeySpec(org.bouncycastle.util.encoders.Base64.decode("5iDgcOCrf5tMC50ly+UCjPQs6kxRnUDa"), "DESede");
        byte[] ivBytes2 = org.bouncycastle.util.encoders.Base64.decode("7r2HsM/vgPc=");
        byte[] bytes2 = EncryptUtil.encryptMessage("DESEDECBC", true, originalKey.getEncoded(), ivBytes2, M.getBytes(StandardCharsets.UTF_8));
        System.out.println(Base64.toBase64String(bytes2));

        byte[] bytes3 = EncryptUtil.encryptMessage("DESEDECBC", false, originalKey.getEncoded(), ivBytes2, Base64.decode(bytes2));
        System.out.println(new String(Base64.decode(bytes3)));

    }

    @Test
    @SneakyThrows
    public void p7VerifyTest() {
        String sm2PriBase64 = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgMn51c2ejddEL1LkNPeGLlRV9b2wYhVewUEVRMNyWCIOgCgYIKoEcz1UBgi2hRANCAAT2/Pq7swpbdh++lCIZGADzG5TeJCs8pJH2Eqa/uMvUFlRjraA60fgRFsMPiJLlI22GjfjD4EgmknKGQs86HFjM";
        String sm2PubBase64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE9vz6u7MKW3YfvpQiGRgA8xuU3iQrPKSR9hKmv7jL1BZUY62gOtH4ERbDD4iS5SNtho34w+BIJpJyhkLPOhxYzA==";
        // 过期了的
        String sm2PubCertBase64 = "MIIBRjCB7aADAgECAgcEYtU35+9OMAoGCCqBHM9VAYN1MCoxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVDaGluYTELMAkGA1UECwwCQ0EwHhcNMjMwNjI0MDQ0OTI3WhcNMjQwNjIzMDQ0OTI3WjAqMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFQ2hpbmExCzAJBgNVBAsMAkNBMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE9vz6u7MKW3YfvpQiGRgA8xuU3iQrPKSR9hKmv7jL1BZUY62gOtH4ERbDD4iS5SNtho34w+BIJpJyhkLPOhxYzDAKBggqgRzPVQGDdQNIADBFAiEA9v8HfLQQsGWeuRo0Rl0RxBTt0Rh0QcL1dK6/w3F8ZU8CICG99NxTXdcuxXO1+FkrQebyaXoo4G7T8JDknRBp0iVK";
        sm2PubCertBase64 = "MIIBRTCB7aADAgECAgcEYtU35+9OMAoGCCqBHM9VAYN1MCoxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVDaGluYTELMAkGA1UECwwCQ0EwHhcNMjMwNjI0MDkyMzA0WhcNMjQwNjI0MDkyNDA0WjAqMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFQ2hpbmExCzAJBgNVBAsMAkNBMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE9vz6u7MKW3YfvpQiGRgA8xuU3iQrPKSR9hKmv7jL1BZUY62gOtH4ERbDD4iS5SNtho34w+BIJpJyhkLPOhxYzDAKBggqgRzPVQGDdQNHADBEAiB6TsUAmx/8ABnq05BDMpsf9sIBQYhvsWLaj1EervwjcgIgCSAxYChHBLt8rHfvokW/PgJWcixSrfeTjc3ib/rKYP0=";
        sm2PubCertBase64 = "MIICsTCCAlSgAwIBAgIQcXOk5/ReSk50YPYOOK5y0zAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwNjI1MDIwNTI1WhcNMjQwNzI1MDIwNTI1WjBxMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExCzAJBgNVBAUMAjExMTMwMQYDVQQDDCoxNDgzOTg3NzQ2MjIwNTAzMDQwQOa1i+ivleWFrOWPuG9uZUAwMkAwMDIwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAATB6pJ1ZS5bT5bXKRvk/0d4+lKG4052YMxthW7dPQCtyV1+1is3q9ICz6j2bRB6gB6pkkwlwoW0Q3HEPcyGc0bwo4IBDjCCAQowHwYDVR0jBBgwFoAUMIj+P2KgJfTzKz2pbaagTAtMyrUwHQYDVR0OBBYEFDdYtWtClmJCPy8G48lZFh9aL7V8MAsGA1UdDwQEAwIE8DCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDEuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMSxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAMBggqgRzPVQGDdQUAA0kAMEYCIQDGTFECy7oBe4rNDd0DUbtywa8exNTeFZPGmeReXcoYjgIhAIpOz5pH3lKi0WS8hsfnfvlndwAIMyqQ05PYp9tegY3z";

        X509Certificate x509Certificate1 = new X509Certificate(Base64.decode(sm2PubCertBase64));
        // 私钥
        KeyFactory keyFactory = KeyFactory.getInstance("EC", BC);
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(sm2PriBase64)));

        // P7
        // 分离
        byte[] p7MessageSignDetach = SignatureUtil.P7MessageSignDetach("SM3withSM2", M.getBytes(), privateKey, x509Certificate1);
        System.out.println(Base64.toBase64String(p7MessageSignDetach));
        String p7Base64 = "TUlBR0NpcUJITTlWQmdFRUFnS2dnRENBQWdFRE1RNHdEQVlJS29FY3oxVUJneEVGQURDQUJnb3FnUnpQVlFZQkJBSUJBQUNnZ0RDQ0FVVXdnZTJnQXdJQkFnSUhCR0xWTitmdlRqQUtCZ2dxZ1J6UFZRR0RkVEFxTVFzd0NRWURWUVFHRXdKRFRqRU9NQXdHQTFVRUNnd0ZRMmhwYm1FeEN6QUpCZ05WQkFzTUFrTkJNQjRYRFRJek1EWXlOREE1TWpNd05Gb1hEVEkwTURZeU5EQTVNalF3TkZvd0tqRUxNQWtHQTFVRUJoTUNRMDR4RGpBTUJnTlZCQW9NQlVOb2FXNWhNUXN3Q1FZRFZRUUxEQUpEUVRCWk1CTUdCeXFHU000OUFnRUdDQ3FCSE05VkFZSXRBMElBQlBiOCtydXpDbHQySDc2VUloa1lBUE1ibE40a0t6eWtrZllTcHIrNHk5UVdWR090b0RyUitCRVd3dytJa3VVamJZYU4rTVBnU0NhU2NvWkN6em9jV013d0NnWUlLb0VjejFVQmczVURSd0F3UkFJZ2VrN0ZBSnNmL0FBWjZ0T1FRektiSC9iQ0FVR0liN0ZpMm85UkhxNzhJM0lDSUFrZ01XQW9Sd1M3Zkt4Mzc2SkZ2ejRDVm5Jc1VxMzNrNDNONG0vNnltRDlBQUF4Z2dFNE1JSUJOQUlCQVRBMU1Db3hDekFKQmdOVkJBWVRBa05PTVE0d0RBWURWUVFLREFWRGFHbHVZVEVMTUFrR0ExVUVDd3dDUTBFQ0J3UmkxVGZuNzA0d0RBWUlLb0VjejFVQmd4RUZBS0NCbFRBWkJna3Foa2lHOXcwQkNRTXhEQVlLS29FY3oxVUdBUVFDQVRBY0Jna3Foa2lHOXcwQkNRVXhEeGNOTWpRd05qSTBNRGt5TXpJMVdqQXBCZ2txaGtpRzl3MEJDVFF4SERBYU1Bd0dDQ3FCSE05VkFZTVJCUUNoQ2dZSUtvRWN6MVVCZzNVd0x3WUpLb1pJaHZjTkFRa0VNU0lFSUtZVXo5aVZYdCtjb2dpNG9wbVJNZHNMMnZsS2Jrb1dvYTVaSnZYT092VEJNQW9HQ0NxQkhNOVZBWU4xQkVZd1JBSWdPRmE5M0lNeFNPQ0xjV3g4WDM3aVJ4VDJwWGpFTVhTMDY5ajFCdk1CR0xRQ0lEVzR0cFlJMk90TCtFdS9lV1BXYlhGSGtPZVZNbFg2NXdPcklrWTJkWEIwQUFBQUFBQUE=";
        boolean p7MessageVerifyDetach = SignatureUtil.P7MessageVerifyDetach(M.getBytes(), Base64.decode(p7Base64));
        // boolean p7MessageVerifyDetach = SignatureUtil.P7MessageVerifyDetach(M.getBytes(), p7MessageSignDetach);
        System.out.println("P7分离>> " + p7MessageVerifyDetach);

        // 嵌入
        // byte[] p7MessageSignAttach = SignatureUtil.P7MessageSignAttach("SM3withSM2", M.getBytes(), privateKey, x509Certificate1);
        // boolean p7MessageVerifyAttach = SignatureUtil.P7MessageVerifyAttach(p7MessageSignAttach);
        // System.out.println("P7嵌入>> " + p7MessageVerifyAttach);


    }

}
