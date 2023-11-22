package certTest;

import certTest.saxon.CertUtils;
import certTest.saxon.rsa.RSAUtils;
import certTest.saxon.utils.ResultUtils;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.security.Provider;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-14 13:54
 **/
public class rsa_demo {

    private static final String M = "ggk911_RSA_demo";

    private static final Provider BC = new BouncyCastleProvider();
    /**
     * 1年后
     */
    private static final long CERT_EXPIRE = System.currentTimeMillis() + 1 * 1000L * 60 * 60 * 24 * 365;

    private static final String PWD = "123456";

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("//*************************************************封装的RSA工具类**********************************************************//");

        // 颁发者固定写死
        String issuerStr = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,E=13983053455@163.com,L=重庆,ST=重庆";
        // 使用者，需自定义传参带入
        String subjectStr = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,E=13983053455@163.com,L=重庆,ST=重庆";
        // 颁发地址，后续用到什么项目，公司地址等
        String certificateCRL = "https://www.mcsca.com.cn/";

        Map<String, byte[]> cert2 = RSAUtils.createCert(PWD, issuerStr, subjectStr, certificateCRL, 365, 2048);
        FileUtil.writeBytes(cert2.get("keyStoreData"), "C:\\Users\\ggk911\\Desktop\\rsa_2048.p12");
        FileUtil.writeBytes(cert2.get("certificateData"), "C:\\Users\\ggk911\\Desktop\\rsa_2048.cer");

        //*RSA签名
        ResultUtils signRSACert = CertUtils.signRSACert(cert2.get("keyStoreData"), M, PWD);
        Map<String, Object> result = (Map<String, Object>) signRSACert.getObject();
        System.out.println("RSA签名值：" + result.get("signData"));
        System.out.println("RSA公钥Base64：" + Base64.toBase64String((byte[]) result.get("publicKey")));

        //*RSA验签
        ResultUtils verifyRSACert = CertUtils.verifyRSACert(cert2.get("keyStoreData"), PWD, M, (String) result.get("signData"));
        Map<String, Object> result2 = (Map<String, Object>) verifyRSACert.getObject();
        System.out.println("RSA验签结果:" + result2.get("verifyResult"));

        System.out.println("//*************************************************RSA证书反序列化**********************************************************//");

        X509CertificateHolder certHldr = new X509CertificateHolder(cert2.get("certificateData"));
        SubjectPublicKeyInfo k = certHldr.getSubjectPublicKeyInfo();
        CertificateFactory fact = CertificateFactory.getInstance("X.509", BC);
        X509Certificate cert3 = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(cert2.get("certificateData")));
        System.out.println(cert3.getIssuerDN().toString());

        System.out.println("//*************************************************RSA-CSR生成**********************************************************//");

        System.out.println("RSA P10 BASE64:" + CsrUtil.generateCsr(true, "1234"));


    }
}
