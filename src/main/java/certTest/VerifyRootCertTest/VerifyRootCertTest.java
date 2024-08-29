package certTest.VerifyRootCertTest;

import certTest.createCert.PemUtil;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * 验根测试
 *
 * @author TangHaoKai
 * @version V1.0 2024/7/2 20:03
 */
public class VerifyRootCertTest {

    private static final Provider BC = new BouncyCastleProvider();
    private static final String ROOT = "src/main/java/certTest/VerifyRootCertTest";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    @SneakyThrows
    public void verifyTest() {
        String rootPath = "SM2 MCSCA.cer";
        String userPath = "USER CERT FROM SM2 MCSCA.cer";
        rootPath = "MCSCA.cer";
        userPath = "Auser.cer";
        X509CertificateHolder CACertificateHolder = (X509CertificateHolder) PemUtil.objectFromFile(Paths.get(ROOT, userPath).toAbsolutePath().toString());
        X509Certificate x509Certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(CACertificateHolder);

        // TBSCert
        byte[] tbsCertificate = x509Certificate.getTBSCertificate();
        System.out.println("TBScert>> " + Hex.toHexString(tbsCertificate));
        // 算法
        System.out.println("SigAlgOID>> " + x509Certificate.getSigAlgOID());
        System.out.println("SigAlgName>> " + x509Certificate.getSigAlgName());
        // 签名
        byte[] signatureBytes = x509Certificate.getSignature();
        System.out.println("SigValue>> " + Hex.toHexString(signatureBytes));

        // 验签(用中间CA的公钥证书 验 TBSCert)
        ByteArrayInputStream bis = new ByteArrayInputStream(FileUtil.readBytes(Paths.get(ROOT, rootPath).toAbsolutePath().toString()));
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", BC);
        Certificate middleCert = certificateFactory.generateCertificate(bis);

        Signature signature = Signature.getInstance(x509Certificate.getSigAlgName(), BC);
        signature.initVerify(middleCert.getPublicKey());
        signature.update(tbsCertificate);
        System.out.println("verify>> " + signature.verify(signatureBytes));
    }
}
