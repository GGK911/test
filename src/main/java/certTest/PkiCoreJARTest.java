package certTest;

import certTest.createCert.PemUtil;
import cn.com.mcsca.pki.core.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import cn.com.mcsca.pki.core.bouncycastle.cert.jcajce.JcaX509ContentVerifierProviderBuilder;
import cn.com.mcsca.pki.core.bouncycastle.pkcs.PKCS10CertificationRequest;
import cn.com.mcsca.pki.core.util.CertRequestUtil;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.com.mcsca.pki.core.x509.X509Certificate;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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

}
