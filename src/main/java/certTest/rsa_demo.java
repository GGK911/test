package certTest;

import certTest.saxon.CertUtils;
import certTest.saxon.rsa.RSAUtils;
import certTest.saxon.utils.ResultUtils;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-14 13:54
 **/
public class rsa_demo {

    private static final String M = "ggk911_RSA_demo";

    private static final Provider BC = new BouncyCastleProvider();
    /**
     * 1年后
     */
    private static final long CERT_EXPIRE = System.currentTimeMillis() + 1 * 1000L * 60 * 60 * 24 * 365;

    private static final String PWD = "123456";

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("//*************************************************封装的RSA工具类**********************************************************//");

        // 颁发者固定写死
        String issuerStr = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,E=13983053455@163.com,L=重庆,ST=重庆";
        // 使用者，需自定义传参带入
        String subjectStr = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,E=13983053455@163.com,L=重庆,ST=重庆";
        // 颁发地址，后续用到什么项目，公司地址等
        String certificateCRL = "https://www.mcsca.com.cn/";

        Map<String, byte[]> cert2 = RSAUtils.createCert(PWD, issuerStr, subjectStr, certificateCRL, 365, 2048);
        // FileUtil.writeBytes(cert2.get("keyStoreData"), "C:\\Users\\ggk911\\Desktop\\rsa_2048.p12");
        // FileUtil.writeBytes(cert2.get("certificateData"), "C:\\Users\\ggk911\\Desktop\\rsa_2048.cer");

        //*RSA签名
        ResultUtils signRSACert = CertUtils.signRSACert(cert2.get("keyStoreData"), M, PWD);
        Map<String, Object> result = (Map<String, Object>) signRSACert.getObject();
        System.out.println("RSA签名值：" + result.get("signData"));
        System.out.println("RSA公钥Base64：" + Base64.toBase64String((byte[]) result.get("publicKey")));

        //*RSA验签
        ResultUtils verifyRSACert = CertUtils.verifyRSACert(cert2.get("keyStoreData"), PWD, M, (String) result.get("signData"));
        Map<String, Object> result2 = (Map<String, Object>) verifyRSACert.getObject();
        System.out.println("RSA验签结果:" + result2.get("verifyResult"));

        System.out.println("//*************************************************RSA证书反序列化**********************************************************//");

