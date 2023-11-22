package certTest.saxon.rsa;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/***
 * RSA证书生成工具类
 * @author saxon
 *
 */
public class RSAUtils {

    /***
     * 密钥对 生成器
     * @param certNum 证书位数
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static KeyPair getKey(int certNum) throws NoSuchAlgorithmException {
        // 密钥对 生成器，RSA算法 生成的  提供者是 BouncyCastle
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
        // 密钥长度 1024
        generator.initialize(certNum);
        // 证书中的密钥 公钥和私钥
        KeyPair keyPair = generator.generateKeyPair();
        return keyPair;
    }

    /**
     * RSA证书生成
     *
     * @param password       密码
     * @param issuerStr      颁发机构信息
     * @param subjectStr     使用者信息
     * @param certificateCRL 颁发地址
     * @param certExpire     证书有效期 （单位天）
     * @param certNum        证书位数：一般1024或2048
     * @return
     */
    public static Map<String, byte[]> createCert(String password, String issuerStr, String subjectStr, String certificateCRL, long certExpire, int certNum) {

        Map<String, byte[]> result = new HashMap<>();
        ByteArrayOutputStream out = null;
        try {
            //  生成JKS证书
            //  KeyStore keyStore = KeyStore.getInstance("JKS");
            //  标志生成PKCS12证书
            KeyStore keyStore = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
            keyStore.load(null, null);
            KeyPair keyPair = getKey(certNum);
            //  issuer与 subject相同的证书就是CA证书
            Certificate cert = generateCertificateV3(issuerStr, subjectStr, keyPair, result, certificateCRL, null, certExpire);
            // cretkey随便写，标识别名
            keyStore.setKeyEntry("知录API信息分享中心", keyPair.getPrivate(), password.toCharArray(), new Certificate[]{cert});
            out = new ByteArrayOutputStream();
            cert.verify(keyPair.getPublic());
            keyStore.store(out, password.toCharArray());
            byte[] keyStoreData = out.toByteArray();
            result.put("keyStoreData", keyStoreData);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    /**
     * @param issuerStr      颁发机构信息
     * @param subjectStr     使用者信息
     * @param keyPair        密钥对
     * @param result
     * @param certificateCRL 颁发地址
     * @param extensions
     * @return
     */
    public static Certificate generateCertificateV3(String issuerStr, String subjectStr, KeyPair keyPair, Map<String, byte[]> result, String certificateCRL, List<Extension> extensions, long certExpire) {

        ByteArrayInputStream bout = null;
        X509Certificate cert = null;
        try {
            System.out.println("公钥：" + keyPair.getPublic());
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            Date notBefore = new Date();

            certExpire = 1L * certExpire * 24 * 60 * 60 * 1000;
            // Calendar rightNow = Calendar.getInstance();
            // rightNow.setTime(notBefore);
            // // 日期加1年
            // rightNow.add(Calendar.YEAR, 1);
            // Date notAfter = rightNow.getTime();
            Date notAfter = new Date(System.currentTimeMillis() + certExpire);
            // 证书序列号
            BigInteger serial = BigInteger.probablePrime(256, new Random());
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(new X500Name(issuerStr), serial, notBefore, notAfter, new X500Name(subjectStr), publicKey);
            // SHA256withRSA
            JcaContentSignerBuilder jBuilder = new JcaContentSignerBuilder("SHA1withRSA");
            SecureRandom secureRandom = new SecureRandom();
            jBuilder.setSecureRandom(secureRandom);
            // 私钥签名
            ContentSigner singer = jBuilder.setProvider(new BouncyCastleProvider()).build(privateKey);
            // CRL分发点
            ASN1ObjectIdentifier cRLDistributionPoints = new ASN1ObjectIdentifier("2.5.29.31");
            GeneralName generalName = new GeneralName(GeneralName.uniformResourceIdentifier, certificateCRL);
            GeneralNames seneralNames = new GeneralNames(generalName);
            DistributionPointName distributionPoint = new DistributionPointName(seneralNames);
            DistributionPoint[] points = new DistributionPoint[1];
            points[0] = new DistributionPoint(distributionPoint, null, null);
            CRLDistPoint cRLDistPoint = new CRLDistPoint(points);
            builder.addExtension(cRLDistributionPoints, true, cRLDistPoint);
            // 用途
            ASN1ObjectIdentifier keyUsage = new ASN1ObjectIdentifier("1.3.14.3.2.26");
            // | KeyUsage.nonRepudiation | KeyUsage.keyCertSign
            builder.addExtension(keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
            if (false) {
                // 基本限制 X509Extension.java
                ASN1ObjectIdentifier basicConstraints = new ASN1ObjectIdentifier("2.5.29.19");
                builder.addExtension(basicConstraints, true, new BasicConstraints(true));
            } else {
                // 时间戳
                builder.addExtension(org.bouncycastle.asn1.x509.Extension.extendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_timeStamping));
            }
            // privKey:使用自己的私钥进行签名，CA证书
            if (extensions != null) {
                for (Extension ext : extensions) {
                    builder.addExtension(
                            new ASN1ObjectIdentifier(ext.getOid()),
                            ext.isCritical(),
                            ASN1Primitive.fromByteArray(ext.getValue()));
                }
            }
            X509CertificateHolder holder = builder.build(singer);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            bout = new ByteArrayInputStream(holder.toASN1Structure().getEncoded());
            cert = (X509Certificate) cf.generateCertificate(bout);
            byte[] certBuf = holder.getEncoded();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // 证书数据
            result.put("certificateData", certBuf);
            //公钥
            result.put("publicKey", publicKey.getEncoded());
            //私钥
            result.put("privateKey", privateKey.getEncoded());
            //证书有效开始时间
            result.put("notBefore", format.format(notBefore).getBytes(StandardCharsets.UTF_8));
            //证书有效结束时间
            result.put("notAfter", format.format(notAfter).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bout != null) {
                try {
                    bout.close();
                } catch (IOException e) {
                }
            }
        }
        return cert;
    }

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // CN: 名字与姓氏    OU : 组织单位名称
        // O ：组织名称  L : 城市或区域名称  E : 电子邮件
        // ST: 州或省份名称  C: 单位的两字母国家代码

        //颁发者固定写死
        String issuerStr = "CN=ZHONGLZ,OU=ZHONGLZ,O=知录API,C=CN,E=1260649342@qq.com,L=长沙,ST=湖南";
        //使用者，需自定义传参带入
        String subjectStr = "CN=zhonglz,OU=Zhonglz,O=知录API,C=CN,E=1260649342@qq.com,L=长沙,ST=湖南";
        //颁发地址，后续用到什么项目，公司地址等
        String certificateCRL = "https://gitee.com/zhong-zhuang";
        //证书密码
        String certPasswrod = "123456";
        //证书有效期 365天
        long certExpire = 365;
        //证书位数
        int certNum = 2048;
        Map<String, byte[]> result = createCert(certPasswrod, issuerStr, subjectStr, certificateCRL, certExpire, certNum);
        //以下生成两种格式证书
        FileOutputStream outPutStream = new FileOutputStream("C:\\Users\\ggk911\\Desktop\\test5.p12"); // p12 私钥证书
        outPutStream.write(result.get("keyStoreData"));
        outPutStream.close();

        FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\ggk911\\Desktop\\test5.cer"));//cer公钥证书
        fos.write(result.get("certificateData"));
        fos.flush();
        fos.close();
    }

}
