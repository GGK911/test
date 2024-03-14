package certTest;

import certTest.saxon.CertUtils;
import certTest.saxon.rsa.RSAUtils;
import certTest.saxon.utils.ResultUtils;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
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
        // FileUtil.writeBytes(cert2.get("keyStoreData"), "C:\\Users\\ggk911\\Desktop\\rsa_2048.p12");
        // FileUtil.writeBytes(cert2.get("certificateData"), "C:\\Users\\ggk911\\Desktop\\rsa_2048.cer");

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

        // System.out.println("RSA P10 BASE64:" + CsrUtil.generateCsr(true, "1234"));

        // 生成RSA1024公私钥
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BC);
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        // 打印私钥
        PrivateKey privateKey = keyPair.getPrivate();
        PemFormatUtil.priKeyToPem(privateKey);
        // 打印公钥
        PublicKey publicKey = keyPair.getPublic();
        PemFormatUtil.pubKeyToPem(publicKey);

        // Subject
        String subjectParam = "CN=GGK911,OU=GGK911,O=GGK911,C=CN," + BCStyle.E + "=13983053455@163.com,L=重庆,ST=重庆";
        X500Principal subject = new X500Principal(subjectParam);

        // SHA256withRSA算法 签名者对象
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BC)
                .build(privateKey);

        // 创建 CSR
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, publicKey);
        PKCS10CertificationRequest rsaCsr = builder.build(signer);
        // 打印 CSR
        PemFormatUtil.csrToPem(rsaCsr);
        System.out.println("----------打印Base64格式CSR");
        System.out.println(Base64.toBase64String(rsaCsr.getEncoded()));

        System.out.println("//*************************************************加密-解密**********************************************************//");

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", BC);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encData = cipher.doFinal(M.getBytes(StandardCharsets.UTF_8));
        System.out.println("ENC>> "+ Hex.toHexString(encData));

        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] deData = cipher.doFinal(encData);
        System.out.println("解密>> "+new String(deData));

    }
}
