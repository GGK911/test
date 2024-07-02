package certTest.createCert;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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
 * @author TangHaoKai
 * @version V1.0 2024/6/24 11:57
 */
public class createTest02 {
    private static final Provider BC = new BouncyCastleProvider();
    private static final CertificateFactory certificateFactory;
    private static final KeyFactory sm2KeyFactory;
    private static final KeyPairGenerator keyPairGenerator;

    static {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("EC", BC);
            sm2KeyFactory = KeyFactory.getInstance("EC", BC);
            certificateFactory = CertificateFactory.getInstance("X.509", BC);
        } catch (NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @SneakyThrows
    public void sm2Cert() {
        // jdk sun自带
        // g.initialize(new ECNamedCurveGenParameterSpec("secp112r1"));
        // g.initialize(new ECNamedCurveGenParameterSpec("secp256k1"));
        // jdk没有的
        keyPairGenerator.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"));
        KeyPair p = keyPairGenerator.generateKeyPair();
        // 生成的
        PrivateKey priKey = p.getPrivate();
        PublicKey pubKey = p.getPublic();

        if (true) {
            String sm2PriBase64 = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgMn51c2ejddEL1LkNPeGLlRV9b2wYhVewUEVRMNyWCIOgCgYIKoEcz1UBgi2hRANCAAT2/Pq7swpbdh++lCIZGADzG5TeJCs8pJH2Eqa/uMvUFlRjraA60fgRFsMPiJLlI22GjfjD4EgmknKGQs86HFjM";
            String sm2PubBase64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE9vz6u7MKW3YfvpQiGRgA8xuU3iQrPKSR9hKmv7jL1BZUY62gOtH4ERbDD4iS5SNtho34w+BIJpJyhkLPOhxYzA==";

            priKey = sm2KeyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(sm2PriBase64)));
            pubKey = sm2KeyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(sm2PubBase64)));
        }

        System.out.println("priKey>> " + Base64.toBase64String(priKey.getEncoded()));
        System.out.println("pubKey>> " + Base64.toBase64String(pubKey.getEncoded()));

        // 颁发者
        String issuer = "C=CN,O=China,OU=CA";
        // 使用者
        String subject = "C=CN,O=China,OU=CA";
        // V3版本
        JcaX509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(new X500Name(issuer),
                new BigInteger("1234567812345678"),
                // notBefore
                DateUtil.offset(DateUtil.date(), DateField.YEAR, -1),
                // notAfter
                // 1年有效期
                // DateUtil.offset(DateUtil.date(), DateField.YEAR, 1),
                // 1分钟有效期
                DateUtil.offset(DateUtil.date(), DateField.MINUTE, 1),
                new X500Name(subject),
                pubKey);
        ContentSigner signer = new JcaContentSignerBuilder("SM3withSM2").setProvider(BC).build(priKey);
        X509CertificateHolder certificateHolder = certificateBuilder.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificateHolder);
        System.out.println("certificate>> " + Base64.toBase64String(certificate.getEncoded()));
    }
}
