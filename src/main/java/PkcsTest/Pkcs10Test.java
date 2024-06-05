package PkcsTest;


import cn.com.mcsca.pki.core.crypto.asymmetric.SM2CipherUtil;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.jcajce.JcaX509ContentVerifierProviderBuilder;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author TangHaoKai
 * @version V1.0 2024/2/6 14:14
 **/
public class Pkcs10Test {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final Provider BC = new BouncyCastleProvider();

    @Test
    public void changeattrTest() throws Exception {
        String csr = "MIIBxzCCAWwCAQAwUjELMAkGA1UEBhMCQ04xDTALBgNVBAoMBENGQ0ExEjAQBgNVBAsMCUN1c3RvbWVyczEgMB4GA1UEAwwXQ0ZDQUBNb2JpbGVAQW5kcm9pZEAxLjAwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQrCjfwIwvBGHVQ198hrTwmEjUQVYmiwmFoz6tjwAWLQm5zVHpY6QAKD2SSTmT4+hQGfp+xcH04HrE32safG8H8oIG3MBMGCSqGSIb3DQEJBxMGMTExMTExMIGfBgkqhkiG9w0BCT8EgZEwgY4CAQEEgYgAtAAAAAEAAJwB9iDvTwQqVjum4JT5qJLFdhenAAo2jod1lKXocBBnAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABGgshB8jnnNOuEcgtmg7VsLb+JLWMtG8PWexmtZ4y/pQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAwGCCqBHM9VAYN1BQADRwAwRAIgEozHNG9pq7VuDZgOQkXdYk3mC2ATfp+za/OWyGTDEzQCIG1aFX6El0w+32fvZ933kE+oFwPKo00qD914QF6YAyEw";
        ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(csr));
        //将hex转换为byte输出
        ASN1Primitive asn1Primitive;
        while ((asn1Primitive = asn1InputStream.readObject()) != null) {
            ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
            // sequence.getObjectAt(0)
        }

    }

    /**
     * SM2，256
     */
    @Test
    @SneakyThrows
    public void createCsrTest() {
        Security.addProvider(new BouncyCastleProvider());
        // 获取SM2曲线参数
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        // 默认32字节，256bit位
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", BC);
        generator.initialize(ecSpec);
        KeyPair keyPair = generator.generateKeyPair();
        PrivateKey aPrivate = keyPair.getPrivate();
        System.out.println("privateKey>> " + Hex.toHexString(aPrivate.getEncoded()));
        System.out.println("privateKeyBase64>> " + Base64.toBase64String(aPrivate.getEncoded()));
        PublicKey aPublic = keyPair.getPublic();
        System.out.println("publicKey>> " + Hex.toHexString(aPublic.getEncoded()));
        System.out.println("publicKeyBase64>> " + Base64.toBase64String(aPublic.getEncoded()));

        String subjectParam = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,EMAILADDRESS=13983053455@163.com,L=重庆,ST=重庆";
        X500Principal subject = new X500Principal(subjectParam);
        // SHA256withRSA算法
        ContentSigner signer = new JcaContentSignerBuilder("SM3withSm2")
                .setProvider(BC)
                .build(aPrivate);
        // CSR builder
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());
        // 添加属性
        // DERPrintableString password = new DERPrintableString("secret123");
        // builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, password);
        // 创建
        PKCS10CertificationRequest sm2Csr = builder.build(signer);
        System.out.println("CSR>> " + Base64.toBase64String(sm2Csr.getEncoded()));
    }

    /**
     * RSA
     */
    @Test
    @SneakyThrows
    public void createCsrTest02() {
        Security.addProvider(BC);
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BC);
        // generator.initialize(1024);
        generator.initialize(2048);
        // generator.initialize(4096);
        KeyPair keyPair = generator.generateKeyPair();
        final PublicKey aPublic = keyPair.getPublic();
        final PrivateKey aPrivate = keyPair.getPrivate();
        System.out.println("privateKey>> " + Hex.toHexString(aPrivate.getEncoded()));
        System.out.println("privateKeyBase64>> " + Base64.toBase64String(aPrivate.getEncoded()));
        System.out.println("pubKey>> " + Hex.toHexString(aPublic.getEncoded()));
        System.out.println("pubKeyBase64>> " + Base64.toBase64String(aPublic.getEncoded()));
        String subjectParam = "CN=MCSCA";
        X500Principal subject = new X500Principal(subjectParam);
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BC)
                .build(aPrivate);
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, aPublic);
        // DERPrintableString password = new DERPrintableString("secret123");
        // builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, password);

        PKCS10CertificationRequest csr = builder.build(signer);
        System.out.println("CSR>> " + Base64.toBase64String(csr.getEncoded()));
    }

    @Test
    @SneakyThrows
    public void changeCsrTest() {
        String p10 = "MIIBszCCAVgCAQAwPjEYMBYGA1UEAwwPY2VydFJlcXVpc2l0aW9uMRUwEwYDVQQKDAxDRkNBIFRFU1QgQ0ExCzAJBgNVBAYTAkNOMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEgHzEIkEHDrO9csZrFarvVr/rd1Q5SPaHfCo7M1Virn4gWoZ/0e5YQf0rdEShDU7Q+GG4hEFKLeKTuW9cjajNw6CBtzATBgkqhkiG9w0BCQcTBjExMTExMTCBnwYJKoZIhvcNAQk/BIGRMIGOAgEBBIGIALQAAAABAAAHc+H/wtpyhBmciNWQuV/xw5hbOIX03qAY3k2s8o4JAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAARZh4VjfGxg3lstDTHiNh5K3sU/E3GQrPmKwsC7+g0/QAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAMBggqgRzPVQGDdQUAA0cAMEQCIGYDuRY5AXixhP1k/Nri0lyrtg7JXvyFHArEf/BDL31DAiBHUCClX17mvDzJ3ol3XZywjcGpdCFl8F+LYxPhlOCZtQ==";
        ASN1Sequence pkcsSeq = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(Base64.decode(p10)));
        ASN1Sequence cerResInfo = ASN1Sequence.getInstance(pkcsSeq.getObjectAt(0));

        ASN1Sequence passwordAttrSet = ASN1Sequence.getInstance(ASN1TaggedObject.getInstance(cerResInfo.getObjectAt(3)).getBaseObject());
        ASN1Sequence passwordAttr = ASN1Sequence.getInstance(passwordAttrSet.getObjectAt(0));
        ASN1ObjectIdentifier pwdAttrOid = ASN1ObjectIdentifier.getInstance(passwordAttr.getObjectAt(0));
        ASN1PrintableString pwdAttrValue = ASN1PrintableString.getInstance(passwordAttr.getObjectAt(1));

        Attribute attribute = new Attribute(pwdAttrOid, new DERSet(pwdAttrValue));

        CertificationRequestInfo certificationRequestInfo = new CertificationRequestInfo(X500Name.getInstance(cerResInfo.getObjectAt(1)), SubjectPublicKeyInfo.getInstance(cerResInfo.getObjectAt(2)), new DERSet(attribute));
        CertificationRequest pkcs10 = new CertificationRequest(certificationRequestInfo, AlgorithmIdentifier.getInstance(pkcsSeq.getObjectAt(1)), ASN1BitString.getInstance(pkcsSeq.getObjectAt(2)));
        System.out.println(Base64.toBase64String(pkcs10.getEncoded()));
    }

    @Test
    @SneakyThrows
    public void sm2EncPriDecode() {
        String xy = "040773e1ffc2da7284199c88d590b95ff1c3985b3885f4dea018de4dacf28e09034598785637c6c60de5b2d0d31e2361e4adec53f137190acf98ac2c0bbfa0d3f4";
        // 使用BouncyCastle提供的ECPointUtil类将字节数组转换为ECPoint对象
        KeyFactory fact = KeyFactory.getInstance("EC", BC);
        // 使用BouncyCastle提供的ECNamedCurveTable类获取SM2的参数规范
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");

        // 使用SM2的参数规范创建EC参数规范
        ECNamedCurveSpec ecSpec = new ECNamedCurveSpec(
                sm2Spec.getName(),
                sm2Spec.getCurve(),
                sm2Spec.getG(),
                sm2Spec.getN(),
                sm2Spec.getH(),
                sm2Spec.getSeed()
        );

        ECPoint ecPoint = ECPointUtil.decodePoint(ecSpec.getCurve(), Hex.decode(xy));

        // 使用SM2算法创建EC公钥规范
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(ecPoint, ecSpec);

        // 使用BouncyCastle提供的KeyFactory类根据EC公钥规范生成公钥对象
        PublicKey publicKey = fact.generatePublic(ecPublicKeySpec);

        System.out.println(Hex.toHexString(publicKey.getEncoded()));

    }

    @Test
    @SneakyThrows
    public void asn1Test() {
// 版本号
        ASN1Integer version = new ASN1Integer(1);

        // ASN1OctetString类型数据
        byte[] data = "Hello, ASN.1!".getBytes();
        ASN1OctetString octetString = new DEROctetString(data);

        // 组装ASN1Sequence
        ASN1Encodable[] sequenceElements = new ASN1Encodable[]{version, octetString};
        ASN1Sequence sequence = new DERSequence(sequenceElements);
        // 打印编码后的ASN1Sequence
        System.out.println("Encoded ASN1Sequence: " + Hex.toHexString(sequence.getEncoded()));


        // X9ECParameters sm2p256v1 = SECNamedCurves.getByName("sm2p256v1");
        // byte[] privateKeyBytes = Hex.decode(decode);
        // ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(new BigInteger(privateKeyBytes), new ECParameterSpec(sm2p256v1.getCurve(), sm2p256v1.getG(), sm2p256v1.getN(), sm2p256v1.getH()));
        // KeyFactory keyFact = KeyFactory.getInstance("EC", new BouncyCastleProvider());
        // BCECPrivateKey privateKey = (BCECPrivateKey) keyFact.generatePrivate(ecPrivateKeySpec);
        //
        // KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
        // keyPairGenerator.initialize();
    }

    @Test
    @SneakyThrows
    public void getPubFromPkcs10() {
        // SM2
        // String cfcaPkcs10 = "MIIBszCCAVgCAQAwPjEYMBYGA1UEAwwPY2VydFJlcXVpc2l0aW9uMRUwEwYDVQQKDAxDRkNBIFRFU1QgQ0ExCzAJBgNVBAYTAkNOMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEgHzEIkEHDrO9csZrFarvVr/rd1Q5SPaHfCo7M1Virn4gWoZ/0e5YQf0rdEShDU7Q+GG4hEFKLeKTuW9cjajNw6CBtzATBgkqhkiG9w0BCQcTBjExMTExMTCBnwYJKoZIhvcNAQk/BIGRMIGOAgEBBIGIALQAAAABAAAHc+H/wtpyhBmciNWQuV/xw5hbOIX03qAY3k2s8o4JAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAARZh4VjfGxg3lstDTHiNh5K3sU/E3GQrPmKwsC7+g0/QAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAMBggqgRzPVQGDdQUAA0cAMEQCIGYDuRY5AXixhP1k/Nri0lyrtg7JXvyFHArEf/BDL31DAiBHUCClX17mvDzJ3ol3XZywjcGpdCFl8F+LYxPhlOCZtQ==";
        // ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(cfcaPkcs10));
        // ASN1Primitive asn1Primitive;
        // while ((asn1Primitive = asn1InputStream.readObject()) != null) {
        //     ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
        //     ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
        //     ASN1Sequence sequence1 = ASN1Sequence.getInstance(requestInfo.getObjectAt(2));
        //     ASN1BitString instance = ASN1BitString.getInstance(sequence1.getObjectAt(1));
        //
        //     ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        //     ECDomainParameters domainParameters = new ECDomainParameters(sm2Spec.getCurve(), sm2Spec.getG(), sm2Spec.getN(), sm2Spec.getH());
        //     // 使用 domainParameters 创建椭圆曲线点对象
        //     org.bouncycastle.math.ec.ECPoint ecPoint = domainParameters.getCurve().decodePoint(instance.getBytes());
        //     // 使用公钥点创建 ECPublicKeyParameters 对象
        //     ECParameterSpec ecParameterSpec = new ECParameterSpec(sm2Spec.getCurve(), sm2Spec.getG(), sm2Spec.getN(), sm2Spec.getH());
        //     KeyFactory keyFact = KeyFactory.getInstance("EC", BC);
        //     BCECPublicKey createECPublicKey = (BCECPublicKey) keyFact.generatePublic(new org.bouncycastle.jce.spec.ECPublicKeySpec(ecPoint, ecParameterSpec));
        //     System.out.println(Hex.toHexString(createECPublicKey.getEncoded()));
        // }


        // RSA
        String cfcaPkcs10Base64 = "MIIDxjCCAq4CAQAwPjEYMBYGA1UEAwwPY2VydFJlcXVpc2l0aW9uMRUwEwYDVQQKDAxDRkNBIFRFU1QgQ0ExCzAJBgNVBAYTAkNOMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiZDe8fvxlJkt+2jz7Kr3Uqy6/883ZGYttapmsbQ2IZi32GnIVU8Zm//UsVgxTdXHGTTu+BFKFiFAWloF9k2tOPt7EGLnbfFoaGSTnm/SafgEiDUZlWsogVl6S6J+qIjLTWAhyF4WBr8gHtzJB1G/hjK/URREMmNijk9pzU7Z1scfpe+sr1rw39n+w+wbY5SEKIUd/8KbQTyUqQ2Fo72QpSaqha86uoKjZsG3S0Zl2JFbipM9u3j5gSJUXZ/nrnWCP7GkLybQSeLPEls9Q4DOfn/hMUWmaSVsS821vuWREdtRKANoTLWMbsjn0QGsejq6sugss95SQLYvccLkP+AJpwIDAQABoIIBQTATBgkqhkiG9w0BCQcTBjExMTExMTCCASgGCSqGSIb3DQEJPwSCARkwggEVAgEBBIIBDjCCAQoCggEBAIqK8yRhx1Tq+ZCD9bzHrRS6eGst8OjYsHZnkjXXcdWCCo3pjSZK1EhKwLINe7aiT3CspkI8DySfATpOHdOjh1fu0V3w/xX05Q5ePzanpGNIY5QQ6NVYTuB4LdvnaWFqIPZQ9ZrP1H5l2+ASRUM83DuDy/+hWS8IaITFx0ueDEjByc5P5rbEvhOlleYpdnb0P3BzXgz/HZelbLvyJBX6SZ+qQdrc3Dw45GfGm2azv5+bMzP1XKmKQ3Pb/ciR2FXObqnhA3XMx0UOwpLyRYwfHMiEh7sg80QMOQ5GR3oyZRCS4fRW9Fi3O8cWKihHNkBgYzfjadhbB/65pHZuMypNfd0CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAG27391+bNyW8G3WR9D0xmRgvpoW1OqGFJ0EdOnMd8Mnjq9fG5PLz4mUnx6R1a/kLhk33j+WK3yMoKrTlzLMt5bXjA8aUTGt2nL9nftwITF2PTm9Kk8Tfg9xBM8C2NIoXstbeGWF93lC/rVAz7IKd5eoWLiBxYfUPRsFyJ7VDLYFshFKLx0zz6s66vd3yijSQofCSMtV8MZARhANhdXBx9tuNDy6D5OgRGHseEaYnSjBMAt9KBsAWxeqFnebNuyzx2eZKQIDmy1kfkbyAoonf27dilXLptjxkf8v4/njKWvDWurgb4XOIa0U2CevPzkrIwh2jZ1aaB2n1wLmzl4BriA==";
        ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(cfcaPkcs10Base64));
        ASN1Primitive asn1Primitive;
        while ((asn1Primitive = asn1InputStream.readObject()) != null) {
            ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
            ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
            ASN1Sequence sequence1 = ASN1Sequence.getInstance(requestInfo.getObjectAt(2));
            ASN1BitString instance = ASN1BitString.getInstance(sequence1.getObjectAt(1));
            ASN1Sequence sequence2 = ASN1Sequence.getInstance(instance.getBytes());

            BigInteger n = ASN1Integer.getInstance(sequence2.getObjectAt(0)).getValue();
            BigInteger e = ASN1Integer.getInstance(sequence2.getObjectAt(1)).getValue();
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
            PublicKey publicKey = keyFactory.generatePublic(rsaPublicKeySpec);
            System.out.println(Hex.toHexString(publicKey.getEncoded()));
        }

    }

    @Test
    @SneakyThrows
    public void signVerifyTest() {
        String xyd = "86da257a6f1c402cd694b22809ede0021e05611bb2077d753fe87160a039a72a1b6e36ac53c145ea6af6904cbbbac229b9771c535bb643305b9871b213bd13ec9bc80070025bfbb69af775e2cb6aa656ab2d71493392ae4df2ec949404105b6c";
        // xyd = "3cb6474789515c8fa5ba67b59cc8f6042453a10204eabe5cf313fc2b03afb3b726f8825689489605ba8383fb4b41300890446c12b7348329fdf3e74687e81647475350f9d84727b2dc854e9a790e9e815117b498adf9ce304c53e899dde6d0ed";
        String d = xyd.substring(128);
        PublicKey publicKey = parsePubFromQ(xyd.substring(0, 128));
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");

        ECParameterSpec ecSpec = new ECParameterSpec(
                sm2Spec.getCurve(),
                sm2Spec.getG(),
                sm2Spec.getN(),
                sm2Spec.getH());
        KeyFactory fact = KeyFactory.getInstance("ECDSA", BC);
        BigInteger d1 = new BigInteger(1, Hex.decode(d));
        PrivateKey privateKey = fact.generatePrivate(new ECPrivateKeySpec(d1, ecSpec));

        // 签名 验签
        String M = "test";
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
    }

    @Test
    @SneakyThrows
    public void ciTest() {
        String cfcaPkcs10 = "MIIBszCCAVgCAQAwPjEYMBYGA1UEAwwPY2VydFJlcXVpc2l0aW9uMRUwEwYDVQQKDAxDRkNBIFRFU1QgQ0ExCzAJBgNVBAYTAkNOMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEgHzEIkEHDrO9csZrFarvVr/rd1Q5SPaHfCo7M1Virn4gWoZ/0e5YQf0rdEShDU7Q+GG4hEFKLeKTuW9cjajNw6CBtzATBgkqhkiG9w0BCQcTBjExMTExMTCBnwYJKoZIhvcNAQk/BIGRMIGOAgEBBIGIALQAAAABAAAHc+H/wtpyhBmciNWQuV/xw5hbOIX03qAY3k2s8o4JAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAARZh4VjfGxg3lstDTHiNh5K3sU/E3GQrPmKwsC7+g0/QAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAMBggqgRzPVQGDdQUAA0cAMEQCIGYDuRY5AXixhP1k/Nri0lyrtg7JXvyFHArEf/BDL31DAiBHUCClX17mvDzJ3ol3XZywjcGpdCFl8F+LYxPhlOCZtQ==";

        String createPriHex = "308193020100301306072a8648ce3d020106082a811ccf5501822d047930770201010420d873c87879974f477e293da9487907b04046c485820d21e0f0e3c0dd305daa4ca00a06082a811ccf5501822da14403420004e86cf685cc7e32a061435b295dd4b6697a54a965456c9d45df9f314fc594d2df7eb3d1d8dc9e6dbc3403cf4ce22c4a7184f1c8d790084e7c9714185c366651ba";
        String createPubHex = "3059301306072a8648ce3d020106082a811ccf5501822d03420004e86cf685cc7e32a061435b295dd4b6697a54a965456c9d45df9f314fc594d2df7eb3d1d8dc9e6dbc3403cf4ce22c4a7184f1c8d790084e7c9714185c366651ba";
        String createPkcs10 = "3082015d30820103020100308186310f300d06035504080c06e9878de5ba86310f300d06035504070c06e9878de5ba863122302006092a864886f70d01090116133133393833303533343535403136332e636f6d310b300906035504061302434e310f300d060355040a130647474b393131310f300d060355040b130647474b393131310f300d0603550403130647474b3931313059301306072a8648ce3d020106082a811ccf5501822d03420004e86cf685cc7e32a061435b295dd4b6697a54a965456c9d45df9f314fc594d2df7eb3d1d8dc9e6dbc3403cf4ce22c4a7184f1c8d790084e7c9714185c366651baa01a301806092a864886f70d010907310b1309736563726574313233300a06082a811ccf55018375034800304502202e2b55c04dc2b1234584e389f7562339dee1f44f7aad2e264526cfabf61151bc022100e9e62e004c097af72a3bdc82b264913393eae2be73a3bf4401ebd96dda5730ac";

        String cfcaPkcs10ApplySignCert = "MIICxzCCAmqgAwIBAgIQejAUUVKU2z0DTcmDs2aRxTAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwMzAxMTA0NTE5WhcNMjkwMjI4MTA0NTE5WjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE3NjMwMTc2ODY5NDc4MDcyMzJA5ZSQ5aW95YevQDAxQDA1NzBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABIB8xCJBBw6zvXLGaxWq71a/63dUOUj2h3wqOzNVYq5+IFqGf9HuWEH9K3REoQ1O0PhhuIRBSi3ik7lvXI2ozcOjggEcMIIBGDAfBgNVHSMEGDAWgBTxIgpnmI3147KqwxdrwEIfvku9djAdBgNVHQ4EFgQUs4+vi3p0Pg7FCd31OYal4Qe6gTMwDAYDVR0TBAUwAwEBADALBgNVHQ8EBAMCBPAwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDAYIKoEcz1UBg3UFAANJADBGAiEApRTDOx3TzxMiduhvSAd4rXCCSxQdBu6EUCKmjrVgnbYCIQDjvvAmHzjzDS80zf0rn7Fb6mCjI1PPIEleFTYjJ8DpUA==";
        String createPkcs10ApplySignCert = "MIICuTCCAlygAwIBAgIQZVqJYUlGpXieHix1naIv5DAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwMzAxMTA0NTE5WhcNMjkwMjI4MTA0NTE5WjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE3NjMwMTc2ODY5NDc4MDcyMzJA5ZSQ5aW95YevQDAxQDA1NzBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABOhs9oXMfjKgYUNbKV3Utml6VKllRWydRd+fMU/FlNLffrPR2Nyebbw0A89M4ixKcYTxyNeQCE58lxQYXDZmUbqjggEOMIIBCjALBgNVHQ8EBAMCBsAwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwHQYDVR0OBBYEFIRcSUBQEQ7pv6x/VQRFbeMrjBx4MB8GA1UdIwQYMBaAFPEiCmeYjfXjsqrDF2vAQh++S712MAwGCCqBHM9VAYN1BQADSQAwRgIhAJYXjqtPNik7bG4NVTwbKrQYZNMBpanTzlkmY8Xhec5DAiEAy0aI9sFS7oOcQt62F72De3MBBRXRBE+vX7snBbPyotE=";
        String createPkcs10ApplyEncCert = "MIICuDCCAlygAwIBAgIQSKvBF/SzFz/Ja5/YVop9qjAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwMzAxMTA0NTE5WhcNMjkwMjI4MTA0NTE5WjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE3NjMwMTc2ODY5NDc4MDcyMzJA5ZSQ5aW95YevQDAxQDA1NzBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABOBsbbbtjW68mSvhEVstFggJ7+jX6nfJYQ82PoDmz7HvvzKFYJTfVnoWi7ki/qLjWMQ+9hOuf7TTlNVU31W4AFujggEOMIIBCjALBgNVHQ8EBAMCBDAwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwHQYDVR0OBBYEFCEdKcd0eaDwNJJ5vJLfNT5F7+oPMB8GA1UdIwQYMBaAFPEiCmeYjfXjsqrDF2vAQh++S712MAwGCCqBHM9VAYN1BQADSAAwRQIgPfjzCE1H1cqSAvXtk0FSwWhlEEbyVDtU/iVeXenQ93UCIQCVEJilvi+CgfCiK4hA1hM6PBsQrtTaB5tsqw1bMYDf9Q==";
        String createPkcs10EncPrivateKey = "MIIEQgYKKoEcz1UGAQQCBKCCBDIwggQuAgEBMYHTMIHQAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQZVqJYUlGpXieHix1naIv5DALBgkqgRzPVQGCLQMEezB5AiAgK9wa49cBIvO58/qC1nS1Z9/cB6Awh98vZiTtgloEowIhAOrtGTj6iFqlfJoDuKVI2zj0EHVJlDak6UNIPKE5NB4wBCDmDE2XGH9MEaMmczijdbEZZKEvx16MLOyTW3DJr8BNOgQQ3iecE4GecLE+OD32WlKpKjEMMAoGCCqBHM9VAYMRMFkGCiqBHM9VBgEEAgEwCQYHKoEcz1UBaIBA1dWxOCmjmEv+swk5PzjiQdXVsTgpo5hL/rMJOT844kGPwwytJnvKxXH1saRj89Chvg/ahKwKuj0v4UIUd/JetaCCAe8wggHrMIIBj6ADAgECAhBmGlO15VIwOaiyxJ6pnaeUMAwGCCqBHM9VAYN1BQAweDELMAkGA1UEBhMCQ04xEjAQBgNVBAgMCUNob25ncWluZzE6MDgGA1UECgwxRWFzdC1aaG9uZ3h1biBDZXJ0aWZpY2F0ZSBBdXRob3JpdHkgQ2VudGVyIENPLkxURDEZMBcGA1UEAwwQRWFzdC1aaG9uZ3h1biBDQTAeFw0yMzA4MTcwMTI4MzNaFw0zMzA4MTQwMTI4MzNaMC8xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UEAwwHU00yU0lHTjBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABCA6bLiYQEN5maqDtr6rfmqg5/v+GhZHlIDdoQ/SWQ+Hc3SedRXcMas5oWfBUAZ+Nko2nc1+1LMWsRJi4DgDr36jQjBAMB8GA1UdIwQYMBaAFAnuuwf+H95XmJJu7j/LUmpL+SYdMB0GA1UdDgQWBBSJqOoC7mJ441HP32hg8CmGbLpezDAMBggqgRzPVQGDdQUAA0gAMEUCIBnnZHGxXcc7Ar6Wzd3NJ+8Da5TwK8Jn8J7S8p4xsPmwAiEAmW7lerdM1A6+7W4BEfLh69J/fC+d+Hzzx2h/ohWLKQkxgfYwgfMCAQEwgYwweDELMAkGA1UEBhMCQ04xEjAQBgNVBAgMCUNob25ncWluZzE6MDgGA1UECgwxRWFzdC1aaG9uZ3h1biBDZXJ0aWZpY2F0ZSBBdXRob3JpdHkgQ2VudGVyIENPLkxURDEZMBcGA1UEAwwQRWFzdC1aaG9uZ3h1biBDQQIQZhpTteVSMDmossSeqZ2nlDAKBggqgRzPVQGDETALBgkqgRzPVQGCLQEERjBEAiA4XRuZ24bg5alWcsNJ1GERgLUoo664xGWEeccVq6E14wIgbND2lEEoCuHflcPtKsDJ1v1fgaE3Y4lIkLpg26/J64A=";

        // 临时公钥
        KeyFactory keyFact = KeyFactory.getInstance("EC", BC);
        BCECPublicKey createECPublicKey = (BCECPublicKey) keyFact.generatePublic(new X509EncodedKeySpec(Hex.decode(createPubHex)));
        ECParameterSpec createECPublicKeySpec = createECPublicKey.getParameters();
        ECDomainParameters createPubECDomainParameters = new ECDomainParameters(
                createECPublicKeySpec.getCurve(),
                createECPublicKeySpec.getG(),
                createECPublicKeySpec.getN());
        org.bouncycastle.math.ec.ECPoint createECPublicKeyQPoint = createECPublicKey.getQ();
        ECPublicKeyParameters createECPublicKeyParameters = new ECPublicKeyParameters(createECPublicKeyQPoint, createPubECDomainParameters);

        String cfcaPkcs10InterXYHex = getQFromCfcaPkcs10(cfcaPkcs10);
        PublicKey cfcaPkcs10InterPublicKey = parsePubFromQ(cfcaPkcs10InterXYHex);
        System.out.println("CSR里面自定义公钥：" + Hex.toHexString(cfcaPkcs10InterPublicKey.getEncoded()));
        BCECPublicKey cfcaPkcs10InterECPublicKey = (BCECPublicKey) cfcaPkcs10InterPublicKey;
        ECParameterSpec localECParameterSpec = cfcaPkcs10InterECPublicKey.getParameters();
        ECDomainParameters localECDomainParameters = new ECDomainParameters(
                localECParameterSpec.getCurve(),
                localECParameterSpec.getG(),
                localECParameterSpec.getN());
        org.bouncycastle.math.ec.ECPoint cfcaInterPublicKeyQPoint = cfcaPkcs10InterECPublicKey.getQ();
        ECPublicKeyParameters cfcaPkcs10InterPublicKeyParameters = new ECPublicKeyParameters(cfcaInterPublicKeyQPoint, localECDomainParameters);
        // 使用生成的私钥解加密私钥
        String decodeCreatePkcs10EncPrivateKeyD = analysisSM2EncKey(createPkcs10EncPrivateKey, createPriHex);
        // 组成：XYD
        // 这里的XY得从加密证书中取出
        CertificateFactory fact = CertificateFactory.getInstance("X.509", BC);
        X509Certificate cert3 = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(Base64.decode(createPkcs10ApplyEncCert)));
        BCECPublicKey encCertPublicKey = (BCECPublicKey) cert3.getPublicKey();
        org.bouncycastle.math.ec.ECPoint encCertPublicKeyQ = encCertPublicKey.getQ();

        byte[] X = new byte[32];
        System.arraycopy(encCertPublicKeyQ.getRawXCoord().toBigInteger().toByteArray(), 1, X, 0, X.length);
        byte[] Y = new byte[32];
        System.arraycopy(encCertPublicKeyQ.getAffineYCoord().toBigInteger().toByteArray(), 1, Y, 0, Y.length);
        byte[] XYD = Arrays.concatenate(X, Y, Hex.decode(decodeCreatePkcs10EncPrivateKeyD));
        String XYDHex = Hex.toHexString(XYD);
        System.out.println("encInterX>> " + Hex.toHexString(X));
        System.out.println("encInterX>> " + Hex.toHexString(Y));
        System.out.println("XYDHex>> " + XYDHex);
        // hash
        String hash = getHash(X, Y, XYDHex);
        System.out.println("hash>> " + hash);
        // 加密
        String encData = sm2Enc(XYDHex, cfcaPkcs10InterPublicKeyParameters);
        System.out.println("密文>> " + encData);

        // 第二种加密
        SM2 sm2 = new SM2(null, cfcaPkcs10InterECPublicKey);
        String encData2 = Hex.toHexString(sm2.encrypt(Hex.decode(XYDHex)));
        System.out.println("密文2>> " + encData2);

        // 第三章加密
        String encData3 = Hex.toHexString(SM2CipherUtil.encryptWithOld(Hex.decode(XYDHex), cfcaPkcs10InterECPublicKey));
        System.out.println("密文3>> " + encData3);

        // 拼接，组ASN1结构，拼接前缀
        // String concatenateHex = concatenateCfcaSpecial(Hex.toHexString(Arrays.concatenate(X, Y)), hash, encData);
        //
        String special = cfcaSpecial(encData3);
        System.out.println(">> " + special);

        System.out.println("-------------------------------------------------");

        // 解密 从SM2加密证书私钥(密文F)中拿到 加密数据 这个数据的排序是特定的
        String tempSM2Private = "308188020100301306072a8648ce3d020106082a811ccf5501822d046e306c020101022100ac8f74aa3bc94c76510b7065176e59ba89893105ced490d93abe5fd7d9a734afa144034200040773e1ffc2da7284199c88d590b95ff1c3985b3885f4dea018de4dacf28e09034598785637c6c60de5b2d0d31e2361e4adec53f137190acf98ac2c0bbfa0d3f4";
        String tempSM2Public = "3059301306072a8648ce3d020106082a811ccf5501822d034200040773e1ffc2da7284199c88d590b95ff1c3985b3885f4dea018de4dacf28e09034598785637c6c60de5b2d0d31e2361e4adec53f137190acf98ac2c0bbfa0d3f4";
        // 测试
        // tempSM2Private = createPriHex;
        // tempSM2Public = createPubHex;
        // 例子
        // tempSM2Private = "308188020100301306072a8648ce3d020106082a811ccf5501822d046e306c020101022100fbd67a5299f59131fb7114670028c52dca67d67ec556b04a31b6279131f46082a14403420004807cc42241070eb3bd72c66b15aaef56bfeb77543948f6877c2a3b335562ae7e205a867fd1ee5841fd2b7444a10d4ed0f861b884414a2de293b96f5c8da8cdc3";

        String doubleEncryptedPrivateKey = "MIHGAgECBIHA/ftI80kQj268TH+EIg55eRYvrHmalBj79H0qPXFPKlYkx9jvn1NYwn/VComk5nntEty9koqTuyE4ncyulM36MgakBAbAZOWYCSg0XQAI6Xc76TAkyPPkkWloebsVP79s+l9dk5vaKcgtgIyyR/risvA7KRvrVM/SgA5WkaAe0Mlghqau7f5YRmaVOflP9IZT6f9Cd7IebJXiQRMriMoHzcdY/6VxBZnjshwYLh2JJjTop81bkKffeg6sLelbp8SE";
        // 测试
        doubleEncryptedPrivateKey = special.substring(80).replace(",", "");
        // 例子
        doubleEncryptedPrivateKey = "MIHGAgECBIHA/ftI80kQj268TH+EIg55eRYvrHmalBj79H0qPXFPKlYkx9jvn1NY,wn/VComk5nntEty9koqTuyE4ncyulM36MgakBAbAZOWYCSg0XQAI6Xc76TAkyPPk,kWloebsVP79s+l9dk5vaKcgtgIyyR/risvA7KRvrVM/SgA5WkaAe0Mlghqau7f5Y,RmaVOflP9IZT6f9Cd7IebJXiQRMriMoHzcdY/6VxBZnjshwYLh2JJjTop81bkKff,eg6sLelbp8SE".replace(",", "");

        ASN1Sequence seq = ASN1Sequence.getInstance(Base64.decode(doubleEncryptedPrivateKey));
        DEROctetString prioct = (DEROctetString) seq.getObjectAt(1);
        byte[] priprobyte = prioct.getOctets();


        ASN1Sequence seqPri = ASN1Sequence.getInstance(Hex.decode(tempSM2Private));
        BigInteger d = null;
        ASN1Encodable context = seqPri.getObjectAt(2);
        if (context instanceof DEROctetString) {
            d = dIntegerFrom((DEROctetString) context);
        }

        // 使用BouncyCastle提供的ECNamedCurveTable类获取SM2的参数规范
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(d, new ECParameterSpec(sm2Spec.getCurve(), sm2Spec.getG(), sm2Spec.getN(), sm2Spec.getH()));
        KeyFactory keyFact2 = KeyFactory.getInstance("EC", new BouncyCastleProvider());
        BCECPrivateKey privateKey = (BCECPrivateKey) keyFact2.generatePrivate(ecPrivateKeySpec);

        String base64SM2Pri = Base64.toBase64String(privateKey.getEncoded());

        KeyFactory kf = KeyFactory.getInstance("EC", BC);
        //初始化sm2加密
        BCECPrivateKey pri = (BCECPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(base64SM2Pri)));
        System.out.println("调整结构前的数据>> " + Hex.toHexString(priprobyte));
        String encString = decrypt(priprobyte);
        // String encString = Hex.toHexString(priprobyte);
        System.out.println("要解密的数据>> " + encString);
        SM2 sm21 = new SM2(privateKey, null);
        // String decrypt = Hex.toHexString(sm21.decrypt(Hex.decode(encString)));
        // System.out.println("解密出来的数据>> " + decrypt);
        byte[] deText = SM2CipherUtil.decryptWithOld(Hex.decode(encString), pri);
        System.out.println("第二种解密>> " + Hex.toHexString(deText));
    }

    @Test
    @SneakyThrows
    public void cfcaRSATest() {
        String cfcaPkcs10Base64 = "MIIDxjCCAq4CAQAwPjEYMBYGA1UEAwwPY2VydFJlcXVpc2l0aW9uMRUwEwYDVQQKDAxDRkNBIFRFU1QgQ0ExCzAJBgNVBAYTAkNOMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiZDe8fvxlJkt+2jz7Kr3Uqy6/883ZGYttapmsbQ2IZi32GnIVU8Zm//UsVgxTdXHGTTu+BFKFiFAWloF9k2tOPt7EGLnbfFoaGSTnm/SafgEiDUZlWsogVl6S6J+qIjLTWAhyF4WBr8gHtzJB1G/hjK/URREMmNijk9pzU7Z1scfpe+sr1rw39n+w+wbY5SEKIUd/8KbQTyUqQ2Fo72QpSaqha86uoKjZsG3S0Zl2JFbipM9u3j5gSJUXZ/nrnWCP7GkLybQSeLPEls9Q4DOfn/hMUWmaSVsS821vuWREdtRKANoTLWMbsjn0QGsejq6sugss95SQLYvccLkP+AJpwIDAQABoIIBQTATBgkqhkiG9w0BCQcTBjExMTExMTCCASgGCSqGSIb3DQEJPwSCARkwggEVAgEBBIIBDjCCAQoCggEBAIqK8yRhx1Tq+ZCD9bzHrRS6eGst8OjYsHZnkjXXcdWCCo3pjSZK1EhKwLINe7aiT3CspkI8DySfATpOHdOjh1fu0V3w/xX05Q5ePzanpGNIY5QQ6NVYTuB4LdvnaWFqIPZQ9ZrP1H5l2+ASRUM83DuDy/+hWS8IaITFx0ueDEjByc5P5rbEvhOlleYpdnb0P3BzXgz/HZelbLvyJBX6SZ+qQdrc3Dw45GfGm2azv5+bMzP1XKmKQ3Pb/ciR2FXObqnhA3XMx0UOwpLyRYwfHMiEh7sg80QMOQ5GR3oyZRCS4fRW9Fi3O8cWKihHNkBgYzfjadhbB/65pHZuMypNfd0CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAG27391+bNyW8G3WR9D0xmRgvpoW1OqGFJ0EdOnMd8Mnjq9fG5PLz4mUnx6R1a/kLhk33j+WK3yMoKrTlzLMt5bXjA8aUTGt2nL9nftwITF2PTm9Kk8Tfg9xBM8C2NIoXstbeGWF93lC/rVAz7IKd5eoWLiBxYfUPRsFyJ7VDLYFshFKLx0zz6s66vd3yijSQofCSMtV8MZARhANhdXBx9tuNDy6D5OgRGHseEaYnSjBMAt9KBsAWxeqFnebNuyzx2eZKQIDmy1kfkbyAoonf27dilXLptjxkf8v4/njKWvDWurgb4XOIa0U2CevPzkrIwh2jZ1aaB2n1wLmzl4BriA==";
        String createPriHex = "308204be020100300d06092a864886f70d0101010500048204a8308204a40201000282010100c050d4d6a439f2613cc2264548f9589df1d07f9373931056d19f095f5727ba969a9bdf75e03c66fc1885e7e2dde70b1d67aeebde1a6bb8b373ccad4224c7fc47d1af3d82877685876d92b08832c7c9c0ac688e4548b72488ea55c4a2415dac0648b59a5e49fc7d6a734ff7d11c729d16e5f97800ed8875c4559178e4e55c07e9a23687c03f75eced5d1136582a311e173347129d360c04430f96b671fbe44224f4f1864f26128a37e9d5273c40c9b1a27ae563913c22da94d2ecffb7e711ea0070012d739f5edf58e3802362ae037d7be1636267487b3356009082487e13226432650636b047c80f1ce4bf46071732dee2178eaed2ca6aa188404291ecfda6a10203010001028201001b9ee05aad7e8711b11cbf4b5f2634b48688e7f1c0fd23b11c6d2776f27909c2dc94bb7cde063ed241216642fe94d2d357d3571bf0967d1204f52603246e368d08100f990224a31be6c0552b5283c035b46d55d59c92752456e79794a827b06fbc9c9c7da349a8a793c52df5f20c82044a6650766da71626eb15381443deba61f321968657cb7fa44405e2a042660909827147bf59cf7d4159a2007bad06c702229b9f51ed878abe26c048f2b0da5f19e6f30eef297e702cdd5253fd2f3d6b95065cb4dbb3b07b7cfcb3cc1b5e09560b1a4c0d38ea86b01ad2ecfebde6b0b66c09731e937828168b461edb9abfbda00915332d9cef6b2cd374ed98f217b4821b02818100f7f59691bb05a213a501ed44f4197222abe80164716c3cc9d525a8678fb5fb0609a828aac0f052daaa3f5a0866b9a863137420ce6e4f3d035c6b1d90d475e616be6790a6728d62029d125b790714aa55f4b81da10e9d51b1f18e8d8d84cf792c305f0bcf2ab79b6d0564e765d813bbff911d7eacee2cedcd12830bc9ae8412fb02818100c68d52b71530a6ad496a26457c6415fc21934f294df357c6b7d3edf8369da6f4724a328145e94c1c68a7db050cb78c90aefccd7a4be2d3ff1c7a26988240fd245feb254f6233e678a26afc165c8a8b478824379214b9a678b7cd75a3346033be8f2d130b11e01df6b32d2b50de6010cb0532491eae5db3b2b3504a39384c5a130281802b3144812e2ad508c6fd86ef33f61ac39f5e529fc4822e349a446d095d273e0ad733cba1b2ea2a049d135c038aa15c8ba59fdcf7189004f8c8e87fc5f2bc76d00f6acaefb3949d3607566348b9cb07c0e15811113512826094cb582d06ad7d560d37013f9d194ea1b706d4a810f39f13ed9b4dada1ef819cef549481153d3c71028181008fce8e0952ccb0fc337f318704684d50175565718a26e4fd65e996a33f9381f7eccb76b710d6bfac97d4739abdb2379a1753bcd386211ce0da54529e054162d7b4ad5091a593e7aa9a2098c2ed75df19092f0a1cc0ec11f9bf4e8d948e7bdb1e7afeef9b05f06863653522f96886f5c2c5d9bd01fed29933a61252200d79a78702818100e5f8531a4c83215a41cc75a93e9e3333f79002cae143d82991e93ab87c07d988300ff3f996b888fc0f6947df5a0e890dd9cdc39180a9733fa10efefcfa30c2e80dbbf8c073aeea471eb31596f571cb41b63f03ddd4e020822cfd85876355da2b08139a199f7f90107e6f72898a297249bf0c9ceb297c1dd6b96f3aa1129c617a";
        String createPubHex = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100c050d4d6a439f2613cc2264548f9589df1d07f9373931056d19f095f5727ba969a9bdf75e03c66fc1885e7e2dde70b1d67aeebde1a6bb8b373ccad4224c7fc47d1af3d82877685876d92b08832c7c9c0ac688e4548b72488ea55c4a2415dac0648b59a5e49fc7d6a734ff7d11c729d16e5f97800ed8875c4559178e4e55c07e9a23687c03f75eced5d1136582a311e173347129d360c04430f96b671fbe44224f4f1864f26128a37e9d5273c40c9b1a27ae563913c22da94d2ecffb7e711ea0070012d739f5edf58e3802362ae037d7be1636267487b3356009082487e13226432650636b047c80f1ce4bf46071732dee2178eaed2ca6aa188404291ecfda6a10203010001";
        String createPkcs10 = "MIIC5jCCAc4CAQAwgYYxDzANBgNVBAgMBumHjeW6hjEPMA0GA1UEBwwG6YeN5bqGMSIwIAYJKoZIhvcNAQkBFhMxMzk4MzA1MzQ1NUAxNjMuY29tMQswCQYDVQQGEwJDTjEPMA0GA1UEChMGR0dLOTExMQ8wDQYDVQQLEwZHR0s5MTExDzANBgNVBAMTBkdHSzkxMTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMBQ1NakOfJhPMImRUj5WJ3x0H+Tc5MQVtGfCV9XJ7qWmpvfdeA8ZvwYhefi3ecLHWeu694aa7izc8ytQiTH/EfRrz2Ch3aFh22SsIgyx8nArGiORUi3JIjqVcSiQV2sBki1ml5J/H1qc0/30RxynRbl+XgA7Yh1xFWReOTlXAfpojaHwD917O1dETZYKjEeFzNHEp02DARDD5a2cfvkQiT08YZPJhKKN+nVJzxAybGieuVjkTwi2pTS7P+35xHqAHABLXOfXt9Y44AjYq4DfXvhY2JnSHszVgCQgkh+EyJkMmUGNrBHyA8c5L9GBxcy3uIXjq7SymqhiEBCkez9pqECAwEAAaAaMBgGCSqGSIb3DQEJBzELEwlzZWNyZXQxMjMwDQYJKoZIhvcNAQELBQADggEBACfaws3McdCoOiG/j7yxQDDDRkIJEFlhN7vj5q2wHCsIkXfU+r2V88RctPq99deJn34VOUdLZvZCUmyuENfpQtvzaULTGXPWKIMdL+4G8qwCOixIMkhDiZXzDK5MX2AVPnBcgdzf8nBJlyQo6EkpDXKP9fvJgj/+Q1Lfwi82ik/dp/wul73WnHyErKeIy38BORtGpVWgZuxmXLzkFC69joeqBYcgB2rucpJUSR8n4l6X75w5JuOjO2+eoSFoYyiABHcPqW6Kl9OQJifnRxN5TSUWanW7Dqa3H+kvYUjY2RaAbFPCDwuCwKxA17zEz0mkyb0U6F/G5s3Hfw+Jnt/OpqA=";
        String createPkcs10EncPrivateKey = "8ZiIxkqcHKBoDUUKO6hSLhQkHmOtGVP4STd12pi73EBLgSuntHbDTCJIrxHKk1ZWazTB/t2B3ocLFYRxvqPML7pUyRM8Wfj0l/X9kRflTC0KWS9YXPhFb8H/las6CD5EzAstCZQ1f5DMJGO66yeg1YaxR9xbVh2XbFwyHLKIU8TZjFSPR0Vc6/iGg5jXyUlCdWmKtwRsXCtgGOmEujNE8Eizl3FG0X1KTvrH5Rrt7TWLhbhR1dSWIRaRg35LcikMqell88KdBjK6NfmD0RrcClB1HMO7Cw4A9lx1DjyqBoO9ZlLK2R6wn4c6uPBfv+8c/RXm12FTQFdo4uzkh8ZwDDmkCove2JbP7VplCkfvRhCwbbGarJK2zuLbaIty6Ng8LWEXrLJ1kTIBi5W8S/bhJUovsM3kPNsduzMQkqzn+/U1ADkAHBmXboZZnL3dO9NcUwxMzHGzbyb6TtXSyvXWdBYulA5IqINP+DWc3x81Si8INX7nZIcYa3LUgcgSIFGVYwxiVCtMmPYKoe2HELhjnqQKMIq6bnhSmTEL9HKLGOumomLbzJ3o0Vlm/+ywzDXPTf0hFjwad7fskNCrRnixaKKVZDf/oasdw6yHKaFX86y58i4H+F8YxJ/6eERm7o99t24JHg8BZ50w4+0MjyZb9TP1uDdbQClKVV8h884d7wOyb05n0fzM82U/rCzjUzCzrzOOmLEuOkUpEs7nRHmSFW/xBhnDO1sOjWxHKjJfG9xqLf58+cTcREr7cy9+8qZHaqAwEKpfQ5yWZTUdkR3EhdPdjMMGdO0esTbjAwZLzTa7GHhcdzqvGO4qchw4OctFfd5/zYIdoozZ7KwjcFMwSsaPLjgtIgfvgUlxrT/hqTkMxWW+fM5JQu6JhoNTXjRMPEDTeFvA2KSCZO56tgrZZVXhwjZZpvQcsqajIIZeMVvVDWIuoX3R7BFc0QKW5sKIoG0MfESWNxZY9Ls1q26Ld5FJqDhaqiBCYnR8TOwOWjUNvcN1vR2U9ioxrb9Uf7tuIl0QK6i1lzliznirMUYbvgxQvM/vww0duo7wNceNSbfGLof9/1Os+ofRHdxhtHyaBwfH8m4smzLdmrPa5Hzyo2Sfixw4yDdEUyS6N2RxtDxOM6qEi0YJqymx0q6FwkLFWa491VGverchY2T/1/e3JgUAkQj+ZDGpWd1tY7dXZke0VO/nUvkEyRV+rcF/Ft+o7lZB1hnSr09KffaKCwjvhU3dwq6yKPOx+d8CTLNHQ+AdGyeDJzhE2mXbgLmeedJYjQAMcPqeH5pH3jQfYyofMuJEofxmeL0hlMrOcpsF9VTmqIulJP30HJwh2P5cfG9+BtnZlsCXX/OUBvTPWJ0fEWKs7U6zmGF3MOLeqtvA/ZVR3+hjj1QgiTsVzkzaKf+Yu5D+k2AaCaHrElvPiu9nAknHXj1X9/rr5J+eBqOhU2c8/ufQarYRD4H2T2/wJgmG5glt6bYPpvmg/CPFY25WMN5rQ30iaJuu0AL2yUE5KzC6M+GodxBgHnOTb4nC5siKbyqqq2Zit6g9SIOa2Pn4P8oHroBf2JBP/Y/IuJRBHq88zIX+8RYXjUBjaSaKdkwDi7JXnMrg/1gNZXEtZGpEFO8=";
        String encSessionKey = "JtlRSH7KsFXiOe9NgcAqcnayjutCJ10Y9UxPVwpFlWTt2yasVjFRxCUi8csrnclr1wKIapMeichuR9f2GEYWbca8/Wgw96M0RVxtyLeRowsyrKnR175+dmlMENGhYwJkJVOjEN5hb68Z3X1W7/vgsr2kxFQ3CooxnlpUJeUxUxmKmdXJ0DciekLkZBYP4RhmRB8DD9TeX8utofR5jdh0gvjxqzg+O1Ocq85YEbLouDeuJ4roXyQVuTl1NXMioVFbfwVERQD+tGScwhmyDjJLzgdVs71xlkibCV22ROrna2LKxH3TG+KcOYdaZnwd1b4I6D2DF4kRWf9Dc06mZ+3c7g==";
        PublicKey cfcaPub = null;
        // 获取CFCA PublicKey
        ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(cfcaPkcs10Base64));
        ASN1Primitive asn1Primitive;
        while ((asn1Primitive = asn1InputStream.readObject()) != null) {
            ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
            ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
            ASN1TaggedObject attr = (ASN1TaggedObject) requestInfo.getObjectAt(3);
            ASN1Sequence attrSeq = (ASN1Sequence) attr.getBaseObject();
            ASN1Sequence diySeq = (ASN1Sequence) attrSeq.getObjectAt(1);
            ASN1OctetString diySeqOctetStr = (ASN1OctetString) diySeq.getObjectAt(1);
            ASN1Sequence diyOctetStrSeq = (ASN1Sequence) new ASN1InputStream(diySeqOctetStr.getOctets()).readObject();
            ASN1OctetString tempPublic = (ASN1OctetString) diyOctetStrSeq.getObjectAt(1);
            ASN1Sequence cfcaPubAsn1 = ASN1Sequence.getInstance(tempPublic.getOctets());
            BigInteger n = ASN1Integer.getInstance(cfcaPubAsn1.getObjectAt(0)).getValue();
            BigInteger e = ASN1Integer.getInstance(cfcaPubAsn1.getObjectAt(1)).getValue();
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            cfcaPub = keyFactory.generatePublic(rsaPublicKeySpec);
            System.out.println("临时公钥>> " + Hex.toHexString(cfcaPub.getEncoded()));
        }


        KeyFactory kf = KeyFactory.getInstance("RSA", BC);
        PrivateKey pri = kf.generatePrivate(new PKCS8EncodedKeySpec(Hex.decode(createPriHex)));
        // 解密sessionKey
        Cipher instance1 = Cipher.getInstance("RSA/ECB/PKCS1Padding", BC);
        instance1.init(Cipher.DECRYPT_MODE, pri);
        byte[] sessionKey = instance1.doFinal(Base64.decode(encSessionKey));
        System.out.println("解密sessionKey>> " + Hex.toHexString(sessionKey));
        // 解密encPri
        Cipher instance2 = Cipher.getInstance("RC4");
        SecretKeySpec rc4key = new SecretKeySpec(sessionKey, "RC4");
        instance2.init(Cipher.DECRYPT_MODE, rc4key);
        byte[] deEncPri = instance2.doFinal(Base64.decode(createPkcs10EncPrivateKey));
        System.out.println("解密encPri>> " + Hex.toHexString(deEncPri));
        // 加密证书私钥
        PrivateKey encPri = kf.generatePrivate(new PKCS8EncodedKeySpec(deEncPri));

        ASN1Sequence seq1 = ASN1Sequence.getInstance(deEncPri);
        ASN1Sequence encPriParamAsn1 = ASN1Sequence.getInstance(ASN1OctetString.getInstance(seq1.getObjectAt(2)).getOctets());
        String priParamHex = Hex.toHexString(encPriParamAsn1.getEncoded());
        System.out.println("encPriParamAsn1>> " + priParamHex);
        System.out.println("原文>> " + priParamHex);
        // DESede加密 这个KEY是要反给用户的
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
        keyGenerator.init(168); // 168位密钥
        SecretKey secretKey = keyGenerator.generateKey();
        String secreKeyHex = Hex.toHexString(secretKey.getEncoded());
        System.out.println("对称KEY>> " + secreKeyHex);
        Cipher instance3 = Cipher.getInstance("DESede/ECB/PKCS7Padding", BC); // PKCS7Padding
        instance3.init(Cipher.ENCRYPT_MODE, secretKey);
        String encPriParamHex = Hex.toHexString(instance3.doFinal(Hex.decode(priParamHex)));
        System.out.println("对称加密后密文>> " + encPriParamHex);
        // 在用公钥加密这个对称KEY
        // PublicKey createPub = kf.generatePublic(new X509EncodedKeySpec(Hex.decode(createPubHex)));
        Cipher cipher3 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher3.init(Cipher.ENCRYPT_MODE, cfcaPub);
        String encSecreKey = Hex.toHexString(cipher3.doFinal(Hex.decode(secreKeyHex)));
        System.out.println("公钥加密后的对称KEY>> " + encSecreKey);

        // 组装ASN1Sequence
        ASN1Integer version = new ASN1Integer(91);
        ASN1OctetString octetString = new DEROctetString(Hex.decode(encSecreKey));
        ASN1Encodable[] sequenceElements = new ASN1Encodable[]{version, octetString};
        ASN1Sequence sequence = new DERSequence(sequenceElements);
        String encSecreKeyBase64 = Base64.toBase64String(sequence.getEncoded());
        // 组装ASN1Sequence
        ASN1Integer version2 = new ASN1Integer(91);
        ASN1OctetString octetString2 = new DEROctetString(Hex.decode(encPriParamHex));
        ASN1Encodable[] sequenceElements2 = new ASN1Encodable[]{version2, octetString2};
        ASN1Sequence sequence2 = new DERSequence(sequenceElements2);
        String encPriParamBase64 = Base64.toBase64String(sequence2.getEncoded());
        // 格式
        System.out.println(">>" + cfcaRsaSpecial(encSecreKeyBase64, encPriParamBase64));

        System.out.println("-------------------------------------------------------------------");

        // 临时公钥
        String tempPub = "30820122300d06092a864886f70d01010105000382010f003082010a02820101008a8af32461c754eaf99083f5bcc7ad14ba786b2df0e8d8b076679235d771d5820a8de98d264ad4484ac0b20d7bb6a24f70aca6423c0f249f013a4e1dd3a38757eed15df0ff15f4e50e5e3f36a7a46348639410e8d5584ee0782ddbe769616a20f650f59acfd47e65dbe01245433cdc3b83cbffa1592f086884c5c74b9e0c48c1c9ce4fe6b6c4be13a595e6297676f43f70735e0cff1d97a56cbbf22415fa499faa41dadcdc3c38e467c69b66b3bf9f9b3333f55ca98a4373dbfdc891d855ce6ea9e10375ccc7450ec292f2458c1f1cc88487bb20f3440c390e46477a32651092e1f456f458b73bc7162a28473640606337e369d85b07feb9a4766e332a4d7ddd0203010001";
        // 临时密钥
        String tempprikey = "308204bd020100300d06092a864886f70d0101010500048204a7308204a302010002820101008a8af32461c754eaf99083f5bcc7ad14ba786b2df0e8d8b076679235d771d5820a8de98d264ad4484ac0b20d7bb6a24f70aca6423c0f249f013a4e1dd3a38757eed15df0ff15f4e50e5e3f36a7a46348639410e8d5584ee0782ddbe769616a20f650f59acfd47e65dbe01245433cdc3b83cbffa1592f086884c5c74b9e0c48c1c9ce4fe6b6c4be13a595e6297676f43f70735e0cff1d97a56cbbf22415fa499faa41dadcdc3c38e467c69b66b3bf9f9b3333f55ca98a4373dbfdc891d855ce6ea9e10375ccc7450ec292f2458c1f1cc88487bb20f3440c390e46477a32651092e1f456f458b73bc7162a28473640606337e369d85b07feb9a4766e332a4d7ddd02030100010282010075ebcb047e8d51842723908e4b3e91dd80d21a382243ae9c944b97b1684da5f367d45ea41ba1b98a46c639472cb3c8f760ea332151497a5c3489521025219d8384ec83cf2aef9f608f983ca9e28b134e7c237d3910ce7764ac262c748dc7b2b397b455388e094a049cdfdded424bbd10931fc1d7adb750aad5c819d80d105ba40444ea5358720ae70feedc77681ff887b8593514f16eb617f92196d91eb9029620e7a5e6d35964be3a7c8e9f4f01f74d508ea6d2c426f8215bcfd743e1af395916805335be5439529808417f2afd12255daeddcb2563fac3322123b1f1d10eead4fea6f5e78f461ab805f3e1c7e2143253c0333a84fafdf72c823a55acc6006902818100d084dbfa1e6044446f3f792b51d1a6b0351b5b697cb32bac6db6a8212cb3a64fa25c0808d972b85759cf92ef12f05c51ab01262dd68ff37a44ef9461f92d8f7527aeda7a00301bb4f5594acf97cb046db75ae1304332e4c4502fa40e1667a7d75944edbe555d50c236fec16a91f8ce71a10c946eec66039c899f56963731abaf02818100aa16fc790d3ac31ac43d2a19145ffbb72796538c1a077a903e97f43035a424b50672d49b4cc1a06252949b21f045a0e4ce8d7108185e7ed22b150e295057ad1b6a3bbc144ee17461ca3cff4fc9a0ec84084fd0a20efc9ef4b277803550be69c07b496d8a6d19fd9fc285242be6600ee62e336c38cfed9248c63e7073fd5cd63302818100c65739eee5e9e7c5b89405aba0fdecb1fb31f4779877ccf90cbb0b9dd2a9676ea41f2a3d50a9009386081dd96e8857280fa2de3007c19175221149710fd5b87955afca7cf5a4cd1609b24fa8bad80f74554e0a991fb24f4c3481bc68f202d2f1d225f086752877e2e4f0da0586a7c5a6ebebcd5cdd5c03df6840b9d9d4d2585d02818011cdce27ada6d4fd5c453abedd4f1ebe113abf0c0f3d712ae0226fc10c1c5e6b447d889ba8f12ba49f184714ca5dbf75b3d94257a2956199064171f6091ee453d64ee2650dccb3adf8c961c02c4ed7f30a1bb0af05fa9378f1a5689a2c1582644f83641ff5439f34b5dbd1e97677df976086a97158f279397ce6d4468fe2c3d90281800ead731b79da03bd9f3add8441a72ddae253083de6cc02055f762d91970c2e2054baf02a4b205bcf2e75b9c8ef69358a27215861f0583fa1b2ee283b7f354f65b025da214d14f267d86fc1c97f098b95df2473e25f68ef4be5c31eae6b30292c1b4b14880a52a92e67db3cfbc2ec1cf0cb326d4190c5385206b4944e485f20b3";
        // 测试
        // tempprikey = createPriHex;

        String pkcs8 = Base64.toBase64String(Hex.decode(tempprikey));
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(pkcs8));
        KeyFactory keyf = KeyFactory.getInstance("RSA");
        PrivateKey priv = keyf.generatePrivate(priPKCS8);
        String encprikey1 = "MIIBCAICAJEEggEAO3dD4ikI6xD3TsSTI1MzKSRbU/Ek1K1zD98zHG4tgXaTDNr4TgccupYnyUdxRJVD5uRWC1oF7iCIln9ZzSAXsds67sa1wUVaFDSAPdAyjuovd1QyR6qwnlcZ06KeQiBBR1XLjRXOqW4GpivOQQvU4TN4oQvnrGz3aAWPh1eMQ7KJozX01cWMQRSAnAghcPN2NTmIVqdZqvoZ8bX9n8NF3Hh+hB08GfHrXkGIiBILJQOY+E/CDLZT2t737V+OLq9YuTwiOzMAOhbF70h6cyyXJdKzi09TGw3qOjMmznc73CENmpZl0WIBGBlb5gqXJA6PFCZE3i/gjY3ubf5lNiEj+Q==";
        String encprikey2 = "MIIEtwIBAgSCBLCT9RMIQQgU41PFF3+GsQLA03mAqs8s+ecRF/Ptoaj/GWBy7y5nYgb0OqRGXi5v6yUROBAjUMN+TdMeAmBaNNk2y/rSK6Taefbfr/1dI3yP3NNv7isxKwMJRh+8NEZeBjNb+ULFFlCtRTMNBBEpiNjeC4i+hIn8z+Oogqd96p7Ycgw1dUJ4GmipsPT775rl6IClGDDrpFFbET7RK0mBol9MADfvxwQEgVoMPIbuJkg32hSE2XDiK32Zc0r59BlJMuMjE8xjF4H1/JEnywrL4/mT9G+SC41EmHia2rixY6KvfFdWC4jtA9+sxzCu0hDV466GM9iHBHskwemPo9zQUIiQwIVrPH/CW/7YYoNL/tCTERj+mI8u+KwU3R3xziaewk799QGCX9eMhlWcKXPcKaOiZIRS6Gz13TXqtS5LOmVXoX0vW3ftrQepPsyDpT1yjPsHsJg8aP4DwjwAS8BMK2rLVqMMsLzkwED/QQQcgXl7YlqjV97YsntjsN/NfS/8P4x/FeKHrw9+MNPh0TwfQs432yplvwAbB4KldUFOhZDUWDLLiRPeJwtx4CQZNx7m0wH6P23VhELjz9gtyx/ZnU1A2B8ozdZ0oFO552fCtbSDdyk7Os4bECOZ8HyDaX5b+nMubQyc7NHpUFzuNptpXd+i7eW76JNzDw37KTvz3a/52qYtkVi0k9ZheUlDFE3oQwmFVFzCvDlLbIoMN/71Lr1m1tu/UYegDiFLw5MlDqUyq7w+VdZ7mkG9ps95DX+amQ/oC7QHh85tNe7akBtpsEG9wlm7anbXd6PIyzjbCpibYJJmBW52WoUZ70gg4SbrpJOwG16znMwuNN0FmjOToWjDr1hZZoQ9OBxAx2uT/lepDHvmDCyROgzk5OkLZes5lU+nNZcZQmDWPoa6qlAepJH6yrkHTdc/VhhaNwLkqwATNoOn/lRw8KwtYzbpQnFn7qo7dmxG+EEpDUO38DxpRaqCb7KR4yPBFsq6U9LAN/kEnJFCGVGhNdbv5jKIbogE/Ijd+CGxV4l+Uvz9NMu/G73oDyTBC03IqvimkT3sM9zDqDIlg9/5+6+9Um8362pe1LhlYdIRgDLjWX5nw0wx8X8N/cXz2TVxCB90BRhNmMwTmNg0XvIY9c87YuXck3vXQFgVYQSuN2htIcqmDroxSq7OFirFUeaJEdRB9hFNyH48BACjYa0ohlti3Rnu2zX3EjsTMzuuAye+0OrhZu53XmWjMmiJ+kmf8jzmr9UUlsW4QEKpeBN3petGyxpevFX4BkEmfBc6kTG+qQ8ljuW23+pyIQVn7ZjJ/8xJLS+U9A0STuB4fKhpOnQ8RW4/au7tyLqK1Tc8s977QQRT0LhsPKDJp9oZRSkIr+0NjEDfhDROzrWr3KpNggvTsRvN9C1IrvFdi7tAXak/FnLHueHIW4YDhzulPdca163HdZ2lGpty2aFKdL3+SiVuff3Wgj1JUNt04kvmf4SN8DL4XLEw9osBKy7l+xBt22htpBX9QFEo2AWC1+soTr+Uv0sA+OrBE+GdDCmXPDma1YTWmzt4TnYIeCczEJaH+mfa9ZimSmps1p1fqaM097rURls08ov+O+s=";
        // 测试
        encprikey1 = encSecreKeyBase64;
        encprikey2 = encPriParamBase64;
        // 例子
        encprikey1 = "MIIBBwIBWwSCAQAGVbH2+N6JGaO+iLj0RL110gzVK3pVNoyChpbIJ98klTcOie+E,KS7b0wnnawEZD2d0fcXSXKfWqrInUzGAfgyYSulRcbpT12MBHMB3+qmqIwc9U4nW,BqL+az5PlrpbIrD27l2PGGI2BYC4j2apkVBzzCQyp39jHy6XBijTr48IIk7iGvuZ,QMv6BVJvz5PQsjkCNiuG3g7tZiz/L52+EFLNeO+qDNGTjvTKEA5e6qIxzyLQQ2E9,k65ZT0u+JYwVcPQQnlgiDhOk6hKBis9o/QOGMftj4XYBInczcuEi54ajqLv1qkTx,MAsY3kFDaCitn1K948tnq15SCWDiBkac88h1".replace(",", "");
        encprikey2 = "MIIErwIBWwSCBKjKPWSn2cMAZXmPgbohj7IobwU3EAq8ZQ63gj+iop8BY2fJ/PaW,Hlp5SAAHlQf4asMjmY/8kumQF+weXgdAt9qSGWsr3vkEzvyC0ATKuUbG9O7Hzcf2,UTrR6JRGMYQITXaG1hHHYGrH/pBhtgm2tx6ivME0Mv8KBy+TQHkjXV5Z9yNYblAX,Vi7ZFJ5kaycFfm3mzieUapE3uCLCl6TP0+9faAyNCbmrNDObiIw//9J+L7fxAd7r,gnTS2cU4dO3gZWLHjPoxE2QJK8hKWLI3pVTITunFdem/SBJTY8uXDkfQD9RlqBuO,4RX33RCLjpQ+olfqGcIP8tTlD7HJyQnRWow8N877MvpCIEBKlcjIXWS0GjlZCKiU,FRhp6FM3OtI0UKh3cS60uxZK2axLZaQZbdQZRFLB5N7+UySvcdrUNkOnDBsZiJBf,5q744gNxem3yaKlFs9dAxV7eLD27bF8L+SrcW6p1LFLu/QloJlr1qir/w2UZvQxG,42htPHWXGPkRUxlHOQZg5we1Ah7o9wR+855rdRXBNh5nfQKVdClT2DqQdOFLD05R,GbIOZ6DDzorGkVA0cJO9UWtiSJopnypguRQ4bvvNMu+DfiR9ryrAvvGWjuh2aeZQ,EEJ1JjQXBlwBkBN00yMrHKnxyLN7P1LAlTv0euK3OXZoHZ23wnL/vZcP7gjGOKEW,pxUtkghMwDDDylKFvoudeO25VpHfTMnVE34oUwnvz1pQ4EkIhm+LIXAqCVkRePiI,QkhUd1kkjI8lqQpDWi8Q4I8hkJfVdhO/Gs73TRuZjgujfaHUxICROffAFDAYdjkR,7pZ2NjryUGXDzV2AJkRE8srzwsoque5J9TZtzFVMXOULe8LVNbYzBaq5QJWb/Kt4,Zfmz/HEjNEpThLB5RWRuzLuu14g/TWrDsDObJHYc/VMIIknhRe1u252md4RA5hi6,WfrQvoUn8uUSZyPyAWqb9r0r2qXAlmcSgyAuk9uTsIduxsW9CqpuUNZem4Q9MZyo,f8vVoJGbDbGyv5ImrVHMQRc0mNJ2YpL/eeCq5PQR6GHC/LJ/35A3oyAXVQAfxEU6,OgEr45TGgE73eIyoM47a1Slg88gZxE2xXBs3y4CV1fyg/cfhgGvzCLHn7dEj5bRV,paGlUK9n62q9ryEdVYUVRtwBMJ1vEpbMzesZGAiM8Su2pebO4BaeXEFbqNeTw0WV,FAH7WFudDNbHfXXKGrBRG1lRfpVKn+0jM0oC3ByNSiDGJqt7VyDnXNBdr3w2r2KW,4jiBOWN9bf5nlz9GHW28I33jocQgyroVEayF0yktmuEGEUjEmieM4xZCh1EhOVsI,byHZOYcZV+OY3uCPERyyFRD+QzQ9H81vn3NZl7e0OCiKYb9QvVj5mnfW7L6gSlKh,zI0PD8IK+X9NQGSoqexKovXndoSbuIOvwmQQqZNDHkoOD9exsTw3zxrd+1ScWCX9,4F9v2IpC5ifid6KIOVl2JOOSlBKf51sZttknjYGvW6rrWEYdntSCAWn0pQ57vNVC,8ofMrBEaPCsZ31v9Klo2JSG6GcP2/yQBZAkl35ahU0+GifnLRzZQZiIUuXphdWsj,JbJ0".replace(",", "");

        ASN1Sequence seq = ASN1Sequence.getInstance(Base64.decode(encprikey1));
        DEROctetString prioct = (DEROctetString) seq.getObjectAt(1);
        // encprikey1
        byte[] priprobyte = prioct.getOctets();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, priv);
        byte[] rv = cipher.doFinal(priprobyte);
        System.out.println("symmetry对称算法密钥：" + Hex.toHexString(rv));

        ASN1Sequence seq2 = ASN1Sequence.getInstance(Base64.decode(encprikey2));
        DEROctetString prioct2 = (DEROctetString) seq2.getObjectAt(1);
        byte[] priprobyte2 = prioct2.getOctets();
        String priprobytebase64 = Base64.toBase64String(priprobyte2);
        System.out.println("被对称密钥加密的密文>> " + priprobytebase64);


        //Key key = new SecretKeySpec(rv, "DESede");
        Cipher instance = Cipher.getInstance("DESede/ECB/PKCS7Padding", BC); // PKCS7Padding
        SecretKeySpec key = new SecretKeySpec(rv, "DESede");
        instance.init(Cipher.DECRYPT_MODE, key);
        byte[] pkcs1prikey = instance.doFinal(Base64.decode(priprobytebase64));

