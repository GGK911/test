package certTest;

import certTest.crypto.SM2Util;
import certTest.saxon.CertUtils;
import certTest.saxon.sm2.Sm2Utils;
import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.com.mcsca.util.CertUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.signers.PlainDSAEncoding;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECKeyUtil;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

/**
 * sm2测试
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-09 15:12
 **/
public class sm2_demo {

    private static final String M = "ggk911_SM2_demo";

    private static final Provider BC = new BouncyCastleProvider();
    /**
     * 1年后
     */
    private static final long CERT_EXPIRE = System.currentTimeMillis() + 1 * 1000L * 60 * 60 * 24 * 365;

    private static final String PWD = "123456";
    private static final PublicKey publicKey;
    private static final PrivateKey privateKey;
    private static final KeyPair keyPair;

    private static final CertificateFactory certificateFactory;

    static {
        //加载BC
        Security.addProvider(BC);
        // 获取SM2椭圆曲线的参数
        ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");
        // 获取一个椭圆曲线类型的密钥对生成器
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("EC", BC);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 使用SM2参数初始化生成器
        try {
            assert kpg != null;
            kpg.initialize(sm2Spec);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        // 获取密钥对
        keyPair = kpg.generateKeyPair();
        publicKey = keyPair.getPublic();
        // System.out.println(publicKey.toString());
        // System.out.println(Base64.toBase64String(publicKey.getEncoded()));
        privateKey = keyPair.getPrivate();
        // System.out.println(privateKey.toString());
        // System.out.println(Base64.toBase64String(privateKey.getEncoded()));
        try {
            certificateFactory = CertificateFactory.getInstance("X.509", BC);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }


    @SneakyThrows
    public static void main(String[] args) {

        SM2Util sm2 = new SM2Util();

        System.out.println("原文：" + M);
        String publicKeyBase64 = Base64.toBase64String(publicKey.getEncoded());
        System.out.println("公钥Base64：" + publicKeyBase64);
        String privateKeyBase64 = Base64.toBase64String(privateKey.getEncoded());
        System.out.println("密钥Base64：" + privateKeyBase64);

        System.out.println("公钥Hex：" + Hex.toHexString(publicKey.getEncoded()));
        System.out.println("私钥Hex：" + Hex.toHexString(privateKey.getEncoded()));

        System.out.println("//*************************************************加密-解密**********************************************************//");

        String data = sm2.encrypt(publicKey, M);
        System.out.println("加密数据：" + data);
        String text = sm2.decrypt(privateKey, data);
        System.out.println("解密数据：" + text);

        System.out.println("//*************************************************签名-验签**********************************************************//");

        // 生成SM2sign with sm3 签名验签算法实例
        Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), BC);
        signature.initSign(privateKey);
        signature.update(M.getBytes(StandardCharsets.UTF_8));
        byte[] sign = signature.sign();
        System.out.println("SM2sign with sm3 签名值Base64:" + Base64.toBase64String(sign));

        signature.initVerify(publicKey);
        signature.update(M.getBytes(StandardCharsets.UTF_8));
        boolean verify = signature.verify(sign);
        System.out.println("SM2sign with sm3 验签结果:" + verify);

        System.out.println("//*************************************************Blowfish**********************************************************//");

        KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
        keyGenerator.init(64);
        SecretKey key = keyGenerator.generateKey();
        System.out.println(Base64.toBase64String(key.getEncoded()));

        System.out.println("//*************************************************密钥对的反序列化**********************************************************//");

        byte[] encPub = Base64.decode(publicKeyBase64);
        byte[] encPri = Base64.decode(privateKeyBase64);

        KeyFactory keyFact = KeyFactory.getInstance("EC", BC);
        // 根据采用的编码结构反序列化公私钥
        PublicKey pub = keyFact.generatePublic(new X509EncodedKeySpec(encPub));
        System.out.println(pub.toString());
        PrivateKey pri = keyFact.generatePrivate(new PKCS8EncodedKeySpec(encPri));
        Signature signature2 = Signature.getInstance("SM3withSm2", BC);
        signature2.initVerify(pub);
        signature2.update(M.getBytes(StandardCharsets.UTF_8));
        boolean verify2 = signature2.verify(sign);
        System.out.println("反序列化SM2sign with sm3 验签结果:" + verify2);

        System.out.println("//*************************************************SM2证书**********************************************************//");

        ContentSigner sigGen = new JcaContentSignerBuilder("SM3withSM2")
                .setProvider(BC)
                // .build(keyPair.getPrivate());
                .build(privateKey);

        //*构造标识信息构造（DN）
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        // 国家代码
        builder.addRDN(BCStyle.C, "CN");
        // 组织
        builder.addRDN(BCStyle.O, "dlyd");
        // 省份
        builder.addRDN(BCStyle.ST, "Chongqing");
        // 地区
        builder.addRDN(BCStyle.L, "Chongqing");
        // 邮箱
        builder.addRDN(BCStyle.E, "13983053455@163.com");

        //*获取扩展密钥用途构造（可选）
        // 构造容器对象
        ASN1EncodableVector vector = new ASN1EncodableVector();
        // 客户端身份认证
        vector.add(KeyPurposeId.id_kp_clientAuth);
        // 安全电子邮件
        vector.add(KeyPurposeId.id_kp_emailProtection);

        //*构造X.509 第3版的证书构建者
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                // 颁发者信息
                builder.build()
                // 证书序列号
                , BigInteger.valueOf(1)
                // 证书生效日期
                , new Date(System.currentTimeMillis())
                // 证书失效日期
                , new Date(CERT_EXPIRE)
                // 使用者信息（PS：由于是自签证书，所以颁发者和使用者DN都相同）
                , builder.build()
                // 证书公钥
                // , keyPair.getPublic())
                , publicKey)
                /*
                设置证书扩展
                证书扩展属性，请根据需求设定，参数请参考 《RFC 5280》
                 */
                // 设置密钥用法
                .addExtension(Extension.keyUsage, false, new X509KeyUsage(X509KeyUsage.digitalSignature | X509KeyUsage.nonRepudiation))
                // 设置扩展密钥用法：客户端身份认证、安全电子邮件
                .addExtension(Extension.extendedKeyUsage, false, new DERSequence(vector))
                // 基础约束,标识是否是CA证书，这里false标识为实体证书
                .addExtension(Extension.basicConstraints, false, new BasicConstraints(false))
                // Netscape Cert Type SSL客户端身份认证
                .addExtension(MiscObjectIdentifiers.netscapeCertType, false, new NetscapeCertType(NetscapeCertType.sslClient));

        //*将证书构造参数装换为X.509证书对象
        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider(BC)
                .getCertificate(certGen.build(sigGen));

        //*保存证书
        // 获取ASN.1编码的证书字节码
        byte[] asn1BinCert = certificate.getEncoded();
        // 编码为BASE64 便于传输
        byte[] base64EncodedCert = Base64.encode(asn1BinCert);
        // FileUtil.writeBytes(base64EncodedCert, "C:\\Users\\ggk911\\Desktop\\SM2DEMO证书.cer");
        System.out.println("sm2证书>> " + new String(base64EncodedCert));

        System.out.println("//*************************************************HuTool-SM2**********************************************************//");

        System.out.println("超长密钥对");
        KeyPair sm2Pair = SecureUtil.generateKeyPair("SM2");
        SM2 sm22 = SmUtil.sm2(sm2Pair.getPrivate(), sm2Pair.getPublic());
        System.out.println("SM2私钥HEX:" + HexUtil.encodeHexStr(sm2Pair.getPrivate().getEncoded()));
        System.out.println("SM2公钥HEX:" + HexUtil.encodeHexStr(sm2Pair.getPublic().getEncoded()));
        byte[] sign2 = sm22.sign(M.getBytes(StandardCharsets.UTF_8));
        System.out.println("SM2签名值Base64:" + Base64.toBase64String(sign2));
        boolean verify3 = sm22.verify(M.getBytes(StandardCharsets.UTF_8), sign2);
        System.out.println("SM2验签结果:" + verify3);

        System.out.println("标准长度密钥对");
        SM2 sm24 = SmUtil.sm2();
        //这里会自动生成对应的随机秘钥对 , 注意！ 这里一定要强转，才能得到对应有效的秘钥信息
        byte[] privateKey2 = BCUtil.encodeECPrivateKey(sm24.getPrivateKey());
        //这里公钥不压缩  公钥的第一个字节用于表示是否压缩  可以不要
        byte[] publicKey2 = ((BCECPublicKey) sm24.getPublicKey()).getQ().getEncoded(false);
        System.out.println("SM2私钥HEX: " + HexUtil.encodeHexStr(privateKey2));
        System.out.println("SM2公钥HEX: " + HexUtil.encodeHexStr(publicKey2));
        byte[] sign4 = sm24.sign(M.getBytes(StandardCharsets.UTF_8));
        System.out.println("SM2签名值HEX:" + HexUtil.encodeHexStr(sign4));
        boolean verify4 = sm24.verify(M.getBytes(StandardCharsets.UTF_8), sign4);
        System.out.println("SM2验签结果:" + verify4);

