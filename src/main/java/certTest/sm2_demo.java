package certTest;

import certTest.saxon.CertUtils;
import certTest.saxon.sm2.Sm2Utils;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

/**
 * sm2加解密测试
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


    @SneakyThrows
    public static void main(String[] args) {
        //加载BC
        Security.addProvider(BC);

        SM2Util sm2 = new SM2Util();
        // 获取SM2椭圆曲线的参数
        ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");
        // 获取一个椭圆曲线类型的密钥对生成器
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", BC);
        // 使用SM2参数初始化生成器
        kpg.initialize(sm2Spec);
        // 获取密钥对
        KeyPair keyPair = kpg.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        System.out.println(publicKey.toString());
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println(privateKey.toString());
        System.out.println("原文：" + M);
        String publicKeyBase64 = Base64.toBase64String(publicKey.getEncoded());
        System.out.println("公钥Base64：" + publicKeyBase64);
        String privateKeyBase64 = Base64.toBase64String(privateKey.getEncoded());
        System.out.println("密钥Base64：" + privateKeyBase64);

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
                .build(keyPair.getPrivate());

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
                , keyPair.getPublic())
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
        FileUtil.writeBytes(base64EncodedCert, "C:\\Users\\ggk911\\Desktop\\SM2DEMO证书.cer");

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

        FileUtil.writeBytes(cert.get("certData"), "C:\\Users\\ggk911\\Desktop\\SM2证书pfx.pfx");
        FileUtil.writeBytes(Base64.encode(cert.get("publicKey")), "C:\\Users\\ggk911\\Desktop\\SM2证书pubkey.key");
        FileUtil.writeBytes(Base64.encode(cert.get("privateKey")), "C:\\Users\\ggk911\\Desktop\\SM2证书prikey.key");
        FileUtil.writeBytes(Base64.encode(cert.get("cer")), "C:\\Users\\ggk911\\Desktop\\SM2证书cer.cer");

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
        FileOutputStream out = new FileOutputStream("C:\\Users\\ggk911\\Desktop\\SM2证书jks.jks");
        outputKeyStore.store(out, passwd);
        out.close();
    }
}
