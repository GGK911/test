package certTest;

import certTest.saxon.CertUtils;
import certTest.saxon.rsa.RSAUtils;
import certTest.saxon.utils.ResultUtils;
import cn.hutool.core.io.FileUtil;
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
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
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
        certHldr = new X509CertificateHolder(Base64.decode("MIIECjCCAvKgAwIBAgIQV9SsoHR0+EBy2uTlFuORvDANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yMDExMjUwMTE5NTNaFw0yNTExMjQwMTE5NTNaMBkxFzAVBgNVBAMMDioubWNzY2EuY29tLmNuMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7MqC9e2BB1wQhMe6+ImWhEPw79SiVJ6nssOvEYb2GLAtsoIgNy10GWiRxTXgfHFOXXvuzd3T3SWYH9KaBx33NB+0Uwww2fHPxj3wP7/s2pACi9RhjyvgYW3tIah1X1SFXZe8n88Lwlxm59hQTjBvMDEMUlxVff4iHqrR5HxGfX4LseFQpIe3XytZyopB+9fclld5OPn+qrXh4/xHnrk5cOfTOwPD/lLe7nMk0OKZjHNk5vYPQo9EYrHiuMc0BWQ5RphkS5gRRbDK/XCGbej2OZM537Ytg6F8S9Bvve/ozqZClhTzG/gvLLcnx05CXMSY8K5wxoosEewkmqyFAbDHHQIDAQABo4IBKDCCASQwHwYDVR0jBBgwFoAUO9/3EEzFZuEmqSzgUrLGxA6R+R0wHQYDVR0OBBYEFFtA5OJFIoxnO7PzuT0201p7a6Z7MC0GA1UdEQQmMCSCDioubWNzY2EuY29tLmNuggxtY3NjYS5jb20uY26HBH8AAAEwDAYDVR0TBAUwAwEBADAOBgNVHQ8BAf8EBAMCBeAwdgYDVR0fBG8wbTBKoEigRqREMEIxCzAJBgNVBAYTAkNOMRIwEAYDVQQKDAlNQ1NDQSBSU0ExEDAOBgNVBAsMB0FERDFDUkwxDTALBgNVBAMMBGNybDEwH6AdoBuGGWh0dHA6Ly8xMjcuMC4wLjEvY3JsMS5jcmwwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA0GCSqGSIb3DQEBCwUAA4IBAQBuz3nBf4hlCMpZA/Ci3thosx9fQftCNfDIuaqj+dvaJSaXwg0/i5Ltf2XLLUOR4twNI7cjBSnBRD4gymBTsaSv2Did1lwBJP0tj3tXMFR8ElfExOimPN4rWPeXH9zm8rL0tRDFS8t+b6jvBKf1tFqKveT/MCOk6k2cjEqNCmcSS1dO4HdkGJ56ZSe9xUsSq86a1RlthTATWXq16bn9PtXKQmNFCqntFt3h1mG14TaMlsVJL0VH5IxbrEn5Hb7YSj12SwG2uAHZ2zmNYIiB2MKTrIEXVWPKxOxq+37d5iu0uAuHfa9lLEcoAScWBgEOaHccGRTeXBnem2xWLRBvUQEj"));
        SubjectPublicKeyInfo k = certHldr.getSubjectPublicKeyInfo();
        System.out.println("k>> " + Base64.toBase64String(k.getEncoded()));

        CertificateFactory fact = CertificateFactory.getInstance("X.509", BC);
        X509Certificate cert3 = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(cert2.get("certificateData")));
        cert3 = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(Base64.decode("MIIECjCCAvKgAwIBAgIQV9SsoHR0+EBy2uTlFuORvDANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yMDExMjUwMTE5NTNaFw0yNTExMjQwMTE5NTNaMBkxFzAVBgNVBAMMDioubWNzY2EuY29tLmNuMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7MqC9e2BB1wQhMe6+ImWhEPw79SiVJ6nssOvEYb2GLAtsoIgNy10GWiRxTXgfHFOXXvuzd3T3SWYH9KaBx33NB+0Uwww2fHPxj3wP7/s2pACi9RhjyvgYW3tIah1X1SFXZe8n88Lwlxm59hQTjBvMDEMUlxVff4iHqrR5HxGfX4LseFQpIe3XytZyopB+9fclld5OPn+qrXh4/xHnrk5cOfTOwPD/lLe7nMk0OKZjHNk5vYPQo9EYrHiuMc0BWQ5RphkS5gRRbDK/XCGbej2OZM537Ytg6F8S9Bvve/ozqZClhTzG/gvLLcnx05CXMSY8K5wxoosEewkmqyFAbDHHQIDAQABo4IBKDCCASQwHwYDVR0jBBgwFoAUO9/3EEzFZuEmqSzgUrLGxA6R+R0wHQYDVR0OBBYEFFtA5OJFIoxnO7PzuT0201p7a6Z7MC0GA1UdEQQmMCSCDioubWNzY2EuY29tLmNuggxtY3NjYS5jb20uY26HBH8AAAEwDAYDVR0TBAUwAwEBADAOBgNVHQ8BAf8EBAMCBeAwdgYDVR0fBG8wbTBKoEigRqREMEIxCzAJBgNVBAYTAkNOMRIwEAYDVQQKDAlNQ1NDQSBSU0ExEDAOBgNVBAsMB0FERDFDUkwxDTALBgNVBAMMBGNybDEwH6AdoBuGGWh0dHA6Ly8xMjcuMC4wLjEvY3JsMS5jcmwwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA0GCSqGSIb3DQEBCwUAA4IBAQBuz3nBf4hlCMpZA/Ci3thosx9fQftCNfDIuaqj+dvaJSaXwg0/i5Ltf2XLLUOR4twNI7cjBSnBRD4gymBTsaSv2Did1lwBJP0tj3tXMFR8ElfExOimPN4rWPeXH9zm8rL0tRDFS8t+b6jvBKf1tFqKveT/MCOk6k2cjEqNCmcSS1dO4HdkGJ56ZSe9xUsSq86a1RlthTATWXq16bn9PtXKQmNFCqntFt3h1mG14TaMlsVJL0VH5IxbrEn5Hb7YSj12SwG2uAHZ2zmNYIiB2MKTrIEXVWPKxOxq+37d5iu0uAuHfa9lLEcoAScWBgEOaHccGRTeXBnem2xWLRBvUQEj")));
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
        publicRsaCertDlyd = "MIIECjCCAvKgAwIBAgIQV9SsoHR0+EBy2uTlFuORvDANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yMDExMjUwMTE5NTNaFw0yNTExMjQwMTE5NTNaMBkxFzAVBgNVBAMMDioubWNzY2EuY29tLmNuMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7MqC9e2BB1wQhMe6+ImWhEPw79SiVJ6nssOvEYb2GLAtsoIgNy10GWiRxTXgfHFOXXvuzd3T3SWYH9KaBx33NB+0Uwww2fHPxj3wP7/s2pACi9RhjyvgYW3tIah1X1SFXZe8n88Lwlxm59hQTjBvMDEMUlxVff4iHqrR5HxGfX4LseFQpIe3XytZyopB+9fclld5OPn+qrXh4/xHnrk5cOfTOwPD/lLe7nMk0OKZjHNk5vYPQo9EYrHiuMc0BWQ5RphkS5gRRbDK/XCGbej2OZM537Ytg6F8S9Bvve/ozqZClhTzG/gvLLcnx05CXMSY8K5wxoosEewkmqyFAbDHHQIDAQABo4IBKDCCASQwHwYDVR0jBBgwFoAUO9/3EEzFZuEmqSzgUrLGxA6R+R0wHQYDVR0OBBYEFFtA5OJFIoxnO7PzuT0201p7a6Z7MC0GA1UdEQQmMCSCDioubWNzY2EuY29tLmNuggxtY3NjYS5jb20uY26HBH8AAAEwDAYDVR0TBAUwAwEBADAOBgNVHQ8BAf8EBAMCBeAwdgYDVR0fBG8wbTBKoEigRqREMEIxCzAJBgNVBAYTAkNOMRIwEAYDVQQKDAlNQ1NDQSBSU0ExEDAOBgNVBAsMB0FERDFDUkwxDTALBgNVBAMMBGNybDEwH6AdoBuGGWh0dHA6Ly8xMjcuMC4wLjEvY3JsMS5jcmwwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA0GCSqGSIb3DQEBCwUAA4IBAQBuz3nBf4hlCMpZA/Ci3thosx9fQftCNfDIuaqj+dvaJSaXwg0/i5Ltf2XLLUOR4twNI7cjBSnBRD4gymBTsaSv2Did1lwBJP0tj3tXMFR8ElfExOimPN4rWPeXH9zm8rL0tRDFS8t+b6jvBKf1tFqKveT/MCOk6k2cjEqNCmcSS1dO4HdkGJ56ZSe9xUsSq86a1RlthTATWXq16bn9PtXKQmNFCqntFt3h1mG14TaMlsVJL0VH5IxbrEn5Hb7YSj12SwG2uAHZ2zmNYIiB2MKTrIEXVWPKxOxq+37d5iu0uAuHfa9lLEcoAScWBgEOaHccGRTeXBnem2xWLRBvUQEj";
        pribateKeyRsaDlyd = Base64.toBase64String(FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\RSA-PRI.key"));

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
            byte[] patchDeData = cipher.doFinal(patchEncData);
            System.out.println("patchDeData>> " + new String(patchDeData));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("NO PATCH!!!");
        }

        System.out.println("//*************************************************从私钥中派生公钥**********************************************************//");

        // 提取私钥中的模数和公开指数
        RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) patchPriKey;
        BigInteger modulus = rsaPrivateKey.getModulus();
        BigInteger publicExponent = rsaPrivateKey.getPublicExponent();

        // 根据模数和公开指数生成公钥
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
        PublicKey createPubFromPri = keyFactory.generatePublic(keySpec);
        String pubFromPriBase64 = Base64.toBase64String(createPubFromPri.getEncoded());
        System.out.println(String.format("%-16s", "pubFromPriBase64>> ") + pubFromPriBase64);


    }

    @Test
    @SneakyThrows
    public void certVerifySign() {
        String certBase64 = "MIIEiTCCA3GgAwIBAgIIdMgAKwBRbXkwDQYJKoZIhvcNAQELBQAwUjELMAkGA1UEBhMCQ04xLzAtBgNVBAoMJlpoZWppYW5nIERpZ2l0YWwgQ2VydGlmaWNhdGUgQXV0aG9yaXR5MRIwEAYDVQQDDAlaSkNBIE9DQTMwHhcNMTkwNTI3MTAwMDA0WhcNMjAwNTI3MTAwMDA0WjBWMQswCQYDVQQGEwJDTjEYMBYGA1UECwwP5bCa5bCa562+5o6l5Y+jMS0wKwYDVQQDDCTph43luoblr4zmsJHpk7booYzogqHku73mnInpmZDlhazlj7gwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAJVtyR9gYfD8yLvYJNTOAMBIu2DDd/XX8HUJmbhuMYRrEOY76sCHyXVmWgt0l9qCThzctdKXcyX09YYToH8tic6cBBMSrZ0sRIu6bdYZ/I9YBtjV8sdHdXYjIo+ZagP+HDPn/Xuv5+CE8noaNgq26V6ZJ7B82JN3OHOxH9+S0z/zAgMBAAGjggHhMIIB3TAMBgNVHRMEBTADAQEAMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDBDALBgNVHQ8EBAMCAMgwEQYJYIZIAYb4QgEBBAQDAgCAMB8GA1UdIwQYMBaAFPTpdGdhq8lTVY/yIMmg9FQSmVeIMIGoBgNVHR8EgaAwgZ0wgZqggZeggZSGgZFsZGFwOi8vbGRhcC56amNhLmNvbS5jbi9DTj1aSkNBIE9DQTMsQ049WkpDQSBPQ0EzLCBPVT1DUkxEaXN0cmlidXRlUG9pbnRzLCBvPXpqY2E/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MIGiBggrBgEFBQcBAQSBlTCBkjCBjwYIKwYBBQUHMAKGgYJsZGFwOi8vbGRhcC56amNhLmNvbS5jbi9DTj1aSkNBIE9DQTMsQ049WkpDQSBPQ0EzLCBPVT1jQUNlcnRpZmljYXRlcywgbz16amNhP2NBQ2VydGlmaWNhdGU/YmFzZT9vYmplY3RDbGFzcz1jZXJ0aWZpY2F0aW9uQXV0aG9yaXR5MB0GA1UdDgQWBBT71snMdBjERRAAOq6u8UsAaqRVKzANBgkqhkiG9w0BAQsFAAOCAQEAXasbpYMrDSx19ZKoxkHVWw7Cc+/8gPr5me4AMqabdYuM+kJHLP72vTMI8bO5vWIZ2Y6UJ33CX7bmDNGYun5nFtl8qXZmiCiTliuI3JlJiuY4fZheM9/YLc9n3acGUWVwX23DcWkmX+RZWLWew8seNA+PHPW2+61dlqWcRJEHz9ovrihXsQQ6dYkuwzT89G5VCRvo6AdheZl7v75olDcKkAJg/iYxVEz+FJ5b43WPYg8PmY1urY54akBFzmJhyQn9KPtPpDqEKUVvsVfEbIN8EJMkUqWKdFPpDuMedmqE4cXWttiglqecH2BBLg3mTGYVYyKbtVcxtGzkak5KO1hW2A==";
        String certIssuerBase64 = "MIIFlTCCA32gAwIBAgIQVOnZ9UY/NQPg/ZkseCDMxjANBgkqhkiG9w0BAQsFADBaMQswCQYDVQQGEwJDTjEwMC4GA1UECgwnQ2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRkwFwYDVQQDDBBDRkNBIElkZW50aXR5IENBMB4XDTE1MDYzMDA2MTAzNVoXDTI5MTIyODA2MTAzNVowWzELMAkGA1UEBhMCQ04xMDAuBgNVBAoMJ0NoaW5hIEZpbmFuY2lhbCBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTEaMBgGA1UEAwwRQ0ZDQSBJZGVudGl0eSBPQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDZTopbvqP8Z/VwguJny8ZxXdu56w/fOyAjRgNTziIpYj69aLmjN5930BbPJpYj+3HF/L0XExX0+iRv3cIgzpc7E3AP4kUswCuoL1r38POSVkZU53GtdXBXrqR0DH6yANScxZ9IM8AB9sq/UD+2gA9KOi7+0U0KQlkFBsl279kKEmQ4nEGlRMF7+F93Lk0v/mfYWQwgNAtovuFsvJNWwpSXxkXfj+HSogVDVje5iwO0vgAfl6lnhSjIR4t0ucJhUgNbO2XoVJHgGwq/zwG3NbVHUyk9qdOXyn8Ys/9l3vd4ODpTuGH0KSI0z5obgMQqekdItrA/2fmowIfBCY7ubMvFAgMBAAGjggFUMIIBUDAzBggrBgEFBQcBAQQnMCUwIwYIKwYBBQUHMAGGF2h0dHA6Ly9vY3NwLmNmY2EuY29tLmNuMB8GA1UdIwQYMBaAFMCsdqLTXf/2zRYAWzinf1V9hVlsMA8GA1UdEwEB/wQFMAMBAf8wRAYDVR0gBD0wOzA5BgRVHSAAMDEwLwYIKwYBBQUHAgEWI2h0dHA6Ly93d3cuY2ZjYS5jb20uY24vdXMvdXMtMTcuaHRtMD8GA1UdHwQ4MDYwNKAyoDCGLmh0dHA6Ly9jcmwuY2ZjYS5jb20uY24vSWRlbnRpdHlDQS9SU0EvY3JsMS5jcmwwDgYDVR0PAQH/BAQDAgEGMB0GA1UdDgQWBBScRPS/N49GC1mR5bbYHA53vJrycjAxBgNVHSUEKjAoBggrBgEFBQcDAgYIKwYBBQUHAwMGCCsGAQUFBwMEBggrBgEFBQcDCDANBgkqhkiG9w0BAQsFAAOCAgEAYCbAsudnjFHaMHmMsPJMGE2Qvorft/lJVwJawgldCkJErUx0chD8iAKVK7QW+02+sNioLrJAGdkOf4uaWBybQPGEI4uMsWOHKwReBDfiIJW5u/dyMBvnM1Wu3CvR0yEtBCj3j84MtU77ilXZmbPC++N/fCS6TbVpyxByI/auVOIiVcWYlTTAO3htGYIojuhbRUvH78AMkV0suUt1GIqjq651zPflDCQBregm4GJnHplmq+rDS8fvSZJ5Y4g77Ui/eCq8YCO3G31vwWsmvbq2X75/rvBIDWIV0uzhJEKEzb/PJDD1xxuoPsNEgr4J4/14dWz1JJ6Ngx2HHVB++LLyogr8ld3Q6x7rSVXmQNvq0HlepIqLSIS6Gh7Etfc/rKbrE/NrxsYqgffqix6y5gcHg903AQK4na1QXUj8OJOnnmAmYWnf+AOtZYg0R258k9r80uITLj7EMOox5B08/Ev8fxSFEBeE1TKSpiGnuPK7QTiOsL1JJLhaC/g6+Be8dwDBLNY4Obz30lYK75/kFXmSiINGxL3jQoEdBaVFvKjgXyPfajhxdFIdLa+V64PW3BABfqT3izj1LIlXa0pCX2i0P/2G6X7sYaKgBup3lKlRbyAMSu9c2Ge1dal2Cd8VK+vXsGxnVG/2MDiQxE/dEIvDhUIXAp8qJYf11bRFBIUpceY=";
        certBase64 = "MIIFXjCCBEagAwIBAgIQIBUEaVzjfEX2kpkv2keNLjANBgkqhkiG9w0BAQsFADBbMQswCQYDVQQGEwJDTjEwMC4GA1UECgwnQ2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRowGAYDVQQDDBFDRkNBIElkZW50aXR5IE9DQTAeFw0xNzEwMTYwOTA3NDhaFw0yMDEwMTYwOTA3NDhaMIGnMQswCQYDVQQGEwJDTjEPMA0GA1UECAwG5rWZ5rGfMQ8wDQYDVQQHDAbmna3lt54xMDAuBgNVBAoMJ+adreW3nuWwmuWwmuetvue9kee7nOenkeaKgOaciemZkOWFrOWPuDESMBAGA1UECwwJ5Lqn5ZOB6YOoMTAwLgYDVQQDDCfmna3lt57lsJrlsJrnrb7nvZHnu5znp5HmioDmnInpmZDlhazlj7gwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCvM6LpNg4ug9e6C1u+DIoyVvar1xSch1Up9FAKGvTYGucN3dS9mz7CzprohZ+R35w12A6q5AcUEb7hv6WZa9JaUk/02HuPiDBWEKpX/h7C5sL5i3ccKH/AAqopNhxuy6k88QZSyqKSE2XRHJ5hG2qB4LChYB/h/Gan2ZeRBMZSPpHxhnDDE1nbI8pEkUD9gWog+iokLlgphWWgE50rGg/Ouk3OvFD6nRq95Oai/e4kqQyQBwvZGrw+NC3hpjS72RuvLqHKZvT3P9UST6Ns/PdHOr4af955hXrbpiRuLKOy8KtEuJWcFCnQdswkd9uawwr66tpHIz6YNAsIvd/pmro7AgMBAAGjggHPMIIByzAMBgNVHRMBAf8EAjAAMHkGCCsGAQUFBwEBBG0wazAoBggrBgEFBQcwAYYcaHR0cDovL29jc3AuY2ZjYS5jb20uY24vb2NzcDA/BggrBgEFBQcwAoYzaHR0cDovL2d0Yy5jZmNhLmNvbS5jbi9pZGVudGl0eW9jYS9pbmRlbnRpdHlvY2EuY2VyMEIGA1UdEQQ7MDmgNwYKKwYBBAGCNxQCA6ApDCfmna3lt57lsJrlsJrnrb7nvZHnu5znp5HmioDmnInpmZDlhazlj7gwDgYDVR0PAQH/BAQDAgbAMB0GA1UdDgQWBBT4emnyJol06htjAlExs1c7CO2rBjAfBgNVHSUEGDAWBggrBgEFBQcDBAYKKwYBBAGCNwoDDDAfBgNVHSMEGDAWgBScRPS/N49GC1mR5bbYHA53vJrycjBIBgNVHSAEQTA/MD0GCGCBHIbvKgUBMDEwLwYIKwYBBQUHAgEWI2h0dHA6Ly93d3cuY2ZjYS5jb20uY24vdXMvdXMtMTcuaHRtMEEGA1UdHwQ6MDgwNqA0oDKGMGh0dHA6Ly9jcmwuY2ZjYS5jb20uY24vSWRlbnRpdHlPQ0EvUlNBL2NybDEwLmNybDANBgkqhkiG9w0BAQsFAAOCAQEAi7TDenrOyFIzDyad81t8t1lUp3/MXLrYIEA+ZLGNUJJHlAFQWawVDYBplbYjNeKANMCFzH/tdsBzYxU0oDyHIaNKfBuViLSagu92XYe3weWO6Z03tvnNU0zYlhskH501jR9JgKXOMHZ60RqZWdMiy4erIzrJFVAPPnaLIyfGIgbwZmQVLEVOE5K5p8+AwgPMIRblc4gZ4mAk+IuSuipKl/AVafEqd8NCFh+iYPr0XZSiLj5Sm/I6gUo6MjeBFjoqXLkVg33vlJq+CCbIAEYz6iifftpdCPHkEa7EaGN+T2P7SNSkVaxOtyVvvzZCMAdWqCV74GJTyDD0n/Bgd8d37A==";
        CertificateFactory certFact = CertificateFactory.getInstance("X.509", BC);
        X509Certificate c = (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(Base64.decode(certBase64)));
        X509Certificate cIssuer = (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(Base64.decode(certIssuerBase64)));
        Signature signature = Signature.getInstance("1.2.840.113549.1.1.11", BC);
        signature.initVerify(cIssuer.getPublicKey());
        signature.update(c.getTBSCertificate());
        boolean verify = signature.verify(c.getSignature());
        System.out.println(verify);
    }

    @Test
    @SneakyThrows
    public void decryptTest() {
        // 4096
        String rsaPriBase64 = "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";
        String rsaCipherBase64 = "lcx9f1jMeC79dY5pTh6w4T46QIwUoNuejpAumtVDGjQc1CVVJljA3OShe2OxeNLQCtOizgWGZQ6fU4+27w0h3QSxF1AQbrLPDzcQTTVJ9Eh+iy3sU6prsbOiw2asUqnjBtFPQauZkxegjD/mrNMypcH3csrZ2ymA5wmO/3Dcw3yx+JJXDw7YjLWoBXCqymvPxEhTQ6ZCwIkcVqHn6jdG/xH+DYU5++4brvtiWK0qeb+OLzQ+irEPofrawvdYxSDUC4Btz0Tu4f5BCLSsjiRa64lEXlv8HZJDWLdN+XgqWPzvovlGp7VbfU3e73lf6Y+/Tlv+KxwjjLAqj5NsNyL8KIp+5THdLzcPYgilqUo/3vvKELcxwRajvZNkw0bqp2BBYsxfmpD0IL/W7ljxZUY3nx+oOb+xqqvKJmXjHoQgh7AH9wsrP3eS4rX888JCTYQ3Su8aK2emN+EnDyw0ehCA1OtvH/S6KJAG1KHel5Y5WYlGtIDYlBFk+GTL4PG9ncbpaRW16JQClZasb94SDc4yl7Cn8zHOSUi4QGYX0y/IWBCFe1CO470GBQIb1UrfnUrJbhg1DLHnnDqw0r5OBU7x/qrMAEm8d5pBdPXpt9/sXEDPhdVgAuIapPRhICmipQv1C5R+okZqACEQUwLbRYoAAvT6/QJeleK8kZhbjddIKRU=";

        // // 2048
        // rsaPriBase64 = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCt/H3QPiv0dEpT97DASAI0iSNMo/HukWBLoGGLJCTdHr6MULvB0bipzXX3YvUyKb7w5HMhbIJT8HqwXzT6sn0xONulol5SBdiuOiVOpzjCaZi5H/CVEjzpZxKmfepx7CvZFvvaNht+ktM1iFd4lFDaIuXULozwSI03pQJa0quxK7UYycJvlv/fTJvcy3Tam6exLGS1ikDG3IQxm+prHH9TGLTX8Vi7/Qb6oa34sxFLPMfAWyfpGvJWdQ0+dlW3pihlVzpy1goW30QadBIVfmPRlsTwIi/v2CPxBVkJR1MMdAwSs2BbDpnjnmKEHfgvtpzbt8zGH8v4wzfM5hAPTJkRAgMBAAECggEAOhfdAHvJ3Gk56jiBfWptxhHmRfBV1yUWZJoMB9QNNQHPePV6A7jdcmEGmxAhM9sphLCa2pWrHJDmvl5Dx9NyJs3H1VHWMYf/Ngl3LIB8XLm6g1GtWHELApnmac4QSnKjn2xjspQvcP9gY3b/UeUf6/4MBGF4sMBg2qwPkbjXGCQldxEIL/NzMtokgBRg4qcLimE2o0UgWnnbsFA6MeEr37TIarAMPBOWl/vveAeXvsBWoVEy8xxW9SZvev2su7vnUC3T9lvIFTDZxgZOqVpS+V4txxxL0dEN6Lhr3Wcesp6Q/hdBat/Zh0YfNqklq4EdsV20IIj7P5r7Zx1NWxYOOwKBgQDT+Qw0UsZ9RbSd4vNdUKcFaEdTpCmFAT+fUFsAPEs1ICyss8KrkFrjVxsMRRW8EerP8b8Jxnh8mVilh8Cr+rQBy2LCd4bqDOJ1QXqakqpZP8nMVemlxJ4sxCHH5/JLo29rxpOdGqDTV7WU0osrxMjCHdxhPuWX5NrRTAOHNRK1bwKBgQDSH6KcFkdx2XukREAMXbqa+s1RW+z40jdFJc9Oo75DuZvmLJChvT1AkbVOA6tH0x+6i1NvfcVl0ROHsAEeM36bSOLaur2EInlXtryi8m36CuxCCd4wutfKwekrbycuE/VhmfWeFTpKQyuIqsorzu1xVFKyGUZEeQGs+GddFp9ZfwKBgFOxfZQbKqvDnfDI7vXlJk0IQ0FPC+Wr/IbWn1eUds1fCsSVpUNAF+KzPca2cEJ1gJTiCl+9sUzg7xNMalJ9Bh2/RBssPmSi+bb6R4JIbmIWwYKgWUROQXXDauMXPZ+HxDJ1kUryfJV1iAheqPx7Ehky2RS8VMKIZa1Fqg70cZKZAoGASVQe9GfWAQZL3tSuoWIoWCOEeYsiFOePwELkqOX95TamEFqnErqu4JS4+APRx1wHa5tpv7Pdqp2fOjATA84mtdxmxrx6nN1HCXRNOnRuKt+a+aTl9I2P90Nasvy0aKEwdiI7lDKGSBpO2//2U/l/5Zfu0BZ/7i9ohUufDt5xN9MCgYEAmPP8SfSxa1VwCvk5JLmfrFGUj57rKVGdYBDO8aj26qQgSltTErkBWlSdD8Pv9Cp3rXeghC/QlHK/Iiky3IW8iQc46eUydMsE+0c0qF7YABBJyeyEoH/7nvc/mL7671c6ksJ4Ycxu7pNNzik4B3XNUgLZqGDm0v2LOWt32xadC6Q=";
        // rsaCipherBase64 = "NYt4xr2ui5QOiTEMAatad0V8WNMUGvz4Hz8j8VY35wVGUSY8tFS2PmzrQLpzJAO0fWYFh5c37OCg+svheWxfQGEuW5S+4YVS5Vy9ai9R82tVqTQdvQ0nEV+YozGWyBPnHK5ozVvFZrUUM87ir/lCwziXvb2+kU009g3O/rHUr8wROUyPgO6GyOnZ1lIZzK4daooghJ2dSndGOMnY4Vr+Iad20m1wOlAe6FEkjNsBp697T02KKCFfiLTpJgL0eVKC4gjWNIhEMf8aK1eMCYxOt/UxhA/WJS1ki9tojaEsgjBEZPAUHc4KACPQYrLGSyJBibHe1pZ5KnvZwYdlV4EWJg==";
        //
        // // 1024
        // rsaPriBase64 = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOM+tI+MKXzE0XPvX58YGYJOKQA3WNCk9REPCxniHQZyBWHN/BFdFGcOo6Q0+ITZgsJoAtBjON5VAcp7crieF3Bd9rstJ/LOm1aVTRYSnq8s4cusGC5hHZouQ89Kd6j+oR3d9xHTosSnksdYRsVNyOCsiQVQf93IaMRHOEwhpC+RAgMBAAECgYAvY6093knslhLj7YBwahMvxGLyg5hG8o+UviFfhXLuHCsZKV6utE2D7F6d6NoGwmFtb0le8cIzQ2D7O+FBtN5IEQGB+kR9YttIoI70uezWXCYg9y103P/3h8Z0/r+h1KSJ41R0cwJfY4ZQqKPA563SqQyULdx8Yy1RlCyc92Ge7QJBAPZSM2y4f7EyyNYnbK51yFKe1dF5G+7DFtMswRopaqAd0okk+SPu9B6nnhXtJhAVZH2h/PUV7YPBA7SaIHQP4f8CQQDsLJz1rfZbtvjuCgvmr/Lg2Ib6XOmaoQHySfik1viNe+Kuqwb1N6gKMEHfdcicOV6lfEw2UEDbOPruGQHtGs5vAkEAp6P8QcMzjKrOiwmb6wRYSPq29PCi9RWrZB+ycJa9bam+Tv5t3WnUlURoz+1tmUal9OcZXMgGtUPYvMKk0bi5iwJAC25NWobE+cNtYPgTg2LYupFAVzXQCK/qTDPQWQoelZp9aj7U42GCPJwBVDN7NjSApDnT5n0T5ohnfMaxFSzK+QJBAMMYc58SV55WZ3wrjDlqouQMgTU9KDoo9AgD5ND8eA6QxI+OgtJMckTOf3EwNR8zahG1Qm2+86qFAcvVe5vxpHc=";
        // rsaCipherBase64 = "pa/aQEoNk4KzvY32UiBhNf+rxwGhSV+b/4HZug0RQV/d2Vkp/kbHx8UtRJuNBzylauVuRfcNEMvrXy2kUWeaet6BIMNwJ8cDvFzfEq4T6BsVKcAhc8qHORkeGjUL1zJ52RAOMBH6WV+cFVdj93UHhrNBYj8dxJc8c+f3KC7QwHI=";

        KeyFactory kf = KeyFactory.getInstance("RSA", BC);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(rsaPriBase64)));

        // Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS5Padding", BC);
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding", BC);
        // Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", BC);
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decryptData = cipher.doFinal(Base64.decode(rsaCipherBase64));
        String deHex = Hex.toHexString(decryptData);
        System.out.println(String.format("%-16s", "deHex>> ") + deHex);


    }

}
