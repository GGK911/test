package certTest.pkiCoreTest;

import certTest.createCert.PemUtil;
import cn.com.mcsca.pki.core.bouncycastle.asn1.x500.X500Name;
import cn.com.mcsca.pki.core.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import cn.com.mcsca.pki.core.bouncycastle.cert.jcajce.JcaX509ContentVerifierProviderBuilder;
import cn.com.mcsca.pki.core.bouncycastle.pkcs.PKCS10CertificationRequest;
import cn.com.mcsca.pki.core.util.CertRequestUtil;
import cn.com.mcsca.pki.core.util.KeyUtil;
import cn.com.mcsca.pki.core.util.P12Util;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.com.mcsca.pki.core.x509.X509Certificate;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/18 14:11
 */
public class PkiCoreJARTest {
    private static final String ROOT = "src/main/java/certTest/createCert";
    private static final Provider BC = new BouncyCastleProvider();

    @Test
    @SneakyThrows
    public void P7Message() {

    }

    @Test
    @SneakyThrows
    public void envelopeData() {
        String M = "唐好凯";

        String test01 = "MIIDdTCCAl2gAwIBAgIBAjANBgkqhkiG9w0BAQsFADBAMQswCQYDVQQGEwJDTjEPMA0GA1UEChMGR0dLOTExMQ8wDQYDVQQLEwZHR0s5MTExDzANBgNVBAMTBkdHSzkxMTAeFw0yNDA0MTIwNDAwMjlaFw0yOTA0MTEwNDAwMjlaMGQxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVDaGluYTENMAsGA1UECwwERExZRDESMBAGA1UEAwwJVGltZVN0YW1wMSIwIAYJKoZIhvcNAQkBFhMxMzk4MzA1MzQ1NUAxNjMuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqzsrjWJYu3evWWrwHYpIGqD6U4Tgf+sllJ+Iq5SLl3DYm7z6LUNzFD5capnXzYmx20C/jg5TRY+VDNrGCupne4vYAqn58ecCgCYWfkvXbQEjLrShvoXyLHfTHvmnanIchbiJscbg9DpDmWM3SqT5zy1CZJLfQxSh2f6lIn02XnTJF5jKrZ9TDooYLCUDWDVYbd/X2SNpyybVsAPTw24Eb+hrrO9t5SXAbN85ycZRdyho/RPX9OwqLhkba5hlUxRmpW9tvxqrA3+LsnOcduwMtHyIU//cFpl9GydbwbPDp4KVSYI3CV70ZCH59CGSRZKWFz40ucY10LHNxssUdbnN+wIDAQABo1YwVDAdBgNVHQ4EFgQU4gh3nyNkTlGuCYd3r73LILYB7KEwHwYDVR0jBBgwFoAU+EopEhChxDzBCm2yL4V3UHB9dPQwEgYDVR0TAQH/BAgwBgEB/wIBADANBgkqhkiG9w0BAQsFAAOCAQEAKQ1TeBgsmWPW0VkQL3J1499SJdAN4kaav88qHLeK6FPZ89kWZ591pfJh9J67uU/OzF9U2KBeeNbNXluUnX8GY+QZz8PUzFKoh3gpW9FCpDl7lAbuJUtCaIOg3iTlZ+uhO7pOQhWdVugUCBtC5OzP5LX6spnfhmLheu6bXyq5bvyuLty51qUClJZ6MWEpKxbn0te8KFs+cMKwaliyydoYo7OGd8BJjLIPlIfhy4sipY1mFEb8SNSWAxtBCzIvpwxCnUeeSrk2+frtacLm8vXi/wP/Y74fs6ILhNfr8Xp1aQix2c7I8st0uC1t2ZEfnNs+ZXcsAUUuBejnE+WnVFNuUg==";
        CertificateFactory factory = CertificateFactory.getInstance("X.509", BC);
        Certificate cert = factory.generateCertificate(new ByteArrayInputStream(Base64.decode(test01)));
        X509Certificate x509Certificate = new X509Certificate(cert.getEncoded());

        KeyFactory kf = KeyFactory.getInstance("RSA", BC);
        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT + "/TSPriKey.key").toAbsolutePath().toString());
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(pemKeyPair.getPublicKeyInfo().getEncoded()));

        byte[] signAttach = SignatureUtil.P7MessageSignAttach("sha1WithRSA", M.getBytes(StandardCharsets.UTF_8), privateKey, x509Certificate);
        System.out.println(new String(signAttach));
    }

    @Test
    @SneakyThrows
    public void signVerify() {
        String P10 = "MIIBGTCBxQIBATBlMRIwEAYDVQQDDAnlp5rnq5HngpwxDzANBgNVBAsMBui3r+i+vjEPMA0GA1UECgwG5rWL6K+VMQ8wDQYDVQQHDAbph43luoYxDzANBgNVBAgMBumHjeW6hjELMAkGA1UEBgwCQ04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQ41IlFMddxJ17/dXi6P31w/kRCWNwdcOkEBVPTdZRupkQw9XX2grvvDjFMTRfFU84RsY0f0w/o7EwkkuKp/NVEMAwGCCqBHM9VAYN1BQADQQBwfGXFsT551OsOxOW7zkbOakm+ORPboejJk/y2RItkFQFEdtXGyBfSAVnfgQr+woCYWKhgXzfPMG/UZeprMdV1";

        PKCS10CertificationRequest request = new PKCS10CertificationRequest(Base64.decode(P10));
        SubjectPublicKeyInfo publicKeyInfo = request.getSubjectPublicKeyInfo();
        boolean signatureValid = request.isSignatureValid((new JcaX509ContentVerifierProviderBuilder()).setProvider(new cn.com.mcsca.pki.core.bouncycastle.jce.provider.BouncyCastleProvider()).build(publicKeyInfo));
        System.out.println(signatureValid);
    }

    @Test
    @SneakyThrows
    public void verifyP10() {
        String P10 = "MIIBGTCBxQIBATBlMRIwEAYDVQQDDAnlp5rnq5HngpwxDzANBgNVBAsMBui3r+i+vjEPMA0GA1UECgwG5rWL6K+VMQ8wDQYDVQQHDAbph43luoYxDzANBgNVBAgMBumHjeW6hjELMAkGA1UEBgwCQ04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQ41IlFMddxJ17/dXi6P31w/kRCWNwdcOkEBVPTdZRupkQw9XX2grvvDjFMTRfFU84RsY0f0w/o7EwkkuKp/NVEMAwGCCqBHM9VAYN1BQADQQBwfGXFsT551OsOxOW7zkbOakm+ORPboejJk/y2RItkFQFEdtXGyBfSAVnfgQr+woCYWKhgXzfPMG/UZeprMdV1";
        System.out.println(CertRequestUtil.verifyP10(P10));
    }

    @Test
    @SneakyThrows
    public void doSign() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("test", "1234");
        System.out.println(jsonObject.toJSONString());
        String sign = SignatureUtil.doSign("MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQghclRtEAEW7H1y4G+1DGqFATtjb8RkU6tM+wW57G9rRugCgYIKoEcz1UBgi2hRANCAATJjsgrOBf9y4XAQuLldhbbBWuq+9i9e6Hk7DZTfOH0s1WmIOJXtCdU02QlWfmNO0VL2iZaART1g24f4+xDJTdE", jsonObject);
        System.out.println(sign);
        sign = "MEYCIQCpAxdyBhu4AAepRnm8Vtgcmg7kp/2q0y0uehVeZEtuJwIhAOMdhnNtCLQSkjXZH9hYtahqb7N5MrVUmxPQCaqNWWjR";
        System.out.println(SignatureUtil.verifySignByPublicKey("C98EC82B3817FDCB85C042E2E57616DB056BAAFBD8BD7BA1E4EC36537CE1F4B355A620E257B42754D3642559F98D3B454BDA265A0114F5836E1FE3EC43253744", jsonObject, sign));
    }

    @Test
    @SneakyThrows
    public void keyTest() {
        KeyPair keyPair = KeyUtil.generateKeyPair("SM2", 256);
        byte[] bytes = CertRequestUtil.generateP10("SM3withSM2", "CN=1234", keyPair);
        System.out.println(new String(bytes));

        KeyPair keyPair2 = KeyUtil.generateKeyPair("RSA", 2048);
        byte[] bytes2 = CertRequestUtil.generateP10("SHA1withRSA", "CN=1234", keyPair2);
        System.out.println(new String(bytes2));

    }

    @Test
    @SneakyThrows
    public void genP10Test() {
        String signCertPriBase64 = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCoiqx5m55h/9wFi7mhpko/AEcZs1ilcXqSfepWse4uoBw6PYYdGtk+B6YxXRbNuxYvuvNN+PJv+FeQb7Z2BwvY6w1uaupoX4xKj1z/fD5ZFuZA77DyxixLovPTBT0GWPzsl+LZ9GyLOAQMRvOAemEu+4ABTE/5L1AlPjfI2Grz1KrknZUdQ563wXDllIvcFcseDGk6iz1oeOyI3Ka6ge3MRL8Ex1wMYkVnxLZHvExbO9Dh5bNxoCu8DhOENNWaYfWJ2CDqcp2Qgx16sMqd9qJzWIDd2/unM6mKLnyQJiQA5vySs3EL+ebTvAn+T/pPsaOv3EzWdAZbrUTv/hPXxeijAgMBAAECggEASIT6t4ytNo+n9T7szdBGbBtLfJR91Roh2nyRY2JGEQvVeX3Vghp2VUlnSiA62qTgZAM2A1vVvTYHP1/CC8D2aQyih9s7J1PNgptzMX8dOKowwsofwiZhEt53uVmsq1mI4qhr9MpGFhUxMLMqvSJrRJL0vho+4wJnos7FpAby5hztr6s223J6ZL5vSpqPpuNcAcww3dpon7ehXscyyrC/v6DPr1QnkC5x/dzWnIGcTCxHw+mnLGEhm3k7EM6xsjwnGWDBYs5FeyLkIvcR1llrALjsI58vNotQJJuroGnFeNdLiN1/xQiaMLeYxmSbRuI/4MCMMJUYOewDNTU1AwyuyQKBgQDbkeLoxudz48JT6mB2xYA7RYTbQxARn9bj72WCW7+nljIdvcECOpGe7KOpFm75zvxXU7N1BwXfV6LP6QrFtvfGHxki4+1OoIzFPQLxnjYRfztmbvkIgMeF9VvMt/UmrO3kTIP5vpPRJYahY77+VLqf66FhwXV0u3GD6szOStWUNQKBgQDEgWSK0GpLbSigzGD0Sw6RZ+bhUITacNDeMNwR4OtN6lFsCB9ag/yD6GpoxY7u8TrosyQr7sDYW0dRbEk0jBwbxpV2BuMRGeH2Gv0oZPSHBUW9OI8O80598qPlH97LnAenLxJYleLKsMecWbK7QaTgR3/AMdxmVMf8YatyKL50dwKBgFNGiWDvz2jMwS9CfUOOtIvGWhUu4jFNBht8+GrwkUfmVyughEtsGz7DUW8X6w8jyeD1BeMkvr1uZ3mjUUqbkm257bal66MekUVdVnh9INSSBN1cyWbIMORFooOKYZhBjhhATO2zsixopx8ezZl4WS++Fn8U+I9FaFA4BWVYTJjhAoGBAKR92/gHdrMk8TmJXC+jFNLLMw2xJUTl4zHbnJyqts5GM1pHgld23M5eo5SIq5mA/VsFemX8OhnibtTN3InML0tg9IQUR8ds7yXgecJyn+7WjwZSbg6JxU7Q6jlGcG3ocs+UK7tT3MDnMnDC9UajMK09rMbp9iEDH1U5PRcW3DcJAoGAaPAcNS3kZgWAo6dsBsVZVXwH+T23N8uCV9ncJpHchQNaBELcj6c2+ZooZzMuQqbnu2TK3vR7rspe8J0XkfedvW1dQzqsDAdmTyOyFLlTViKmch/hHjBZrPwAkMT5X/xgw7C1SwDePC7MG2CQ7iiuGeVgqq965imC135G+HNaScA=";
        String signCertBase64 = "MIIEqTCCA5GgAwIBAgIQV6yU2Y6ZdtLxIqm4M+t2vzANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yNDA2MjkwNjUyNDJaFw0yNTA2MjkwNjUyNDJaMHExCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UECwwHbG9jYWxSQTEbMBkGA1UEBQwSMzcxNzI0MjAwMjA2MDUyMjEwMSMwIQYDVQQDDBpUcGVyUlNBeDFA5ZSQ5aW95YevQDAxQDE1NjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKiKrHmbnmH/3AWLuaGmSj8ARxmzWKVxepJ96lax7i6gHDo9hh0a2T4HpjFdFs27Fi+680348m/4V5BvtnYHC9jrDW5q6mhfjEqPXP98PlkW5kDvsPLGLEui89MFPQZY/OyX4tn0bIs4BAxG84B6YS77gAFMT/kvUCU+N8jYavPUquSdlR1DnrfBcOWUi9wVyx4MaTqLPWh47IjcprqB7cxEvwTHXAxiRWfEtke8TFs70OHls3GgK7wOE4Q01Zph9YnYIOpynZCDHXqwyp32onNYgN3b+6czqYoufJAmJADm/JKzcQv55tO8Cf5P+k+xo6/cTNZ0BlutRO/+E9fF6KMCAwEAAaOCAW8wggFrMB8GA1UdIwQYMBaAFLlR6QHwqSMfik7pDmsH0AxKLaagMB0GA1UdDgQWBBQpnB5VhQmdHa9G39biFxLXqVt1vzBEBgNVHSAEPTA7MDkGByqBHIeECwQwLjAsBggrBgEFBQcCARYgaHR0cHM6Ly93d3cubWNzY2EuY29tLmNuL2Nwcy5odG0wCwYDVR0PBAQDAgbAMHgGA1UdHwRxMG8wSKBGoESkQjBAMQswCQYDVQQGEwJDTjEMMAoGA1UECgwDSklUMRAwDgYDVQQLDAdBREQxQ1JMMREwDwYDVQQDDAhjcmwxMTE3NjAjoCGgH4YdaHR0cDovLzEyNy4wLjAuMS9jcmwxMTE3Ni5jcmwwXAYIKwYBBQUHAQEEUDBOMCgGCCsGAQUFBzAChhxodHRwOi8vMTI3LjAuMC4xL2NhaXNzdWUuaHRtMCIGCCsGAQUFBzABhhZodHRwOi8vMTI3LjAuMC4xOjIwNDQzMA0GCSqGSIb3DQEBCwUAA4IBAQB8ZGTa4JtWkKomoZD3h4FaIiH3yGcTetLWQ8gSQsZGYl1Tu8G6d8Y9I02TQtxsK0eYF75G03oDi+FVWNJrv/uX8o6rSVWrmYENMB0KX4yVLIRORQnied3Ia4INVimj0LG5zZspXCzvE8TF61KHsNzhZFLIoQce6A1ByCMp5y7YqoHQd3l4tEDco18DPZoIYbrbWNffjeeGsJeZHkaP0eGsE26UdE9ZMXeZnQRmFKAGFIKIk8+dEyevx+JgzjztVXpCZ/86m5ICCc2wuP6lCJrMZCBTnWZNI36y+FoKHO2cdYX/mryU3zF+zxH6p3kIaRaeYiNCweGkX5sF9iQZYcfb";
        String certPfxPassword = "L*c7tq2s";
        byte[] bytes = P12Util.generatePfx(signCertPriBase64, signCertBase64, certPfxPassword);
        System.out.println(Base64.toBase64String(bytes));
    }

    @Test
    @SneakyThrows
    public void parseCertItem() {
        String signCertBase64 = "MIIEqTCCA5GgAwIBAgIQV6yU2Y6ZdtLxIqm4M+t2vzANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yNDA2MjkwNjUyNDJaFw0yNTA2MjkwNjUyNDJaMHExCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UECwwHbG9jYWxSQTEbMBkGA1UEBQwSMzcxNzI0MjAwMjA2MDUyMjEwMSMwIQYDVQQDDBpUcGVyUlNBeDFA5ZSQ5aW95YevQDAxQDE1NjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKiKrHmbnmH/3AWLuaGmSj8ARxmzWKVxepJ96lax7i6gHDo9hh0a2T4HpjFdFs27Fi+680348m/4V5BvtnYHC9jrDW5q6mhfjEqPXP98PlkW5kDvsPLGLEui89MFPQZY/OyX4tn0bIs4BAxG84B6YS77gAFMT/kvUCU+N8jYavPUquSdlR1DnrfBcOWUi9wVyx4MaTqLPWh47IjcprqB7cxEvwTHXAxiRWfEtke8TFs70OHls3GgK7wOE4Q01Zph9YnYIOpynZCDHXqwyp32onNYgN3b+6czqYoufJAmJADm/JKzcQv55tO8Cf5P+k+xo6/cTNZ0BlutRO/+E9fF6KMCAwEAAaOCAW8wggFrMB8GA1UdIwQYMBaAFLlR6QHwqSMfik7pDmsH0AxKLaagMB0GA1UdDgQWBBQpnB5VhQmdHa9G39biFxLXqVt1vzBEBgNVHSAEPTA7MDkGByqBHIeECwQwLjAsBggrBgEFBQcCARYgaHR0cHM6Ly93d3cubWNzY2EuY29tLmNuL2Nwcy5odG0wCwYDVR0PBAQDAgbAMHgGA1UdHwRxMG8wSKBGoESkQjBAMQswCQYDVQQGEwJDTjEMMAoGA1UECgwDSklUMRAwDgYDVQQLDAdBREQxQ1JMMREwDwYDVQQDDAhjcmwxMTE3NjAjoCGgH4YdaHR0cDovLzEyNy4wLjAuMS9jcmwxMTE3Ni5jcmwwXAYIKwYBBQUHAQEEUDBOMCgGCCsGAQUFBzAChhxodHRwOi8vMTI3LjAuMC4xL2NhaXNzdWUuaHRtMCIGCCsGAQUFBzABhhZodHRwOi8vMTI3LjAuMC4xOjIwNDQzMA0GCSqGSIb3DQEBCwUAA4IBAQB8ZGTa4JtWkKomoZD3h4FaIiH3yGcTetLWQ8gSQsZGYl1Tu8G6d8Y9I02TQtxsK0eYF75G03oDi+FVWNJrv/uX8o6rSVWrmYENMB0KX4yVLIRORQnied3Ia4INVimj0LG5zZspXCzvE8TF61KHsNzhZFLIoQce6A1ByCMp5y7YqoHQd3l4tEDco18DPZoIYbrbWNffjeeGsJeZHkaP0eGsE26UdE9ZMXeZnQRmFKAGFIKIk8+dEyevx+JgzjztVXpCZ/86m5ICCc2wuP6lCJrMZCBTnWZNI36y+FoKHO2cdYX/mryU3zF+zxH6p3kIaRaeYiNCweGkX5sF9iQZYcfb";
        X509Certificate x509Certificate = new X509Certificate(Base64.decode(signCertBase64));
        Date notAfter = x509Certificate.getNotAfter();
        Date notBefore = x509Certificate.getNotBefore();
        X500Name subjectX500Name = x509Certificate.getSubjectX500Name();
        X500Name issuerX500Name = x509Certificate.getIssuerX500Name();
        String SN = Hex.toHexString(x509Certificate.getSerialNumber().toByteArray());
        System.out.println("失效时间>> " + notAfter);
        System.out.println("过期时间>> " + notBefore);
        System.out.println("DN>> " + subjectX500Name.toString());
        System.out.println("颁发者>> " + issuerX500Name.toString());
        System.out.println("序列号>> " + SN);
    }

}
