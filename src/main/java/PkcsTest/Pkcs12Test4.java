package PkcsTest;

import cn.com.mcsca.pki.core.util.KeyUtil;
import cn.com.mcsca.pki.core.util.P12Util;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

/**
 * @author TangHaoKai
 * @version V1.0 2024/6/24 13:14
 */
public class Pkcs12Test4 {
    private static final Provider BC = new BouncyCastleProvider();
    private static final String ROOT = "src/main/java/PkcsTest";
    private static final char[] PASSWD = {'1', '2', '3', '4', '5', '6'};
    private static final KeyFactory rsaKeyFactory;
    private static final KeyFactory sm2KeyFactory;
    private static final CertificateFactory certificateFactory;

    static {
        try {
            rsaKeyFactory = KeyFactory.getInstance("RSA", BC);
            sm2KeyFactory = KeyFactory.getInstance("EC", BC);
            certificateFactory = CertificateFactory.getInstance("X.509", BC);
        } catch (NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @SneakyThrows
    public void analysis() {
        // 尝试读取
        byte[] bytes = FileUtil.readBytes(Paths.get(ROOT + "/pfxTest03.pfx").toAbsolutePath().toString());
        KeyStore ks = KeyStore.getInstance("PKCS12", BC);
        ks.load(new ByteArrayInputStream(bytes), PASSWD);
        Enumeration<String> aliases = ks.aliases();
        char[] PASSWD = {'1', '2', '3', '4', '5', '6'};
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Key key = ks.getKey(alias, PASSWD);
            if (key != null) {
                // 私钥
                PrivateKey priKey = (PrivateKey) key;
                System.out.println("priKey>> " + Base64.toBase64String(priKey.getEncoded()));

                // 证书
                Certificate[] certificateChain = ks.getCertificateChain(alias);
                for (Certificate certificate : certificateChain) {
                    System.out.println("certificate>> " + Base64.toBase64String(certificate.getEncoded()));

                    // 公钥
                    PublicKey publicKey = certificate.getPublicKey();
                    System.out.println("pubKey>> " + Base64.toBase64String(publicKey.getEncoded()));
                }
            }
        }

    }

    @Test
    @SneakyThrows
    public void analysis02() {
        // pki-core中KeyUtil.getPrivateKeyBySM2方法不能解析标志P12结构(能解析的实际是CFCA的结构)
        // RSA
        byte[] bytes = FileUtil.readBytes(Paths.get(ROOT + "/pfxTest03.pfx").toAbsolutePath().toString());
        PrivateKey privateKeyByPFX = KeyUtil.getPrivateKeyBySM2(Base64.encode(bytes), "123456");
    }

    @Test
    @SneakyThrows
    public void pfxToSM2Test() {
        // 尝试读取
        byte[] bytes = FileUtil.readBytes(Paths.get(ROOT + "/pfxTest03.pfx").toAbsolutePath().toString());
        KeyStore ks = KeyStore.getInstance("PKCS12", BC);
        ks.load(new ByteArrayInputStream(bytes), PASSWD);
        Enumeration<String> aliases = ks.aliases();
        char[] PASSWD = {'1', '2', '3', '4', '5', '6'};
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Key key = ks.getKey(alias, PASSWD);
            if (key != null) {
                // 私钥
                PrivateKey priKey = (PrivateKey) key;
                System.out.println("priKey>> " + Base64.toBase64String(priKey.getEncoded()));

                // 证书
                Certificate[] certificateChain = ks.getCertificateChain(alias);
                for (Certificate certificate : certificateChain) {
                    System.out.println("certificate>> " + Base64.toBase64String(certificate.getEncoded()));

                    // 公钥
                    PublicKey publicKey = certificate.getPublicKey();
                    System.out.println("pubKey>> " + Base64.toBase64String(publicKey.getEncoded()));

                    // 转SM2文件
                    byte[] sm2FileBytes = P12Util.generateSM2P12Data(Base64.toBase64String(priKey.getEncoded()), Base64.toBase64String(certificate.getEncoded()), "123456");
                    String sm2Base64 = new String(sm2FileBytes, StandardCharsets.UTF_8);
                    System.out.println(String.format("%-16s", "sm2Base64>> ") + sm2Base64);

                    FileUtil.writeBytes(sm2FileBytes, Paths.get(ROOT + "/pfxTest03.sm2").toAbsolutePath().toString());
                }
            }
        }
    }

}
