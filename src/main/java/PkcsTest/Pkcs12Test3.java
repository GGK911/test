package PkcsTest;

import cn.com.mcsca.pki.core.util.P12Util;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 封装PKCS12（PFX）
 *
 * @author TangHaoKai
 * @version V1.0 2024/6/15 12:31
 */
public class Pkcs12Test3 {
    private static final Provider BC = new BouncyCastleProvider();
    private static final String ROOT = "src/main/java/PkcsTest";
    private static final char[] PASSWD = {'1', '2', '3', '4', '5', '6'};
    private static final KeyFactory rsaKeyFactory;
    private static final KeyFactory sm2KeyFactory;
    private static final CertificateFactory certificateFactory;

    static {
        try {
            rsaKeyFactory = KeyFactory.getInstance("RSA", BC);
            sm2KeyFactory = KeyFactory.getInstance("EC", BC);
            certificateFactory = CertificateFactory.getInstance("X.509", BC);
        } catch (NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @SneakyThrows
    public void RSA() {
        String priBase64 = "MIIEpAIBAAKCAQEArmcTN2XQZvATcSKEZf2zN0d5j4FtFOHdgwTN8f5zagknyZ1R+VNxsxQcfp4EkiWStygV+RjlFIXSsiTdT2eA4iUhZdMMCuJIlVMjslumChCIMvMQEAAsEjlgWTNKF48upHpReh20tZYHREbLhtA4GnvUysKborT3AiccQFldEEQSRNhv461Pc2FEd4Gn12WuR+ZOVcUDLWUh360NnREQarRuqSmg7HZ2fHDGVZ6pvMlgvdlyfUJ0IkPhsxQPhKzQ4L/3NOX2bOfxQ1umE0BVcVy0nlUB0By9NDoMbT+rYaMlXnw0uVjGHI0IuM5SHmGtLQ64BXBPnhRqJgve/kyjqwIDAQABAoIBADC/IsHjNWLwS57dtQAE72jOE44m8ORbVVzfpOi05HGI6ZQS4uy7hBY30tcZN9HZqq6DB9E+QhmAZing3/LnzUBofHNHkCAiq4MBYALkVv6NLGSBR8YRJkBZxTJpgZYgMTtL7SYi03XF33YYC58859GPNyUNTA8oYbo6UE00LSz0Q66g9YahiOACNVJHqbAGPmeSuRdYwstvrsmAOVc4GXcB53oVPtch6wZFKoph1aDH90r/iZCc6A9NPXWJn4OueDodMFv4jRz9s+KGMzdkrrGp+3O6S5Yp5ztb20y52quGQbG4YewFVoAYMX00hEPVdjqDr1PJ2jXter6McXfZaTECgYEA2p4DdKF0jt0jgnoiNNDQ8GS1zC2j8kS0zL9hIDGQKQmR+ouBRwIYs991B5HhnWkpo33CipbH4SIkKhhYPfYItkl+xFDiZ0AGeo3EcExFmnPHkPx8KHQle8Udedt1bFrQTiOHGthJFGkD1eUh7tWcrhpfmAYv51afitaISjWa5ScCgYEAzDmQXEf29p8ywSwe2xcg9tNTYa1YEin5j2MAK13JnzIEw2JSgHtrZd7N81/A9gqYODEYqKyAqHazSTdo7iHpyT8M89FbrCqiHYQBSVIJCjnOiMMABHsN2SHstjnz9P57OfPLQ5NsvA0NWuN3JjUKSQC2iLxLFaHTR7vcsAghR90CgYBMJjY17fdXvBeeX0SC+SmOUsYwdMVioiQIHbGLMThx0u+SQf2p6kUIgpGVLW9VEUh7tlaJR7Qf63CFaS1zh7vJIxiQ8Uurg1W+YptGMoscHbAqBIiK3sofrq1xwMvKDmf4j8+DvkrWXS4E12dWdfm5e15I6f7NqhKwncGjd8CYiQKBgQC6olCfRNUZBNvpVVzNdBGYAABPX48A6oM62oqpFwjPB3L667vh1uiLLFaid3tsXrpFf/i9DQh27CNkYdLu7OcuFwTLPSS97ihBOQXD78h7I+DvcuVtMtew+yPMVKHx3y7WA0pa8zuHj5YI3Z9ht08y8AfgZdNobX8cKz6/UG65sQKBgQCPQCBKFaNqscHOu1sXCdPDdvJNCfFoNPzHg1gRXIkSGJQvHD99BBM3PrqAxSETi7z2lIk421dJBOjvrngcdJ0XSDHo1rLUBAiu42tloP32nN0tvCm9oJ3HX5drLHCae5dgLV8BcAN3LaRaJvsAzp3s5/QO9azjjvjsn0JpaDGGOg==";
        String pubCertBase64 = "MIIDZDCCAkygAwIBAgIHBGLVN+fvTjANBgkqhkiG9w0BAQsFADBAMQswCQYDVQQGEwJDTjEPMA0GA1UEChMGR0dLOTExMQ8wDQYDVQQLEwZHR0s5MTExDzANBgNVBAMTBkdHSzkxMTAeFw0yNDAzMzAwNjIzNDlaFw0yOTAzMjkwNjIzNDlaMEAxCzAJBgNVBAYTAkNOMQ8wDQYDVQQKEwZHR0s5MTExDzANBgNVBAsTBkdHSzkxMTEPMA0GA1UEAxMGR0dLOTExMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArmcTN2XQZvATcSKEZf2zN0d5j4FtFOHdgwTN8f5zagknyZ1R+VNxsxQcfp4EkiWStygV+RjlFIXSsiTdT2eA4iUhZdMMCuJIlVMjslumChCIMvMQEAAsEjlgWTNKF48upHpReh20tZYHREbLhtA4GnvUysKborT3AiccQFldEEQSRNhv461Pc2FEd4Gn12WuR+ZOVcUDLWUh360NnREQarRuqSmg7HZ2fHDGVZ6pvMlgvdlyfUJ0IkPhsxQPhKzQ4L/3NOX2bOfxQ1umE0BVcVy0nlUB0By9NDoMbT+rYaMlXnw0uVjGHI0IuM5SHmGtLQ64BXBPnhRqJgve/kyjqwIDAQABo2MwYTAdBgNVHQ4EFgQU+EopEhChxDzBCm2yL4V3UHB9dPQwHwYDVR0jBBgwFoAU+EopEhChxDzBCm2yL4V3UHB9dPQwCwYDVR0PBAQDAgEGMBIGA1UdEwEB/wQIMAYBAf8CAQMwDQYJKoZIhvcNAQELBQADggEBAJhCcO8Off9EImGEKW46SQR3ZT3NOMCr1+S3LIScNrX0Zm9dzg1McJxS2D6dbmnV17z9Or/fPXKX3vBsOKavZTMQa++ytTloRwuCuRnt0RggIoX5Tx9nwjPgNeOoV/utR23g6gzXvRiv6ICYI1H7I1G4eKmEe42W9UULtgTnbCUke7uxZdGrBhslr7cpyYqENc5ZktbH0IH7D1PUvu9fwWoejIVjnOP6fkNAkGtZTGN8TuO3CgWhoS9P+AiyxjWs3hlAcidqzHq5wT+Uu7XiBfxoqmyKKXhJxVqnQ0tk96cgvyixweBkn+5UTl29Kvwi7+M7vLcJd/X28i+IIEpFdL4=";

        PrivateKey privateKey = rsaKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(priBase64)));
        X509CertificateHolder x509CertificateHolder = new X509CertificateHolder(Base64.decode(pubCertBase64));
        SubjectPublicKeyInfo k = x509CertificateHolder.getSubjectPublicKeyInfo();
        X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decode(pubCertBase64)));
        PublicKey publicKey = rsaKeyFactory.generatePublic(new X509EncodedKeySpec(k.getEncoded()));
        X509Certificate[] certificates = new X509Certificate[]{x509Certificate};
        PKCS12PfxPdu pkcs12PfxPdu = createPfx(privateKey, publicKey, certificates);
        String pkcs12Base64 = Base64.toBase64String(pkcs12PfxPdu.getEncoded());
        System.out.println(pkcs12Base64);
        FileUtil.writeBytes(pkcs12PfxPdu.getEncoded(), Paths.get(ROOT + "/test03.pfx").toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void SM2() {
        String sm2PriBase64 = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgMn51c2ejddEL1LkNPeGLlRV9b2wYhVewUEVRMNyWCIOgCgYIKoEcz1UBgi2hRANCAAT2/Pq7swpbdh++lCIZGADzG5TeJCs8pJH2Eqa/uMvUFlRjraA60fgRFsMPiJLlI22GjfjD4EgmknKGQs86HFjM";
        String sm2PubBase64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE9vz6u7MKW3YfvpQiGRgA8xuU3iQrPKSR9hKmv7jL1BZUY62gOtH4ERbDD4iS5SNtho34w+BIJpJyhkLPOhxYzA==";
        String sm2PubCertBase64 = "MIIBRjCB7aADAgECAgcEYtU35+9OMAoGCCqBHM9VAYN1MCoxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVDaGluYTELMAkGA1UECwwCQ0EwHhcNMjMwNjI0MDU1MDAxWhcNMjUwNjI0MDU1MDAxWjAqMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFQ2hpbmExCzAJBgNVBAsMAkNBMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE9vz6u7MKW3YfvpQiGRgA8xuU3iQrPKSR9hKmv7jL1BZUY62gOtH4ERbDD4iS5SNtho34w+BIJpJyhkLPOhxYzDAKBggqgRzPVQGDdQNIADBFAiEA5Pi1bfOcNwPp3CPPMXKzBZfAJSPbqDMAGA+PKNwDOAsCIC7N8psr9AAgC+ZdBp/7o+tVE1E+z9JUlqgeccMjitPW";

        PrivateKey priKey = sm2KeyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(sm2PriBase64)));
        PublicKey pubKey = sm2KeyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(sm2PubBase64)));
        X509CertificateHolder x509CertificateHolder = new X509CertificateHolder(Base64.decode(sm2PubCertBase64));
        SubjectPublicKeyInfo k = x509CertificateHolder.getSubjectPublicKeyInfo();
        X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decode(sm2PubCertBase64)));
        PublicKey publicKey = sm2KeyFactory.generatePublic(new X509EncodedKeySpec(k.getEncoded()));
        X509Certificate[] certificates = new X509Certificate[]{x509Certificate};
        PKCS12PfxPdu pkcs12PfxPdu = createPfx(priKey, publicKey, certificates);
        String pkcs12Base64 = Base64.toBase64String(pkcs12PfxPdu.getEncoded());
        System.out.println("pkcs12Base64>> " + pkcs12Base64);
        FileUtil.writeBytes(pkcs12PfxPdu.getEncoded(), Paths.get(ROOT + "/pfxTest03.pfx").toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void pkiCoreTest() {
        String sm2PriBase64 = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgMn51c2ejddEL1LkNPeGLlRV9b2wYhVewUEVRMNyWCIOgCgYIKoEcz1UBgi2hRANCAAT2/Pq7swpbdh++lCIZGADzG5TeJCs8pJH2Eqa/uMvUFlRjraA60fgRFsMPiJLlI22GjfjD4EgmknKGQs86HFjM";
        String sm2PubBase64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE9vz6u7MKW3YfvpQiGRgA8xuU3iQrPKSR9hKmv7jL1BZUY62gOtH4ERbDD4iS5SNtho34w+BIJpJyhkLPOhxYzA==";
        String sm2PubCertBase64 = "MIIBRjCB7aADAgECAgcEYtU35+9OMAoGCCqBHM9VAYN1MCoxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVDaGluYTELMAkGA1UECwwCQ0EwHhcNMjMwNjI0MDU1MDAxWhcNMjUwNjI0MDU1MDAxWjAqMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFQ2hpbmExCzAJBgNVBAsMAkNBMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE9vz6u7MKW3YfvpQiGRgA8xuU3iQrPKSR9hKmv7jL1BZUY62gOtH4ERbDD4iS5SNtho34w+BIJpJyhkLPOhxYzDAKBggqgRzPVQGDdQNIADBFAiEA5Pi1bfOcNwPp3CPPMXKzBZfAJSPbqDMAGA+PKNwDOAsCIC7N8psr9AAgC+ZdBp/7o+tVE1E+z9JUlqgeccMjitPW";

        PrivateKey priKey = sm2KeyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(sm2PriBase64)));
        PublicKey pubKey = sm2KeyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(sm2PubBase64)));
        X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decode(sm2PubCertBase64)));

        byte[] generatePfx = P12Util.generatePfx(priKey, pubKey, x509Certificate, "123456");
        System.out.println("generatePfx>> " + Base64.toBase64String(generatePfx));
        FileUtil.writeBytes(generatePfx, Paths.get(ROOT + "/pfxTest03.pfx").toAbsolutePath().toString());
    }

    /**
     * 封装PKCS12
     *
     * @param privKey 私钥
     * @param pubKey  公钥
     * @param chain   证书链
     * @return PKCS12
     */
    @SneakyThrows
    public static PKCS12PfxPdu createPfx(PrivateKey privKey, PublicKey pubKey, X509Certificate[] chain) {
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        // 用户证书
        PKCS12SafeBagBuilder eeCertBagBuilder = new JcaPKCS12SafeBagBuilder(chain[0]);
        eeCertBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("ggk911's Key"));
        eeCertBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, extUtils.createSubjectKeyIdentifier(pubKey));

        PKCS12SafeBagBuilder keyBagBuilder = new JcaPKCS12SafeBagBuilder(privKey, (
                new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, CBCBlockCipher.newInstance(new DESedeEngine()))
        ).build(PASSWD));

        keyBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("ggk911's Key"));
        keyBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, extUtils.createSubjectKeyIdentifier(pubKey));
        // construct the actual key store
        PKCS12PfxPduBuilder pfxPduBuilder = new PKCS12PfxPduBuilder();

        PKCS12SafeBag[] certs = new PKCS12SafeBag[1];
        certs[0] = eeCertBagBuilder.build();
        pfxPduBuilder.addEncryptedData((new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, CBCBlockCipher.newInstance(new RC2Engine())).build(PASSWD)), certs);

        pfxPduBuilder.addData(keyBagBuilder.build());

        PKCS12PfxPdu pfxPdu = pfxPduBuilder.build(new BcPKCS12MacCalculatorBuilder(), PASSWD);
        return pfxPdu;
    }

}
