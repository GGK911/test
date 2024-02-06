package PkcsTest;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import javax.security.auth.x500.X500Principal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;

/**
 * @author TangHaoKai
 * @version V1.0 2024/2/6 14:14
 **/
public class Pkcs10Test {

    private static final Provider BC = new BouncyCastleProvider();

    @Test
    public void changeattrTest() throws Exception {
        String csr = "MIIBxzCCAWwCAQAwUjELMAkGA1UEBhMCQ04xDTALBgNVBAoMBENGQ0ExEjAQBgNVBAsMCUN1c3RvbWVyczEgMB4GA1UEAwwXQ0ZDQUBNb2JpbGVAQW5kcm9pZEAxLjAwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQrCjfwIwvBGHVQ198hrTwmEjUQVYmiwmFoz6tjwAWLQm5zVHpY6QAKD2SSTmT4+hQGfp+xcH04HrE32safG8H8oIG3MBMGCSqGSIb3DQEJBxMGMTExMTExMIGfBgkqhkiG9w0BCT8EgZEwgY4CAQEEgYgAtAAAAAEAAJwB9iDvTwQqVjum4JT5qJLFdhenAAo2jod1lKXocBBnAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABGgshB8jnnNOuEcgtmg7VsLb+JLWMtG8PWexmtZ4y/pQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAwGCCqBHM9VAYN1BQADRwAwRAIgEozHNG9pq7VuDZgOQkXdYk3mC2ATfp+za/OWyGTDEzQCIG1aFX6El0w+32fvZ933kE+oFwPKo00qD914QF6YAyEw";
        ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(csr));
        //将hex转换为byte输出
        ASN1Primitive asn1Primitive;
        while ((asn1Primitive = asn1InputStream.readObject()) != null) {
            ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
            // sequence.getObjectAt(0)
        }

    }

    /**
     * SM2，256
     */
    @Test
    @SneakyThrows
    public void createCsrTest() {
        Security.addProvider(new BouncyCastleProvider());
        // 获取SM2曲线参数
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        // 默认32字节，256bit位
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", BC);
        generator.initialize(ecSpec);
        KeyPair keyPair = generator.generateKeyPair();
        final PrivateKey aPrivate = keyPair.getPrivate();
        System.out.println("privateKey>> " + Hex.toHexString(aPrivate.getEncoded()));
        final PublicKey aPublic = keyPair.getPublic();
        System.out.println("publicKey>> " + Hex.toHexString(aPublic.getEncoded()));

        String subjectParam = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,EMAILADDRESS=13983053455@163.com,L=重庆,ST=重庆";
        X500Principal subject = new X500Principal(subjectParam);
        // SHA256withRSA算法
        ContentSigner signer = new JcaContentSignerBuilder("SM3withSm2")
                .setProvider(BC)
                .build(aPrivate);
        // CSR builder
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());
        // 添加属性
        DERPrintableString password = new DERPrintableString("secret123");
        builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, password);
        // 创建
        PKCS10CertificationRequest sm2Csr = builder.build(signer);
        System.out.println("CSR>> " + Base64.toBase64String(sm2Csr.getEncoded()));
    }

    /**
     * RSA
     */
    @Test
    @SneakyThrows
    public void createCsrTest02() {
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BC);
        // generator.initialize(1024);
        // generator.initialize(2048);
        generator.initialize(4096);
        KeyPair keyPair = generator.generateKeyPair();
        String subjectParam = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,EMAILADDRESS=13983053455@163.com,L=重庆,ST=重庆";
        X500Principal subject = new X500Principal(subjectParam);
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BC)
                .build(keyPair.getPrivate());
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());
        DERPrintableString password = new DERPrintableString("secret123");
        builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, password);

        PKCS10CertificationRequest csr = builder.build(signer);
        System.out.println("CSR>> " + Base64.toBase64String(csr.getEncoded()));
    }


}
