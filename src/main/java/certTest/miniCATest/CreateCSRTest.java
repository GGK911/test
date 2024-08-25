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
        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT, "priKey.key").toAbsolutePath().toString());
        PrivateKey privateKey = rsaKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
        PublicKey publicKey = rsaKeyFactory.generatePublic(new X509EncodedKeySpec(pemKeyPair.getPublicKeyInfo().getEncoded()));
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
