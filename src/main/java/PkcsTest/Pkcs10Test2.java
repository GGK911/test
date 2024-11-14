package PkcsTest;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;

import javax.security.auth.x500.X500Principal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;

/**
 * @author TangHaoKai
 * @version V1.0 2024/9/10 15:10
 */
public class Pkcs10Test2 {
    private static final Provider BC = new BouncyCastleProvider();

    public static void main(String[] args) throws Exception {
        sm2P10Test();
        rsaP10Test();
    }

    public static void sm2P10Test() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        // 获取SM2曲线参数
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        // 默认32字节，256bit位
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", BC);
        generator.initialize(ecSpec);
        KeyPair keyPair = generator.generateKeyPair();
        PrivateKey aPrivate = keyPair.getPrivate();
        System.out.println("privateKeyBase64>> " + Base64.toBase64String(aPrivate.getEncoded()));
        PublicKey aPublic = keyPair.getPublic();
        System.out.println("publicKeyBase64>> " + Base64.toBase64String(aPublic.getEncoded()));

        String subjectParam = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,EMAILADDRESS=13983053455@163.com,L=重庆,ST=重庆";
        X500Principal subject = new X500Principal(subjectParam);
        // SHA256withRSA算法
        ContentSigner signer = new JcaContentSignerBuilder("SM3withSm2")
                .setProvider(BC)
                .build(aPrivate);
        // CSR builder
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());
        // 创建
        PKCS10CertificationRequest sm2Csr = builder.build(signer);
        System.out.println("CSR>> " + Base64.toBase64String(sm2Csr.getEncoded()));
    }

    /**
     * RSA
     */
    public static void rsaP10Test() throws Exception {
        Security.addProvider(BC);
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BC);
        // 密钥长度
        // generator.initialize(1024);
        generator.initialize(2048);
        // generator.initialize(4096);
        KeyPair keyPair = generator.generateKeyPair();
        final PublicKey aPublic = keyPair.getPublic();
        final PrivateKey aPrivate = keyPair.getPrivate();
        System.out.println("privateKeyBase64>> " + Base64.toBase64String(aPrivate.getEncoded()));
        System.out.println("pubKeyBase64>> " + Base64.toBase64String(aPublic.getEncoded()));
        String subjectParam = "CN=MCSCA";
        X500Principal subject = new X500Principal(subjectParam);
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider(BC)
                .build(aPrivate);
        PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, aPublic);
        PKCS10CertificationRequest csr = builder.build(signer);
        System.out.println("CSR>> " + Base64.toBase64String(csr.getEncoded()));
    }


}
