package certTest.miniCATest;

import certTest.createCert.PemUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/13 15:35
 */
public class CreateCertTest extends MiniCATest {

    @Test
    @SneakyThrows
    public void createRSACert() {
        PEMKeyPair rootKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT, "rootPriKey.key").toAbsolutePath().toString());
        PrivateKey rootPrivateKey = rsaKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(rootKeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey rootPublicKey = rsaKeyFactory.generatePublic(new X509EncodedKeySpec(rootKeyPair.getPublicKeyInfo().getEncoded()));
        X509CertificateHolder rootCert = (X509CertificateHolder) PemUtil.objectFromFile(Paths.get(ROOT, "root.cer").toAbsolutePath().toString());
        PKCS10CertificationRequest certReq = (PKCS10CertificationRequest) PemUtil.objectFromFile(Paths.get(ROOT, "rsa.csr").toAbsolutePath().toString());

        // 序列号
        BigInteger snAllocator = new BigInteger(64, new SecureRandom());
        // 使用使用者证书公钥生成x509证书对象
        X509v3CertificateBuilder v3CertGen = new JcaX509v3CertificateBuilder(
                rootCert.getSubject(),
                snAllocator,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + (5L * 365 * 24 * 60 * 60 * 1000)),
                certReq.getSubject(),
                certReq.getSubjectPublicKeyInfo());
        // 扩展工具
        JcaX509ExtensionUtils utils = new JcaX509ExtensionUtils();
        // 主体密钥标识符
        v3CertGen.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                utils.createSubjectKeyIdentifier(certReq.getSubjectPublicKeyInfo()));
        // 颁发机构密钥标识符
        v3CertGen.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                utils.createAuthorityKeyIdentifier(rootPublicKey));
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
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider(BC).build(rootPrivateKey);
        X509CertificateHolder certificateHolder = v3CertGen.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificateHolder);
        System.out.println("CERTIFICATE>> " + Base64.toBase64String(certificate.getEncoded()));
        PemUtil.objectToFile(certificate, Paths.get(ROOT, "rsa.cer").toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void createSM2Cert() {
        PEMKeyPair rootSM2KeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT, "rootSm2PriKey.key").toAbsolutePath().toString());
        PrivateKey rootSM2PrivateKey = sm2KeyFactory.generatePrivate(new PKCS8EncodedKeySpec(rootSM2KeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey rootSM2PublicKey = sm2KeyFactory.generatePublic(new X509EncodedKeySpec(rootSM2KeyPair.getPublicKeyInfo().getEncoded()));
        X509CertificateHolder rootCert = (X509CertificateHolder) PemUtil.objectFromFile(Paths.get(ROOT, "sm2Root.cer").toAbsolutePath().toString());
        PKCS10CertificationRequest certReq = (PKCS10CertificationRequest) PemUtil.objectFromFile(Paths.get(ROOT, "sm2.csr").toAbsolutePath().toString());

        // 序列号
        BigInteger snAllocator = new BigInteger(64, new SecureRandom());
        // 使用使用者证书公钥生成x509证书对象
        X509v3CertificateBuilder v3CertGen = new JcaX509v3CertificateBuilder(
                rootCert.getSubject(),
                snAllocator,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + (5L * 365 * 24 * 60 * 60 * 1000)),
                certReq.getSubject(),
                certReq.getSubjectPublicKeyInfo());
        // 扩展工具
        JcaX509ExtensionUtils utils = new JcaX509ExtensionUtils();
        // 主体密钥标识符
        v3CertGen.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                utils.createSubjectKeyIdentifier(certReq.getSubjectPublicKeyInfo()));
        // 颁发机构密钥标识符
        v3CertGen.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                utils.createAuthorityKeyIdentifier(rootSM2PublicKey));
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
        ContentSigner signer = new JcaContentSignerBuilder("SM3withSM2").setProvider(BC).build(rootSM2PrivateKey);
        X509CertificateHolder certificateHolder = v3CertGen.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificateHolder);
        System.out.println("CERTIFICATE>> " + Base64.toBase64String(certificate.getEncoded()));
        PemUtil.objectToFile(certificate, Paths.get(ROOT, "sm2.cer").toAbsolutePath().toString());
    }

}
