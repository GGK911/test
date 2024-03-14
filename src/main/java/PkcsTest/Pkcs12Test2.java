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
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
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
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.util.encoders.Base64;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author TangHaoKai
 * @version V1.0 2023-12-06 10:10
 **/
public class Pkcs12Test2 {
    private static final Provider BC = new BouncyCastleProvider();
    private static final char[] PASSWD = {'1', '2', '3', '4', '5', '6'};

    @SneakyThrows
    public static void main(String[] args) {
        // set up the keys
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC", BC);

        // jdk sun自带
        // g.initialize(new ECNamedCurveGenParameterSpec("secp112r1"));
        // g.initialize(new ECNamedCurveGenParameterSpec("secp256k1"));
        // jdk没有的
        g.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"));

        KeyPair p = g.generateKeyPair();

        PrivateKey privKey = p.getPrivate();
        PublicKey pubKey = p.getPublic();
        System.out.println(Base64.toBase64String(pubKey.getEncoded()));
        System.out.println(Base64.toBase64String(privKey.getEncoded()));

        // 证书链
        X509Certificate[] chain = createCertChain(g, pubKey);

        // pfx
        PKCS12PfxPdu pfx = createPfx(privKey, pubKey, chain);
        FileUtil.writeBytes(pfx.getEncoded(), "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\pfxTest02.pfx");

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
     * 生成证书链
     *
     * @param pubKey 公钥
     * @return 证书链
     */
    @SneakyThrows
    private static X509Certificate[] createCertChain(KeyPairGenerator g, PublicKey pubKey) {
        // CA公私钥
        KeyPair CAKeyPair = g.generateKeyPair();
        PrivateKey caPrivKey = CAKeyPair.getPrivate();
        PublicKey caPubKey = CAKeyPair.getPublic();
        // 中间
        KeyPair IMKeyPair = g.generateKeyPair();
        PrivateKey intPrivKey = IMKeyPair.getPrivate();
        PublicKey intPubKey = IMKeyPair.getPublic();

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
                // thk's todo 控制证书生效时间
                // DateUtil.parse("202312081405000", "yyyyMMddHHmmssSSS"),
                new Date(),
                new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365)),
                // new Date(System.currentTimeMillis() + (1000L * 1)),
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
        v3CertBuilder.addExtension(
                Extension.authorityInfoAccess,
                true,
                new AuthorityInformationAccess(new AccessDescription[]{new AccessDescription(AccessDescription.id_ad_ocsp, new GeneralName(GeneralName.dNSName, "http://127.0.0.1/caissue.htm")), new AccessDescription(AccessDescription.id_ad_caIssuers, new GeneralName(GeneralName.dNSName, "http://127.0.0.1:20443"))})
        );

        // Key Usage 密钥用法
        v3CertBuilder.addExtension(
                Extension.keyUsage,
                true,
                new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment)
        );

        // Basic Constraints 基本约束
        v3CertBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(3)
        );

        // Extended Key Usage 增强型密钥用法
        v3CertBuilder.addExtension(
                Extension.extendedKeyUsage,
                true,
                new ExtendedKeyUsage(KeyPurposeId.id_kp_timeStamping)
        );
        // SM3withSM2 OID 为1.2.156.10197.1.501
        ContentSigner signer = new JcaContentSignerBuilder("SM3withSM2").setProvider(BC).build(caPrivKey);
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
                utils.createAuthorityKeyIdentifier(caCert));
        // 基本约束
        v3CertBuilder.addExtension(
                Extension.basicConstraints,
                false,
                new BasicConstraints(0));

        // Key Usage 密钥用法
        v3CertBuilder.addExtension(
                Extension.keyUsage,
                false,
                new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign | KeyUsage.digitalSignature | KeyUsage.keyEncipherment)
                // new KeyUsage(KeyUsage.cRLSign | KeyUsage.digitalSignature | KeyUsage.keyEncipherment)
        );


        ContentSigner signer = new JcaContentSignerBuilder("SM3withSM2").setProvider(BC).build(caPrivKey);
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
        // V3版本
        JcaX509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(new X500Name(issuer),
                new BigInteger("1234567812345678"),
                new Date(),
                new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)),
                new X500Name(subject),
                caPubKey);
        // V1版本
        // JcaX509v1CertificateBuilder certificateBuilder = new JcaX509v1CertificateBuilder(
        //         new X500Name(issuer),
        //         new BigInteger("1"),
        //         new Date(),
        //         new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365)),
        //         new X500Name(subject),
        //         caPubKey
        // );

        ContentSigner signer = new JcaContentSignerBuilder("SM3withSM2").setProvider(BC).build(caPrivKey);
        X509CertificateHolder certificateHolder = certificateBuilder.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certificateHolder);
        return certificate;
    }


}
