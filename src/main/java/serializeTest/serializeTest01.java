package serializeTest;

import certTest.PemFormatUtil;
import certTest.saxon.sm2.CertificateUtils;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import javax.security.auth.x500.X500Principal;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author TangHaoKai
 * @version V1.0 2023/12/25 11:54
 **/
public class serializeTest01 {

    private static final Provider BC = new BouncyCastleProvider();

    // 国密推荐曲线
    public static BigInteger gx = new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
    public static BigInteger gy = new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);

    // 密码参数对象
    public static SM2P256V1Curve curve = new SM2P256V1Curve();

    public static BigInteger n = curve.getOrder();
    public static BigInteger h = curve.getCofactor();

    // 通过国密推荐曲线计算出g点
    public static ECPoint point = curve.createPoint(gx, gy);

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("//*************************************************SM2-CSR生成**********************************************************//");

        // SM2椭圆曲线参数
        ECDomainParameters domainParameters = new ECDomainParameters(curve, point, n, h);

        ECParameterSpec parameterSpec = new ECParameterSpec(domainParameters.getCurve(), domainParameters.getG(), domainParameters.getN(), domainParameters.getH());

        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", BC);
        generator.initialize(parameterSpec);
        KeyPair keyPair = generator.generateKeyPair();
        // 打印私钥
        PrivateKey privateKey = keyPair.getPrivate();
        PemFormatUtil.priKeyToPem(privateKey);
        // 打印公钥
        PemFormatUtil.pubKeyToPem(keyPair.getPublic());

        // Subject
        String subjectParam = "CN=GGK911,OU=GGK911,O=GGK911,C=CN," + BCStyle.E + "=13983053455@163.com,L=重庆,ST=重庆";
        X500Principal subject = new X500Principal(subjectParam);
        // SHA256withRSA算法 签名者对象
        ContentSigner signer = new JcaContentSignerBuilder("SM3withSm2")
                .setProvider(BC)
                .build(privateKey);

        // 创建 CSR
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());

        //
        DERPrintableString password = new DERPrintableString("secret123");
        builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, password);

        PKCS10CertificationRequest sm2Csr = builder.build(signer);
        // 打印 CSR
        PemFormatUtil.csrToPem(sm2Csr);
        System.out.println("----------打印Base64格式CSR");
        System.out.println(Base64.toBase64String(sm2Csr.getEncoded()));

        System.out.println("//*************************************************SM2-CSR签发证书**********************************************************//");

        Certificate cert = CertificateUtils.makeCertificate(sm2Csr.getEncoded(), 365);

        System.out.println(Base64.toBase64String(cert.getEncoded()));

        // System.out.println("//*************************************************SM2证书序列化**********************************************************//");
        //
        // String filePath = "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\serializeTest\\test.cer";
        // FileOutputStream certOut = new FileOutputStream(filePath);
        // try (ObjectOutputStream oos = new ObjectOutputStream(certOut)) {
        //     oos.writeObject(cert);
        // }
    }

    @Test
    @SneakyThrows
    public void testDeSer() {
        System.out.println("//*************************************************SM2证书反序列化**********************************************************//");

        String filePath = "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\serializeTest\\test.cer";
        FileInputStream certIn = new FileInputStream(filePath);
        try (ObjectInputStream ois = new ObjectInputStream(certIn)) {
            System.out.println(Arrays.stream(Security.getProviders()).map(Provider::getName).collect(Collectors.joining(",")));
            Security.removeProvider("SUN");
            Security.addProvider(new BouncyCastleProvider());
            Object o = ois.readObject();
            System.out.println(Arrays.stream(Security.getProviders()).map(Provider::getName).collect(Collectors.joining(",")));
        }
    }
}
