package certTest.timeStamp;

import certTest.createCert.PemUtil;
import certTest.miniCATest.MiniCATest;
import cn.hutool.core.io.FileUtil;
import com.itextpdf.text.pdf.security.TSAClient;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampResponseGenerator;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenGenerator;
import org.bouncycastle.tsp.cms.CMSTimeStampedDataGenerator;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.ujmp.core.util.Base64;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-14 13:56
 **/
public class tsp_demo extends MiniCATest {

    private static final Provider BC = new BouncyCastleProvider();
    /**
     * 1年后
     */
    private static final long CERT_EXPIRE = System.currentTimeMillis() + 1 * 1000L * 60 * 60 * 24 * 365;

    // private static final String TSA_URL = "http://127.0.0.1:8085/timestamp/ts/timestamp/rfc3161";
    private static final String TSA_URL = "http://1.12.67.126:8082/tsa/sign?type=RSA";
    
    private static final String TSP_ROOT = "src/main/java/certTest/timeStamp";

    @Test
    @SneakyThrows
    public void createTimeStampRequestTest() {
        byte[] message = "唐好凯".getBytes(StandardCharsets.UTF_8);
        // hash摘要
        CMSTimeStampedDataGenerator cmsTimeStampedDataGenerator = new CMSTimeStampedDataGenerator();
        BcDigestCalculatorProvider calculatorProvider = new BcDigestCalculatorProvider();
        // DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.MD5));
        // DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA1));
        DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA256));
        // DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA512));
        // DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SM3));
        cmsTimeStampedDataGenerator.initialiseMessageImprintDigestCalculator(hashCalculator);
        hashCalculator.getOutputStream().write(message);
        hashCalculator.getOutputStream().close();
        byte[] digest2 = hashCalculator.getDigest();
        // 请求
        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
        // TimeStampRequest request = reqGen.generate(new AlgorithmIdentifier(TSPAlgorithms.SM3), digest2);
        TimeStampRequest request = reqGen.generate(new AlgorithmIdentifier(TSPAlgorithms.SHA256), digest2);
        String tsqHex = Hex.toHexString(request.getEncoded());
        System.out.println(String.format("%-16s", "tsqHex>> ") + tsqHex);
        Path tsqOutPath = Paths.get(TSP_ROOT, "test2.tsq");
        FileUtil.writeBytes(request.getEncoded(), tsqOutPath.toFile());
    }

    @Test
    @SneakyThrows
    public void createTimeStampResponseTest() {
        Path tsqPath = Paths.get(TSP_ROOT, "test2.tsq");
        byte[] requestBytes = FileUtil.readBytes(tsqPath);
        TimeStampRequest request = new TimeStampRequest(requestBytes);
        // 序列号
        BigInteger serial = BigInteger.probablePrime(256, new Random());

        String certBase64 = "MIIDUjCCAjqgAwIBAgIJAJqM5wq22ZbpMA0GCSqGSIb3DQEBCwUAMDIxCzAJBgNVBAYTAkNOMQ8wDQYDVQQKEwZNSU5JQ0ExEjAQBgNVBAMTCVJPT1RSU0FDQTAeFw0yMjExMTMwODM0NDlaFw0yMzExMTMwODM0NDlaMCIxDzANBgNVBAYTBk1JTklDQTEPMA0GA1UEAxMGZ2drOTExMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm3Sovos0RDIKholSrRHYSXZ7g+yXd8ydt2IhNMm5UEUDyFQdJE2+JNPslq7twbWCsovBrcYDALM8g+KtSJzysqeCNC4NW8aQGPFDyuw66FppZYzTxPHhlq798BcULKS2aQFNMIK6aEGXSohRzhyf/onHSEUTIvOIT2lOy7YqaSwv6+stnWIpr49HrKRusp7PV+Fsg81J6OIeqDG4OMTmaLzcUAtrPUwvgrAcTuFHcRZSLmzcSj+crTxCet0ISYKu5YZjTKVapW2xm/Y7kbmIQcRXbpGc577WCAGd2uofzJCoQdw7NYkTj20sQ3q5J8t1Ml/lPw9QNYEArueJuCnvFQIDAQABo3sweTAdBgNVHQ4EFgQU13b4QtdRhqKypHAv5eDLL+D8K5wwHwYDVR0jBBgwFoAUCXbWaz4Ac7IoLrfrDN8vVwuITeQwCwYDVR0PBAQDAgEGMBIGA1UdEwEB/wQIMAYBAf8CAQMwFgYDVR0lAQH/BAwwCgYIKwYBBQUHAwgwDQYJKoZIhvcNAQELBQADggEBAG36+y6Zk2PEo6qfna9x7zMYYCYf9itBEUk7S1jw8qtru8GB/q/gnzVWu7eqLNmN1G2ppTgPXYFjPzR8TQh2CItlMCvllyLTMjXYIhcn3eCPMbdIUi5yL7GNCbSnq0BDhSO/nWOhcsHNuL8tihHRRYDWTJe3khROQrnOKxV5OULVc7BKE0j8F+5UncOGC/Abcefhj8nQGGmZGus6RKQbHk34ooaRVYi143BxbGH0tj/KyyaGlG/I6ntbMQ/gSjxsAs5G5CIABawPSKUTauSoelduRk6Htu1LNnu9PLtr3OfNWYCo69w9aFQKUDChxoJ9BJi6yPIyqF004F3m/nAG2hQ=";
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate generateCertificateV3 = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(Base64.decode(certBase64)));

        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT, "priKey.key").toAbsolutePath().toString());
        PrivateKey privateKey = rsaKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));

        ArrayList<X509Certificate> x509Certificates = new ArrayList<>();
        x509Certificates.add(generateCertificateV3);
        Store certs = new JcaCertStore(x509Certificates);

        BcDigestCalculatorProvider calculatorProvider = new BcDigestCalculatorProvider();
        DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA256));

        // 构造器
        TimeStampTokenGenerator tsTokenGen = new TimeStampTokenGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider(BC).build("SHA256withRSA", privateKey, generateCertificateV3), hashCalculator, new ASN1ObjectIdentifier("1.2"));
        tsTokenGen.addCertificates(certs);
        TimeStampResponseGenerator tsRespGen = new TimeStampResponseGenerator(tsTokenGen, TSPAlgorithms.ALLOWED);
        // 生成
        TimeStampResponse tsResp = tsRespGen.generate(request, serial, new Date());
        TimeStampToken timeStampToken = tsResp.getTimeStampToken();
        String tsrHex = Hex.toHexString(tsResp.getEncoded());
        System.out.println(String.format("%-16s", "tsrHex>> ") + tsrHex);
        Path tsrOutPath = Paths.get(TSP_ROOT, "test2.tsr");
        FileUtil.writeBytes(tsResp.getEncoded(), tsrOutPath.toFile());

    }

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("//*************************************************时间戳生成**********************************************************//");

        // 序列号
        BigInteger serial = BigInteger.probablePrime(256, new Random());

        String certBase64 = "MIIB7TCCAZOgAwIBAgIJAJ8oGUKFa+wqMAoGCCqBHM9VAYN1MDIxCzAJBgNVBAYTAkNOMQ8wDQYDVQQKEwZNSU5JQ0ExEjAQBgNVBAMTCVJPT1RTTTJDQTAeFw0yNDEwMTgwOTAxMjVaFw0yOTEwMTcwOTAxMjVaMCIxCzAJBgNVBAYTAkNOMRMwEQYDVQQDEwoxMC4yNTUuNy42MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAELmu1u7xsf7+FYAeTBtDd9qnBw41vhTy0N/vVwbCgu44Hy61J3zwuKq1GzXiFtO+2YM36uxNcMQ6c7mPUX5+bDaOBoTCBnjAdBgNVHQ4EFgQUIhkgc6L9EUKhoRzN8zitoudZlIwwHwYDVR0jBBgwFoAU4BGGHGMDIY0chgEm2/th7W1A0+owCwYDVR0PBAQDAgEGMBIGA1UdEwEB/wQIMAYBAf8CAQMwOwYDVR0RBDQwMoIMd3d3Lm1zY2EuY29tggltY3NjYS5jb22CCG1zY2EuY29tgg13d3cubWNzY2EuY29tMAoGCCqBHM9VAYN1A0gAMEUCIQCc5t1U1cIq7SHr+DRm9Bgi1QKqJwt9mrM8NzGTKTf0JwIgPbZWktES0n8QOuN3m0SnZR/VIe82PrdMRfjJMiU/lLg=";
        CertificateFactory cf = CertificateFactory.getInstance("X.509", BC);
        X509Certificate generateCertificateV3 = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(Base64.decode(certBase64)));

        PEMKeyPair pemKeyPair = (PEMKeyPair) PemUtil.objectFromFile(Paths.get(ROOT, "sm2PriKey.key").toAbsolutePath().toString());
        PrivateKey privateKey = sm2KeyFactory.generatePrivate(new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
        
        // hash摘要
        CMSTimeStampedDataGenerator cmsTimeStampedDataGenerator = new CMSTimeStampedDataGenerator();
        BcDigestCalculatorProvider calculatorProvider = new BcDigestCalculatorProvider();
        // DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.MD5));
        // DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA1));
        DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA256));
        // DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA512));
        // DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SM3));
        cmsTimeStampedDataGenerator.initialiseMessageImprintDigestCalculator(hashCalculator);
        hashCalculator.getOutputStream().write(generateCertificateV3.getEncoded());
        hashCalculator.getOutputStream().close();
        byte[] digest2 = hashCalculator.getDigest();
        ArrayList<X509Certificate> x509Certificates = new ArrayList<>();
        x509Certificates.add(generateCertificateV3);
        Store certs = new JcaCertStore(x509Certificates);
        // 请求
        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
        // TimeStampRequest request = reqGen.generate(new AlgorithmIdentifier(TSPAlgorithms.SM3), digest2);
        TimeStampRequest request = reqGen.generate(new AlgorithmIdentifier(TSPAlgorithms.SHA256), digest2);
        Path tsqOutPath = Paths.get(TSP_ROOT, "test2.tsq");
        FileUtil.writeBytes(request.getEncoded(), tsqOutPath.toFile());
        // 构造器
        TimeStampTokenGenerator tsTokenGen = new TimeStampTokenGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider(BC).build("SHA256withRSA", privateKey, generateCertificateV3), hashCalculator, new ASN1ObjectIdentifier("1.2"));
        tsTokenGen.addCertificates(certs);
        TimeStampResponseGenerator tsRespGen = new TimeStampResponseGenerator(tsTokenGen, TSPAlgorithms.ALLOWED);
        // 生成
        TimeStampResponse tsResp = tsRespGen.generate(request, serial, new Date());
        TimeStampToken timeStampToken = tsResp.getTimeStampToken();
        Path tsrOutPath = Paths.get(TSP_ROOT, "test2.tsq");
        FileUtil.writeBytes(tsResp.getEncoded(), tsrOutPath.toFile());

        System.out.println("//*************************************************URL请求时间戳**********************************************************//");

        TSAClient tsaClient = new TSAClientExtend(TSA_URL, null, null);
        byte[] timeStampToken2 = tsaClient.getTimeStampToken(request.getEncoded());
        System.out.println(new DERBitString(timeStampToken2));

        System.out.println("//*************************************************时间戳验证**********************************************************//");

        // CMSTimeStampedData cmsTimeStampedData = cmsTimeStampedDataGenerator.generate(timeStampToken, generateCertificateV3.getEncoded());
        // byte[] timeStampedData = cmsTimeStampedData.getEncoded();
        // // verify
        // DigestCalculatorProvider newCalculatorProvider = new BcDigestCalculatorProvider();
        // DigestCalculator imprintCalculator = cmsTimeStampedData.getMessageImprintDigestCalculator(newCalculatorProvider);
        // CMSTimeStampedData newCMSTimeStampedData = new CMSTimeStampedData(timeStampedData);
        // byte[] newContent = newCMSTimeStampedData.getContent();
        // // 是否相等
        // imprintCalculator.getOutputStream().write(newContent);
        // byte[] digest = imprintCalculator.getDigest();
        // TimeStampToken[] tokens = cmsTimeStampedData.getTimeStampTokens();
        // for (TimeStampToken token : tokens) {
        //     cmsTimeStampedData.validate(newCalculatorProvider, digest, token);
        // }
    }

}
