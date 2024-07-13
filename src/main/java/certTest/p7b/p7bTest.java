package certTest.p7b;

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

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TangHaoKai
 * @version V1.0 2024/6/27 9:48
 */
public class p7bTest {
    private static final Provider BC = new BouncyCastleProvider();

    private static CertificateFactory certificateFactory;
    private static final String ROOT = "src/main/java/certTest/p7b";

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            certificateFactory = CertificateFactory.getInstance("X.509", BC);
        } catch (CertificateException e) {
            System.out.println("初始化证书工厂异常");
            throw new RuntimeException(e);
        }
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

    @Test
    @SneakyThrows
    public void packageP7b() {
        List<String> certList = new ArrayList<>();
        certList.add("MIIC/zCCAqOgAwIBAgIQGyeCk4o979VZ0RDhHHU48zAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwNTIwMDkwODMwWhcNMjUwNTIwMDkwODMwWjB4MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjQyMDI4MTE5ODQxMTI1MTI2MzEqMCgGA1UEAwwhMTc4NDQ4Nzc0Njk1ODcwMDU0NUDorrjmhadAMDFAMDAxMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEpvn+9wFZ48XD15KoQp3CTfoww2acsY+MW46v8K1dgMw2wWpwt6FTYAfzMSk3hzKMKJVXkjS1SwW3ksm2azrI9KOCAVYwggFSMB8GA1UdIwQYMBaAFLkj5hx/z7c8WCRbpAqtihGXenu2MB0GA1UdDgQWBBQfVOIxUyQ2zi20M0uO5g4imnn7IzBEBgNVHSAEPTA7MDkGByqBHIeECwEwLjAsBggrBgEFBQcCARYgaHR0cHM6Ly93d3cubWNzY2EuY29tLmNuL2Nwcy5odG0wCwYDVR0PBAQDAgTwMIG8BgNVHR8EgbQwgbEwLqAsoCqGKGh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMS5jcmwwf6B9oHuGeWxkYXA6Ly93d3cubWNzY2EuY29tLmNuOjEwMzg5L0NOPWNybDEsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDAYIKoEcz1UBg3UFAANIADBFAiCAP8XU1X7pNqkL4sd5HRUbnJqwLd4xlImNCFhSg6kiowIgWHDJXsZt9oeu1pm8E+9gt0mzWKACUXssvPB/L40MKb4A");
        // certList.add("MIICgTCCAiWgAwIBAgIQVn7l0kAA6S9mVsDdu6k+oTAMBggqgRzPVQGDdQUAMC4xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVOUkNBQzEPMA0GA1UEAwwGUk9PVENBMB4XDTE4MTEwNTAyMzU0N1oXDTM4MTAzMTAyMzU0N1owLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABGL17gBv7xGY99pYA8IFH3H3VxbulydJGz5Fu3nYnx2FiU4BG8qLNCib5hnKzG6nVgeNel1IVKrsTX86CDQ6lj6jggEiMIIBHjAfBgNVHSMEGDAWgBRMMrGX2TMbxKYFwcbli2Jb8Jd2WDAPBgNVHRMBAf8EBTADAQH/MIG6BgNVHR8EgbIwga8wQaA/oD2kOzA5MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTlJDQUMxDDAKBgNVBAsMA0FSTDEMMAoGA1UEAwwDYXJsMCqgKKAmhiRodHRwOi8vd3d3LnJvb3RjYS5nb3YuY24vYXJsL2FybC5jcmwwPqA8oDqGOGxkYXA6Ly9sZGFwLnJvb3RjYS5nb3YuY246Mzg5L0NOPWFybCxPVT1BUkwsTz1OUkNBQyxDPUNOMA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUuSPmHH/PtzxYJFukCq2KEZd6e7YwDAYIKoEcz1UBg3UFAANIADBFAiEA2HUeRfOn8spFQIkj9zbhVfc0qx9R8lwP40QcRWHIcMcCIHzDLXW5xAkevJaXFcsZJ/Q6CmXK5X+Soq+W/7+DaO8p");

        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        List<JcaX509CertificateHolder> certHolders = new ArrayList<>();
        for (String certBase64 : certList) {
            X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decode(certBase64)));
            certHolders.add(new JcaX509CertificateHolder(x509Certificate));
        }
        generator.addCertificates(new JcaCertStore(certHolders));
        CMSSignedData signedData = generator.generate(new CMSProcessableByteArray(new byte[0]), true);
        byte[] p7bData = signedData.getEncoded();
        Path outPath = Paths.get(ROOT, "package.p7b");
        FileUtil.writeBytes(p7bData, outPath.toAbsolutePath().toString());
    }

}