        System.out.println("指定私钥签名");
        ECPrivateKeyParameters privateKeyParameters = BCUtil.toSm2Params(HexUtil.encodeHexStr(privateKey2));
        SM2 sm23 = new SM2(privateKeyParameters, null);
        sm23.usePlainEncoding();
        sm23.setMode(SM2Engine.Mode.C1C2C3);
        byte[] sign3 = sm23.sign(M.getBytes(StandardCharsets.UTF_8));
        System.out.println("SM2签名值HEX:" + HexUtil.encodeHexStr(sign3));

        System.out.println("指定公钥验签");
        String publicKeyHex = HexUtil.encodeHexStr(publicKey2);
        if (publicKeyHex.length() == 130) {
            //这里需要去掉开始第一个字节 第一个字节表示标记
            publicKeyHex = publicKeyHex.substring(2);
        }
        String xhex = publicKeyHex.substring(0, 64);
        String yhex = publicKeyHex.substring(64, 128);
        ECPublicKeyParameters ecPublicKeyParameters = BCUtil.toSm2Params(xhex, yhex);
        //创建sm2 对象
        SM2 sm25 = new SM2(null, ecPublicKeyParameters);
        //这里需要手动设置，sm2 对象的默认值与我们期望的不一致 , 使用明文编码
        sm25.usePlainEncoding();
        sm25.setMode(SM2Engine.Mode.C1C2C3);
        boolean verify5 = sm25.verify(M.getBytes(StandardCharsets.UTF_8), HexUtil.decodeHex(HexUtil.encodeHexStr(sign3)));
        System.out.println("SM2验签结果:" + verify5);


        System.out.println("//*************************************************封装的SM2工具类**********************************************************//");

        KeyPair keyPair2 = Sm2Utils.generateKeyPair();
        System.out.println(keyPair2.getPrivate());
        System.out.println(keyPair2.getPublic());
        Map<String, byte[]> cert = CertUtils.createSM2CertToOne("1", "1", "1", "1", "1", "1", 365, PWD);

        // FileUtil.writeBytes(cert.get("certData"), "C:\\Users\\ggk911\\Desktop\\SM2证书pfx.pfx");
        // FileUtil.writeBytes(Base64.encode(cert.get("publicKey")), "C:\\Users\\ggk911\\Desktop\\SM2证书pubkey.key");
        // FileUtil.writeBytes(Base64.encode(cert.get("privateKey")), "C:\\Users\\ggk911\\Desktop\\SM2证书prikey.key");
        // FileUtil.writeBytes(Base64.encode(cert.get("cer")), "C:\\Users\\ggk911\\Desktop\\SM2证书cer.cer");

        System.out.println("//*************************************************pfx转jks**********************************************************//");

        char[] passwd = PWD.toCharArray();
        ByteArrayInputStream fs = new ByteArrayInputStream(cert.get("certData"));
        // 使用BC初始化KeyStore
        KeyStore store = KeyStore.getInstance("PKCS12", BC);
        // 从给定的输入流加载密钥库
        store.load(fs, passwd);
        fs.close();
        // BC不提供对于JKS的KeyStore，直接初始化KeyStore
        KeyStore outputKeyStore = KeyStore.getInstance("JKS");
        outputKeyStore.load(null, passwd);

        // 证书写入
        Enumeration<String> enumas = store.aliases();
        while (enumas.hasMoreElements()) {
            String keyAlias = enumas.nextElement();
            System.out.println("alias=[" + keyAlias + "]");
            if (store.isKeyEntry(keyAlias)) {
                // 获取私钥
                Key key3 = store.getKey(keyAlias, passwd);
                Certificate[] certChain = store.getCertificateChain(keyAlias);
                outputKeyStore.setKeyEntry(keyAlias, key3, passwd, certChain);
            }
        }
        // FileOutputStream out = new FileOutputStream("C:\\Users\\ggk911\\Desktop\\SM2证书jks.jks");
        // outputKeyStore.store(out, passwd);
        // out.close();

        System.out.println("//*************************************************core工具密钥对生成**********************************************************//");

        SecuEngine secuEngine = new SecuEngine();
        String pri22 = secuEngine.GenKey(2, 256);
        KeyFactory kf = KeyFactory.getInstance("EC", BC);

