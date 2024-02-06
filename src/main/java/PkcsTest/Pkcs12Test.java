package PkcsTest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX500NameUtil;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Date;

/**
 * @author TangHaoKai
 * @version V1.0 2023-12-01 16:56
 **/
public class Pkcs12Test {
    private static final Provider BC = new BouncyCastleProvider();
    private static final char[] PASSWD = {'1', '2', '3', '4', '5', '6'};
    /**
     * personal keys
     */
    private static final RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
            new BigInteger("b4a7e46170574f16a97082b22be58b6a2a629798419be12872a4bdba626cfae9900f76abfb12139dce5de56564fab2b6543165a040c606887420e33d91ed7ed7", 16),
            new BigInteger("11", 16));
    private static final RSAPrivateCrtKeySpec privKeySpec = new RSAPrivateCrtKeySpec(
            new BigInteger("b4a7e46170574f16a97082b22be58b6a2a629798419be12872a4bdba626cfae9900f76abfb12139dce5de56564fab2b6543165a040c606887420e33d91ed7ed7", 16),
            new BigInteger("11", 16),
            new BigInteger("9f66f6b05410cd503b2709e88115d55daced94d1a34d4e32bf824d0dde6028ae79c5f07b580f5dce240d7111f7ddb130a7945cd7d957d1920994da389f490c89", 16),
            new BigInteger("c0a0758cdf14256f78d4708c86becdead1b50ad4ad6c5c703e2168fbf37884cb", 16),
            new BigInteger("f01734d7960ea60070f1b06f2bb81bfac48ff192ae18451d5e56c734a5aab8a5", 16),
            new BigInteger("b54bb9edff22051d9ee60f9351a48591b6500a319429c069a3e335a1d6171391", 16),
            new BigInteger("d3d83daf2a0cecd3367ae6f8ae1aeb82e9ac2f816c6fc483533d8297dd7884cd", 16),
            new BigInteger("b8f52fc6f38593dabb661d3f50f8897f8106eee68b1bce78a95b132b4e5b5d19", 16));
    /**
     * CA keys
     */
    private static final RSAPublicKeySpec caPubKeySpec = new RSAPublicKeySpec(
            new BigInteger("b259d2d6e627a768c94be36164c2d9fc79d97aab9253140e5bf17751197731d6f7540d2509e7b9ffee0a70a6e26d56e92d2edd7f85aba85600b69089f35f6bdbf3c298e05842535d9f064e6b0391cb7d306e0a2d20c4dfb4e7b49a9640bdea26c10ad69c3f05007ce2513cee44cfe01998e62b6c3637d3fc0391079b26ee36d5", 16),
            new BigInteger("11", 16));
    private static final RSAPrivateCrtKeySpec caPrivKeySpec = new RSAPrivateCrtKeySpec(
            new BigInteger("b259d2d6e627a768c94be36164c2d9fc79d97aab9253140e5bf17751197731d6f7540d2509e7b9ffee0a70a6e26d56e92d2edd7f85aba85600b69089f35f6bdbf3c298e05842535d9f064e6b0391cb7d306e0a2d20c4dfb4e7b49a9640bdea26c10ad69c3f05007ce2513cee44cfe01998e62b6c3637d3fc0391079b26ee36d5", 16),
            new BigInteger("11", 16),
            new BigInteger("92e08f83cc9920746989ca5034dcb384a094fb9c5a6288fcc4304424ab8f56388f72652d8fafc65a4b9020896f2cde297080f2a540e7b7ce5af0b3446e1258d1dd7f245cf54124b4c6e17da21b90a0ebd22605e6f45c9f136d7a13eaac1c0f7487de8bd6d924972408ebb58af71e76fd7b012a8d0e165f3ae2e5077a8648e619", 16),
            new BigInteger("f75e80839b9b9379f1cf1128f321639757dba514642c206bbbd99f9a4846208b3e93fbbe5e0527cc59b1d4b929d9555853004c7c8b30ee6a213c3d1bb7415d03", 16),
            new BigInteger("b892d9ebdbfc37e397256dd8a5d3123534d1f03726284743ddc6be3a709edb696fc40c7d902ed804c6eee730eee3d5b20bf6bd8d87a296813c87d3b3cc9d7947", 16),
            new BigInteger("1d1a2d3ca8e52068b3094d501c9a842fec37f54db16e9a67070a8b3f53cc03d4257ad252a1a640eadd603724d7bf3737914b544ae332eedf4f34436cac25ceb5", 16),
            new BigInteger("6c929e4e81672fef49d9c825163fec97c4b7ba7acb26c0824638ac22605d7201c94625770984f78a56e6e25904fe7db407099cad9b14588841b94f5ab498dded", 16),
            new BigInteger("dae7651ee69ad1d081ec5e7188ae126f6004ff39556bde90e0b870962fa7b926d070686d8244fe5a9aa709a95686a104614834b0ada4b10f53197a5cb4c97339", 16));
    /**
     * intermediate keys
     */
    private static final RSAPublicKeySpec intPubKeySpec = new RSAPublicKeySpec(
            new BigInteger("8de0d113c5e736969c8d2b047a243f8fe18edad64cde9e842d3669230ca486f7cfdde1f8eec54d1905fff04acc85e61093e180cadc6cea407f193d44bb0e9449b8dbb49784cd9e36260c39e06a947299978c6ed8300724e887198cfede20f3fbde658fa2bd078be946a392bd349f2b49c486e20c405588e306706c9017308e69", 16),
            new BigInteger("ffff", 16));
    private static final RSAPrivateCrtKeySpec intPrivKeySpec = new RSAPrivateCrtKeySpec(
            new BigInteger("8de0d113c5e736969c8d2b047a243f8fe18edad64cde9e842d3669230ca486f7cfdde1f8eec54d1905fff04acc85e61093e180cadc6cea407f193d44bb0e9449b8dbb49784cd9e36260c39e06a947299978c6ed8300724e887198cfede20f3fbde658fa2bd078be946a392bd349f2b49c486e20c405588e306706c9017308e69", 16),
            new BigInteger("ffff", 16),
            new BigInteger("7deb1b194a85bcfd29cf871411468adbc987650903e3bacc8338c449ca7b32efd39ffc33bc84412fcd7df18d23ce9d7c25ea910b1ae9985373e0273b4dca7f2e0db3b7314056ac67fd277f8f89cf2fd73c34c6ca69f9ba477143d2b0e2445548aa0b4a8473095182631da46844c356f5e5c7522eb54b5a33f11d730ead9c0cff", 16),
            new BigInteger("ef4cede573cea47f83699b814de4302edb60eefe426c52e17bd7870ec7c6b7a24fe55282ebb73775f369157726fcfb988def2b40350bdca9e5b418340288f649", 16),
            new BigInteger("97c7737d1b9a0088c3c7b528539247fd2a1593e7e01cef18848755be82f4a45aa093276cb0cbf118cb41117540a78f3fc471ba5d69f0042274defc9161265721", 16),
            new BigInteger("6c641094e24d172728b8da3c2777e69adfd0839085be7e38c7c4a2dd00b1ae969f2ec9d23e7e37090fcd449a40af0ed463fe1c612d6810d6b4f58b7bfa31eb5f", 16),
            new BigInteger("70b7123e8e69dfa76feb1236d0a686144b00e9232ed52b73847e74ef3af71fb45ccb24261f40d27f98101e230cf27b977a5d5f1f15f6cf48d5cb1da2a3a3b87f", 16),
            new BigInteger("e38f5750d97e270996a286df2e653fd26c242106436f5bab0f4c7a9e654ce02665d5a281f2c412456f2d1fa26586ef04a9adac9004ca7f913162cb28e13bf40d", 16));


    @SneakyThrows
    public static void main(String[] args) {
        // set up the keys
        KeyFactory fact = KeyFactory.getInstance("RSA", BC);
        PrivateKey privKey = fact.generatePrivate(privKeySpec);
        PublicKey pubKey = fact.generatePublic(pubKeySpec);
        // 证书链
        X509Certificate[] chain = createCertChain(fact, pubKey);
        // pfx
        PKCS12PfxPdu pfx = createPfx(privKey, pubKey, chain);
        FileUtil.writeBytes(pfx.getEncoded(), "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\pfxTest01.pfx");
        // 制作完成

        // 尝试读取
        byte[] bytes = FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\pfxTest01.pfx");
        KeyStore ks = KeyStore.getInstance("PKCS12", BC);
        ks.load(new ByteArrayInputStream(bytes), PASSWD);
        // 读取私钥
        PrivateKey recPrivateKey = (PrivateKey) ks.getKey("ggk911's Key", PASSWD);
        // 相等
        System.out.println(privKey.equals(recPrivateKey));
        // 读取证书
        Certificate[] certificateChain = ks.getCertificateChain("ggk911's Key");
        // 相等
        System.out.println(Arrays.equals(chain, certificateChain));


    }

    @SneakyThrows
    private static PKCS12PfxPdu createPfx(PrivateKey privKey, PublicKey pubKey, X509Certificate[] chain) {
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        // 根证书
        PKCS12SafeBagBuilder taCertBagBuilder = new JcaPKCS12SafeBagBuilder(chain[2]);
        taCertBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("Bouncy Primary Certificate"));
        // 中间证书
        PKCS12SafeBagBuilder caCertBagBuilder = new JcaPKCS12SafeBagBuilder(chain[1]);
        caCertBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("Bouncy Intermediate Certificate"));
        // 用户证书
        PKCS12SafeBagBuilder eeCertBagBuilder = new JcaPKCS12SafeBagBuilder(chain[0]);
        eeCertBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("ggk911's Key"));
        eeCertBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, extUtils.createSubjectKeyIdentifier(pubKey));

        PKCS12SafeBagBuilder keyBagBuilder = new JcaPKCS12SafeBagBuilder(privKey, (
                new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, CBCBlockCipher.newInstance(new DESedeEngine()))
        ).build(PASSWD));

        keyBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("ggk911's Key"));
        keyBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, extUtils.createSubjectKeyIdentifier(pubKey));
        // construct the actual key store
        PKCS12PfxPduBuilder pfxPduBuilder = new PKCS12PfxPduBuilder();

        PKCS12SafeBag[] certs = new PKCS12SafeBag[3];
        certs[0] = eeCertBagBuilder.build();
        certs[1] = caCertBagBuilder.build();
        certs[2] = taCertBagBuilder.build();
        pfxPduBuilder.addEncryptedData((new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, CBCBlockCipher.newInstance(new RC2Engine())).build(PASSWD)), certs);

        pfxPduBuilder.addData(keyBagBuilder.build());

        PKCS12PfxPdu pfxPdu = pfxPduBuilder.build(new BcPKCS12MacCalculatorBuilder(), PASSWD);
        return pfxPdu;
    }

    /**
     * 生成RSA密钥对
     *
     * @return 密钥对
     */
    @SneakyThrows
    private static KeyPair getRsaKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BC);
        generator.initialize(16);
        KeyPair keyPair = generator.generateKeyPair();
        return keyPair;
    }

    /**
     * 生成证书链
     *
     * @param pubKey 公钥
     * @return 证书链
     */
    @SneakyThrows
    private static X509Certificate[] createCertChain(KeyFactory fact, PublicKey pubKey) {
        // CA公私钥
        PrivateKey caPrivKey = fact.generatePrivate(caPrivKeySpec);
        PublicKey caPubKey = fact.generatePublic(caPubKeySpec);
        PrivateKey intPrivKey = fact.generatePrivate(intPrivKeySpec);
        PublicKey intPubKey = fact.generatePublic(intPubKeySpec);

        X509Certificate[] chain = new X509Certificate[3];

        chain[2] = createMasterCert(caPubKey, caPrivKey);
        chain[1] = createIntermediateCert(intPubKey, caPrivKey, chain[2]);
        chain[0] = createCert(pubKey, intPrivKey, intPubKey);
        return chain;
    }

    /**
     * 生成用户证书
     *
     * @param pubKey    用户公钥
     * @param caPrivKey CA私钥
     * @param caPubKey  CA公钥
     * @return 用户证书
     */
    @SneakyThrows
    private static X509Certificate createCert(PublicKey pubKey, PrivateKey caPrivKey, PublicKey caPubKey) {
        // 颁发者
        X500NameBuilder issuerBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        issuerBuilder.addRDN(BCStyle.C, "CN");
        issuerBuilder.addRDN(BCStyle.O, "China");
        issuerBuilder.addRDN(BCStyle.OU, "Intermediate");
        issuerBuilder.addRDN(BCStyle.EmailAddress, "13983053455@163.com");

        // 使用者
        X500NameBuilder subjectBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        subjectBuilder.addRDN(BCStyle.C, "CN");
        subjectBuilder.addRDN(BCStyle.O, "China");
        subjectBuilder.addRDN(BCStyle.L, "Chongqing");
        subjectBuilder.addRDN(BCStyle.CN, "GGK911");
        subjectBuilder.addRDN(BCStyle.EmailAddress, "13983053455@163.com");
        // V3版本
        X509v3CertificateBuilder v3CertBuilder = new JcaX509v3CertificateBuilder(
                issuerBuilder.build(),
                BigInteger.valueOf(3),
                // DateUtil.parse("202312070940000", "yyyyMMddHHmmssSSS"),
                new Date(),
                new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365)),
                subjectBuilder.build(),
                pubKey);
        // 扩展
        JcaX509ExtensionUtils utils = new JcaX509ExtensionUtils();
        // 主体密钥标识符
        v3CertBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                utils.createSubjectKeyIdentifier(pubKey));

        // 颁发机构密钥标识符
        v3CertBuilder.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                utils.createAuthorityKeyIdentifier(caPubKey));

        // cRLDistributionPoints CRL分发点
        v3CertBuilder.addExtension(
                Extension.cRLDistributionPoints,
                false,
                new DERSequence());

        // Certificate Policies 证书策略
        // v3CertBuilder.addExtension(
        //         Extension.certificatePolicies,
        //         true,
        //         new CertificatePolicies(new PolicyInformation[]{PolicyInformation.getInstance(new GeneralName(GeneralName.uniformResourceIdentifier, "http://127.0.0.1"))})
        // );

        // Authority Info Access 授权信息访问
        // v3CertBuilder.addExtension(
        //         Extension.authorityInfoAccess,
        //         true,
        //         new AuthorityInformationAccess(new AccessDescription[]{new AccessDescription(AccessDescription.id_ad_ocsp, new GeneralName(GeneralName.dNSName, "http://127.0.0.1/caissue.htm")), new AccessDescription(AccessDescription.id_ad_caIssuers, new GeneralName(GeneralName.dNSName, "http://127.0.0.1:20443"))})
        // );

        // Key Usage 密钥用法
        v3CertBuilder.addExtension(
                Extension.keyUsage,
                true,
                new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation)
        );

        // Basic Constraints 基本约束
        v3CertBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(3)
        );

        // Extended Key Usage 增强型密钥用法
        // v3CertBuilder.addExtension(
        //         Extension.extendedKeyUsage,
        //         true,
        //         new ExtendedKeyUsage(KeyPurposeId.id_kp_timeStamping)
        // );
        ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BC).build(caPrivKey);
        X509CertificateHolder certificateHolder = v3CertBuilder.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificateHolder);
        return certificate;
    }

    /**
     * 生成中间证书
     *
     * @param pubKey    公钥
     * @param caPrivKey CA私钥
     * @param caCert    CA证书
     * @return 中间证书
     */
    @SneakyThrows
    private static X509Certificate createIntermediateCert(PublicKey pubKey, PrivateKey caPrivKey, X509Certificate caCert) {
        // 使用者
        X500NameBuilder subjectBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        subjectBuilder.addRDN(BCStyle.C, "CN");
        subjectBuilder.addRDN(BCStyle.O, "China");
        subjectBuilder.addRDN(BCStyle.OU, "Intermediate");
        subjectBuilder.addRDN(BCStyle.EmailAddress, "13983053455@163.com");
        // V3版本
        X509v3CertificateBuilder v3CertBuilder = new JcaX509v3CertificateBuilder(
                JcaX500NameUtil.getIssuer(caCert),
                BigInteger.valueOf(2),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * 5)),
                subjectBuilder.build(),
                pubKey);
        // 扩展
        JcaX509ExtensionUtils utils = new JcaX509ExtensionUtils();
        // 主体密钥标识符
        v3CertBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                utils.createSubjectKeyIdentifier(pubKey));
        // 颁发机构密钥标识符
        v3CertBuilder.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                utils.createAuthorityKeyIdentifier(caCert));
        // 基本约束
        v3CertBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(0));
        ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BC).build(caPrivKey);
        X509CertificateHolder certificateHolder = v3CertBuilder.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificateHolder);
        return certificate;
    }

    /**
     * CA证书
     *
     * @param caPubKey  CA公钥
     * @param caPrivKey CA私钥
     * @return CA
     */
    @SneakyThrows
    private static X509Certificate createMasterCert(PublicKey caPubKey, PrivateKey caPrivKey) {
        // 颁发者
        String issuer = "C=CN,O=China,OU=CA";
        // 使用者
        String subject = "C=CN,O=China,OU=CA";
        // // V3版本
        // JcaX509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(new X500Name(issuer),
        //         new BigInteger("1234567812345678"),
        //         new Date(),
        //         new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)),
        //         new X500Name(subject),
        //         caPubKey);
        // V1版本
        JcaX509v1CertificateBuilder certificateBuilder = new JcaX509v1CertificateBuilder(
                new X500Name(issuer),
                new BigInteger("1"),
                new Date(),
                new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * 10)),
                new X500Name(subject),
                caPubKey
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BC).build(caPrivKey);
        X509CertificateHolder certificateHolder = certificateBuilder.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificateHolder);
        return certificate;
    }

}
