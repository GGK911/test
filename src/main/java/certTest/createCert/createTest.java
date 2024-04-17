package certTest.createCert;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX500NameUtil;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 * 创建证书测试类
 *
 * @author TangHaoKai
 * @version V1.0 2024/3/30 12:57
 */
public class createTest {
    private static final Provider BC = new BouncyCastleProvider();
    private static final String ROOT = "src/main/java/certTest/createCert";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    //************************************************TS证书****************************************************************//

    @Test
    @SneakyThrows
    public void createTSCert() {
        PEMKeyPair pemCAKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT + "/CAPriKey.key").toAbsolutePath().toString());
        KeyFactory kf = KeyFactory.getInstance("RSA", BC);
        PrivateKey CAPrivateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(pemCAKeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey CAPublicKey = kf.generatePublic(new X509EncodedKeySpec(pemCAKeyPair.getPublicKeyInfo().getEncoded()));
        X509CertificateHolder CACertificateHolder = (X509CertificateHolder) PemUtil.objectFromFile(Paths.get(ROOT + "/CACert.cer").toAbsolutePath().toString());
        X509Certificate CACert = new JcaX509CertificateConverter().setProvider(BC).getCertificate(CACertificateHolder);

        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT + "/TSPriKey.key").toAbsolutePath().toString());
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(pemKeyPair.getPublicKeyInfo().getEncoded()));

        // 使用者
        X500NameBuilder subjectBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        subjectBuilder.addRDN(BCStyle.C, "CN");
        subjectBuilder.addRDN(BCStyle.O, "China");
        subjectBuilder.addRDN(BCStyle.OU, "DLYD");
        subjectBuilder.addRDN(BCStyle.CN, "TimeStamp");
        subjectBuilder.addRDN(BCStyle.EmailAddress, "13983053455@163.com");
        // V3版本
        X509v3CertificateBuilder v3CertBuilder = new JcaX509v3CertificateBuilder(
                JcaX500NameUtil.getIssuer(CACert),
                BigInteger.valueOf(2),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * 5)),
                subjectBuilder.build(),
                publicKey);
        // 扩展
        JcaX509ExtensionUtils utils = new JcaX509ExtensionUtils();
        // 主体密钥标识符
        v3CertBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                utils.createSubjectKeyIdentifier(publicKey));
        // 颁发机构密钥标识符
        v3CertBuilder.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                utils.createAuthorityKeyIdentifier(CAPublicKey));
        // 基本约束
        v3CertBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(0));
        // 增强型密钥用法 - 时间戳
        v3CertBuilder.addExtension(
                Extension.extendedKeyUsage,
                true,
                new ExtendedKeyUsage(KeyPurposeId.id_kp_timeStamping));
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider(BC).build(CAPrivateKey);
        X509CertificateHolder certificateHolder = v3CertBuilder.build(signer);
        X509Certificate TSCert = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificateHolder);
        System.out.println("CERTIFICATE>> " + Base64.toBase64String(TSCert.getEncoded()));
        PemUtil.objectToFile(TSCert, Paths.get(ROOT + "/TSCert.cer").toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void createTSKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BC);
        // generator.initialize(1024);
        generator.initialize(2048);
        // generator.initialize(4096);
        KeyPair keyPair = generator.generateKeyPair();
        PublicKey aPublic = keyPair.getPublic();
        PrivateKey aPrivate = keyPair.getPrivate();
        System.out.println("priKey>> " + Hex.toHexString(aPrivate.getEncoded()));
        System.out.println("pubKey>> " + Hex.toHexString(aPublic.getEncoded()));
        PemUtil.objectToFile(aPrivate, Paths.get(ROOT + "/TSPriKey.key").toAbsolutePath().toString());
        PemUtil.objectToFile(aPublic, Paths.get(ROOT + "/TSPubKey.key").toAbsolutePath().toString());
    }

    //************************************************CA证书****************************************************************//

    /**
     * 生成CA密钥对
     */
    @Test
    @SneakyThrows
    public void createCAKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BC);
        // generator.initialize(1024);
        generator.initialize(2048);
        // generator.initialize(4096);
        KeyPair keyPair = generator.generateKeyPair();
        PublicKey aPublic = keyPair.getPublic();
        PrivateKey aPrivate = keyPair.getPrivate();
        System.out.println("priKey>> " + Hex.toHexString(aPrivate.getEncoded()));
        System.out.println("pubKey>> " + Hex.toHexString(aPublic.getEncoded()));
        PemUtil.objectToFile(aPrivate, Paths.get(ROOT + "/CAPriKey.key").toAbsolutePath().toString());
        PemUtil.objectToFile(aPublic, Paths.get(ROOT + "/CAPubKey.key").toAbsolutePath().toString());
    }

    /**
     * 生成CA的证书请求
     */
    @Test
    @SneakyThrows
    public void createCACSR() {
        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT + "/CAPriKey.key").toAbsolutePath().toString());
        KeyFactory kf = KeyFactory.getInstance("RSA", BC);
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(pemKeyPair.getPublicKeyInfo().getEncoded()));

        String subjectParam = "CN=GGK911,OU=GGK911,O=GGK911,C=CN";
        X500Principal subject = new X500Principal(subjectParam);
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BC)
                .build(privateKey);
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, publicKey);
        DERPrintableString password = new DERPrintableString("123456");
        builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, password);

        PKCS10CertificationRequest csr = builder.build(signer);
        System.out.println("CSR>> " + Base64.toBase64String(csr.getEncoded()));
        PemUtil.objectToFile(csr, Paths.get(ROOT + "/CACsr.csr").toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void createCACertFromCSR() {
        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT + "/CAPriKey.key").toAbsolutePath().toString());
        KeyFactory kf = KeyFactory.getInstance("RSA", BC);
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(pemKeyPair.getPublicKeyInfo().getEncoded()));
        PKCS10CertificationRequest request = (PKCS10CertificationRequest) PemUtil.objectFromFile(Paths.get(ROOT + "/CACsr.csr").toAbsolutePath().toString());

        // 序列号
        BigInteger snAllocator = new BigInteger("1234567812345678");
        // 使用使用者证书公钥生成x509证书对象
        X509v3CertificateBuilder v3CertGen = new JcaX509v3CertificateBuilder(
                request.getSubject(),
                snAllocator,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + (5L * 365 * 24 * 60 * 60 * 1000)),
                request.getSubject(),
                publicKey);
        // 扩展工具
        JcaX509ExtensionUtils utils = new JcaX509ExtensionUtils();
        // 主体密钥标识符
        v3CertGen.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                utils.createSubjectKeyIdentifier(publicKey));
        // 颁发机构密钥标识符
        v3CertGen.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                utils.createAuthorityKeyIdentifier(publicKey));
        // 密钥用法
        v3CertGen.addExtension(
                Extension.keyUsage,
                false,
                new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));
        //  基本约束
        v3CertGen.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(3));
        // 增强型密钥用法 - 时间戳
        // v3CertGen.addExtension(
        //         Extension.extendedKeyUsage,
        //         true,
        //         new ExtendedKeyUsage(KeyPurposeId.id_kp_timeStamping));
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider(BC).build(privateKey);
        X509CertificateHolder certificateHolder = v3CertGen.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificateHolder);
        System.out.println("CERTIFICATE>> " + Base64.toBase64String(certificate.getEncoded()));
        PemUtil.objectToFile(certificate, Paths.get(ROOT + "/CACert.cer").toAbsolutePath().toString());
    }

}
