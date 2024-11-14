package certTest.p7b;

import certTest.createCert.PemUtil;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        // certList.add("MIIC/zCCAqOgAwIBAgIQGyeCk4o979VZ0RDhHHU48zAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwNTIwMDkwODMwWhcNMjUwNTIwMDkwODMwWjB4MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjQyMDI4MTE5ODQxMTI1MTI2MzEqMCgGA1UEAwwhMTc4NDQ4Nzc0Njk1ODcwMDU0NUDorrjmhadAMDFAMDAxMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEpvn+9wFZ48XD15KoQp3CTfoww2acsY+MW46v8K1dgMw2wWpwt6FTYAfzMSk3hzKMKJVXkjS1SwW3ksm2azrI9KOCAVYwggFSMB8GA1UdIwQYMBaAFLkj5hx/z7c8WCRbpAqtihGXenu2MB0GA1UdDgQWBBQfVOIxUyQ2zi20M0uO5g4imnn7IzBEBgNVHSAEPTA7MDkGByqBHIeECwEwLjAsBggrBgEFBQcCARYgaHR0cHM6Ly93d3cubWNzY2EuY29tLmNuL2Nwcy5odG0wCwYDVR0PBAQDAgTwMIG8BgNVHR8EgbQwgbEwLqAsoCqGKGh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMS5jcmwwf6B9oHuGeWxkYXA6Ly93d3cubWNzY2EuY29tLmNuOjEwMzg5L0NOPWNybDEsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDAYIKoEcz1UBg3UFAANIADBFAiCAP8XU1X7pNqkL4sd5HRUbnJqwLd4xlImNCFhSg6kiowIgWHDJXsZt9oeu1pm8E+9gt0mzWKACUXssvPB/L40MKb4A");
        // certList.add("MIICgTCCAiWgAwIBAgIQVn7l0kAA6S9mVsDdu6k+oTAMBggqgRzPVQGDdQUAMC4xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVOUkNBQzEPMA0GA1UEAwwGUk9PVENBMB4XDTE4MTEwNTAyMzU0N1oXDTM4MTAzMTAyMzU0N1owLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABGL17gBv7xGY99pYA8IFH3H3VxbulydJGz5Fu3nYnx2FiU4BG8qLNCib5hnKzG6nVgeNel1IVKrsTX86CDQ6lj6jggEiMIIBHjAfBgNVHSMEGDAWgBRMMrGX2TMbxKYFwcbli2Jb8Jd2WDAPBgNVHRMBAf8EBTADAQH/MIG6BgNVHR8EgbIwga8wQaA/oD2kOzA5MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTlJDQUMxDDAKBgNVBAsMA0FSTDEMMAoGA1UEAwwDYXJsMCqgKKAmhiRodHRwOi8vd3d3LnJvb3RjYS5nb3YuY24vYXJsL2FybC5jcmwwPqA8oDqGOGxkYXA6Ly9sZGFwLnJvb3RjYS5nb3YuY246Mzg5L0NOPWFybCxPVT1BUkwsTz1OUkNBQyxDPUNOMA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUuSPmHH/PtzxYJFukCq2KEZd6e7YwDAYIKoEcz1UBg3UFAANIADBFAiEA2HUeRfOn8spFQIkj9zbhVfc0qx9R8lwP40QcRWHIcMcCIHzDLXW5xAkevJaXFcsZJ/Q6CmXK5X+Soq+W/7+DaO8p");
        // sm2根
        // certList.add("MIIBszCCAVegAwIBAgIIaeL+wBcKxnswDAYIKoEcz1UBg3UFADAuMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTlJDQUMxDzANBgNVBAMMBlJPT1RDQTAeFw0xMjA3MTQwMzExNTlaFw00MjA3MDcwMzExNTlaMC4xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVOUkNBQzEPMA0GA1UEAwwGUk9PVENBMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEMPCca6pmgcchsTf2UnBeL9rtp4nw+itk1Kzrmbnqo05lUwkwlWK+4OIrtFdAqnRTV7Q9v1htkv42TsIutzd126NdMFswHwYDVR0jBBgwFoAUTDKxl9kzG8SmBcHG5YtiW/CXdlgwDAYDVR0TBAUwAwEB/zALBgNVHQ8EBAMCAQYwHQYDVR0OBBYEFEwysZfZMxvEpgXBxuWLYlvwl3ZYMAwGCCqBHM9VAYN1BQADSAAwRQIgG1bSLeOXp3oB8H7b53W+CKOPl2PknmWEq/lMhtn25HkCIQDaHDgWxWFtnCrBjH16/W3Ezn7/U/Vjo5xIpDoiVhsLwg==");
        // certList.add("MIICgTCCAiWgAwIBAgIQVn7l0kAA6S9mVsDdu6k+oTAMBggqgRzPVQGDdQUAMC4xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVOUkNBQzEPMA0GA1UEAwwGUk9PVENBMB4XDTE4MTEwNTAyMzU0N1oXDTM4MTAzMTAyMzU0N1owLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABGL17gBv7xGY99pYA8IFH3H3VxbulydJGz5Fu3nYnx2FiU4BG8qLNCib5hnKzG6nVgeNel1IVKrsTX86CDQ6lj6jggEiMIIBHjAfBgNVHSMEGDAWgBRMMrGX2TMbxKYFwcbli2Jb8Jd2WDAPBgNVHRMBAf8EBTADAQH/MIG6BgNVHR8EgbIwga8wQaA/oD2kOzA5MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTlJDQUMxDDAKBgNVBAsMA0FSTDEMMAoGA1UEAwwDYXJsMCqgKKAmhiRodHRwOi8vd3d3LnJvb3RjYS5nb3YuY24vYXJsL2FybC5jcmwwPqA8oDqGOGxkYXA6Ly9sZGFwLnJvb3RjYS5nb3YuY246Mzg5L0NOPWFybCxPVT1BUkwsTz1OUkNBQyxDPUNOMA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUuSPmHH/PtzxYJFukCq2KEZd6e7YwDAYIKoEcz1UBg3UFAANIADBFAiEA2HUeRfOn8spFQIkj9zbhVfc0qx9R8lwP40QcRWHIcMcCIHzDLXW5xAkevJaXFcsZJ/Q6CmXK5X+Soq+W/7+DaO8p");

        // certList.add("MIIBujCCAV+gAwIBAgIQF1iSg3cTLzuvjZ1dQGmzrDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwIBcNMjMwODE3MDIzNjIyWhgPMjA1MzA4MDkwMjM2MjJaMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAARq3n5sX8TdRNmYM2JdN3TKMAfRNFqpSAxLTh9Da+esqimrXlCdg5WMMopRJmIRdCwlxLffo7wZnCgX3ZA57I/do10wWzALBgNVHQ8EBAMCAQYwDAYDVR0TBAUwAwEB/zAdBgNVHQ4EFgQUMIj+P2KgJfTzKz2pbaagTAtMyrUwHwYDVR0jBBgwFoAUMIj+P2KgJfTzKz2pbaagTAtMyrUwDAYIKoEcz1UBg3UFAANHADBEAiAVdc6tFHp4Gg337hsCvEd5rOwYs8/dLiIflyMO4gtbcQIgQkAtpRAFhnRynez19uEo9Tade6Heu0MXQKrp/BVjt0Y=");

        // 正式环境根证书sm2
        // certList.add("MIIC9TCCApmgAwIBAgIQaxlxj6PONXVAZS9jSqFHxzAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQxMDIyMDQyNzQyWhcNMjUxMDIyMDQyNzQyWjBsMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjQ1MjEyNDE5ODMwNDI5MDAyNjEeMBwGA1UEAwwVSERKVEDlvKDnkLzkuLlAMDFAMDAxMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE2A05SqTF4dj+856V7m0WP/PQDegYgjRzRGfwNyx6zjVvNIu1yyqZ1zNoVRoG7W4v2fF5kaF0MzolZSQe0kKG+qOCAVgwggFUMAsGA1UdDwQEAwIE8DAfBgNVHSMEGDAWgBS5I+Ycf8+3PFgkW6QKrYoRl3p7tjAdBgNVHQ4EFgQUrPZnUIfjxqkTao08WzMYQqMEQAcwgb4GA1UdHwSBtjCBszAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwxLmNybDCBgKB+oHyGemxkYXA6Ly9sZGFwLm1jc2NhLmNvbS5jbjoxMDM4OS9DTj1jcmwxLE9VPUNSTCxPPU1DU0NBLEM9Q04/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MEQGA1UdIAQ9MDswOQYHKoEch4QLATAuMCwGCCsGAQUFBwIBFiBodHRwczovL3d3dy5tY3NjYS5jb20uY24vY3BzLmh0bTAMBggqgRzPVQGDdQUAA0gAMEUCIQDMrT4cPjSxeAlYezvLtuVT+jJ3js8CG0YjfSVF+1vU0AIgAExkfNBPt6OvKIfZmOoCFhRZf+Fkocdqa7xDP0r7qkA=");
        // 测试环境根证书sm2
        // certList.add("MIIBuzCCAV+gAwIBAgIQawXI9s8NzHnBm5PlBh9HRDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwIBcNMjAwNDI0MTQwNzU1WhgPMjA1MDA0MTcxNDA3NTVaMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASTFIp6vFYitNsFI35CmdfNDZMzHK9L2azopOpIJXLUYRpH5gI6v8IA2qKC8cAra2p+rb6qS75QUwDBxgVELq7Ko10wWzALBgNVHQ8EBAMCAQYwDAYDVR0TBAUwAwEB/zAdBgNVHQ4EFgQU8SIKZ5iN9eOyqsMXa8BCH75LvXYwHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwDAYIKoEcz1UBg3UFAANIADBFAiBjj2WAf1xek+XYSwjqsMkWuOPtafHL3cYhmkp9QVZZJwIhAMs3DFTfq6K+cs1Dqz4MsZzeODYzibmOYtcxEstB97cZ");
        // 国家根证书.cer
        Path countryRootCertPath = Paths.get("src/main/java/certTest/p7b", "国家根证书.cer");
        // 正式环境-使用国家根签的大陆云盾sm2根.cer
        Path dlydSm2RootCertPath = Paths.get("src/main/java/certTest/p7b", "正式环境-使用国家根签的大陆云盾sm2根.cer");
        Path outPath = Paths.get(ROOT, "package.p7b");
        packageP7b(certList, outPath);
    }

    @Test
    @SneakyThrows
    public void batchPackageTest() {
        // 国家根证书.cer
        Path countryRootCertPath = Paths.get("src/main/java/certTest/p7b", "国家根证书.cer");
        // 正式环境-使用国家根签的大陆云盾sm2根.cer
        Path dlydSm2RootCertPath = Paths.get("src/main/java/certTest/p7b", "正式环境-使用国家根签的大陆云盾sm2根.cer");
        // 正式环境-大陆云盾RSA根.cer
        Path dlydRSARootCertPath = Paths.get("src/main/java/certTest/p7b", "正式环境-大陆云盾RSA根.cer");
        Path dlydRSARootCertPath2 = Paths.get("src/main/java/certTest/p7b", "正式环境-大陆云盾RSA根2.cer");
        byte[] countryRootCertBytes = FileUtil.readBytes(countryRootCertPath);
        byte[] dlydSm2RootCertBytes = FileUtil.readBytes(dlydSm2RootCertPath);
        byte[] dlydRSARootCertBytes = FileUtil.readBytes(dlydRSARootCertPath);
        byte[] dlydRSARootCertBytes2 = FileUtil.readBytes(dlydRSARootCertPath2);

        // File dir = FileUtil.file("C:\\Users\\ggk911\\Desktop\\RSA-P10");
        File dir = FileUtil.file("C:\\Users\\ggk911\\Desktop\\SM2-P10");
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            List<String> certStrList = new ArrayList<>();
            for (File certFile : files) {
                if (certFile.getName().contains(".p7b") && !certFile.getName().contains("-NEW")) {
                    byte[] fileBytes = FileUtil.readBytes(certFile);
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
                        // if (!rdNs[0].getFirst().getValue().toString().contains("MCSCA")) {
                        if (!rdNs[0].getFirst().getValue().toString().contains("ROOT")) {
                            String cert = Base64.toBase64String(x509Certificate.getEncoded());
                            certStrList = new ArrayList<>();
                            certStrList.add(cert);
                            certStrList.add(Base64.toBase64String(countryRootCertBytes));
                            certStrList.add(Base64.toBase64String(dlydSm2RootCertBytes));

                            // FileUtil.writeBytes(cert.getBytes(StandardCharsets.UTF_8), new File("C:\\Users\\ggk911\\Desktop\\新根\\BASE64编码\\RSA证书链-NEW\\单独的根证书", FileUtil.getPrefix(certFile.getName()) + "-NEW.cer"));

                            Path out = new File(certFile.getParent(), FileUtil.getPrefix(certFile.getName()) + "-NEW." + FileUtil.getSuffix(certFile.getName())).toPath();
                            packageP7b(certStrList, out);
                        }
                        System.out.println("=========================================x509Certificate===========================================");
                    }
                }
            }
            // 打包所有相同算法根
            // certStrList.add(Base64.toBase64String(countryRootCertBytes));
            // certStrList.add(Base64.toBase64String(dlydSm2RootCertBytes));
            // certStrList.add(Base64.toBase64String(dlydRSARootCertBytes));
            // certStrList.add(Base64.toBase64String(dlydRSARootCertBytes2));
            // Path out = new File(dir, "ALL-SM2.p7b").toPath();
            // packageP7b(certStrList, out);
        }
    }

    @SneakyThrows
    private static void packageP7b(List<String> certList, Path outPath) throws CertificateException, CMSException, IOException {
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        List<JcaX509CertificateHolder> certHolders = new ArrayList<>();
        for (String certBase64 : certList) {
            X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decode(certBase64)));
            certHolders.add(new JcaX509CertificateHolder(x509Certificate));
        }
        generator.addCertificates(new JcaCertStore(certHolders));
        CMSSignedData signedData = generator.generate(new CMSProcessableByteArray(new byte[0]), true);

        PemUtil.objectToFile(signedData, "C:\\Users\\ggk911\\Desktop\\2.p7b");


        // byte[] p7bData = signedData.getEncoded();
        // String p7bBase64 = Base64.toBase64String(p7bData);
        // System.out.println(String.format("%-16s", "p7bBase64>> ") + p7bBase64);
        // FileUtil.writeBytes(Base64.encode(p7bData), outPath.toAbsolutePath().toString());
    }

}