//	        ASN1EncodableVector asn1ev = new ASN1EncodableVector();
//	        ASN1OctetString data = new DEROctetString(pkcs1prikey);
//	        asn1ev.add(data);
//	        DERSequence derseq = new DERSequence(asn1ev);
//	        System.out.println(Base64.toBase64String(derseq.getEncoded()));

        System.out.println(Base64.toBase64String(pkcs1prikey));
    }

    @Test
    @SneakyThrows
    public void judgeCfcaPkcs10() {
        // try {
        //     // RSA
        //     String pkcs10 = "MIIDxjCCAq4CAQAwPjEYMBYGA1UEAwwPY2VydFJlcXVpc2l0aW9uMRUwEwYDVQQKDAxDRkNBIFRFU1QgQ0ExCzAJBgNVBAYTAkNOMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiZDe8fvxlJkt+2jz7Kr3Uqy6/883ZGYttapmsbQ2IZi32GnIVU8Zm//UsVgxTdXHGTTu+BFKFiFAWloF9k2tOPt7EGLnbfFoaGSTnm/SafgEiDUZlWsogVl6S6J+qIjLTWAhyF4WBr8gHtzJB1G/hjK/URREMmNijk9pzU7Z1scfpe+sr1rw39n+w+wbY5SEKIUd/8KbQTyUqQ2Fo72QpSaqha86uoKjZsG3S0Zl2JFbipM9u3j5gSJUXZ/nrnWCP7GkLybQSeLPEls9Q4DOfn/hMUWmaSVsS821vuWREdtRKANoTLWMbsjn0QGsejq6sugss95SQLYvccLkP+AJpwIDAQABoIIBQTATBgkqhkiG9w0BCQcTBjExMTExMTCCASgGCSqGSIb3DQEJPwSCARkwggEVAgEBBIIBDjCCAQoCggEBAIqK8yRhx1Tq+ZCD9bzHrRS6eGst8OjYsHZnkjXXcdWCCo3pjSZK1EhKwLINe7aiT3CspkI8DySfATpOHdOjh1fu0V3w/xX05Q5ePzanpGNIY5QQ6NVYTuB4LdvnaWFqIPZQ9ZrP1H5l2+ASRUM83DuDy/+hWS8IaITFx0ueDEjByc5P5rbEvhOlleYpdnb0P3BzXgz/HZelbLvyJBX6SZ+qQdrc3Dw45GfGm2azv5+bMzP1XKmKQ3Pb/ciR2FXObqnhA3XMx0UOwpLyRYwfHMiEh7sg80QMOQ5GR3oyZRCS4fRW9Fi3O8cWKihHNkBgYzfjadhbB/65pHZuMypNfd0CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAG27391+bNyW8G3WR9D0xmRgvpoW1OqGFJ0EdOnMd8Mnjq9fG5PLz4mUnx6R1a/kLhk33j+WK3yMoKrTlzLMt5bXjA8aUTGt2nL9nftwITF2PTm9Kk8Tfg9xBM8C2NIoXstbeGWF93lC/rVAz7IKd5eoWLiBxYfUPRsFyJ7VDLYFshFKLx0zz6s66vd3yijSQofCSMtV8MZARhANhdXBx9tuNDy6D5OgRGHseEaYnSjBMAt9KBsAWxeqFnebNuyzx2eZKQIDmy1kfkbyAoonf27dilXLptjxkf8v4/njKWvDWurgb4XOIa0U2CevPzkrIwh2jZ1aaB2n1wLmzl4BriA==";
        //     // pkcs10 = "MIICnzCCAYcCAQAwQDELMAkGA1UEBhMCQ04xDzANBgNVBAoTBkdHSzkxMTEPMA0GA1UECxMGR0dLOTExMQ8wDQYDVQQDEwZHR0s5MTEwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCOD752e3cqYF23OA3LXs5VvzjLHINmcPuTiDvJJ7vY6DQCI3bcoGiYbGg2MzNp6ZXfrZwZcgjqFxeoG9Lf/NAq+OcJTtoTH4WpW5SroXp8C2VzoArLNLK0LXRVqLZCW0QUzqkXnxMlmhSWofa7gdPJK3nud33tEi1V8vuBUiV6CLa5/QnaDQEzNh6TSidLFbDrJswYmXiwQ04HPbx49251/97cfxRKLkLlI3pCX9AU3SjBWzwlzdwJfPGAYwgL6CI4EVY97HvzVKUhfGfUKpg4pgsk0uI/tCcIcPyrB7nIZmeH1yqMyAOmOTPzS24ALIS4Oivqen5DP6tsSX48g2+FAgMBAAGgGjAYBgkqhkiG9w0BCQcxCxMJc2VjcmV0MTIzMA0GCSqGSIb3DQEBCwUAA4IBAQBuqCSZk7A7u46wvYU4/j9ZdRpjdu8NLp24G5kVvy4tDjNIR6My9vrSVhWQQjbYjdOcY9FpPXPyEhsH99b/jydiaoZI9IP4Bm1uPfe1ybUm0F1EzY1d8yCiI2Ai2KUdRf7xAElQ5c4N3QR2i4N5KnpfNppL61bQi3uNxM3JSQ3EC7R3VEhEb4Lco12JXpYVgOLOJNbhzqJjjR10dIjcb8D2/T0q09kjn68wxOprawGTNPz/gL4CPmAQKXXYWClr/osvCh8Ub84AQh+rfC4J9JhGiNvts3oxzW7Oy19a9Lp14erzAjaPgxkE6Dt3r3dcyOckkTasscFHHHJuv2IUXHuT";
        //     ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(pkcs10));
        //     ASN1Primitive asn1Primitive;
        //     PublicKey cfcaPub = null;
        //     while ((asn1Primitive = asn1InputStream.readObject()) != null) {
        //         ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
        //         ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
        //         ASN1TaggedObject attr = (ASN1TaggedObject) requestInfo.getObjectAt(3);
        //         ASN1Sequence attrSeq = (ASN1Sequence) attr.getBaseObject();
        //         System.out.println(attrSeq.size());
        //         if ((attrSeq.getObjectAt(1) instanceof ASN1Sequence) && (attrSeq.getObjectAt(0) instanceof ASN1Sequence)) {
        //             System.out.println("cfca的");
        //             return;
        //         } else {
        //             System.out.println("普通的");
        //             return;
        //         }
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // SM2
        String sm2Pkcs10 = "MIIBszCCAVgCAQAwPjEYMBYGA1UEAwwPY2VydFJlcXVpc2l0aW9uMRUwEwYDVQQKDAxDRkNBIFRFU1QgQ0ExCzAJBgNVBAYTAkNOMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEgHzEIkEHDrO9csZrFarvVr/rd1Q5SPaHfCo7M1Virn4gWoZ/0e5YQf0rdEShDU7Q+GG4hEFKLeKTuW9cjajNw6CBtzATBgkqhkiG9w0BCQcTBjExMTExMTCBnwYJKoZIhvcNAQk/BIGRMIGOAgEBBIGIALQAAAABAAAHc+H/wtpyhBmciNWQuV/xw5hbOIX03qAY3k2s8o4JAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAARZh4VjfGxg3lstDTHiNh5K3sU/E3GQrPmKwsC7+g0/QAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAMBggqgRzPVQGDdQUAA0cAMEQCIGYDuRY5AXixhP1k/Nri0lyrtg7JXvyFHArEf/BDL31DAiBHUCClX17mvDzJ3ol3XZywjcGpdCFl8F+LYxPhlOCZtQ==";
        // sm2Pkcs10 = "MIIBXTCCAQMCAQAwgYYxDzANBgNVBAgMBumHjeW6hjEPMA0GA1UEBwwG6YeN5bqGMSIwIAYJKoZIhvcNAQkBFhMxMzk4MzA1MzQ1NUAxNjMuY29tMQswCQYDVQQGEwJDTjEPMA0GA1UEChMGR0dLOTExMQ8wDQYDVQQLEwZHR0s5MTExDzANBgNVBAMTBkdHSzkxMTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABO9mGcLj6r8O11t6XHHLZz63iXqlp+9kf5OnbvOM+8hlZikLAOhH6+Ocf5vjWFM20Z7QuZ10tq0Y4Ac0DZYqVBegGjAYBgkqhkiG9w0BCQcxCxMJc2VjcmV0MTIzMAoGCCqBHM9VAYN1A0gAMEUCIH5t+/sJMlVIX34KbHnxYJ3IenoO7uvCLPYGz1YeXuXVAiEA2q27ujBbz3q6jcbM31kMW0c4puQh6p5OkhtpXUuHVUI=";
        ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(sm2Pkcs10));
        ASN1Primitive asn1Primitive;
        while ((asn1Primitive = asn1InputStream.readObject()) != null) {
            ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
            ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
            ASN1TaggedObject attr = (ASN1TaggedObject) requestInfo.getObjectAt(3);
            ASN1Sequence attrSeq = (ASN1Sequence) attr.getBaseObject();
            System.out.println(attrSeq.size());
            if ((attrSeq.getObjectAt(1) instanceof ASN1Sequence) && (attrSeq.getObjectAt(0) instanceof ASN1Sequence)) {
                System.out.println("cfca的");
                return;
            } else {
                System.out.println("普通的");
                return;
            }
        }


    }

    public static String decrypt(byte[] encryptData) throws Exception {
        BigInteger x;
        BigInteger y;
        byte[] encryptedBytes;
        byte[] hash;
        byte[] c1Xbyte = new byte[32];
        byte[] c1Ybyte = new byte[32];
        System.arraycopy(encryptData, 0, c1Xbyte, 0, 32);
        System.arraycopy(encryptData, 32, c1Ybyte, 0, 32);
        x = new BigInteger(1, c1Xbyte);
        y = new BigInteger(1, c1Ybyte);
        encryptedBytes = new byte[encryptData.length - 96];
        hash = new byte[32];
        System.arraycopy(encryptData, 64, hash, 0, 32);
        System.arraycopy(encryptData, 96, encryptedBytes, 0, encryptedBytes.length);

        System.out.println("x>> " + Hex.toHexString(c1Xbyte));
        System.out.println("x>> " + Hex.toHexString(x.toByteArray()));
        System.out.println("y>> " + Hex.toHexString(c1Ybyte));
        System.out.println("y>> " + Hex.toHexString(y.toByteArray()));
        System.out.println("encry>> " + Hex.toHexString(encryptedBytes));
        System.out.println("HASH>> " + Hex.toHexString(hash));
        final String s = Hex.toHexString(c1Xbyte)
                + Hex.toHexString(c1Ybyte)
                + Hex.toHexString(encryptedBytes)
                + Hex.toHexString(hash);
        System.out.println("调整结构后>> " + s);
        return s;
    }

    private static BigInteger dIntegerFrom(DEROctetString encoding) {
        BigInteger d;
        ASN1Sequence seq = ASN1Sequence.getInstance(encoding.getOctets());
        if (seq.size() < 2)
            throw new SecurityException("encoding context not valid");
        ASN1Encodable value = seq.getObjectAt(1);
        if (value instanceof ASN1Integer) {
            d = ((ASN1Integer) value).getPositiveValue();
        } else if (value instanceof ASN1OctetString) {
            d = new BigInteger(1, ((ASN1OctetString) value).getOctets());
        } else {
            throw new SecurityException("encoding context#d not valid");
        }
        return d;
    }

    public static String concatenate(String q, String hash, String encData) {
        byte[] qBytes = Hex.decode(q);
        final byte[] hashBytes = Hex.decode(hash);
        final byte[] encDataBytes = Hex.decode(encData);
        byte[] concatenate = Arrays.concatenate(qBytes, hashBytes, encDataBytes);
        return Hex.toHexString(concatenate);
    }

    private static String cfcaSpecial(String encData3) {
        try {
            byte[] encDataBytes = Hex.decode(encData3);
            byte[] encDataX = new byte[32];
            System.arraycopy(encDataBytes, 0, encDataX, 0, encDataX.length);
            byte[] encDataY = new byte[32];
            System.arraycopy(encDataBytes, 32, encDataY, 0, encDataY.length);
            byte[] encDataDecode = new byte[encDataBytes.length - 96];
            System.arraycopy(encDataBytes, 64, encDataDecode, 0, encDataDecode.length);
            byte[] encDataHash = new byte[32];
            System.arraycopy(encDataBytes, 64 + encDataBytes.length - 96, encDataHash, 0, encDataHash.length);
            String concatenateHex = Hex.toHexString(Arrays.concatenate(encDataX, encDataY, encDataHash, encDataDecode));
            System.out.println("解密后调整结构>> " + concatenateHex);

            byte[] concatenate = Hex.decode(concatenateHex);
            // 版本号
            ASN1Integer version = new ASN1Integer(2);

            ASN1OctetString octetString = new DEROctetString(concatenate);

            // 组装ASN1Sequence
            ASN1Encodable[] sequenceElements = new ASN1Encodable[]{version, octetString};
            ASN1Sequence sequence = new DERSequence(sequenceElements);
            String base64String = Base64.toBase64String(sequence.getEncoded());
            String addLine = lineFeed(base64String);
            int length = addLine.length();

            final String lengthStr = "0000000000000000" + length;
            String suffix = "0000000000000001" +
                    "0000000000000001" +
                    "0000000000000000" +
                    "0000000000000000" +
                    lengthStr.substring(lengthStr.length() - 16);

            return suffix + addLine;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("sequence对象异常");
        }
    }

    public static String cfcaRsaSpecial(String secretKey, String encData) {
        String model = "0000000000000000";
        String secretKeyLine = lineFeed(secretKey);
        String secretKeyLineLength = model + secretKeyLine.length();
        String encDataLine = lineFeed(encData);
        String encDataLineLength = model + encDataLine.length();
        String suffix = "0000000000000001" +
                "0000000000000001" +
                "0000000000000000" +
                // "0000000000000306" +
                secretKeyLineLength.substring(secretKeyLineLength.length() - 16) +
                secretKeyLine +
                encDataLineLength.substring(encDataLineLength.length() - 16) +
                encDataLine;
        return suffix;
    }

    public static String lineFeed(String originalString) {
        int segmentLength = 64; // 每个分割段的长度

        StringBuilder modifiedString = new StringBuilder(); // 用于存储修改后的字符串

        // 循环遍历原始字符串，每次取出64个字符进行处理
        for (int i = 0; i < originalString.length(); i += segmentLength) {
            // 获取当前分割段的结束索引
            int endIndex = Math.min(i + segmentLength, originalString.length());
            // 截取当前分割段的子字符串
            String segment = originalString.substring(i, endIndex);
            // 在子字符串末尾添加逗号
            modifiedString.append(segment).append(",");
        }
        return modifiedString.toString();
    }

    public static String sm2Enc(String decode, ECPublicKeyParameters aPub) {
        // 创建SM2加密引擎
        SM2Engine engine = new SM2Engine();

        // 初始化加密引擎，使用公钥进行加密
        engine.init(true, new ParametersWithRandom(aPub, new SecureRandom()));
        // 进行加密
        final byte[] message = Hex.decode(decode);
        byte[] ciphertext = new byte[0];
        try {
            ciphertext = engine.processBlock(message, 0, message.length);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
            System.out.println("加密失败");
        }
        return Hex.toHexString(ciphertext);
    }

    public static String getHash(byte[] x, byte[] y, String M) {
        byte[] message = Hex.decode(M);
        final byte[] data = Arrays.concatenate(x, message, y);

        // 创建一个SM3Digest对象
        SM3Digest digest = new SM3Digest();
        // 使用update方法将消息字节数组添加到摘要中
        digest.update(data, 0, data.length);
        // 完成摘要计算并获得哈希值
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        // 将哈希值转换为十六进制字符串
        return Hex.toHexString(hash);
    }

    public static String analysisSM2EncKey(String encryptedPrivateKey, String signPri) throws Exception {
        ASN1Sequence asn1_epk = (ASN1Sequence) ASN1Sequence.fromByteArray(Base64.decode(encryptedPrivateKey));
        KeyFactory kf = KeyFactory.getInstance("EC", new BouncyCastleProvider());
        //初始化sm2加密
        BCECPrivateKey pri = (BCECPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Hex.decode(signPri)));

        ECPrivateKeyParameters aPriv = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(pri);
        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(false, aPriv);

        ASN1TaggedObject dtos = (ASN1TaggedObject) asn1_epk.getObjectAt(1);
        ASN1Sequence asn1_encdata = (ASN1Sequence) dtos.getBaseObject();

        ASN1Set d1_pubkeyencs = (ASN1Set) asn1_encdata.getObjectAt(1);
        ASN1Sequence asn1_pubkeyenc = (ASN1Sequence) d1_pubkeyencs.getObjectAt(0);
        DEROctetString dertostr = (DEROctetString) asn1_pubkeyenc.getObjectAt(3);
        ASN1Sequence encOIDSequence = ASN1Sequence.getInstance(asn1_pubkeyenc.getObjectAt(2));
        // 非对称OID
        ASN1ObjectIdentifier objectIdentifier = ASN1ObjectIdentifier.getInstance(encOIDSequence.getObjectAt(0));
        byte[] xyEncDataHash;
        byte[] sm4encdata;
        // 非对称密文
        byte[] encSm4Bytes = dertostr.getOctets();
        // 区分方式一：通过OID
        if (objectIdentifier.equals(PKCSObjectIdentifiers.rsaEncryption)) {
            if (encSm4Bytes.length == 256) {

            }
            xyEncDataHash = null;
        } else if (objectIdentifier.on(new ASN1ObjectIdentifier("1.2.156.10197.1.301")) || objectIdentifier.equals(new ASN1ObjectIdentifier("1.2.156.10197.1.301"))) {
            // SM2区分方式二：通过密文长度
            // 113 这是不带asn1结构的sm2密文C1C3C2
            if (encSm4Bytes.length == 113) {
                byte[] xbyte = new byte[33];
                System.arraycopy(encSm4Bytes, 0, xbyte, 0, xbyte.length);
                byte[] ybyte = new byte[32];
                System.arraycopy(encSm4Bytes, xbyte.length, ybyte, 0, ybyte.length);
                byte[] hashBytes = new byte[32];
                System.arraycopy(encSm4Bytes, xbyte.length + ybyte.length, hashBytes, 0, hashBytes.length);
                byte[] encDateBytes = new byte[16];
                System.arraycopy(encSm4Bytes, xbyte.length + ybyte.length + hashBytes.length, encDateBytes, 0, encDateBytes.length);

                byte[] xy = new byte[65];
                xy[0] = 4;
                System.arraycopy(xbyte, 1, xy, 1, 32);
                System.arraycopy(ybyte, 0, xy, 1 + 32, 32);

                xyEncDataHash = Arrays.concatenate(xy, encDateBytes, hashBytes);
            } else {
                // 这是带asn1结构的sm2密文
                ASN1Sequence dd = (ASN1Sequence) ASN1Sequence.fromByteArray(encSm4Bytes);

                ASN1Integer x = (ASN1Integer) dd.getObjectAt(0);
                byte[] xbyte = x.getPositiveValue().toByteArray();
                ASN1Integer y = (ASN1Integer) dd.getObjectAt(1);
                byte[] ybyte = y.getPositiveValue().toByteArray();
                DEROctetString hash = (DEROctetString) dd.getObjectAt(2);
                DEROctetString pubencdata = (DEROctetString) dd.getObjectAt(3);

                byte[] xy = new byte[65];
                xy[0] = 4;
                System.arraycopy(xbyte, xbyte.length == 32 ? 0 : 1, xy, 1, 32);
                System.arraycopy(ybyte, ybyte.length == 32 ? 0 : 1, xy, 1 + 32, 32);
                byte[] encDateBytes = new byte[16];
                System.arraycopy(pubencdata.getOctets(), 0, encDateBytes, 0, 16);
                byte[] hashBytes = new byte[32];
                System.arraycopy(hash.getOctets(), 0, hashBytes, 0, 32);
                xyEncDataHash = Arrays.concatenate(xy, encDateBytes, hashBytes);
            }
        } else {
            throw new IllegalArgumentException("未知OID:" + objectIdentifier);
        }
        // sm2解密得到对称密钥的key
        byte[] sm4key = sm2Engine.processBlock(xyEncDataHash, 0, xyEncDataHash.length);
        System.out.println("sm4key HEX>> " + Hex.toHexString(sm4key));
        System.out.println("sm4key Base64>> " + Base64.toBase64String(sm4key));


        ASN1Sequence encdata = (ASN1Sequence) asn1_encdata.getObjectAt(3);
        // thk's todo 2024/5/25 10:48 其实最好是根据OID直接初始化解密上下文
        // 对称OID
        ASN1Sequence symmetryOIDSequence = ASN1Sequence.getInstance(encdata.getObjectAt(1));
        ASN1ObjectIdentifier symmetryOID = ASN1ObjectIdentifier.getInstance(symmetryOIDSequence.getObjectAt(0));
        ASN1TaggedObject symmetryEncData = (ASN1TaggedObject) encdata.getObjectAt(2);
        DEROctetString symmetryEncDataOS = (DEROctetString) symmetryEncData.getBaseObject();
        // 对称密文
        sm4encdata = symmetryEncDataOS.getOctets();
        if (symmetryOID.equals(new ASN1ObjectIdentifier("1.2.156.10197.1.104"))) {
            // sm4解密
            Cipher cipher = Cipher.getInstance("SM4/ECB/NoPadding", new BouncyCastleProvider());
            SecretKeySpec newKey = new SecretKeySpec(sm4key, "SM4");
            cipher.init(Cipher.DECRYPT_MODE, newKey);
            sm4encdata = cipher.doFinal(sm4encdata);
            System.out.println("sm4解密输出HEX>> " + Hex.toHexString(sm4encdata));
            System.out.println("sm4解密输出Base64>> " + Base64.toBase64String(sm4encdata));
            // 这里根据不同CA来截取
            if (encSm4Bytes.length == 113) {
                String hexString = Hex.toHexString(Arrays.copyOfRange(sm4encdata, 0, 32));
                System.out.println("sm4解密截取输出HEX>> " + hexString);
                System.out.println("sm4解密截取输出Base64>> " + Base64.toBase64String(Arrays.copyOfRange(sm4encdata, 0, 32)));
                return hexString;
            } else {
                String hexString = Hex.toHexString(Arrays.copyOfRange(sm4encdata, 32, sm4encdata.length));
                System.out.println("sm4解密截取输出HEX>> " + hexString);
                System.out.println("sm4解密截取输出Base64>> " + Base64.toBase64String(Arrays.copyOfRange(sm4encdata, 32, sm4encdata.length)));
                return hexString;
            }
        } else if (symmetryOID.equals(OIWObjectIdentifiers.desECB)) {
            // thk's todo 2024/5/25 11:01
        }
        return "";
    }

    public static String getQFromCfcaPkcs10(String pkcs10) {
        ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(pkcs10));
        byte[] x = new byte[32];
        byte[] y = new byte[32];
        ASN1Primitive asn1Primitive;
        try {
            while ((asn1Primitive = asn1InputStream.readObject()) != null) {
                ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
                ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
                ASN1TaggedObject attr = (ASN1TaggedObject) requestInfo.getObjectAt(3);
                ASN1Sequence attrSeq = (ASN1Sequence) attr.getBaseObject();
                ASN1Sequence diySeq = (ASN1Sequence) attrSeq.getObjectAt(1);
                ASN1OctetString diySeqOctetStr = (ASN1OctetString) diySeq.getObjectAt(1);
                ASN1Sequence diyOctetStrSeq = (ASN1Sequence) new ASN1InputStream(diySeqOctetStr.getOctets()).readObject();
                ASN1OctetString tempPublic = (ASN1OctetString) diyOctetStrSeq.getObjectAt(1);

                System.arraycopy(tempPublic.getOctets(), 8, x, 0, 32);
                System.arraycopy(tempPublic.getOctets(), 72, y, 0, 32);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取asn1错误");
        }
        return Hex.toHexString(Arrays.concatenate(x, y));
    }

    public static PublicKey parsePubFromQ(String paramQ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final byte[] xy = Hex.decode(paramQ);
        // 创建一个新的字节数组，长度比原始公钥字节数组大1
        byte[] newBytes = new byte[xy.length + 1];
        // 在新数组的第一个位置插入 0x04
        newBytes[0] = 0x04;
        System.arraycopy(xy, 0, newBytes, 1, xy.length);

        KeyFactory fact = KeyFactory.getInstance("EC", new BouncyCastleProvider());
        // 使用BouncyCastle提供的ECNamedCurveTable类获取SM2的参数规范
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");

        // 使用SM2的参数规范创建EC参数规范
        ECNamedCurveSpec ecSpec = new ECNamedCurveSpec(
                sm2Spec.getName(),
                sm2Spec.getCurve(),
                sm2Spec.getG(),
                sm2Spec.getN(),
                sm2Spec.getH(),
                sm2Spec.getSeed()
        );

        ECPoint ecPoint = ECPointUtil.decodePoint(ecSpec.getCurve(), newBytes);

        // 使用SM2算法创建EC公钥规范
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(ecPoint, ecSpec);

        // 使用BouncyCastle提供的KeyFactory类根据EC公钥规范生成公钥对象
        return fact.generatePublic(ecPublicKeySpec);
    }

    @Test
    @SneakyThrows
    public void verifyP10() {
        // 协同生成的
        // 二代SM2 false
        String P10 = "MIIBGTCBxQIBATBlMRIwEAYDVQQDDAnlp5rnq5HngpwxDzANBgNVBAsMBui3r+i+vjEPMA0GA1UECgwG5rWL6K+VMQ8wDQYDVQQHDAbph43luoYxDzANBgNVBAgMBumHjeW6hjELMAkGA1UEBgwCQ04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQ41IlFMddxJ17/dXi6P31w/kRCWNwdcOkEBVPTdZRupkQw9XX2grvvDjFMTRfFU84RsY0f0w/o7EwkkuKp/NVEMAwGCCqBHM9VAYN1BQADQQBwfGXFsT551OsOxOW7zkbOakm+ORPboejJk/y2RItkFQFEdtXGyBfSAVnfgQr+woCYWKhgXzfPMG/UZeprMdV1";
        P10 = "MIIBGTCBxQIBATBlMRIwEAYDVQQDDAnlp5rnq5HngpwxDzANBgNVBAsMBui3r+i+vjEPMA0GA1UECgwG5rWL6K+VMQ8wDQYDVQQHDAbph43luoYxDzANBgNVBAgMBumHjeW6hjELMAkGA1UEBgwCQ04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAARqQUFv4q5EbBjCmS+lCRi/+4hCvDABFAEvEmF3PIHblEQioR+OVhoS2ZskPhsn77XdNduEgBg2/oNTTkfhy1CeMAwGCCqBHM9VAYN1BQADQQBy+THeZLGqSNqLdSQd/vjAte6lrl4JD6hRZgCXoJqwTUjGIn4ye6BSUKHZnjX85WTU2PAoRyafOgA87REloonJ";
        // 一代SM2 false
        P10 = "MIIBGTCBxQIBATBlMRIwEAYDVQQDDAnlp5rnq5HngpwxDzANBgNVBAsMBui3r+i+vjEPMA0GA1UECgwG5rWL6K+VMQ8wDQYDVQQHDAbph43luoYxDzANBgNVBAgMBumHjeW6hjELMAkGA1UEBgwCQ04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQd4YcIs+72tTRSDUDiJkLNha19fhgSkn88ABo/Tc45C6lXuI8Jh1UREkl3dTt25W7f58sB9WTc+/9GXSbrwymAMAwGCCqBHM9VAYN1BQADQQDkc/pxhnmbXlgCfnSrouwrcENB9VarrbkGg2tqZoS37ODwQcxMd0vvuN8PRITqWAqwEJffgzKQih2PXP6vsRrv";
        P10 = "MIIBGTCBxQIBATBlMRIwEAYDVQQDDAnlp5rnq5HngpwxDzANBgNVBAsMBui3r+i+vjEPMA0GA1UECgwG5rWL6K+VMQ8wDQYDVQQHDAbph43luoYxDzANBgNVBAgMBumHjeW6hjELMAkGA1UEBgwCQ04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASZKEMsO06VxxvX6XsMegzBRuOWtEC6/VGnyV8hYcwDbXyO1is5miU3EADoyGKNacIMwe33eahDZOzSQn0fsp22MAwGCCqBHM9VAYN1BQADQQAENuwDT4Q6TDgYWjPPGy2yY/yGlOK2b5KoGPLqOgiW2/B0sO3OGYMQd0ceSDpyTcf4bMJoHD4R1ucYFth8EtyW";
        // 一代RSA true
        // P10 = "MIICqDCCAZACAQEwZTESMBAGA1UEAwwJ5aea56uR54KcMQ8wDQYDVQQLDAbot6/ovr4xDzANBgNVBAoMBua1i+ivlTEPMA0GA1UEBwwG6YeN5bqGMQ8wDQYDVQQIDAbph43luoYxCzAJBgNVBAYMAkNOMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArqE4NpK8Co9OZPfgc0PqI0qv66Aej6dVya+7IXtnW7BD3Rp61RR7EewIYm5gFJf/o/e0erQrxyYzDd+3gbXNPr/oTkLnCXsk4Q1frOidvy7vz+vMzf1+lnJOYtAEeP+hW6PB/z2PT2DrkQiQEkfvowG8y38HUW0iP7MBFn2+HcjBOlKQGUjPfa0nFh8Bi0hWcKDjGWheQcrjNvqMtSgQ1uNF8xsbmjCx2w89s+2rtTElkUsCOBPJrO2+D/VjSCouCsdd4ou5OCpOIa++Jkx6uyMOKKJX+JzFVnA7+18OqiZU3t2QDvBJmEYJhPtcvDDf+sXVHLCvFzpkLbd5NaCJPQIDAQABMA0GCSqGSIb3DQEBBQUAA4IBAQBNUg6hZfd0kRQAdweB5JIH5hi3PcI/kt9e3ihj3RPFwvsNsk6rUuJHMhPsrEEcebRGaxzl9V+s1ejF8PF/CVKgz8Mlu+wfxjp2I9LPur3yqUYMo8IDy6sfVATFu7pJHjLVxLOS5LxEJXZTX1GEZM83lrHWiiPSAQ+WTXD/uEiWuvRbhoVgzvKIWEjfjApvbZQf4cQBLSnLLNTFYJBuLvNcjGA+aC6MtbeN5+EaE5/c8aIsZ53vzgbKTw1g9haO/Y/7BffAT3qQ+J5CErJYlOZKbCqcPfcyp1CZxSt+iLwsEZAog/5te3cP7HC6bVuCK7dWfCPeWlF5y+nDXLG/Ft9l";
        // 我自己生成的 true
        P10 = "MIIBQjCB6QIBADCBhjEPMA0GA1UECAwG6YeN5bqGMQ8wDQYDVQQHDAbph43luoYxIjAgBgkqhkiG9w0BCQEWEzEzOTgzMDUzNDU1QDE2My5jb20xCzAJBgNVBAYTAkNOMQ8wDQYDVQQKEwZHR0s5MTExDzANBgNVBAsTBkdHSzkxMTEPMA0GA1UEAxMGR0dLOTExMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEZ6mEgBhcP8vYM53yGZE8grXFvelJfbHkUbK3u7ytgiXOnJ5QJXfRcNQx3gi5B1oZgqAZtKPNOGjBbV8R6NNq5qAAMAoGCCqBHM9VAYN1A0gAMEUCIGrOP+Z57B1cgg4UAdESb+WsBjl0bgzFN1OdiGb7MZqTAiEA2JBJiBwi5cWturtEPeRl7PzAmgasNgTZU9VpKpK6Ml0=";
        // 16修改0后
        P10 = "MIHRMH4CAQEwHjEPMA0GA1UEAwwGMTIzNDU2MQswCQYDVQQGDAJDTjBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABCfD4lNs0tLdS76tGnkJlUv3hCvqOOgkFzp22qFWDkSd/uVberG4/TvDQ8o5OeuefKDPP8DQfxHMgXKzDlJGGMwwDAYIKoEcz1UBg3UFAANBAE8uFvuN8aqFtIzpELzMWQ3LZXt++EDy8smvy/RsLsNwrB78T3qLKCEk7gGP/nHhI/4Btz62sqXLjx57E6qLJ1g=";
        PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(Base64.decode(P10));
        SubjectPublicKeyInfo subjectPublicKeyInfo = pkcs10CertificationRequest.getSubjectPublicKeyInfo();
        KeyFactory keyFact = KeyFactory.getInstance("EC", BC);
        PublicKey p10Pub = keyFact.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        System.out.println("p10PubBase64>> " + Base64.toBase64String(p10Pub.getEncoded()));
        System.out.println("p10PubHex>> " + Hex.toHexString(p10Pub.getEncoded()));
        System.out.println(pkcs10CertificationRequest.isSignatureValid((new JcaX509ContentVerifierProviderBuilder()).setProvider(BC).build(subjectPublicKeyInfo)));
    }

    @Test
    @SneakyThrows
    public void analysisSM2EncKey() {
        String signPri = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg/re2HViGP2uA7jkANG+Fe8pBPPhjpv6/bTuQRalnDIugCgYIKoEcz1UBgi2hRANCAAS2f9eYIYqgQqYvv/8zZFFVmd/8/+ci3HqsEC96sI80icd+8+sVW1bunQXuxExnD3AMJQ4Ob+pmp/LP8DoAHOqE";
        // 协同 P7 encData SM2 加密证书私钥
        String encPri = "MIIDugYJKoZIhvcNAQcEoIIDqzCCA6cCAQExgcgwgcUCAQAwQTA5MQswCQYDVQQGEwJDTjEqMCgGA1UEAwwh56e75Yqo5LqS6IGU572R5a6J5YWo5pyN5Yqh5bmz5Y+wAgQEaAt7MAoGCCqBHM9VAYItBHEEN5BByfJPSMT0N4qKUKTD2WAKRfLqEMBh71TMpD6mNqxg7PJM3XkvHZduZ5zJHYpy5Csehk9jrLukmkYca6J25uhhsmXMXLYVFtnjQ2xUlhfx4hnX0M7+7T02rsG5ProTIJo355gHzMmcQvKRNtfiMDEMMAoGCCqGSIb3DQIGMEkGCSqGSIb3DQEHBjAKBggqgRyBRQFoAYAwCnJLMPwUuW3pgyV0TZwHJES/RJsOGfhUIaLnWva3lL1nFNqzlAIn4zPWhEYjLnQNoIIBlTCCAZEwggE4oAMCAQICBARoC3swCgYIKoEcz1UBg3UwOTELMAkGA1UEBhMCQ04xKjAoBgNVBAMMIeenu+WKqOS6kuiBlOe9keWuieWFqOacjeWKoeW5s+WPsDAeFw0xNjExMTEwMjM3NThaFw0xNzExMTEwMjM3NThaMDkxCzAJBgNVBAYTAkNOMSowKAYDVQQDDCHnp7vliqjkupLogZTnvZHlronlhajmnI3liqHlubPlj7AwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAARGmDONmcOCdfg47uw3WA9b/IwGJ8RSdFdjVqViF8OyCw8O3L4T9AXFhIT4WY6/jxuHqoQ3a1vh43NQsohiILnioy4wLDALBgNVHQ8EBAMCBsAwHQYDVR0OBBYEFE9rbqRIy1nWWgNtbv81+H8w8sVBMAoGCCqBHM9VAYN1A0cAMEQCIG+gniXBXG5NTVWvwH8w0uEQc9BML24HiBW+50gUmn5CAiBmbsTHKl+LcqBYpV+v/7ZT+YdLhj6oqs4eoiHVLDiTpzGB5DCB4QIBADBBMDkxCzAJBgNVBAYTAkNOMSowKAYDVQQDDCHnp7vliqjkupLogZTnvZHlronlhajmnI3liqHlubPlj7ACBARoC3swCgYIKoZIhvcNAgagPzAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcGMCMGCSqGSIb3DQEJBDEWBBQIDnvZH1gNtggpYu0UhrY1QHrbpTAKBggqgRzPVQGCLQRAifMZI//lRfEsZ9JVjf1jLqaEiCYdBYswUc5M4W80CHJGXVl2OVvAEg8t3jqjrSp6TLZ9Dd6B6+zTiZZeYihYPw==";
        // 协同 P7 encData RSA
        encPri = "MIIGxQYJKoZIhvcNAQcEoIIGtjCCBrICAQExggFbMIIBVwIBADBBMDkxCzAJBgNVBAYTAkNOMSowKAYDVQQDDCHnp7vliqjkupLogZTnvZHlronlhajmnI3liqHlubPlj7ACBARoC3swCwYJKoZIhvcNAQEBBIIBACrbkP/pSh0V3L4nBVF08Bi8PKfuiRWcI9NJ/2eblwwfNksgeFTLDH2kHMptJeWn3vg4RBxl70c8Dp1JmNLygJmoNlAp986y6/38Tb/rbBvzNOU97rkHApDCnx9r5YAt/x/5ewFGRbeP4h7KVET3QFjVRedEJMYVOOeQd0m1UABTKKPoSmI1c7P4v+e5qHGEQVkktQe7GnuO/lQd3e1o5eg4KZD9Q39UcexriHD8+fYKcqF9Yw525oQ2bohWEi1o1dkj3YWUVj0RKhN0EdiEykffXFuCOV5pq0Fc9l5G8oLZrooSnXZxnpH2r9W1Yl3aNmPrwobCr2GNAks+HrQsYkIxCTAHBgUrDgMCGjCCAoAGCSqGSIb3DQEHBjAHBgUrDgMCBoCCAmjaMBiQT9AugF+9V1OEDVV3wUu6QoLaTrtRZkKSRlcQpwla8mcVYjwqFi7/k2AOCMufDd2viY3ox2pyv54n1DgNcFZ6N40VUt0qtO6J5NLWI5ZkcHSE0KwWngq3ncpz4XCrQBeMIYQV6UAK3YFQJzxRCOMO17h0CpzrRDr4sN0IMOQGf3Zgxh3c2iyj1IFRxBGqy0mb6q+Vk6/saIPLs4cm8JZ8TKCGw8E4ZdCsceBDVfGOysstDD/zwd9V8xwhKPIPWVtj/4/eomXsY+dp56iRhr4R+UNl5Z3RjYKUDfo397VxQRtBrGu80l4GLDLO7jI35dwoD8MjA4AcDH2NaNZB72RSSH63G6q8r+bvF6pEg6ClkjP+/Gpdi/eQNq9g3vqzi8pgouliHQLLCmR1XrX8tz54/2EIFTlROECg7OLRLkoBG4j+U7pRXvByWlo34/BvOXXGqsq/yr1yETESz7NjUUqwSSQ/kI5+ReytFCKTHxfHg47lPbk4C+H5fO5ceubIT4qUl8CeQP4vGaBdTZUAoLUMIyswnlzvB+7WaEtDRZKHi6dCpp4LF8okcs4mMI+vrm4r6LcHD7OvHqx6easbMjLXT4kMLn5j0RXy7EqVYzYinZk+5XAc65t83B47LHImSslOjwgT7WuJw2vmfg+nNix5MsqHEodcKwrYXQsLhlrVmF4N3Huz+dhghp2XIBTNiYIwnpH+dDfeNbDVdVeArVjX5YMU2z+OlO4N+vwIbCaEu+Y1FqKs3fOGVQFNq0su2Z2uNsq7s6/lMvbSc+6XJrHEw9alZ5D5nTu7BqFSGImYHuEjHaLdoIIBlTCCAZEwggE4oAMCAQICBARoC3swCgYIKoEcz1UBg3UwOTELMAkGA1UEBhMCQ04xKjAoBgNVBAMMIeenu+WKqOS6kuiBlOe9keWuieWFqOacjeWKoeW5s+WPsDAeFw0xNjExMTEwMjM3NThaFw0xNzExMTEwMjM3NThaMDkxCzAJBgNVBAYTAkNOMSowKAYDVQQDDCHnp7vliqjkupLogZTnvZHlronlhajmnI3liqHlubPlj7AwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAARGmDONmcOCdfg47uw3WA9b/IwGJ8RSdFdjVqViF8OyCw8O3L4T9AXFhIT4WY6/jxuHqoQ3a1vh43NQsohiILnioy4wLDALBgNVHQ8EBAMCBsAwHQYDVR0OBBYEFE9rbqRIy1nWWgNtbv81+H8w8sVBMAoGCCqBHM9VAYN1A0cAMEQCIG+gniXBXG5NTVWvwH8w0uEQc9BML24HiBW+50gUmn5CAiBmbsTHKl+LcqBYpV+v/7ZT+YdLhj6oqs4eoiHVLDiTpzGCASQwggEgAgEAMEEwOTELMAkGA1UEBhMCQ04xKjAoBgNVBAMMIeenu+WKqOS6kuiBlOe9keWuieWFqOacjeWKoeW5s+WPsAIEBGgLezAHBgUrDgMCGqA/MBgGCSqGSIb3DQEJAzELBgkqhkiG9w0BBwYwIwYJKoZIhvcNAQkEMRYEFE4/9UObHqnBTAknRBSEsDmNlPJ3MAsGCSqGSIb3DQEBAQSBgDeJkWzjqFHQpDO1edr7NZtOaNsdrhianlDZx7/ULIV3JqiZPojnQxFOL5HRUBxm5Tt4YZZbysH/drSTrRDNCbTyxfNWUWR8eKpxc2OiDSY3ja9syxw7uCtVVz50YFl+CkmMnxMor4j+IS0ooa4QmM+l9OHA5hxVuUb1octeJxEd";
        // CMS P7 encData RSA
        encPri = "MIIEbgYKKoEcz1UGAQQCBKCCBF4wggRaAgEBMYHSMIHPAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQM+H+NOHuKVG91xHTVW1SlzALBgkqgRzPVQGCLQMEejB4AiBGhxrZDMLeOsTSNCs3RyeOERJyUGMv/HW4trWuQwW46AIgbNUqcCms5NW9ezOxCls6Fa12OcRu2i8wBKjOH+Dh1iwEIH8sXCZiB55F9Ty8/H3TQQnAqeOc+xun//iBmdqpTlhMBBBWbpD1WXPmDYLuKMQ3/3cxMQwwCgYIKoEcz1UBgxEwWQYKKoEcz1UGAQQCATAJBgcqgRzPVQFogEB/mdbFFrBnBARmGrmk86lQf5nWxRawZwQEZhq5pPOpUGsW90Fu6cRUd4q1PCpwJfHYRF0a05iyWQOaA6UqWq4ioIICZjCCAmIwggIGoAMCAQICECEOvUigLs+O06Q1QzCasN8wDAYIKoEcz1UBg3UFADAtMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAMMBU1DU0NBMB4XDTIzMDgxNzAzNTcwNFoXDTMzMDgxNDAzNTcwNFowMTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMRIwEAYDVQQDDAlTWFNNMlNJR04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAARt2y2t+Ii4iiG4VCsbda8IhS7dPdm4I1nYZ5e9f0ztfD6wBMDdBnssYNfJdvXI/coFGfbGXOIdZmi7gyH2DG6Po4IBADCB/TAfBgNVHSMEGDAWgBQwiP4/YqAl9PMrPaltpqBMC0zKtTAdBgNVHQ4EFgQUN4uhRjxJXEJds41dZTspl7Hg2/AwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDAYIKoEcz1UBg3UFAANIADBFAiBCflesXG3BQu0TT6DhAeFOhoKh8NPaaQZeoaqy3OKnSwIhALX6/xPLUJe0V2jfL9vbFKf2ZvdXBLFS62wlq5QQlCGiMYGsMIGpAgEBMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQIQ69SKAuz47TpDVDMJqw3zAKBggqgRzPVQGDETALBgkqgRzPVQGCLQEESDBGAiEAvycDI0bG7TfGoAdcq4lPFokKJnCkyv6nRAEkIfPp2X4CIQDVwb9egr5F9BIMxledEbmZ8NnxhBWCGpFdIGafCxhmfw==";
        analysisSM2EncKey(encPri, Hex.toHexString(Base64.decode(signPri)));
    }

}
