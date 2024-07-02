package certTest;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TangHaoKai
 * @version V1.0 2024/6/27 9:48
 */
public class p7bTest {
    private static final Provider BC = new BouncyCastleProvider();
    private static final String ROOT = "src/main/java/certTest";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    @SneakyThrows
    public void readAndWrite() {
        // 读
        Path filePath = Paths.get(ROOT, "test.p7b");
        byte[] fileBytes = FileUtil.readBytes(filePath.toAbsolutePath().toString());
        CMSSignedData cmsSignedData = new CMSSignedData(Base64.decode(fileBytes));
        List<X509Certificate> certList = new ArrayList<>();
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter().setProvider(BC);
        for (X509CertificateHolder certHolder : cmsSignedData.getCertificates().getMatches(null)) {
            certList.add(converter.getCertificate(certHolder));
        }

        for (X509Certificate x509Certificate : certList) {
            // 使用 BouncyCastle 提供的 JcaX509CertificateHolder 获取 X500Name
            JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder(x509Certificate);
            X500Name subject = certHolder.getSubject();
            RDN[] rdNs = subject.getRDNs(BCStyle.CN);
            System.out.println(rdNs[0].getFirst().getValue().toString());
            // System.out.println(x509Certificate);
            System.out.println("=========================================x509Certificate===========================================");
        }

        // 写
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        List<JcaX509CertificateHolder> certHolders = new ArrayList<>();
        for (X509Certificate x509Certificate : certList) {
            certHolders.add(new JcaX509CertificateHolder(x509Certificate));
        }
        generator.addCertificates(new JcaCertStore(certHolders));
        CMSSignedData signedData = generator.generate(new CMSProcessableByteArray(new byte[0]), true);
        byte[] p7bData = signedData.getEncoded();
        Path outPath = Paths.get(ROOT, "copy.p7b");
        FileUtil.writeBytes(p7bData, outPath.toAbsolutePath().toString());
    }

}