        pri22 = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIFVHQit4CxN1xEssh7zqPAK0NhE+KGIiZRKH+tKiMPNYoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        //初始化sm2加密
        BCECPrivateKey privateKey22 = (BCECPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(pri22)));
        System.out.println("privateKey22>> " + Base64.toBase64String(privateKey22.getEncoded()));

        System.out.println("//*************************************************私钥生成公钥**********************************************************//");

        ECPoint publicPoint = privateKey22.getParameters().getG().multiply(privateKey22.getD());
        // 使用SM2算法创建EC公钥规范
        // ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(publicPoint, ECUtil.getDomainParameters(BouncyCastleProvider.CONFIGURATION, privateKey22.getParameters()));
        BCECPublicKey publicKey22 = (BCECPublicKey) kf.generatePublic(new ECPublicKeySpec(publicPoint, privateKey22.getParameters()));
        byte[] Q = new byte[64];
        byte[] x = publicKey22.getQ().getRawXCoord().getEncoded();
        byte[] y = publicKey22.getQ().getRawYCoord().getEncoded();
        System.arraycopy(x, 0, Q, 0, x.length);
        System.arraycopy(y, 0, Q, 32, y.length);
        System.out.println("publicKey22>> " + Base64.toBase64String(Q));

        System.out.println("//*************************************************证书反序列化**********************************************************//");

        String test01 = "MIICxDCCAmigAwIBAgIQFsXYtLT0AtkQ8Vhf0nQ5PDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwNDE3MDgyNjU2WhcNMjUwNDE3MDgyNjU2WjB3MQswCQYDVQQGEwJDTjENMAsGA1UECgwEdGVzdDEQMA4GA1UECwwHbG9jYWxSQTEVMBMGA1UEBQwMdGVzdDg4OTQ3ODgyMTAwLgYDVQQDDCcxNzY1MjI1MjIzMDA0NzAwNjcyQHRlc3Q5MTIyNTYwNUAwMUAwMDEwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAATuSL3yBH4qoqbNjPTi8IZVPHlbVvm1fuHJUAKpyoyj0XuZGo/gwpOvC7EjnWp6XY2R7git4hJMG5Tv/FihdJ4Ro4IBHDCCARgwHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwHQYDVR0OBBYEFEld9e+6/DxyouXRNOY9vC5QAQ2KMAwGA1UdEwQFMAMBAQAwCwYDVR0PBAQDAgTwMIG6BgNVHR8EgbIwga8wLqAsoCqGKGh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMC5jcmwwfaB7oHmGd2xkYXA6Ly93d3cubWNzY2EuY29tLmNuOjM4OS9DTj1jcmwwLE9VPUNSTCxPPU1DU0NBLEM9Q04/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MAwGCCqBHM9VAYN1BQADSAAwRQIhAOi6xaeBj09b4Ee/3p1XP/s6zgE60kYCPvzCIgHHvFYMAiAkM5Tax4D1o+h9ExOwuUPhpWUKcTrlQn9nfXu9aqU14g==";
        CertificateFactory factory = CertificateFactory.getInstance("X.509", BC);
        Certificate certificate1 = factory.generateCertificate(new ByteArrayInputStream(Base64.decode(test01)));
        System.out.println(certificate1);

        System.out.println("//*************************************************PEM-PARSE**********************************************************//");

        String PEMBase64 = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDdTCCAl2gAwIBAgIBAjANBgkqhkiG9w0BAQsFADBAMQswCQYDVQQGEwJDTjEP\n" +
                "MA0GA1UEChMGR0dLOTExMQ8wDQYDVQQLEwZHR0s5MTExDzANBgNVBAMTBkdHSzkx\n" +
                "MTAeFw0yNDA0MTIwNDAwMjlaFw0yOTA0MTEwNDAwMjlaMGQxCzAJBgNVBAYTAkNO\n" +
                "MQ4wDAYDVQQKDAVDaGluYTENMAsGA1UECwwERExZRDESMBAGA1UEAwwJVGltZVN0\n" +
                "YW1wMSIwIAYJKoZIhvcNAQkBFhMxMzk4MzA1MzQ1NUAxNjMuY29tMIIBIjANBgkq\n" +
                "hkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqzsrjWJYu3evWWrwHYpIGqD6U4Tgf+sl\n" +
                "lJ+Iq5SLl3DYm7z6LUNzFD5capnXzYmx20C/jg5TRY+VDNrGCupne4vYAqn58ecC\n" +
                "gCYWfkvXbQEjLrShvoXyLHfTHvmnanIchbiJscbg9DpDmWM3SqT5zy1CZJLfQxSh\n" +
                "2f6lIn02XnTJF5jKrZ9TDooYLCUDWDVYbd/X2SNpyybVsAPTw24Eb+hrrO9t5SXA\n" +
                "bN85ycZRdyho/RPX9OwqLhkba5hlUxRmpW9tvxqrA3+LsnOcduwMtHyIU//cFpl9\n" +
                "GydbwbPDp4KVSYI3CV70ZCH59CGSRZKWFz40ucY10LHNxssUdbnN+wIDAQABo1Yw\n" +
                "VDAdBgNVHQ4EFgQU4gh3nyNkTlGuCYd3r73LILYB7KEwHwYDVR0jBBgwFoAU+Eop\n" +
                "EhChxDzBCm2yL4V3UHB9dPQwEgYDVR0TAQH/BAgwBgEB/wIBADANBgkqhkiG9w0B\n" +
                "AQsFAAOCAQEAKQ1TeBgsmWPW0VkQL3J1499SJdAN4kaav88qHLeK6FPZ89kWZ591\n" +
                "pfJh9J67uU/OzF9U2KBeeNbNXluUnX8GY+QZz8PUzFKoh3gpW9FCpDl7lAbuJUtC\n" +
                "aIOg3iTlZ+uhO7pOQhWdVugUCBtC5OzP5LX6spnfhmLheu6bXyq5bvyuLty51qUC\n" +
                "lJZ6MWEpKxbn0te8KFs+cMKwaliyydoYo7OGd8BJjLIPlIfhy4sipY1mFEb8SNSW\n" +
                "AxtBCzIvpwxCnUeeSrk2+frtacLm8vXi/wP/Y74fs6ILhNfr8Xp1aQix2c7I8st0\n" +
                "uC1t2ZEfnNs+ZXcsAUUuBejnE+WnVFNuUg==\n" +
                "-----END CERTIFICATE-----\n";
        PEMParser pemParser = new PEMParser(new StringReader(PEMBase64));
        X509CertificateHolder pemToObj = (X509CertificateHolder) pemParser.readObject();
        System.out.println(pemToObj.getSubject().toString());

        System.out.println("//*************************************************PARSE-PRIVATE-FROM-d**********************************************************//");

        String dHex = "9bc80070025bfbb69af775e2cb6aa656ab2d71493392ae4df2ec949404105b6c";
        dHex = Hex.toHexString(Base64.decode("YVg6ANDQdeZK/+tA+7DdaJgiac7wJTAGrS3UytvgweU="));
        dHex = Hex.toHexString(Base64.decode("EKcqeNufxodDNOl6Nv5RbSw5GrKe+KwXlpkeHgITZlU="));
        dHex = Hex.toHexString(Base64.decode("teSBMBQiWjE0xHI8xAB4wxFsFxSzR0R1RsKhg5M/PbY="));
        dHex = "5547422b780b1375c44b2c87bcea3c02b436113e286222651287fad2a230f358";
        dHex = "32ecf90216b997b83fb00ab838b1dbf985d714c5e95451a9f2e8a43c6d5c9e6d";
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        ECParameterSpec ecSpec = new ECParameterSpec(
                sm2Spec.getCurve(),
                sm2Spec.getG(),
                sm2Spec.getN(),
                sm2Spec.getH());
        KeyFactory fact = KeyFactory.getInstance("ECDSA", BC);
        BigInteger d1 = new BigInteger(1, Hex.decode(dHex));
        PrivateKey priFromD = fact.generatePrivate(new ECPrivateKeySpec(d1, ecSpec));
        System.out.println("priFromD>> " + Base64.toBase64String(priFromD.getEncoded()));

        System.out.println("//*************************************************PARSE-PRIVATE-FROM-d-2**********************************************************//");

        BigInteger d2 = new BigInteger(1, Hex.decode(dHex));
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(d2, ecSpec);
        BCECPrivateKey ecPriFromD = new BCECPrivateKey("EC", ecPrivateKeySpec, BouncyCastleProvider.CONFIGURATION);
        System.out.println("ecPriFromD>> " + Base64.toBase64String(ecPriFromD.getEncoded()));

        System.out.println("//*************************************************GEN-PUBLIC-FROM-d**********************************************************//");

        // Q = G * d
        ECPoint pointQ = ecSpec.getG().multiply(d2).normalize();
        ECParameterSpec parameters = ecPriFromD.getParameters();
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(parameters.getCurve().createPoint(pointQ.getXCoord().toBigInteger(), pointQ.getYCoord().toBigInteger()), parameters);
        BCECPublicKey bcecPublicKey = new BCECPublicKey("EC", ecPublicKeySpec, BouncyCastleProvider.CONFIGURATION);
        System.out.println("bcecPublicKey>> " + Base64.toBase64String(bcecPublicKey.getEncoded()));
        System.out.println("bcecPublicKeyQ>> " + Hex.toHexString(bcecPublicKey.getQ().getXCoord().toBigInteger().toByteArray()) + Hex.toHexString(bcecPublicKey.getQ().getYCoord().toBigInteger().toByteArray()));
        System.out.println("//*************************************************GEN-PUBLIC-FROM-Q**********************************************************//");

        // Q
        String xy = "RkoqC/RZjmedSuMUmlQ/y4TTmeYTHHyT1okM0KW67wkn/ys/VmXRnuFIzy6thS98Xk4m/+JgCVa/NDnUz8J5hQ==";
        // xy = "D4qaPtOzxfoMhPH6K2CUofwrtnXTWHMob2RzWWRnM7/XZBMUUDC1ZvkjtIuZ4gxwZRwoFguVc/JWvuxP93cw1A==";
        // 64
        byte[] xyBytes = Base64.decode(xy);
        byte[] xBytes = new byte[32];
        System.arraycopy(xyBytes, 0, xBytes, 0, xBytes.length);
        byte[] yBytes = new byte[32];
        System.arraycopy(xyBytes, xBytes.length, yBytes, 0, yBytes.length);
        BigInteger bigIntegerX = new BigInteger(1, xBytes);
        BigInteger bigIntegerY = new BigInteger(1, yBytes);

        ECPublicKeySpec ecPublicKeySpec2 = new ECPublicKeySpec(ecSpec.getCurve().createPoint(bigIntegerX, bigIntegerY), ecSpec);
        BCECPublicKey bcecPublicKey2 = new BCECPublicKey("EC", ecPublicKeySpec2, BouncyCastleProvider.CONFIGURATION);
        System.out.println("bcecPublicKey2>> " + Base64.toBase64String(bcecPublicKey2.getEncoded()));

        System.out.println("//*************************************************CURVE-PARAM-PEM-TO-NAME-PEM**********************************************************//");

        PrivateKey curveParamPriKey = ECKeyUtil.privateToExplicitParameters(privateKey22, BC);
        System.out.println("curveParamPriKey>> " + Base64.toBase64String(curveParamPriKey.getEncoded()));

        System.out.println("//*************************************************GET-d-FROM-PRIVATEKEY**********************************************************//");
        // 私钥
        KeyFactory keyFactory = KeyFactory.getInstance("EC", BC);
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode("MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg7f5pgWLzFfHLzLaZR/XAQgbpK0lRW59mRtRZFE8txd+gCgYIKoEcz1UBgi2hRANCAAQqCzDg2KJShM+67RqsKHsTrZ2AJx2cA+8EAjDaw05u/AjcGgPIfdE8BYPsUxuhSWTa+oo2I7S0887Su3fuc7hk")));
        BCECPrivateKey bcecPrivateKeyGetD = (BCECPrivateKey) privateKey;
        System.out.println("GET-d-FROM-PRIVATEKEY>> " + Hex.toHexString(privateKey22.getD().toByteArray()));

        System.out.println("//*************************************************GET-Q-FROM-PUBLICKEY**********************************************************//");

        String pubKeyBase64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEKgsw4NiiUoTPuu0arCh7E62dgCcdnAPvBAIw2sNObvwI3BoDyH3RPAWD7FMboUlk2vqKNiO0tPPO0rt37nO4ZA==";
        BCECPublicKey getQFromPublicKey = (BCECPublicKey) keyFact.generatePublic(new X509EncodedKeySpec(Base64.decode(pubKeyBase64)));
        ECPoint q = getQFromPublicKey.getQ();
        System.out.println("GET-Q-FROM-PUBLICKEY-X>> " + Hex.toHexString(removeLeadingZero(q.getXCoord().toBigInteger().toByteArray())));
        System.out.println("GET-Q-FROM-PUBLICKEY-Y>> " + Hex.toHexString(removeLeadingZero(q.getYCoord().toBigInteger().toByteArray())));

        System.out.println("//*************************************************GET-PUB-FROM-CERT**********************************************************//");

        String sm2CertBase64 = "MIICtDCCAlegAwIBAgIQIhSQf3SHYb/5k5qymYnxDTAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwNjI0MDcwNTU5WhcNMjUwNjI0MDcwNTU5WjB0MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExCzAJBgNVBAUMAjExMTYwNAYDVQQDDC0xNDgzOTg3NzQ2MjIwNTAzMDQwQOato+iBlOaUr+S7mOa1i+ivlUAwMkAwMDIwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASnnwwaCb+QtBdEqeKlElMs4gaOmpxXYY+MGmzRRp0sHwS6XSQPUWOOzYUifWs3aMu5BS2pL/x2GY2TjxIXNTSbo4IBDjCCAQowHwYDVR0jBBgwFoAUMIj+P2KgJfTzKz2pbaagTAtMyrUwHQYDVR0OBBYEFKvasfOLDNewQJGVrRnIDdUnR1qJMAsGA1UdDwQEAwIE8DCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDEuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMSxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAMBggqgRzPVQGDdQUAA0kAMEYCIQCJV0TdoczObFpo8IgnzKGBe21d2h3rqvFp+Ob0UiuD8AIhAOy02cA4f44xWQnW+tn+myesLCScxQvfz0BNlH9zYA2B";
        sm2CertBase64 = "MIIDGTCCArygAwIBAgIQU4/TmvMYAZcOPZz3wIphrjAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwODE2MDYwNTI2WhcNMjkwODE1MDYwNTI2WjCBkDELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBI5MTQ0MDQwMDU2OTIwMzQ1MUExQjBABgNVBAMMOTE4MTgxMDY4NTc1MzQ1NTQxMTJA6K+B6IGU5pSv5LuY5pyJ6ZmQ6LSj5Lu75YWs5Y+4QDAyQDAwMTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABCQOXTzNudXUBt6Af+3VOUrCGYdFZMym8Oha76vK54pM2L5NNcuXi+yhb3X22OlM5CbAOJBaJSAEoMfoCCmZBvKjggFWMIIBUjAfBgNVHSMEGDAWgBS5I+Ycf8+3PFgkW6QKrYoRl3p7tjAdBgNVHQ4EFgQUkOUgj7S9IHg3keZGmsC6InCaUDMwRAYDVR0gBD0wOzA5BgcqgRyHhAsCMC4wLAYIKwYBBQUHAgEWIGh0dHBzOi8vd3d3Lm1jc2NhLmNvbS5jbi9jcHMuaHRtMAsGA1UdDwQEAwIE8DCBvAYDVR0fBIG0MIGxMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDEuY3JsMH+gfaB7hnlsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjoxMDM4OS9DTj1jcmwxLE9VPUNSTCxPPU1DU0NBLEM9Q04/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MAwGCCqBHM9VAYN1BQADSQAwRgIhAK+Mb7iD8Ezy4jcJcCdLLs2E8xXGCD/PJi0IznOjcvADAiEApJDWOv58v7pucy2rfqH8SzqA13+uMvmEkzz3A7txegk=";
        X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decode(sm2CertBase64)));
        PublicKey getPubKeyFromCert = x509Certificate.getPublicKey();
        System.out.println("getPubKeyFromCert>> " + Base64.toBase64String(getPubKeyFromCert.getEncoded()));

    }

    /**
     * 给bigInteger去掉前导零
     *
     * @param bytes toBigInteger().toByteArray()
     * @return 去掉前导零
     */
    private static byte[] removeLeadingZero(byte[] bytes) {
        if (bytes.length > 1 && bytes[0] == 0) {
            byte[] result = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, result, 0, result.length);
            return result;
        }
        return bytes;
    }

    @Test
    @SneakyThrows
    public void sm2Encrypt() {
        String str = "62>076=<8>2?4=:9";
        str = "73=429>21=847:48";
        str = "唐好凯";
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        String servicePub = "MIIBMzCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBA0IABEZKKgv0WY5nnUrjFJpUP8uE05nmExx8k9aJDNCluu8JJ/8rP1Zl0Z7hSM8urYUvfF5OJv/iYAlWvzQ51M/CeYU=";
        servicePub = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAERkoqC/RZjmedSuMUmlQ/y4TTmeYTHHyT1okM0KW67wkn/ys/VmXRnuFIzy6thS98Xk4m/+JgCVa/NDnUz8J5hQ==";
        // servicePub = "MIIBMzCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBA0IABA+Kmj7Ts8X6DITx+itglKH8K7Z101hzKG9kc1lkZzO/12QTFFAwtWb5I7SLmeIMcGUcKBYLlXPyVr7sT/d3MNQ=";
        servicePub = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEXwowESkNJvIUvVJFcXnKCjeLw+HTiVhg+kTiSWZ1EsK0LhHv2IYA0Y3DkcsGePD4L10MltWrn9WSnCrarCyvBA==";
        // keynum1
        servicePub = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEqBnJ6CL1bSZFksT3skIcizpOfI2LOuTQxSMpKmA8eNgbeWv+8uSKOrBMbxt8ehrVNJG8HTjQABJI32F1B0sqLQ==";
        // keynum2
        servicePub = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEz1znOnEnicHusf67UuI37VqYWHD1+jK4Kv4qBGBgW7EQHF5tscA9jp4uf69CUsnxWrB2WOnIyyymhz1uR4okBQ==";
        KeyFactory keyFactory = KeyFactory.getInstance("EC", new BouncyCastleProvider());
        BCECPublicKey publicKey = (BCECPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(servicePub)));
        PublicKey publicKey1 = ECKeyUtil.publicToExplicitParameters(publicKey, BC);
        System.out.println(Base64.toBase64String(publicKey1.getEncoded()));

        SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
        ECPublicKeyParameters key = (ECPublicKeyParameters) PublicKeyFactory.createKey(publicKey.getEncoded());
        ParametersWithRandom parameters = new ParametersWithRandom(key, new SecureRandom());
        sm2Engine.init(true, parameters);
        byte[] encrypt = sm2Engine.processBlock(bytes, 0, bytes.length);
        System.out.println("不带结构的密文Base64>> " + Base64.toBase64String(encrypt));
        System.out.println("不带结构的密文HEX>> " + Hex.toHexString(encrypt));
        System.out.println("encrypt加上结构>> " + addEncryptAsn1Struct(Hex.toHexString(encrypt)));
    }

    @Test
    @SneakyThrows
    public void sm2Decrypt() {
        // YVg6ANDQdeZK/+tA+7DdaJgiac7wJTAGrS3UytvgweU=
        // 系统私钥
        String pri = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIGFYOgDQ0HXmSv/rQPuw3WiYImnO8CUwBq0t1Mrb4MHloIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        // pri = Base64.toBase64String(Hex.decode("308192020100301306072A8648CE3D020106082A811CCF5501822D04783076020101042061583A00D0D075E64AFFEB40FBB0DD68982269CEF0253006AD2DD4CADBE0C1E5A00A06082A811CCF5501822DA143034100464A2A0BF4598E679D4AE3149A543FCB84D399E6131C7C93D6890CD0A5BAEF0927FF2B3F5665D19EE148CF2EAD852F7C5E4E26FFE2600956BF3439D4CFC27985"));
        // pri = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIBCnKnjbn8aHQzTpejb+UW0sORqynvisF5aZHh4CE2ZVoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        // 被加密的通讯密钥
        String encStr = "vhobk9SUAmAZwI0UG1D5g4sEP0va93mHrXNJaOnBH1yVM2V6g+7oqz+TEM6UAuHASNmcpd5QWFoTH1NjMe+SmKZqrs4UKWz7Eo4dWf2yQJMzjKPn1wpPb8Cy94Ui5o8GwUSdsITFmy5dyWzLYYBpBg==";

        pri = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgx3r/c0ObfrPT6WJOvWQt6JVxOi4NZDB+i/x/ZBIzs2mgCgYIKoEcz1UBgi2hRANCAARfCjARKQ0m8hS9UkVxecoKN4vD4dOJWGD6ROJJZnUSwrQuEe/YhgDRjcORywZ48PgvXQyW1auf1ZKcKtqsLK8E";
        encStr = "BIl3tUPJYJNqhmS6B+YFLDXjWtNGOX2xaqP8XfRr9UGFjqmaVCcpdr3v+ABhDjonmMj+LFk2pimh+zCYPlaMtf8bzUUu+hsTVuiDgbXq8Ew5+YW6XB5CYKf2CizQFqo+cTIyMjIyMjIyMv6fg8q8uLAF12rTvCzB9zu9LvrUYBw+AYkWz4stmiXM";

        pri = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIDLs+QIWuZe4P7AKuDix2/mF1xTF6VRRqfLopDxtXJ5toIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        encStr = "BK5lVsCPsUpDE5/COut8Bo23tsy12w2KTKHiakhQlhqWVFK647FbhDWIedG7OJTkq6w/Nvtm7OMuZExI06bo86kAiJ0keUc0N23Pp+Z80wMtO8KN+DhOCtgAaVFn6b3AweAPDz69C9wWSA==";

        byte[] encBytes = Base64.decode(encStr);
        KeyFactory keyFact = KeyFactory.getInstance("EC", BC);
        BCECPrivateKey privateKey1 = (BCECPrivateKey) keyFact.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(pri)));
        System.out.println("D>> " + Base64.toBase64String(privateKey1.getD().toByteArray()));

        // SM2Engine engine = new SM2Engine(SM2Engine.Mode.C1C2C3);
        SM2Engine engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(privateKey1.getEncoded());
        engine.init(false, privateKey);
        byte[] decryptBytes = engine.processBlock(encBytes, 0, encBytes.length);
        System.out.println(new String(decryptBytes));
    }

    /**
     * 给密文加上Asn1结构
     *
     * @param sm2Encrypt 不带结构的密文
     * @return 密文
     */
    public static String addEncryptAsn1Struct(String sm2Encrypt) throws IOException {
        if (sm2Encrypt.startsWith("04")) {
            String xCoord = sm2Encrypt.substring(2, 2 + 64);
            String yCoord = sm2Encrypt.substring(2 + 64, 2 + 64 + 64);
            String c3 = sm2Encrypt.substring(2 + 64 + 64, 2 + 64 + 64 + 64);
            String c2 = sm2Encrypt.substring(2 + 64 + 64 + 64);
            // logger.info("xCoord>> " + xCoord);
            BigInteger xCoordbigInteger = new BigInteger(1, Hex.decode(xCoord));
            ASN1Integer xCoordAsn1 = new ASN1Integer(xCoordbigInteger);
            // logger.info("yCoord>> " + yCoord);
            BigInteger yCoordbigInteger = new BigInteger(1, Hex.decode(yCoord));
            ASN1Integer yCoordAsn1 = new ASN1Integer(yCoordbigInteger);


            ASN1OctetString xCoordOctetAsn1 = new DEROctetString(Hex.decode(xCoord));
            ASN1OctetString yCoordOctetAsn1 = new DEROctetString(Hex.decode(yCoord));
            ASN1OctetString c3Asn1 = new DEROctetString(Hex.decode(c3));
            ASN1OctetString c2Asn1 = new DEROctetString(Hex.decode(c2));
            // 将两个 ASN1Integer 对象加入 ASN1EncodableVector
            ASN1EncodableVector vector = new ASN1EncodableVector();
            vector.add(xCoordOctetAsn1);
            vector.add(yCoordOctetAsn1);
            vector.add(c3Asn1);
            vector.add(c2Asn1);
            // 创建 ASN1Sequence
            ASN1Sequence sequence = new DERSequence(vector);
            // 编码为字节数组
            byte[] encoded = sequence.getEncoded();
            return Base64.toBase64String(encoded);

        }
        return null;
    }

    @Test
    @SneakyThrows
    public void sm2Sign() {
        byte[] message = "abc".getBytes(StandardCharsets.UTF_8);
        // message = Hex.decode("66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0");

        System.out.println("messageHEX>> " + Hex.toHexString(message));
        KeyFactory keyFact = KeyFactory.getInstance("EC", BC);
        BCECPrivateKey privateKey = (BCECPrivateKey) keyFact.generatePrivate(new PKCS8EncodedKeySpec(Hex.decode("308193020100301306072a8648ce3d020106082a811ccf5501822d0479307702010104202b3818d2c18042571bf1c5036633c57a8f7db85e3594c2a034c7b0d56bba8193a00a06082a811ccf5501822da1440342000403c97f54590b52fbd3d4e7beebf6e8b777e774c908ebf845875bf254bdbfdef0daa317e184ee64f65712f1b6d5bb98ae4c9371e43364791441d7712ee79e7f9a")));
        privateKey = parsePrivateFromD();
        // privateKey = (BCECPrivateKey) sm2_demo.privateKey;

        ECParameterSpec parameterSpec = privateKey.getParameters();
        ECDomainParameters domainParameters = new ECDomainParameters(parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH());

        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(privateKey.getD(), domainParameters);
        ParametersWithID parametersWithIDPri = new ParametersWithID(ecPrivateKeyParameters, Hex.decodeStrict("31323334353637383132333435363738"));

        SM2Signer signer = new SM2Signer(StandardDSAEncoding.INSTANCE);
        // SM2Signer signer = new SM2Signer(PlainDSAEncoding.INSTANCE);
        signer.init(true, ecPrivateKeyParameters);
        signer.update(message, 0, message.length);
        byte[] signBytes = signer.generateSignature();
        System.out.println("signBase64>> " + Base64.toBase64String(signBytes));
        System.out.println("signHex>> " + Hex.toHexString(signBytes));
    }

    @Test
    @SneakyThrows
    public void sm2VerifySign() {
        // 从P10拿公钥
        KeyFactory keyFact = KeyFactory.getInstance("EC", BC);
        // 协同SM2一代
        String P10 = "MIIBGTCBxQIBATBlMRIwEAYDVQQDDAnlp5rnq5HngpwxDzANBgNVBAsMBui3r+i+vjEPMA0GA1UECgwG5rWL6K+VMQ8wDQYDVQQHDAbph43luoYxDzANBgNVBAgMBumHjeW6hjELMAkGA1UEBgwCQ04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASZKEMsO06VxxvX6XsMegzBRuOWtEC6/VGnyV8hYcwDbXyO1is5miU3EADoyGKNacIMwe33eahDZOzSQn0fsp22MAwGCCqBHM9VAYN1BQADQQAENuwDT4Q6TDgYWjPPGy2yY/yGlOK2b5KoGPLqOgiW2/B0sO3OGYMQd0ceSDpyTcf4bMJoHD4R1ucYFth8EtyW";
        // 自己生成1
        // P10 = "MIIBQjCB6QIBADCBhjEPMA0GA1UECAwG6YeN5bqGMQ8wDQYDVQQHDAbph43luoYxIjAgBgkqhkiG9w0BCQEWEzEzOTgzMDUzNDU1QDE2My5jb20xCzAJBgNVBAYTAkNOMQ8wDQYDVQQKEwZHR0s5MTExDzANBgNVBAsTBkdHSzkxMTEPMA0GA1UEAxMGR0dLOTExMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE9jdVHgMaJmGbdmHpAjjZX9w5LQaZc4sQwCdAeImpPquatiLRuOIg/49lSGdUk9urydKLlLzcDWO0lXJqSNVauaAAMAoGCCqBHM9VAYN1A0gAMEUCIE7q8pezVi8gRbx4oaisHkio3pJlfcnywKfIg6qxVQLQAiEA0vUXx0pbS3Eu2Bz0f/QIYG44p0efZn/x/2uWTmj3IAA=";
        PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(Base64.decode(P10));
        SubjectPublicKeyInfo subjectPublicKeyInfo = pkcs10CertificationRequest.getSubjectPublicKeyInfo();
        BCECPublicKey p10Pub = (BCECPublicKey) keyFact.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));

        // 从公钥证书拿公钥
        String publicKeyCert = "MIICtDCCAlegAwIBAgIQIhSQf3SHYb/5k5qymYnxDTAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwNjI0MDcwNTU5WhcNMjUwNjI0MDcwNTU5WjB0MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExCzAJBgNVBAUMAjExMTYwNAYDVQQDDC0xNDgzOTg3NzQ2MjIwNTAzMDQwQOato+iBlOaUr+S7mOa1i+ivlUAwMkAwMDIwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASnnwwaCb+QtBdEqeKlElMs4gaOmpxXYY+MGmzRRp0sHwS6XSQPUWOOzYUifWs3aMu5BS2pL/x2GY2TjxIXNTSbo4IBDjCCAQowHwYDVR0jBBgwFoAUMIj+P2KgJfTzKz2pbaagTAtMyrUwHQYDVR0OBBYEFKvasfOLDNewQJGVrRnIDdUnR1qJMAsGA1UdDwQEAwIE8DCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDEuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMSxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAMBggqgRzPVQGDdQUAA0kAMEYCIQCJV0TdoczObFpo8IgnzKGBe21d2h3rqvFp+Ob0UiuD8AIhAOy02cA4f44xWQnW+tn+myesLCScxQvfz0BNlH9zYA2B";
        // 非常特殊的证书，证书签名用的sm2，公钥却是1024rsa
        publicKeyCert = "MIIDgDCCAySgAwIBAgIQODWjAOYJB82tkCe1wUW+PzAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwODE2MDIwNjUyWhcNMjUwODE2MDIwNjUyWjB5MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGTAXBgNVBAUMEDExMTExMTExMTExMTExMTExLTArBgNVBAMMJE1DU0NBQOa1i+ivlTA2QDAxMTExMTExMTExMTExMTExQDAwNzCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAv93mgtcabhmIcpgVLLZ7cLHOlLFbbN6/oVsMphVqyoUrWux/zlpbP+dsgnrdgrGwidoceoPa/5CubeEXMNSQuCuC3sIU3ncfEyv2T7BmxYzIzEwy1VRZArv6jouIZmGavgIz+6P5f7ZsH0pHS1th0DZjVYRCHGsrT2/fBxhsyp8CAwEAAaOCAY8wggGLMIG6BgNVHR8EgbIwga8wLqAsoCqGKGh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMC5jcmwwfaB7oHmGd2xkYXA6Ly93d3cubWNzY2EuY29tLmNuOjM4OS9DTj1jcmwwLE9VPUNSTCxPPU1DU0NBLEM9Q04/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MAwGA1UdEwQFMAMBAQAwCwYDVR0PBAQDAgbAMBMGA1UdJQQMMAoGCCsGAQUFBwMCMB0GA1UdDgQWBBQJFyKw7Q1V7HyzxOVQSI6WuBCMMDBcBggrBgEFBQcBAQRQME4wKAYIKwYBBQUHMAKGHGh0dHA6Ly8xMjcuMC4wLjEvY2Fpc3N1ZS5odG0wIgYIKwYBBQUHMAGGFmh0dHA6Ly8xMjcuMC4wLjE6MjA0NDMwHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwDAYIKoEcz1UBg3UFAANIADBFAiEAy1OwPE8hZ++AWbwE74I9IH7GSCRWVEFrSBqKohVVbJgCIFkR26tAH4zqchL1eCPcoRZLKqfZuGLsWWkg/2D/AIpr";
        CertificateFactory certFact = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());
        X509Certificate certificate = (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(Base64.decode(publicKeyCert)));
        p10Pub = (BCECPublicKey) certificate.getPublicKey();

        // keyNum2
        String pubKeyStr = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEz1znOnEnicHusf67UuI37VqYWHD1+jK4Kv4qBGBgW7EQHF5tscA9jp4uf69CUsnxWrB2WOnIyyymhz1uR4okBQ==";
        // keyNum1
        // pubKeyStr = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEqBnJ6CL1bSZFksT3skIcizpOfI2LOuTQxSMpKmA8eNgbeWv+8uSKOrBMbxt8ehrVNJG8HTjQABJI32F1B0sqLQ==";
        // pubKeyStr = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEA8l/VFkLUvvT1Oe+6/bot3fndMkI6/hFh1vyVL2/3vDaoxfhhO5k9lcS8bbVu5iuTJNx5DNkeRRB13Eu555/mg==";
        pubKeyStr = Base64.toBase64String(Hex.decode("308201333081ec06072a8648ce3d02013081e0020101302c06072a8648ce3d0101022100fffffffeffffffffffffffffffffffffffffffff00000000ffffffffffffffff30440420fffffffeffffffffffffffffffffffffffffffff00000000fffffffffffffffc042028e9fa9e9d9f5e344d5a9e4bcf6509a7f39789f515ab8f92ddbcbd414d940e9304410432c4ae2c1f1981195f9904466a39c9948fe30bbff2660be1715a4589334c74c7bc3736a2f4f6779c59bdcee36b692153d0a9877cc62a474002df32e52139f0a0022100fffffffeffffffffffffffffffffffff7203df6b21c6052b53bbf40939d541230201010342000462f5ee006fef1198f7da5803c2051f71f75716ee9727491b3e45bb79d89f1d85894e011bca8b34289be619cacc6ea756078d7a5d4854aaec4d7f3a08343a963e"));
        KeyFactory keyFactory = KeyFactory.getInstance("EC", BC);
        // p10Pub = (BCECPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(pubKeyStr)));
        // p10Pub = (BCECPublicKey) sm2_demo.publicKey;
        // System.out.println("p10PubHex>> " + Hex.toHexString(p10Pub.getEncoded()));

        ECParameterSpec parameterSpec = p10Pub.getParameters();
        ECDomainParameters domainParameters = new ECDomainParameters(parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH());
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(p10Pub.getQ(), domainParameters);
        byte[] ID = new byte[]{49, 50, 51, 52, 53, 54, 55, 56, 49, 50, 51, 52, 53, 54, 55, 56};
        ParametersWithID parametersWithIDPub = new ParametersWithID(publicKeyParameters, ID);

        // 协同SM2一代
        byte[] message = Hex.decode("3081C502010130653112301006035504030C09E5A79AE7AB91E7829C310F300D060355040B0C06E8B7AFE8BEBE310F300D060355040A0C06E6B58BE8AF95310F300D06035504070C06E9878DE5BA86310F300D06035504080C06E9878DE5BA86310B300906035504060C02434E3059301306072A8648CE3D020106082A811CCF5501822D034200049928432C3B4E95C71BD7E97B0C7A0CC146E396B440BAFD51A7C95F2161CC036D7C8ED62B399A25371000E8C8628D69C20CC1EDF779A84364ECD2427D1FB29DB6");
        byte[] signBytes = Hex.decode("0436EC034F843A4C38185A33CF1B2DB263FC8694E2B66F92A818F2EA3A0896DB" + "F074B0EDCE19831077471E483A724DC7F86CC2681C3E11D6E71816D87C12DC96");
        // 文件
        // message = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\IMG_0558.JPG");
        // System.out.println("文件>> " + Hex.toHexString(message));
        message = Hex.decode("c72bcf78390dbfff4248a854a7fab7cded77af6329bac5ed9057c9815bc16c99");
        signBytes = Hex.decode("53576F3CC9162783323D4C196B53FD6993A9B57F5349849AA2A21F882FCEF80A3478BEC49F6082B85BB98957A88D0D3AB4F2D17930045E57EE459F8B3D6AC228");

        message = Hex.decode("31323334353637383132333435363738");
        signBytes = Hex.decode("5727183483A1269E374259CEFF8372F6A7D32D22A13020A8B77BFF2C6A6322634334B5CEDED98D8C0F02654ACB0ACB96B768FA1BE4A1E9C641DCA4789B51B6EC");

        message = "{\"context\":{\"transNo\":\"123456\",\"authType\":[\"00\",\"01\"],\"transType\":[\"00\",\"01\"],\"appID\":\"002\",\"isBase64\":null,\"devices\":null}}".getBytes(StandardCharsets.UTF_8);
        signBytes = Hex.decode("0F818D15AFADA4A972B5CCE03269C2233ADB06A340DED8304EB7218D3086D54D19AC352FB168091157610191D82A67132774C839C41BF47B4C1031155C673F60");

        message = "abc".getBytes(StandardCharsets.UTF_8);
        signBytes = Hex.decode("304502201c373af8eeb163f6a4b3064f410b7ce9adae21f6b96668e6ab4562b05fd3ff980221008e31aa93b644547337a604737e55d5c7aa4bf5f4f9eb5480d57e05828c297206");

        signBytes = Hex.decode("d877de0409000b115332bd0b3682b6a497cf155f2a7da8d20ba60f182f672bdc7efba56e531a34899833fd8ce985628aa66e1504d4bbe38475cf2c260238e301");

        // message = "1234567812345678".getBytes(StandardCharsets.UTF_8);
        // signBytes = Hex.decode("9aabbe12d1cf895163d01a1f294945256b18bc7f72046b8127e80b23d3d7cc714b87a203d704b029f675d89a5fe9f0fe8b2ca4fb7413b1689cab796a6d9d48ac");

        // byte[] signBytes = Hex.decode("30450220" + "0436EC034F843A4C38185A33CF1B2DB263FC8694E2B66F92A818F2EA3A0896DB" + "022100" + "F074B0EDCE19831077471E483A724DC7F86CC2681C3E11D6E71816D87C12DC96");
        // 自己生成1
        // message = Hex.decode("3081E9020100308186310F300D06035504080C06E9878DE5BA86310F300D06035504070C06E9878DE5BA863122302006092A864886F70D01090116133133393833303533343535403136332E636F6D310B300906035504061302434E310F300D060355040A130647474B393131310F300D060355040B130647474B393131310F300D0603550403130647474B3931313059301306072A8648CE3D020106082A811CCF5501822D03420004F637551E031A26619B7661E90238D95FDC392D0699738B10C027407889A93EAB9AB622D1B8E220FF8F6548675493DBABC9D28B94BCDC0D63B495726A48D55AB9A000");
        // signBytes = Hex.decode("30450220"+"4EEAF297B3562F2045BC78A1A8AC1E48A8DE92657DC9F2C0A7C883AAB15502D0"+"022100"+ "D2F517C74A5B4B712ED81CF47FF408606E38A7479F667FF1FF6B964E68F72000");
        message = Hex.decode("3082027fa00302010202104725300f48dc66bfddcb49be712654ff300c06082a811ccf550183750500302d310b300906035504061302434e310e300c060355040a0c054d43534341310e300c06035504030c054d43534341301e170d3233303830343038333533315a170d3233303830353038333533315a308199310b300906035504061302434e310e300c060355040a0c054d435343413110300e060355040b0c076c6f63616c5241311b301906035504050c1239313133303438314d413046594c4c57394a314b304906035504030c423133303534343231373339373336333530373240e6ada6e5ae89e5b882e6b7bce991abe992bbe4ba95e5b7a5e7a88be69c89e99990e585ace58fb8403032403030323059301306072a8648ce3d020106082a811ccf5501822d034200041ab32615df2183bbe840180553b6b5745ef911ece75002547b1b59d0893767742664fbee8f34ea47e9d145aa5baef6e0ca5ebdf91217df103bc3707d00fec9d0a38201103082010c301f0603551d23041830168014b923e61c7fcfb73c58245ba40aad8a11977a7bb6301d0603551d0e041604144df62fd8016a98b646f909f9f8236f5eb6c36b1e300b0603551d0f0404030204f03081bc0603551d1f0481b43081b1302ea02ca02a8628687474703a2f2f7777772e6d637363612e636f6d2e636e2f736d322f63726c2f63726c312e63726c307fa07da07b86796c6461703a2f2f7777772e6d637363612e636f6d2e636e3a31303338392f434e3d63726c312c4f553d43524c2c4f3d4d435343412c433d434e3f63657274696669636174655265766f636174696f6e4c6973743f626173653f6f626a656374636c6173733d63524c446973747269627574696f6e506f696e74");
        signBytes = Hex.decode("30450220" + "00" + "695dc38f417468bffd3be62423e0dc7b1efa9cfabef3793fb0208f29dd3e67" + "0221" + "0098133cb3efcb5422ca56f0f43b454465ec68f19c6d077176d1580580055998cb");
        signBytes = Hex.decode("3044021f" + "" + "695dc38f417468bffd3be62423e0dc7b1efa9cfabef3793fb0208f29dd3e67" + "0221" + "0098133cb3efcb5422ca56f0f43b454465ec68f19c6d077176d1580580055998cb");

        message = "CN=MCSCA@测试06@01111111111111111@007,SERIALNUMBER=1111111111111111,OU=localRA,O=MCSCA,C=CN++本人承诺当前证书信息与身份信息一致，申请使用原证书信息进行证书更新".getBytes(StandardCharsets.UTF_8);
        signBytes = Base64.decode("ZgW3RjnJChz7ypENXqdvTIhElAuN5rTKLTgYeW53YaHNfSMSgArFOa77ufQN0ooB6pMfGiJ+PhZ4jn4eMBEvlk6EGHk/zrnoK0xFSMQszcFos4SS9eq1sVhwyS8AyvgYQWCap2jjoLc7xKKg362e2V/jE4XPc13c5TxNegULHLU=");

        // SM2Signer signer = new SM2Signer(PlainDSAEncoding.INSTANCE, new SM3Digest());
        SM2Signer signer = new SM2Signer(StandardDSAEncoding.INSTANCE, new SM3Digest());
        signer.init(false, publicKeyParameters);
        signer.update(message, 0, message.length);
        boolean verifySignature = signer.verifySignature(signBytes);
        System.out.println(verifySignature);
    }

    @Test
    @SneakyThrows
    public void baseToHex() {
        String base64 = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg/re2HViGP2uA7jkANG+Fe8pBPPhjpv6/bTuQRalnDIugCgYIKoEcz1UBgi2hRANCAAS2f9eYIYqgQqYvv/8zZFFVmd/8/+ci3HqsEC96sI80icd+8+sVW1bunQXuxExnD3AMJQ4Ob+pmp/LP8DoAHOqE";
        String base642 = "MIICtDCCAlegAwIBAgIQIhSQf3SHYb/5k5qymYnxDTAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwNjI0MDcwNTU5WhcNMjUwNjI0MDcwNTU5WjB0MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExCzAJBgNVBAUMAjExMTYwNAYDVQQDDC0xNDgzOTg3NzQ2MjIwNTAzMDQwQOato+iBlOaUr+S7mOa1i+ivlUAwMkAwMDIwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASnnwwaCb+QtBdEqeKlElMs4gaOmpxXYY+MGmzRRp0sHwS6XSQPUWOOzYUifWs3aMu5BS2pL/x2GY2TjxIXNTSbo4IBDjCCAQowHwYDVR0jBBgwFoAUMIj+P2KgJfTzKz2pbaagTAtMyrUwHQYDVR0OBBYEFKvasfOLDNewQJGVrRnIDdUnR1qJMAsGA1UdDwQEAwIE8DCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDEuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMSxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAMBggqgRzPVQGDdQUAA0kAMEYCIQCJV0TdoczObFpo8IgnzKGBe21d2h3rqvFp+Ob0UiuD8AIhAOy02cA4f44xWQnW+tn+myesLCScxQvfz0BNlH9zYA2B";
        System.out.println(Hex.toHexString(Base64.decode(base64)).toUpperCase());
        System.out.println(Hex.toHexString(Base64.decode(base642)).toUpperCase());
    }

    @Test
    @SneakyThrows
    public void hexToBase64() {
        String hex = "3169301806092a864886f70d010903310b06092a864886f70d010701301c06092a864886f70d010905310f170d3234303631323037343935385a302f06092a864886f70d01090431220420b8ec175e7ea7e1d38f29ddccd19ffe1fee1751978cae0cd572c5898d428e94a2";
        System.out.println(Base64.toBase64String(Hex.decode(hex)));
    }

    @Test
    @SneakyThrows
    public void hexDecode() {
        String hexStr = "68747470733a2f2f636f6e73742e6e65742e636e2f";
        System.out.println(new String(Hex.decode(hexStr)));
    }

    @Test
    @SneakyThrows
    public void getDFromPrivate() {
        String privateStr = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQghclRtEAEW7H1y4G+1DGqFATtjb8RkU6tM+wW57G9rRugCgYIKoEcz1UBgi2hRANCAATJjsgrOBf9y4XAQuLldhbbBWuq+9i9e6Hk7DZTfOH0s1WmIOJXtCdU02QlWfmNO0VL2iZaART1g24f4+xDJTdE";
        BCECPrivateKey privateKey = (BCECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(privateStr)));
        System.out.println(privateKey.getD().toString(16));
        System.out.println(Hex.toHexString(privateKey.getD().toByteArray()).toUpperCase());
    }

    @Test
    @SneakyThrows
    public void getQFromPublicCert() {
        String publicCert = "MIICsDCCAlSgAwIBAgIQJQtBeN42MSUY0pPSp9+0BjAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwNTMwMDIwNjU1WhcNMjQwNTMxMDIwNjU1WjBxMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjM3MTcyNDIwMDIwNjA1MjIxMDEjMCEGA1UEAwwaVHBlclNNMngyQOWUkOWlveWHr0AwMUAxMzkwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAARwWwodcIUeWkeYYvAQTRC9d58zg3MkaFF4yYfu29YLAbrma8CvgRl2uBH5Y8OiHZF7zRP/2tQxaaGrRqfwDLy5o4IBDjCCAQowCwYDVR0PBAQDAgbAMIG6BgNVHR8EgbIwga8wLqAsoCqGKGh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMC5jcmwwfaB7oHmGd2xkYXA6Ly93d3cubWNzY2EuY29tLmNuOjM4OS9DTj1jcmwwLE9VPUNSTCxPPU1DU0NBLEM9Q04/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MB0GA1UdDgQWBBR22nFoCGKlsqf3RDsOLat/t2o51DAfBgNVHSMEGDAWgBTxIgpnmI3147KqwxdrwEIfvku9djAMBggqgRzPVQGDdQUAA0gAMEUCIApANPUV4WfHorRT4ol+b9toSsQhZ5okraYUZNopezJbAiEAzZ5j0Jqbz5YFnLAwpt93u4KjM7Q6Z+KmkDStAPKvkL4=";
        CertificateFactory certFact = CertificateFactory.getInstance("X.509", BC);
        X509Certificate certificate = (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(Base64.decode(publicCert)));
        BCECPublicKey publicKey = (BCECPublicKey) certificate.getPublicKey();
        System.out.println(publicKey.getQ().toString());
        System.out.println(Hex.toHexString(publicKey.getQ().getXCoord().toBigInteger().toByteArray()).toUpperCase());
        System.out.println(Hex.toHexString(publicKey.getQ().getYCoord().toBigInteger().toByteArray()).toUpperCase());
    }

    @Test
    @SneakyThrows
    public void getQFromPublicKey() {
        String publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEyY7IKzgX/cuFwELi5XYW2wVrqvvYvXuh5Ow2U3zh9LNVpiDiV7QnVNNkJVn5jTtFS9omWgEU9YNuH+PsQyU3RA==";
        publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEL1q+0/Fa2cqFrEBo+fQRJzLjG1gTGfv+LWHG/QzHbwV5QC2rr9chlm2KnbySEB7+CUUtERK0SYGvVZEe/BKR0Q==";
        BCECPublicKey publicKey1 = (BCECPublicKey) KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey)));
        System.out.println(publicKey1.getQ().toString());
        System.out.println(Hex.toHexString(publicKey1.getQ().getXCoord().toBigInteger().toByteArray()).toUpperCase());
        System.out.println(Hex.toHexString(publicKey1.getQ().getYCoord().toBigInteger().toByteArray()).toUpperCase());
    }

    @Test
    @SneakyThrows
    public BCECPrivateKey parsePrivateFromD() {
        String dHex = "85C951B440045BB1F5CB81BED431AA1404ED8DBF11914EAD33EC16E7B1BDAD1B";
        dHex = "32ecf90216b997b83fb00ab838b1dbf985d714c5e95451a9f2e8a43c6d5c9e6d";
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        ECParameterSpec ecSpec = new ECParameterSpec(
                sm2Spec.getCurve(),
                sm2Spec.getG(),
                sm2Spec.getN(),
                sm2Spec.getH());
        BigInteger d2 = new BigInteger(1, Hex.decode(dHex));
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(d2, ecSpec);
        BCECPrivateKey ecPriFromD = new BCECPrivateKey("EC", ecPrivateKeySpec, BouncyCastleProvider.CONFIGURATION);
        System.out.println("ecPriFromD>> " + Base64.toBase64String(ecPriFromD.getEncoded()));
        return ecPriFromD;
    }

    @Test
    @SneakyThrows
    public void verifyRootCert() {
        String userCertBase64 = "MIICyTCCAmygAwIBAgIQI0FSSvX+iIz0artmESJ03jAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjExMjI5MTEyNjIzWhcNMjExMjMwMTEyNjIzWjCBhjELMAkGA1UEBhMCQ04xEDAOBgNVBAsMB2xvY2FsUkExDjAMBgNVBAoMBU1DU0NBMRswGQYDVQQFDBIyMDIwMzA0NjEyNTM1NDQ1MzUxODA2BgNVBAMMLzEzMDU0NDIxNzM5NzM2MzUwNzJA6YeN5bqG5a+M5rCR6ZO26KGMQDAyQDYxODkzMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEmxPDAGxL4oPvmlUSZy2x+N2hlQhb4kvsEezV5BL33ucKL2HqUHXeqBhnbl6oHqhG2gkD061hrIoiFTdXzi82LqOCARAwggEMMB8GA1UdIwQYMBaAFLkj5hx/z7c8WCRbpAqtihGXenu2MB0GA1UdDgQWBBRSAemKxfYjqDEBJ2Apc6bO7GsF/zALBgNVHQ8EBAMCBPAwgbwGA1UdHwSBtDCBsTAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwxLmNybDB/oH2ge4Z5bGRhcDovL3d3dy5tY3NjYS5jb20uY246MTAzODkvQ049Y3JsMSxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAMBggqgRzPVQGDdQUAA0kAMEYCIQD3NI/lfZ1t9RZRMrnpb3kih0DhWO/IEba+EjAUW79vbQIhAMdf81LmGKG22M0pEMRRPxr0841g9dv0F7GFfUCuN8EN";
        String rootCertBase64 = "MIICgTCCAiWgAwIBAgIQVn7l0kAA6S9mVsDdu6k+oTAMBggqgRzPVQGDdQUAMC4xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVOUkNBQzEPMA0GA1UEAwwGUk9PVENBMB4XDTE4MTEwNTAyMzU0N1oXDTM4MTAzMTAyMzU0N1owLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABGL17gBv7xGY99pYA8IFH3H3VxbulydJGz5Fu3nYnx2FiU4BG8qLNCib5hnKzG6nVgeNel1IVKrsTX86CDQ6lj6jggEiMIIBHjAfBgNVHSMEGDAWgBRMMrGX2TMbxKYFwcbli2Jb8Jd2WDAPBgNVHRMBAf8EBTADAQH/MIG6BgNVHR8EgbIwga8wQaA/oD2kOzA5MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTlJDQUMxDDAKBgNVBAsMA0FSTDEMMAoGA1UEAwwDYXJsMCqgKKAmhiRodHRwOi8vd3d3LnJvb3RjYS5nb3YuY24vYXJsL2FybC5jcmwwPqA8oDqGOGxkYXA6Ly9sZGFwLnJvb3RjYS5nb3YuY246Mzg5L0NOPWFybCxPVT1BUkwsTz1OUkNBQyxDPUNOMA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUuSPmHH/PtzxYJFukCq2KEZd6e7YwDAYIKoEcz1UBg3UFAANIADBFAiEA2HUeRfOn8spFQIkj9zbhVfc0qx9R8lwP40QcRWHIcMcCIHzDLXW5xAkevJaXFcsZJ/Q6CmXK5X+Soq+W/7+DaO8p";

        userCertBase64 = "MIICvDCCAmCgAwIBAgIQIEpHlDrW07HFXAv8ptL8AzAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjExMjMwMDgwMDA3WhcNMjExMjMxMDgwMDA3WjB7MQswCQYDVQQGEwJDTjEQMA4GA1UECwwHbG9jYWxSQTEOMAwGA1UECgwFTUNTQ0ExGzAZBgNVBAUMEjQxMTQyNDE5OTAwMzEwMjA2MzEtMCsGA1UEAwwkMTMwNTQ0MjIzNTU4Nzk2MDgzMkDliJjlh6TkupFAMDFAMDA0MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEgayn0xcwQtqJbvsjVNCGjEg411IDPyaNj/246aop5l2NKla4hvOUClF20UbD3Fto8SbAv8iNrDwy+qqFE7bYR6OCARAwggEMMB8GA1UdIwQYMBaAFLkj5hx/z7c8WCRbpAqtihGXenu2MB0GA1UdDgQWBBQ9W/RgW/zXvKmP4tq+KvQYRc9jWTALBgNVHQ8EBAMCBPAwgbwGA1UdHwSBtDCBsTAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwxLmNybDB/oH2ge4Z5bGRhcDovL3d3dy5tY3NjYS5jb20uY246MTAzODkvQ049Y3JsMSxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAMBggqgRzPVQGDdQUAA0gAMEUCIICbAk1o15PeFH8sGyTOeoF5xrcroY2g0n/fq8ms9Y2yAiAcmqFoLFIWft5xSHgk2DoypwlRHt/9XDvjYWQteU82RgA=";
        rootCertBase64 = "MIICgTCCAiWgAwIBAgIQVn7l0kAA6S9mVsDdu6k+oTAMBggqgRzPVQGDdQUAMC4xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVOUkNBQzEPMA0GA1UEAwwGUk9PVENBMB4XDTE4MTEwNTAyMzU0N1oXDTM4MTAzMTAyMzU0N1owLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABGL17gBv7xGY99pYA8IFH3H3VxbulydJGz5Fu3nYnx2FiU4BG8qLNCib5hnKzG6nVgeNel1IVKrsTX86CDQ6lj6jggEiMIIBHjAfBgNVHSMEGDAWgBRMMrGX2TMbxKYFwcbli2Jb8Jd2WDAPBgNVHRMBAf8EBTADAQH/MIG6BgNVHR8EgbIwga8wQaA/oD2kOzA5MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTlJDQUMxDDAKBgNVBAsMA0FSTDEMMAoGA1UEAwwDYXJsMCqgKKAmhiRodHRwOi8vd3d3LnJvb3RjYS5nb3YuY24vYXJsL2FybC5jcmwwPqA8oDqGOGxkYXA6Ly9sZGFwLnJvb3RjYS5nb3YuY246Mzg5L0NOPWFybCxPVT1BUkwsTz1OUkNBQyxDPUNOMA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUuSPmHH/PtzxYJFukCq2KEZd6e7YwDAYIKoEcz1UBg3UFAANIADBFAiEA2HUeRfOn8spFQIkj9zbhVfc0qx9R8lwP40QcRWHIcMcCIHzDLXW5xAkevJaXFcsZJ/Q6CmXK5X+Soq+W/7+DaO8p";

        boolean verifyCertificate = CertUtil.verifyCertificate(Base64.decode(userCertBase64), Base64.decode(rootCertBase64));
        System.out.println(verifyCertificate);
    }

    @Test
    @SneakyThrows
    public void test02() {
        for (int i = 0; i < 100; i++) {
            String pri = "MIGHAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBG0wawIBAQQg2TSyBsSej2+rzLbzosJISpHpvxnHkytt/ZFya/v3bk6hRANCAASNnEXTr1xvyeiCFyjvJMCitVR0UJ63UzfU4ftoGA0ejs3Pn/CLatCIzLfwjzimiqGf7jG8UxfOEdDY8QElLkqe";
            long before = System.currentTimeMillis();
            String s = SignatureUtil.doSign(pri, "1234");
            System.out.println(s);
            System.out.println(System.currentTimeMillis() - before);
        }
    }

    @Test
    @SneakyThrows
    public void signWithHash() {
        // thk's todo 2024/8/13 9:38 还未成功!!!!!!!!!!!!!!!!!
        BCECPrivateKey bcecPrivateKey = parsePrivateFromD();
        byte[] bytes = "abc".getBytes(StandardCharsets.UTF_8);
        byte[] digest = new byte[32];
        SM3Digest sm3Digest = new SM3Digest();
        sm3Digest.update(bytes, 0, bytes.length);
        sm3Digest.doFinal(digest, 0);
        // 假设你已经有了摘要值 digest
        System.out.println("digest>> " + Hex.toHexString(digest));

        // 初始化签名器
        // 获取 SM2 曲线参数
        ECParameterSpec parameters = bcecPrivateKey.getParameters();
        ECDomainParameters domainParameters = new ECDomainParameters(parameters.getCurve(), parameters.getG(), parameters.getN(), parameters.getH());

        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(bcecPrivateKey.getD(), domainParameters);
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SM3Digest()));
        signer.init(true, ecPrivateKeyParameters);
        // 生成签名 (r, s)

        BigInteger[] signature = signer.generateSignature(Hex.decode("d77baa524888f0bc9706a850c35de891e61d281e3039939c72ae52b781d37431"));

        // 输出签名结果
        System.out.println("r: " + signature[0].toString(16));
        System.out.println("s: " + signature[1].toString(16));
    }

}