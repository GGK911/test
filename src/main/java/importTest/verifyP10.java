package importTest;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.jcajce.JcaX509ContentVerifierProviderBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * 依赖
 * <!-- https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on -->
 * <dependency>
 * <groupId>org.bouncycastle</groupId>
 * <artifactId>bcprov-jdk18on</artifactId>
 * <version>1.76</version>
 * </dependency>
 * <!-- https://mvnrepository.com/artifact/org.bouncycastle/bcpkix-jdk18on -->
 * <dependency>
 * <groupId>org.bouncycastle</groupId>
 * <artifactId>bcpkix-jdk18on</artifactId>
 * <version>1.76</version>
 * </dependency>
 *
 * @author TangHaoKai
 * @version V1.0 2024/9/19 16:36
 */
public class verifyP10 {
    private static final Provider BC = new BouncyCastleProvider();

    public static void main(String[] args) throws Exception {
        verifyP10("MIICoTCCAYkCAQAwXDELMAkGA1UEBhMCQ04xEjAQBgNVBAgTCUNob25ncWluZzESMBAGA1UEBxMJQ2hvbmdxaW5nMQ0wCwYDVQQKEwRNU0NBMRYwFAYDVQQDDA0qLm1zY2Eub3JnLmNuMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7MqC9e2BB1wQhMe6+ImWhEPw79SiVJ6nssOvEYb2GLAtsoIgNy10GWiRxTXgfHFOXXvuzd3T3SWYH9KaBx33NB+0Uwww2fHPxj3wP7/s2pACi9RhjyvgYW3tIah1X1SFXZe8n88Lwlxm59hQTjBvMDEMUlxVff4iHqrR5HxGfX4LseFQpIe3XytZyopB+9fclld5OPn+qrXh4/xHnrk5cOfTOwPD/lLe7nMk0OKZjHNk5vYPQo9EYrHiuMc0BWQ5RphkS5gRRbDK/XCGbej2OZM537Ytg6F8S9Bvve/ozqZClhTzG/gvLLcnx05CXMSY8K5wxoosEewkmqyFAbDHHQIDAQABoAAwDQYJKoZIhvcNAQELBQADggEBAA07Qk5aZJ+31vWWlW4wM3qQAZknlYUKgM1XG1j7dyD+sEFGbYzA/uRvTp6fb6fXoNXTq5uG0fyVJMk6C8CwYjfEwelfWS6n8YCRY9wivsq9AZLslI/w/50QXE2+ouCNYC0eQo2/D0O4IQkHOtAG+VqaQ+1wKydrGhB0CW6eCbkipLJRs1QjVGcBoAeumwxuD1kRO58hMagF/x0wuSXV0UoGrb2LGqYgvwGP2HbFgTAh8xudaDfyWpP/Uf2uJ/iRXnVjiBtWRCbRQJgSHdgau74rM6ZRhJ/ECEZnlI6znq1dD6riGb3Np7agn3ZI4qztRRrpxL6c04VGnj/RFE3Xj+s=");
    }

    public static boolean verifyP10(String P10) throws Exception {
        PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(Base64.decode(P10));
        SubjectPublicKeyInfo subjectPublicKeyInfo = pkcs10CertificationRequest.getSubjectPublicKeyInfo();
        KeyFactory keyFact = KeyFactory.getInstance("RSA", BC);
        // KeyFactory keyFact = KeyFactory.getInstance("EC", BC);
        PublicKey p10Pub = keyFact.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        System.out.println("p10PubBase64>> " + Base64.toBase64String(p10Pub.getEncoded()));
        System.out.println("p10PubHex>> " + Hex.toHexString(p10Pub.getEncoded()));
        boolean signatureValid = pkcs10CertificationRequest.isSignatureValid((new JcaX509ContentVerifierProviderBuilder()).setProvider(BC).build(subjectPublicKeyInfo));
        System.out.println(signatureValid);
        return signatureValid;
    }
}
