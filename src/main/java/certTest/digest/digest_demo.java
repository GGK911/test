package certTest.digest;

import certTest.saxon.rsa.RSAUtils;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.cms.CMSTimeStampedDataGenerator;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.HashMap;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-14 16:00
 **/
public class digest_demo {
    private static final Provider BC = new BouncyCastleProvider();

    private static byte[] bytes;
    private static final BcDigestCalculatorProvider calculatorProvider;

    static {
        String filePath = "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\cutRangeTest2\\range1BeforeAndAfter.pdf";
        filePath = "C:\\Users\\ggk911\\Desktop\\IMG_0558.JPG";

        // bytes = FileUtil.readBytes(filePath);
        bytes = Hex.decode("3045022042267ae3e5959a22d0803b2ad13aed20e042972cdbfa7042a9722f0e1cf994b40221008ed54e0948876c44127d05367847b6a7d062f9cf73b4140e802fc1cf9ea567e1");
        calculatorProvider = new BcDigestCalculatorProvider();
    }

    @SneakyThrows
    public static void main(String[] args) {
        // 颁发者固定写死
        String issuerStr = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,E=13983053455@163.com,L=重庆,ST=重庆";
        // 使用者，需自定义传参带入
        String subjectStr = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,E=13983053455@163.com,L=重庆,ST=重庆";
        // 颁发地址，后续用到什么项目，公司地址等
        String certificateCRL = "https://www.mcsca.com.cn/";
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BC);
        generator.initialize(2048);
        KeyPair keyPairRsa = generator.generateKeyPair();
        X509Certificate generateCertificateV3 = (X509Certificate) RSAUtils.generateCertificateV3(issuerStr, subjectStr, keyPairRsa, new HashMap<>(1), certificateCRL, null, 3650);

        CMSTimeStampedDataGenerator cmsTimeStampedDataGenerator = new CMSTimeStampedDataGenerator();
        // SHA-256 OID: 2.16.840.1.101.3.4.2.1
        DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA256));
        cmsTimeStampedDataGenerator.initialiseMessageImprintDigestCalculator(hashCalculator);

        hashCalculator.getOutputStream().write(generateCertificateV3.getEncoded());
        hashCalculator.getOutputStream().close();
        System.out.println("SHA256:" + Base64.toBase64String(hashCalculator.getDigest()));

        // SHA-1 OID: 1.3.14.3.2.26
        System.out.println("SHA1:" + Base64.toBase64String(calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA1)).getDigest()));

        // SHA512 OID: 2.16.840.1.101.3.4.2.3
        System.out.println("SHA512:" + Base64.toBase64String(calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA512)).getDigest()));

        // SM3 OID: 1.2.156.10197.1.401
        System.out.println("SM3:" + Base64.toBase64String(calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SM3)).getDigest()));

        // MD5 OID: 1.2.840.113549.2.5
        System.out.println("MD5:" + Base64.toBase64String(calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.MD5)).getDigest()));

    }

    @SneakyThrows
    public void digest(DigestCalculator hashCalculator) {
        hashCalculator.getOutputStream().write(bytes);
        hashCalculator.getOutputStream().close();
        System.out.println(Hex.toHexString(hashCalculator.getDigest()));
    }

    @Test
    @SneakyThrows
    public void sm3Test() {
        DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(GMObjectIdentifiers.sm3));
        digest(hashCalculator);
    }

    @Test
    @SneakyThrows
    public void md5Test() {
        DigestCalculator digestCalculator = calculatorProvider.get(new AlgorithmIdentifier(PKCSObjectIdentifiers.md5));
        digest(digestCalculator);
    }

    @Test
    @SneakyThrows
    public void sha1() {
        DigestCalculator digestCalculator = calculatorProvider.get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
        digest(digestCalculator);
    }

    @Test
    @SneakyThrows
    public void sha256() {
        DigestCalculator digestCalculator = calculatorProvider.get(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
        digest(digestCalculator);
    }

    @Test
    @SneakyThrows
    public void simpleDigest() {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(bytes);
        System.out.println(Hex.toHexString(digest.digest()));
    }
}
