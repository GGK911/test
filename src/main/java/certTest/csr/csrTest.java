package certTest.csr;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author TangHaoKai
 * @version V1.0 2024/5/10 11:50
 */
public class csrTest {

    @Test
    @SneakyThrows
    public void judgeRSAOrSM2() {
        String rsaCsr = "MIICVTCCAT0CAQAwEDEOMAwGA1UEAxMFTUNTQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDHFYC9Y2DVC/Aaj00tEHEyfeajvKMG+gHoFovjPAsW5+oPtnW36po7XO4+a33kKQK70xWG6MgzdmKlNNmeSCYaGSTyL7u5t8UN+EyZtNb54IaMALZWp0fwTL+PFgh6s13t5VGY/NBwTWoj+PNzG2w2PCWZhauG+Q5aEy/T9s+MsRwosInVnQSzFc6LETfAfu4SE+FybyMToW2ZuNasxxjiCnbYawodCyde+Kfvx2fiCmfPO/Kmc6hZkuURxH42DbAivlcPj9prspsnZlAN9CMRO7T8aKfokiIMibCbU+1GXaLLjDXjXfiM7ND+9j5Aam16qcvdNwr3rIZi5wKQXC+9AgMBAAGgADANBgkqhkiG9w0BAQsFAAOCAQEAmOoKQdOCxdnLg3N44Qrm3pR2hnZNxgdZkZrToMCK39ALVfrhrZr+jCrnv3tFFBQL6fk1uJTaUNRclLAiivhPk7NiarM9g1vuc/Vo8EVwDPL5lqsfHmKsJCzbKPYujgvZsvARzyO2kM6zr+jUtdWdIHy6lTmRHtlxuOleuvrtzK+i66NhfEb1of7h09tTAIs0t4cyE2FqTMu4i0c3DCAlZTVpUZqvqQi+/ijvU8LEjuCclPbaxURAjKREIbPeKLqTHcizCOtFAL/5xlwexbYU94VzfkBe2Roe2hayWWJ1yER7d21GOuSHs28ixIwWxA+/EIok0DafQNcQ/qmYAo3w2g==";
        PKCS10CertificationRequest rsaCertRequest = new PKCS10CertificationRequest(Base64.decode(rsaCsr));
        SubjectPublicKeyInfo rsaCertRequestSubjectPublicKeyInfo = rsaCertRequest.getSubjectPublicKeyInfo();
        AlgorithmIdentifier rsaAlgorithm = rsaCertRequestSubjectPublicKeyInfo.getAlgorithm();
        // 1.2.840.113549.1.1.1 代表RSA
        System.out.println(rsaAlgorithm.getAlgorithm());
        // 1.2.840.113549.1.1.11 sha256
        System.out.println(rsaCertRequest.getSignatureAlgorithm().getAlgorithm());
        // 1.2.840.113549.1.1.5 sha1
        // 1.2.840.113549.1.1.13 sha512
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
        BCRSAPublicKey publicKey = (BCRSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(rsaCertRequestSubjectPublicKeyInfo.getEncoded()));
        int bitLength = publicKey.getModulus().bitLength();
        System.out.println(bitLength);

        String sm2Csr = "MIIBXjCCAQMCAQAwgYYxDzANBgNVBAgMBumHjeW6hjEPMA0GA1UEBwwG6YeN5bqGMSIwIAYJKoZIhvcNAQkBFhMxMzk4MzA1MzQ1NUAxNjMuY29tMQswCQYDVQQGEwJDTjEPMA0GA1UEChMGR0dLOTExMQ8wDQYDVQQLEwZHR0s5MTExDzANBgNVBAMTBkdHSzkxMTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABAEUDXgD2uSH6siSjCIzwsBxUU4JL/HOwxzDLhbVnQqNLLbvIUJSMM0jFVpyd1PfAQVaKf+GjHxkOJAARe+TBXigGjAYBgkqhkiG9w0BCQcxCxMJc2VjcmV0MTIzMAoGCCqBHM9VAYN1A0kAMEYCIQD+PfVsaLMvEg4uMc7fuxXn7TjGS/8+Dme8R6O0taWoqQIhANYzoZ6DdlHwS23DlkhTWfP1gPqcdE+EegL5rFVkeVsa";
        PKCS10CertificationRequest sm2CertRequest = new PKCS10CertificationRequest(Base64.decode(sm2Csr));
        SubjectPublicKeyInfo sm2CertRequestSubjectPublicKeyInfo = sm2CertRequest.getSubjectPublicKeyInfo();
        AlgorithmIdentifier sm2Algorithm = sm2CertRequestSubjectPublicKeyInfo.getAlgorithm();
        // 1.2.840.10045.2.1 代表ECC
        System.out.println(sm2Algorithm.getAlgorithm());
        System.out.println(sm2CertRequest.getSignatureAlgorithm().getAlgorithm());

    }

}