        X509CertificateHolder certHldr = new X509CertificateHolder(cert2.get("certificateData"));
        SubjectPublicKeyInfo k = certHldr.getSubjectPublicKeyInfo();
        CertificateFactory fact = CertificateFactory.getInstance("X.509", BC);
        X509Certificate cert3 = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(cert2.get("certificateData")));
        System.out.println(cert3.getIssuerDN().toString());

        System.out.println("//*************************************************RSA-CSR生成**********************************************************//");

        // System.out.println("RSA P10 BASE64:" + CsrUtil.generateCsr(true, "1234"));

        // 生成RSA1024公私钥
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BC);
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        // 打印私钥
        PrivateKey privateKey = keyPair.getPrivate();
        PemFormatUtil.priKeyToPem(privateKey);
        // 打印公钥
        PublicKey publicKey = keyPair.getPublic();
        PemFormatUtil.pubKeyToPem(publicKey);

        // Subject
        String subjectParam = "CN=GGK911,OU=GGK911,O=GGK911,C=CN," + BCStyle.E + "=13983053455@163.com,L=重庆,ST=重庆";
        X500Principal subject = new X500Principal(subjectParam);

        // SHA256withRSA算法 签名者对象
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BC)
                .build(privateKey);

        // 创建 CSR
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, publicKey);
        PKCS10CertificationRequest rsaCsr = builder.build(signer);
        // 打印 CSR
        PemFormatUtil.csrToPem(rsaCsr);
        System.out.println("----------打印Base64格式CSR");
        System.out.println(Base64.toBase64String(rsaCsr.getEncoded()));

        System.out.println("//*************************************************加密-解密**********************************************************//");

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", BC);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encData = cipher.doFinal(M.getBytes(StandardCharsets.UTF_8));
        System.out.println("ENC>> " + Hex.toHexString(encData));

        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] deData = cipher.doFinal(encData);
        System.out.println("解密>> " + new String(deData));

        System.out.println("//*************************************************公私钥匹配**********************************************************//");

        // dlyd生产的公钥证书和私钥,不是一对
        String publicRsaCertDlyd = "MIIElDCCA3ygAwIBAgIQJ8iipdsgD5MJrAWwcQvvTzANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yNDA1MDcwNjI0MTJaFw0yNDA1MDgwNjI0MTJaMIGcMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjkxNTAwMTA4TUE1WVE3UDY2NTFOMEwGA1UEAwxFMTYzODc5MzQ5NDYyNDEyNDkyOEDlpKfpmYbkupHnm77nlLXlrZDorqTor4HmnI3liqHmnInpmZDlhazlj7hAMDFAMDA4MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo3+LYwcRb+LhCkl1x5P5U6tjlX6V579c2snqGiSEQMGef/YsDrh5F+la/9fk9pP4W8+cIjnWIdogc7s1LKgCd3ehMoJtCWQFvCulWkZGc8lXFnQQTQM5QP8uVEwKECWrcyC0ilsgr/luV1KXyggdjJd0CsE2wlBRWJbc4UkOqEjUo09BrkQah5HhxivvugVyUPu3rDbyRuVa/mp9wmuvYk3u0hIj0bxAuCuQ+HRCS0ShDlvs1cG3Tp7T6Op4ggKdjkTzIaOE3NsM2dnIPjeKoMPCGg9cKTNibpL2m92W7O+w6rPh0k878TptvvlbqpMbL0eBpH8qaiMGXE5ce8f4NwIDAQABo4IBLjCCASowHwYDVR0jBBgwFoAUO9/3EEzFZuEmqSzgUrLGxA6R+R0wHQYDVR0OBBYEFJcaz7q4VbiSZX1M7e+VxUeKQjCOMEQGA1UdIAQ9MDswOQYHKoEch4QLCTAuMCwGCCsGAQUFBwIBFiBodHRwczovL3d3dy5tY3NjYS5jb20uY24vY3BzLmh0bTALBgNVHQ8EBAMCBsAwgZQGCCsGAQUFBwEBBIGHMIGEMDEGCCsGAQUFBzAChiVodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvbWNzY2EuY2VyMCcGCCsGAQUFBzABhhtodHRwOi8vMTEzLjIwNC4zNC4xODY6MjA0NDQwJgYIKwYBBQUHMAGGGmh0dHA6Ly8xODMuNjYuMTg0LjIyOjIwNDQ0MA0GCSqGSIb3DQEBCwUAA4IBAQBe+p+dLxa+XWhy38SAtop+awB6Ssvchl62YTU69rqNQbyuPh46qNL2GY43ZzBa/ubnYa4ky2LYLQsK7iwetnS4ktomsHx7DHOC35Qc1+LE4SsoyOsbyYSUL1hSCKBiDIK2F4J6qOFUH40shzRll6JcPNLaTACDQI4IKIzZNEYuEkf4if82KGBnsEuxpq4QuGvIIL0PxN+tJMMOF+Q8+zMiV0lDWaH/yhb3qGEaBdw45WM5dhgsj7DZ2vWiRdeDN8UnGjaBdrCaDV/BoSO+vzgmEUq1g8bckUNqW/C9+UlI4HbxeTy0TWB1jmMP16J+H3LapdlYj91/g/jBIhiJxyE0";
        String pribateKeyRsaDlyd = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDjq1Kw33aGB1Sb6G6sNwSW1S629AijkHZOZjquYAAABeKMtZXWMNHTMON4BS5/yAT2Z8ZFtANd3WAWKsLbLOkQQC1Gv7h/+plxtIdJTt5FZtzoGiw+ouR/dR0zrjANEMeUxQOETqo39TyMhmGq/znj1pdnrQpqs0jmYvc4dlH2DVxaPnBZty33SarD9zIgvTZTViEO8BJK+gs3g9uKjPxCglWW++7VnU1BuuAWFG1z5vItnEKM+t1hq3VCkVdLTkmPgYbwaucypUBpvS+mgShuHHU5ee67Im+sZwK0XYDV6ha5V7MJjF8u7gcvWZ8lzd6/lhcwLggaL6i0lPSKvFVdAgMBAAECggEADXtyzSiekzdR6l2sWCR18By1xH3VGSKP7vqc1QC4X0f57409T07azh/iCJR9+XE14gWpNy1+2YsnioiDu4kAUEcMp1jpKyMC/TnMnSm/feqmgFQ0dI0e7PFZhlr1W4C34PZ9y9MAv4mVSVkX5ZFxsy1dD/WuXT06vvHzY/OVzPVG/DZe97+lhdRiHK5KxB+DG+xFq3PLjn0p34/9bqkAfDJx1eFglKiXH96hcPASyr3ekdjXG6f726WjxPpJQIBlsSqARJ12JO/pJ2ZHwGoFshxonNVgWmOMiSPqzUyjRWl2Wfc3Pcbrml16Rrv8akoE6ta+WB+YUUhTdSk5yF7zvQKBgQD5MblqhTn6oayEvUNlw56ggAA2h68etqSuXA+YHeaU1uQv27ut4RdqQ7dbEeAW0v0es9H7JfA4QLyB1oYYket1Qqttsq7wYgiSGqGsxmSMP0xT5jijd2ygcQnXRfsNri7RuLztiD362gJKii128RzYAxGYZ/t04kUcBFQWTIdg5wKBgQDp4xqMMAEbfZfgGhGRO+wK8i5MstL92c9vaSZ+e9vS7QpjtvMMHe+jgY8TU2Qtz1+MYxJx2Xrx7uatl9bbEaq3LG+hYT1l72mvm5heOHNHBEKouuH3T1N27cMmu8ufR99uunL7h101WQFUiubz4SeMDkRvBpobX3OBLcz514VbGwKBgBEfOIU4KDavWyI3uuTIHyMaCGm2wvKSTBhq3OtvyoMZjM45A9k7qRc1Fekc/k+zKY8tfdUK00maMRmeutH/XLVO4maEK376zWn0iH1NmGUGiGEPZX7d2snfWnS/KzPSbwcb8WdEwDV3O0cR3XPZt0ikVVYqdt2eGm+FYJ2znm2rAoGAO2qnTt/PGDXCxHq65cNsRqeZnYB8W2Le8LWswssiYCY50nUir3xXaZk7SLSRqZWZ92cfw251bkq7rXP6cDK/xML0JPI4D9JPbf5AKgd7OZpaeNTpHNO5J2hlgEyLAQ7YMyAIn9+WjTvsO5cB7wjO7CSfu/jYY4XPKQ0CmqHmzesCgYANVA635nIH4Pyryqs1APZ72lLzcq3WYL84jqz+CTsTRTZGrA5Bg4Zjc3FAlib3YcLYpWlo/ccaizH7PXw6xRWtNYp4Y/z4xJQOZesogaIzhnGYmyOMrbsLKw9EA5A7KRnwAVppnS0E0DPHT2GQNuF2LeafUC1GQOBiRMGW/76K0w==";

        CertificateFactory certFact = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());
        X509Certificate patchPubCert = (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(Base64.decode(publicRsaCertDlyd)));
        PublicKey patchPubCertPublicKey = patchPubCert.getPublicKey();
        System.out.println(Base64.toBase64String(patchPubCertPublicKey.getEncoded()));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
        PrivateKey patchPriKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(pribateKeyRsaDlyd)));
        System.out.println(Base64.toBase64String(patchPriKey.getEncoded()));
        try {
            // 加解密测试是否时一对
            Cipher patchCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
            patchCipher.init(Cipher.ENCRYPT_MODE, patchPubCertPublicKey);
            byte[] patchEncData = patchCipher.doFinal("test1234".getBytes(StandardCharsets.UTF_8));
            System.out.println("patchEncData>> " + Hex.toHexString(patchEncData));

            cipher.init(Cipher.DECRYPT_MODE, patchPriKey);
            byte[] patchDeData = cipher.doFinal(encData);
            System.out.println("patchDeData>> " + new String(patchDeData));
        } catch (Exception e) {
            System.out.println("NO PATCH!!!");
        }




    }
}
