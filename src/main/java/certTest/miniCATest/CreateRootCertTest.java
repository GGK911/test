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
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/13 15:19
 */
public class CreateRootCertTest extends MiniCATest {

    @Test
    @SneakyThrows
    public void createRsaRootCert() {
        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT, "rootPriKey.key").toAbsolutePath().toString());
        PrivateKey privateKey = rsaKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey publicKey = rsaKeyFactory.generatePublic(new X509EncodedKeySpec(pemKeyPair.getPublicKeyInfo().getEncoded()));
        // 自签
        X500Principal subject = new X500Principal("CN=ROOTRSACA,O=MINICA,C=CN");
        // 序列号
        BigInteger snAllocator = new BigInteger("1234567812345678");
        // 使用使用者证书公钥生成x509证书对象
        X509v3CertificateBuilder v3CertGen = new JcaX509v3CertificateBuilder(
                subject,
                snAllocator,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + (5L * 365 * 24 * 60 * 60 * 1000)),
                subject,
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
        PemUtil.objectToFile(certificate, Paths.get(ROOT, "root.cer").toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void createSM2RootCert() {
        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT, "rootSm2PriKey.key").toAbsolutePath().toString());
        PrivateKey privateKey = sm2KeyFactory.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey publicKey = sm2KeyFactory.generatePublic(new X509EncodedKeySpec(pemKeyPair.getPublicKeyInfo().getEncoded()));
        // 自签
        X500Principal subject = new X500Principal("CN=ROOTSM2CA,O=MINICA,C=CN");
        // 序列号
        BigInteger snAllocator = new BigInteger("1234567812345678");
        // 使用使用者证书公钥生成x509证书对象
        X509v3CertificateBuilder v3CertGen = new JcaX509v3CertificateBuilder(
                subject,
                snAllocator,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + (5L * 365 * 24 * 60 * 60 * 1000)),
                subject,
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
        ContentSigner signer = new JcaContentSignerBuilder("SM3withSM2").setProvider(BC).build(privateKey);
        X509CertificateHolder certificateHolder = v3CertGen.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificateHolder);
        System.out.println("CERTIFICATE>> " + Base64.toBase64String(certificate.getEncoded()));
        PemUtil.objectToFile(certificate, Paths.get(ROOT, "sm2Root.cer").toAbsolutePath().toString());
    }
}
