package certTest;

import certTest.bouncycastle.SHA256DigestCalculator;
import certTest.saxon.rsa.RSAUtils;
import cn.com.mcsca.bouncycastle.util.CollectionStore;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.Lists;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampResponseGenerator;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenGenerator;
import org.bouncycastle.tsp.cms.CMSTimeStampedData;
import org.bouncycastle.tsp.cms.CMSTimeStampedDataGenerator;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-14 13:56
 **/
public class tsp_demo {

    private static final String M = "ggk911_RSA_demo";

    private static final Provider BC = new BouncyCastleProvider();
    /**
     * 1年后
     */
    private static final long CERT_EXPIRE = System.currentTimeMillis() + 1 * 1000L * 60 * 60 * 24 * 365;

    private static final String PWD = "123456";

    private static final String TSA_URL = "http://127.0.0.1:8084/timestamp/ts/timestamp/rfc3161";

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("//*************************************************时间戳生成**********************************************************//");

        // 颁发者固定写死
        String issuerStr = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,E=13983053455@163.com,L=重庆,ST=重庆";
        // 使用者，需自定义传参带入
        String subjectStr = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,E=13983053455@163.com,L=重庆,ST=重庆";
        // 颁发地址，后续用到什么项目，公司地址等
        String certificateCRL = "https://www.mcsca.com.cn/";
        BigInteger serial = BigInteger.probablePrime(256, new Random());


        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BC);
        generator.initialize(2048);
        KeyPair keyPairRsa = generator.generateKeyPair();
        X509Certificate generateCertificateV3 = (X509Certificate) RSAUtils.generateCertificateV3(issuerStr, subjectStr, keyPairRsa, new HashMap<>(1), certificateCRL, null, 3650);

        // hash摘要
        CMSTimeStampedDataGenerator cmsTimeStampedDataGenerator = new CMSTimeStampedDataGenerator();
        BcDigestCalculatorProvider calculatorProvider = new BcDigestCalculatorProvider();
        DigestCalculator hashCalculator = calculatorProvider.get(new AlgorithmIdentifier(TSPAlgorithms.SHA256));
        cmsTimeStampedDataGenerator.initialiseMessageImprintDigestCalculator(hashCalculator);
        hashCalculator.getOutputStream().write(generateCertificateV3.getEncoded());
        hashCalculator.getOutputStream().close();
        byte[] digest2 = hashCalculator.getDigest();
        ArrayList<X509Certificate> x509Certificates = new ArrayList<>();
        x509Certificates.add(generateCertificateV3);
        Store certs = new JcaCertStore(x509Certificates);
        // 请求
        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
        TimeStampRequest request = reqGen.generate(NISTObjectIdentifiers.id_sha256, digest2);
        FileUtil.writeBytes(request.getEncoded(), "C:\\Users\\ggk911\\Desktop\\test.req");
        // 构造器
        TimeStampTokenGenerator tsTokenGen = new TimeStampTokenGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider(BC).build("SHA256withRSA", keyPairRsa.getPrivate(), generateCertificateV3), hashCalculator, new ASN1ObjectIdentifier("1.2"));
        tsTokenGen.addCertificates(certs);
        TimeStampResponseGenerator tsRespGen = new TimeStampResponseGenerator(tsTokenGen, TSPAlgorithms.ALLOWED);
        // 生成
        TimeStampResponse tsResp = tsRespGen.generate(request, serial, new Date());
        TimeStampToken timeStampToken = tsResp.getTimeStampToken();

        System.out.println("//*************************************************URL请求时间戳**********************************************************//");

        TSAClient tsaClient = new TSAClientExtend(TSA_URL, null, null);
        byte[] timeStampToken2 = tsaClient.getTimeStampToken(request.getEncoded());

        System.out.println("//*************************************************时间戳验证**********************************************************//");

        CMSTimeStampedData cmsTimeStampedData = cmsTimeStampedDataGenerator.generate(timeStampToken, generateCertificateV3.getEncoded());
        byte[] timeStampedData = cmsTimeStampedData.getEncoded();
        // verify
        DigestCalculatorProvider newCalculatorProvider = new BcDigestCalculatorProvider();
        DigestCalculator imprintCalculator = cmsTimeStampedData.getMessageImprintDigestCalculator(newCalculatorProvider);
        CMSTimeStampedData newCMSTimeStampedData = new CMSTimeStampedData(timeStampedData);
        byte[] newContent = newCMSTimeStampedData.getContent();
        // 是否相等
        imprintCalculator.getOutputStream().write(newContent);
        byte[] digest = imprintCalculator.getDigest();
        TimeStampToken[] tokens = cmsTimeStampedData.getTimeStampTokens();
        for (TimeStampToken token : tokens) {
            cmsTimeStampedData.validate(newCalculatorProvider, digest, token);
        }



    }
}
