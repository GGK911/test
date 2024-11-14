package certTest.miniCATest;

import certTest.createCert.PemUtil;
import lombok.SneakyThrows;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import javax.security.auth.x500.X500Principal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 创建CSR
 *
 * @author TangHaoKai
 * @version V1.0 2024/7/13 14:08
 */
public class CreateCSRTest extends MiniCATest {

    @Test
    @SneakyThrows
    public void createRSACSR() {
        String subjectParam = "CN=ggk911,C=MINICA";
        // subjectParam = "CN=*.msca.org.cn,O=MSCA,L=Chongqing,S=Chongqing,C=CN";

        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT, "priKey.key").toAbsolutePath().toString());
        PrivateKey privateKey = rsaKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
        // privateKey = rsaKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode("MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDsyoL17YEHXBCEx7r4iZaEQ/Dv1KJUnqeyw68RhvYYsC2ygiA3LXQZaJHFNeB8cU5de+7N3dPdJZgf0poHHfc0H7RTDDDZ8c/GPfA/v+zakAKL1GGPK+Bhbe0hqHVfVIVdl7yfzwvCXGbn2FBOMG8wMQxSXFV9/iIeqtHkfEZ9fgux4VCkh7dfK1nKikH719yWV3k4+f6qteHj/EeeuTlw59M7A8P+Ut7ucyTQ4pmMc2Tm9g9Cj0RiseK4xzQFZDlGmGRLmBFFsMr9cIZt6PY5kznfti2DoXxL0G+97+jOpkKWFPMb+C8styfHTkJcxJjwrnDGiiwR7CSarIUBsMcdAgMBAAECggEBAOmatG6IvCWVlxpXrUZq/ppIKNeCcA+JYipDUWC6Q+mBTePw6nVlG0fB2SW1DF3YrpYdoS9qLAzdfOHQNHLXBj1fFdhki4FIRVHvYyiRYMsTDeADS6gigj+8Yhg1iG3fBZ+bBdSnP5Ok80TBLCCnOdpSWTSbsSAWanTU0zNVw3kize+59cXjT6lzV9MN9sxDdBdxsF7lStsH3prtQ+EOPlV15OboRGt+i4kDJDOCgJwPF+TyjlQQP2AFtE5wWtcCsF+Qe38aTkrRT+mIGRRD/TapMLI23z62l5G7btkqpRB/TvsSd4qMQfjsKykzvL5Y75gFxiHYsoxUmQkIgU31HCUCgYEA8qn5Fh8QXds4T1H1v3JNlNht5HoJU7Vjvuqui6L1IH/YsaUr00vBaAUFuJhOZ9tFbsya6aS49pBtjSvufxoSjfGivZL874a3mK5sTUyiEggBqZse3mFWJT09ByAR4Wl2MtxuOeXfUX30rQxvQXh75mBpp7EPzki+hDTYwiaHJLcCgYEA+c3pwELfFfa2/YmsjGlLDcycYkOgosjebyiw8Zjz6x+tVapFEOqvn6wk2DynhBEaXrpfYOc/XO8BJwWUigxrDEld5mw+ibKW+Y7e9kniknmxXl1zSrmI++qZmVbxdQZTOkStubG+lnqJnvk6tIdnYTZY9l2xDZcqXdAMxrsIpssCgYEAhn1RwcLhrULsSHniO4K8ILx622APf0dOyucCaf2c4bA5hutGCMs5m878xrwS6FiMeMYJLWjP4kdVkCJDAkqO8gBz86Fdcds2Mfapq4XHZTruwPNp/lHwDp+MDUDm6AktKy8kIA6Y4G9wheAYYS7HbH0O0ZO+cNO8U0V+xMvZpq0CgYEAxvrRZjceEhW6f0xp6FJ62VwReWhbDS+plqu3/koSiUhrPqBpcWcvS6XIl4IBAX7KHYihLsQkwbcTmOyl/CyWHGxYYpH4YqCiB9lZGp+R3ZEXyo3vpycQyXF+thj+LnRCWViZdR/zK1QiSUJHE8QDgfjTdjL9NFEkCyQM+UPESW8CgYEAuBdo/pER7VOYhW8wSnEsPHjg0xDmX1V0G7oumqa4DN7no7mH4xBQdE1jBFWhykAneBxczikL0Q88e76vyqItvok/OOYoJc+aENvhzWcS0ebruz/muT1UDA3uUiWF2F+NmUNEw7XumAlPGEDil419dHOMbhV07yTQugCcEex6e7U=")));

        PublicKey publicKey = rsaKeyFactory.generatePublic(new X509EncodedKeySpec(pemKeyPair.getPublicKeyInfo().getEncoded()));
        // publicKey = rsaKeyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7MqC9e2BB1wQhMe6+ImWhEPw79SiVJ6nssOvEYb2GLAtsoIgNy10GWiRxTXgfHFOXXvuzd3T3SWYH9KaBx33NB+0Uwww2fHPxj3wP7/s2pACi9RhjyvgYW3tIah1X1SFXZe8n88Lwlxm59hQTjBvMDEMUlxVff4iHqrR5HxGfX4LseFQpIe3XytZyopB+9fclld5OPn+qrXh4/xHnrk5cOfTOwPD/lLe7nMk0OKZjHNk5vYPQo9EYrHiuMc0BWQ5RphkS5gRRbDK/XCGbej2OZM537Ytg6F8S9Bvve/ozqZClhTzG/gvLLcnx05CXMSY8K5wxoosEewkmqyFAbDHHQIDAQAB")));

        X500Principal subject = new X500Principal(subjectParam);
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BC)
                .build(privateKey);
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, publicKey);
        // DERPrintableString password = new DERPrintableString("secret123");
        // builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, password);

        PKCS10CertificationRequest csr = builder.build(signer);

        System.out.println("CSRBase64>> " + Base64.toBase64String(csr.getEncoded()));
        System.out.println("CSRHex>> " + Hex.toHexString(csr.getEncoded()));

        Path csrPath = Paths.get(ROOT, "rsa.csr");
        PemUtil.objectToFile(csr, csrPath.toAbsolutePath().toString());
        // openssl
        // 解析请求
        // openssl req -in .\miniCATest\rsa.csr -text -noout
        // openssl req -in .\miniCATest\rsa.csr -inform DER -text -noout
    }

    @Test
    @SneakyThrows
    public void createSM2CSR() {
        String subjectParam = "CN=DLYD,C=MSCA";
        subjectParam = "CN=10.255.7.6,C=CN";
        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT, "sm2PriKey.key").toAbsolutePath().toString());
        PrivateKey privateKey = sm2KeyFactory.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey publicKey = sm2KeyFactory.generatePublic(new X509EncodedKeySpec(pemKeyPair.getPublicKeyInfo().getEncoded()));
        X500Principal subject = new X500Principal(subjectParam);
        ContentSigner signer = new JcaContentSignerBuilder("SM3withSm2")
                .setProvider(BC)
                .build(privateKey);
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, publicKey);

        PKCS10CertificationRequest csr = builder.build(signer);

        System.out.println("CSRBase64>> " + Base64.toBase64String(csr.getEncoded()));
        System.out.println("CSRHex>> " + Hex.toHexString(csr.getEncoded()));

        Path csrPath = Paths.get(ROOT, "sm2.csr");
        PemUtil.objectToFile(csr, csrPath.toAbsolutePath().toString());
    }

}
