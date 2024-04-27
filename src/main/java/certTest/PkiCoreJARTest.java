package certTest;

import certTest.createCert.PemUtil;
import cn.com.mcsca.pki.core.util.CertUtil;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.com.mcsca.pki.core.x509.X509Certificate;
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


}
